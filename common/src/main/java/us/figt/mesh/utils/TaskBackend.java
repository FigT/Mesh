package us.figt.mesh.utils;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.logging.Level;

/**
 * Represents a backend for running tasks asynchronously or synchronously, with support for delayed execution, and logging capabilities.
 *
 * @author FigT
 */
public abstract class TaskBackend {

    private static final @NotNull Map<@NotNull String, @NotNull TaskBackend> BACKENDS = new ConcurrentHashMap<>();

    private boolean debugMode = false;

    /**
     * Retrieves an existing backend instance of the specified type and ID, or creates a new one using the provided supplier if it does not exist.
     *
     * @param backendClass the class of the backend to retrieve or create
     * @param id the unique identifier for the backend
     * @param supplier a supplier that provides a new instance of the backend if it does not already exist
     * @param <T> the type of the backend
     * @return an existing or newly created instance of the specified backend type
     * @throws IllegalStateException if an existing backend with the same ID is not of the expected type
     */
    protected static <T extends TaskBackend> @NotNull T computeBackendIfAbsent(@NotNull Class<T> backendClass, @NotNull String id, @NotNull Supplier<@NotNull TaskBackend> supplier) {
        TaskBackend inMap = BACKENDS.get(id);

        if (inMap == null) {
            TaskBackend backend = supplier.get();
            BACKENDS.put(id, backend);
            return backendClass.cast(backend);
        }

        if (!backendClass.isInstance(inMap)) {
            throw new IllegalStateException("Backend with id '" + id + "' is not of type " + backendClass.getName());
        }

        return backendClass.cast(inMap);

    }

    /**
     * Runs the given runnable <strong>asynchronously</strong>.
     *
     * @param runnable the task to be executed asynchronously
     */
    @ApiStatus.Internal
    public abstract void runAsync(@NotNull Runnable runnable);

    /**
     * Runs the given runnable <strong>synchronously</strong>.
     *
     * @param runnable the task to be executed synchronously
     */
    @ApiStatus.Internal
    public abstract void runSync(@NotNull Runnable runnable);


    /**
     * Runs the given runnable <strong>synchronously</strong> after a specified delay.
     *
     * @param runnable the task to be executed synchronously
     * @param delay the delay in ticks before executing the task
     */
    @ApiStatus.Internal
    public abstract void runSyncLater(@NotNull Runnable runnable, long delay);

    /**
     * Runs the given runnable <strong>asynchronously</strong> after a specified delay.
     *
     * @param runnable the task to be executed asynchronously
     * @param delay the delay in ticks before executing the task
     */
    @ApiStatus.Internal
    public abstract void runAsyncLater(@NotNull Runnable runnable, long delay);

    /**
     * Logs a message at the specified logging level.
     * @param level the logging level
     * @param message the message to be logged
     */
    @ApiStatus.Internal
    public abstract void log(@NotNull Level level, @NotNull String message);

    /**
     * @return if the backend is in debug mode, which may enable additional logging features
     */
    public boolean isDebugMode() {
        return this.debugMode;
    }

    /**
     * Sets the debug mode for the backend, which may enable additional logging features.
     * @param debugMode true to enable debug mode, false to disable it
     * @return the current instance of TaskBackend for method chaining
     */
    @SuppressWarnings("UnusedReturnValue")
    public @NotNull TaskBackend setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (this == o) return true;

        TaskBackend that = (TaskBackend) o;
        return this.debugMode == that.debugMode;
    }

    @Override
    public int hashCode() {
        return Boolean.hashCode(this.debugMode);
    }

    @Override
    public String toString() {
        return "TaskBackend{" +
                "debugMode=" + this.debugMode +
                '}';
    }
}