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
     * A constant representing no delay for task execution.
     */
    long NO_DELAY = 0L;

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
    @NotNull TaskBackend setDebugMode(boolean debugMode);

    /**
     * Logs a message at the specified logging level.
     * @param level the logging level
     * @param message the message to be logged
     */
    @ApiStatus.Internal
    void log(@NotNull Level level, @NotNull String message);


    /**
     * Runs the given runnable in the specified thread context (synchronous or asynchronous) after a specified delay (or immediately if the delay is zero or negative).
     *
     * @param runnable the task to be executed
     * @param context the thread context in which to execute the task (synchronous or asynchronous)
     * @param delay the delay in ticks before executing the task (if zero or negative, the task will be executed immediately)
     */
    @ApiStatus.Internal
    @ApiStatus.NonExtendable
    default void run(@NotNull Runnable runnable, @NotNull ThreadContext context, long delay) {
        if (delay <= NO_DELAY) {
            if (context == ThreadContext.ASYNC) {
                runAsync(runnable);
            } else if (context == ThreadContext.SYNC) {
                runSync(runnable);
            }
            return;
        }


        if (context == ThreadContext.ASYNC) {
            runAsyncLater(runnable, delay);
        } else if (context == ThreadContext.SYNC) {
            runSyncLater(runnable, delay);
        }
    }


    /**
     * Logs a debug message indicating that an exception has been caught, along with the exception's class name.
     * It also prints the stack trace of the exception to the standard error stream.
     * @param throwable the exception that was caught
     */
    @ApiStatus.Internal
    default void debugException(@NotNull Throwable throwable) {
        log(Level.WARNING, "Mesh-" + getClass().getSimpleName() + "-Debug - Caught a " + throwable.getClass().getSimpleName());
        // noinspection CallToPrintStackTrace
        throwable.printStackTrace();
    }
}