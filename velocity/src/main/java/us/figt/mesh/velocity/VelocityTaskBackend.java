package us.figt.mesh.velocity;

import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.proxy.ProxyServer;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.figt.mesh.utils.TaskBackend;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * A {@link com.velocitypowered.api.scheduler.Scheduler}-based implementation of the {@link TaskBackend} interface.
 *
 * @author FigT
 */
public final class VelocityTaskBackend extends TaskBackend {

    private final @NotNull PluginContainer plugin;
    private final @NotNull ProxyServer proxy;
    private final @NotNull Logger logger;

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
        return computeBackendIfAbsent(VelocityTaskBackend.class, plugin.getDescription().getId(), () -> new VelocityTaskBackend(plugin, proxy));
    }

    @Override
    public void runAsync(@NotNull Runnable runnable) {
        this.proxy.getScheduler().buildTask(getPluginInstance(), runnable)
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
        this.proxy.getScheduler().buildTask(getPluginInstance(), runnable)
                .delay(delay * 50L, TimeUnit.MILLISECONDS)
                .schedule();
    }

    @Override
    public void log(@NotNull Level level, @NotNull String message) {
        switch (level.getName()) {
            case "SEVERE" -> this.logger.error(message);
            case "WARNING" -> this.logger.warn(message);
            case "CONFIG" -> this.logger.debug(message);
            case "FINE", "FINER", "FINEST" -> this.logger.trace(message);
            default -> this.logger.info(message);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        VelocityTaskBackend that = (VelocityTaskBackend) o;
        return Objects.equals(this.plugin, that.plugin)
                && Objects.equals(this.proxy, that.proxy)
                && Objects.equals(this.logger, that.logger);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.plugin, this.proxy, this.logger);
    }

    @Override
    public String toString() {
        return "VelocityTaskBackend{" +
                "plugin=" + this.plugin +
                ", proxy=" + this.proxy +
                ", logger=" + this.logger +
                ", debugMode=" + this.isDebugMode() +
                '}';
    }

    private @NotNull Object getPluginInstance() {
        return this.plugin.getInstance().orElseThrow(() -> new IllegalStateException("Plugin instance is not available"));
    }
}
