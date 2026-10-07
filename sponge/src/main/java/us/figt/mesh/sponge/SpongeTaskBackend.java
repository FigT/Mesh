package us.figt.mesh.sponge;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.util.Ticks;
import org.spongepowered.plugin.PluginContainer;
import us.figt.mesh.utils.TaskBackend;
import us.figt.mesh.utils.ThreadContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * A {@link org.spongepowered.api.scheduler.Scheduler}-based implementation of the {@link TaskBackend} interface.
 *
 * @author FigT
 */
public final class SpongeTaskBackend implements TaskBackend {

    private static @Nullable Thread MAIN_THREAD;
    private static final @NotNull Map<@NotNull String, @NotNull SpongeTaskBackend> BACKENDS = new ConcurrentHashMap<>();

    private final @NotNull PluginContainer plugin;
    private boolean debugMode;

    private SpongeTaskBackend(@NotNull PluginContainer plugin) {
        this.plugin = plugin;
    }

    /**
     * Gets the {@link SpongeTaskBackend} instance for the provided plugin.
     * If an instance does not already exist for the plugin, a new one will be created and stored.
     *
     * @param plugin the providing plugin for which to get the {@link SpongeTaskBackend} instance
     * @return the {@link SpongeTaskBackend} instance for the provided plugin
     */
    public static @NotNull SpongeTaskBackend of(@NotNull PluginContainer plugin) {
        return BACKENDS.computeIfAbsent(plugin.metadata().id(), id -> new SpongeTaskBackend(plugin));
    }

    /**
     * Gets the {@link ThreadContext} for the provided thread.
     *
     * @param thread the thread for which to get the {@link ThreadContext}
     * @return the {@link ThreadContext} for the provided thread
     */
    public static synchronized @NotNull ThreadContext getThreadContext(@NotNull Thread thread) {
        if (MAIN_THREAD == null && Sponge.server().onMainThread()) {
            MAIN_THREAD = Thread.currentThread();
        }

        return thread.equals(MAIN_THREAD) ? ThreadContext.SYNC : ThreadContext.ASYNC;
    }



    @Override
    public void runAsync(@NotNull Runnable runnable) {
        Sponge.asyncScheduler().executor(plugin).execute(runnable);
    }

    @Override
    public void runSync(@NotNull Runnable runnable) {
        if (getThreadContext(Thread.currentThread()) == ThreadContext.SYNC) {
            runnable.run();
            return;
        }

        Sponge.server().scheduler().executor(plugin).execute(runnable);
    }

    @Override
    public void runSyncLater(@NotNull Runnable runnable, long delay) {
        Sponge.server().scheduler().submit(Task.builder()
                .plugin(plugin)
                .delay(Ticks.of(delay))
                .execute(runnable)
                .build());
    }

    @Override
    public void runAsyncLater(@NotNull Runnable runnable, long delay) {
        Sponge.asyncScheduler().submit(Task.builder()
                .plugin(plugin)
                .delay(Ticks.of(delay))
                .execute(runnable)
                .build());
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
        org.apache.logging.log4j.Level log4jLevel = switch (level.getName()) {
            case "SEVERE" -> org.apache.logging.log4j.Level.ERROR;
            case "WARNING" -> org.apache.logging.log4j.Level.WARN;
            case "CONFIG", "FINE", "FINER", "FINEST" -> org.apache.logging.log4j.Level.DEBUG;
            default -> org.apache.logging.log4j.Level.INFO;
        };

        plugin.logger().log(log4jLevel, message);
    }
}
