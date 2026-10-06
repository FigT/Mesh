package us.figt.mesh.utils;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

/**
 * Represents a backend for running tasks asynchronously or synchronously, with support for delayed execution and logging.
 * Implementations of this interface should provide the actual execution logic for running tasks in different contexts.
 *
 * @author FigT
 */
public interface TaskBackend {

    /**
     * Runs the given runnable <strong>asynchronously</strong>.
     *
     * @param runnable the task to be executed asynchronously
     */
    @ApiStatus.Internal
    void runAsync(@NotNull Runnable runnable);

    /**
     * Runs the given runnable <strong>synchronously</strong>.
     *
     * @param runnable the task to be executed synchronously
     */
    @ApiStatus.Internal
    void runSync(@NotNull Runnable runnable);


    /**
     * Runs the given runnable <strong>synchronously</strong> after a specified delay.
     *
     * @param runnable the task to be executed synchronously
     * @param delay the delay in ticks before executing the task
     */
    @ApiStatus.Internal
    void runSyncLater(@NotNull Runnable runnable, long delay);

    /**
     * Runs the given runnable <strong>asynchronously</strong> after a specified delay.
     *
     * @param runnable the task to be executed asynchronously
     * @param delay the delay in ticks before executing the task
     */
    @ApiStatus.Internal
    void runAsyncLater(@NotNull Runnable runnable, long delay);

    /**
     * @return if the backend is in debug mode, which may enable additional logging features
     */
    boolean isDebugMode();

    /**
     * Sets the debug mode for the backend, which may enable additional logging features.
     * @param debugMode true to enable debug mode, false to disable it
     * @return the current instance of TaskBackend for method chaining
     */
    @SuppressWarnings("UnusedReturnValue")
    @NotNull TaskBackend setDebugMode(boolean debugMode);

    /**
     * Logs a message at the specified logging level.
     * @param level the logging level
     * @param message the message to be logged
     */
    @ApiStatus.Internal
    void log(@NotNull Level level, @NotNull String message);
}