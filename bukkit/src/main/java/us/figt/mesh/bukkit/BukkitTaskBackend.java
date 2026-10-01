package us.figt.mesh.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.figt.mesh.utils.TaskBackend;
import us.figt.mesh.utils.ThreadContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * A {@link org.bukkit.scheduler.BukkitScheduler}-based implementation of the {@link TaskBackend} interface.
 *
 * @author FigT
 */
public final class BukkitTaskBackend implements TaskBackend {

    private static @Nullable Thread MAIN_THREAD;
    private static final @NotNull Map<@NotNull String, @NotNull BukkitTaskBackend> BACKENDS = new ConcurrentHashMap<>();

    private final @NotNull Plugin plugin;
    private boolean debugMode = false;

    private BukkitTaskBackend(@NotNull Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Gets the {@link BukkitTaskBackend} instance for the provided plugin.
     * If an instance does not already exist for the plugin, a new one will be created and stored.
     *
     * @param plugin the providing plugin for which to get the {@link BukkitTaskBackend} instance
     * @return the {@link BukkitTaskBackend} instance for the provided plugin
     */
    public static BukkitTaskBackend of(@NotNull Plugin plugin) {
        return BACKENDS.computeIfAbsent(plugin.getName(), name -> new BukkitTaskBackend(plugin));
    }

    /**
     * Gets the {@link ThreadContext} for the provided thread.
     *
     * @param thread the thread for which to get the {@link ThreadContext}
     * @return the {@link ThreadContext} for the provided thread
     */
    public static synchronized @NotNull ThreadContext getThreadContext(@NotNull Thread thread) {
        if (MAIN_THREAD == null && Bukkit.getServer().isPrimaryThread()) {
            MAIN_THREAD = Thread.currentThread();
        }

        return thread.equals(MAIN_THREAD) ? ThreadContext.SYNC : ThreadContext.ASYNC;
    }

    @Override
    public void runAsync(@NotNull Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, runnable);
    }

    @Override
    public void runSync(@NotNull Runnable runnable) {
        if (getThreadContext(Thread.currentThread()) == ThreadContext.SYNC) {
            runnable.run();
        } else {
            Bukkit.getScheduler().runTask(this.plugin, runnable);
        }
    }

    @Override
    public void runSyncLater(@NotNull Runnable runnable, long delay) {
        if (delay <= NO_DELAY) {
            runSync(runnable);
            return;
        }

        Bukkit.getScheduler().runTaskLater(this.plugin, runnable, delay);
    }

    @Override
    public void runAsyncLater(@NotNull Runnable runnable, long delay) {
        if (delay <= NO_DELAY) {
            runAsync(runnable);
            return;
        }

        Bukkit.getScheduler().runTaskLaterAsynchronously(this.plugin, runnable, delay);
    }

    @Override
    public boolean isDebugMode() {
        return this.debugMode;
    }

    @Override
    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    @Override
    public void log(@NotNull Level level, @NotNull String message) {
        this.plugin.getLogger().log(level, message);
    }
}
