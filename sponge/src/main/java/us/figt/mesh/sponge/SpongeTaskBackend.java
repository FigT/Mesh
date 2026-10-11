package us.figt.mesh.sponge;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.util.Ticks;
import org.spongepowered.plugin.PluginContainer;
import us.figt.mesh.utils.TaskBackend;
import us.figt.mesh.utils.ThreadContext;

import java.util.Objects;
import java.util.logging.Level;

/**
 * A {@link org.spongepowered.api.scheduler.Scheduler}-based implementation of the {@link TaskBackend} interface.
 *
 * @author FigT
 */
public final class SpongeTaskBackend extends TaskBackend {

    private static @Nullable Thread MAIN_THREAD;

    private final @NotNull PluginContainer plugin;

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
        return computeBackendIfAbsent(
                SpongeTaskBackend.class,
                plugin.metadata().id(),
                () -> new SpongeTaskBackend(plugin)
        );
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
        Sponge.asyncScheduler().executor(this.plugin).execute(runnable);
    }

    @Override
    public void runSync(@NotNull Runnable runnable) {
        if (getThreadContext(Thread.currentThread()) == ThreadContext.SYNC) {
            runnable.run();
            return;
        }

        Sponge.server().scheduler().executor(this.plugin).execute(runnable);
    }

    @Override
    public void runSyncLater(@NotNull Runnable runnable, long delay) {
        Sponge.server().scheduler().submit(Task.builder()
                .plugin(this.plugin)
                .delay(Ticks.of(delay))
                .execute(runnable)
                .build());
    }

    @Override
    public void runAsyncLater(@NotNull Runnable runnable, long delay) {
        Sponge.asyncScheduler().submit(Task.builder()
                .plugin(this.plugin)
                .delay(Ticks.of(delay))
                .execute(runnable)
                .build());
    }

    @Override
    public void log(@NotNull Level level, @NotNull String message) {
        org.apache.logging.log4j.Level log4jLevel = switch (level.getName()) {
            case "SEVERE" -> org.apache.logging.log4j.Level.ERROR;
            case "WARNING" -> org.apache.logging.log4j.Level.WARN;
            case "CONFIG", "FINE", "FINER", "FINEST" -> org.apache.logging.log4j.Level.DEBUG;
            default -> org.apache.logging.log4j.Level.INFO;
        };

        this.plugin.logger().log(log4jLevel, message);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        SpongeTaskBackend that = (SpongeTaskBackend) o;
        return Objects.equals(this.plugin, that.plugin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.plugin);
    }

    @Override
    public String toString() {
        return "SpongeTaskBackend{" +
                "plugin=" + this.plugin +
                ", debugMode=" + this.isDebugMode() +
                '}';
    }
}
