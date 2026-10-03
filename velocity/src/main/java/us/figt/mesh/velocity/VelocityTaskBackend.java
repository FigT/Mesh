package us.figt.mesh.velocity;

import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.proxy.ProxyServer;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.figt.mesh.utils.TaskBackend;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * A {@link com.velocitypowered.api.scheduler.Scheduler}-based implementation of the {@link TaskBackend} interface.
 *
 * @author FigT
 */
public final class VelocityTaskBackend implements TaskBackend {

    private static final @NotNull Map<@NotNull String, @NotNull VelocityTaskBackend> BACKENDS = new ConcurrentHashMap<>();

    private final @NotNull PluginContainer plugin;
    private final @NotNull ProxyServer proxy;
    private final @NotNull Logger logger;

    private boolean debugMode = false;

    private VelocityTaskBackend(@NotNull PluginContainer plugin, @NotNull ProxyServer proxy) {
        this.plugin = plugin;
        this.proxy = proxy;
        this.logger = LoggerFactory.getLogger(plugin.getDescription().getId());
    }

    /**
     * Gets the {@link VelocityTaskBackend} instance for the provided plugin and Velocity proxy server.
     * If an instance does not already exist for the plugin's ID, a new one will be created and stored.
     *
     * @param plugin the providing plugin for which to get the {@link VelocityTaskBackend} instance
     * @param proxy  the Velocity proxy server instance
     * @return the {@link VelocityTaskBackend} instance for the provided plugin
     */
    public static VelocityTaskBackend of(@NotNull PluginContainer plugin, @NotNull ProxyServer proxy) {
        return BACKENDS.computeIfAbsent(plugin.getDescription().getId(), name -> new VelocityTaskBackend(plugin, proxy));
    }

    @Override
    public void runAsync(@NotNull Runnable runnable) {
        proxy.getScheduler().buildTask(getPluginInstance(), runnable)
                .schedule();
    }

    @Override
    public void runSync(@NotNull Runnable runnable) {
        // In Velocity, there is no distinction between sync and async tasks, so we can just call runAsync
        runAsync(runnable);
    }

    @Override
    public void runSyncLater(@NotNull Runnable runnable, long delay) {
        // In Velocity, there is no distinction between sync and async tasks, so we can just call runAsyncLater
        runAsyncLater(runnable, delay);
    }

    @Override
    public void runAsyncLater(@NotNull Runnable runnable, long delay) {
        // the delay is in ticks, and Velocity's scheduler uses milliseconds,
        // so we multiply by 50 (1 tick = 50 ms)
        proxy.getScheduler().buildTask(getPluginInstance(), runnable)
                .delay(delay * 50L, TimeUnit.MILLISECONDS)
                .schedule();
    }

    @Override
    public boolean isDebugMode() {
        return this.debugMode;
    }

    @Override
    public @NotNull TaskBackend setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
        return this;
    }

    @Override
    public void log(@NotNull Level level, @NotNull String message) {
        // this is a gross mapping,
        // but we're using JDK 8 so we can't use enhanced switch statements
        if (level == Level.INFO) {
            logger.info(message);
        } else if (level == Level.WARNING) {
            logger.warn(message);
        } else if (level == Level.SEVERE) {
            logger.error(message);
        } else {
            logger.debug(message);
        }
    }

    private @NotNull Object getPluginInstance() {
        return plugin.getInstance().orElseThrow(() -> new IllegalStateException("Plugin instance is not available"));
    }
}
