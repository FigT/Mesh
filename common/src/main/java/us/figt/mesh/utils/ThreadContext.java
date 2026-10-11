package us.figt.mesh.utils;

/**
 * Represents the context in which a task is executed, either synchronously or asynchronously.
 *
 * @author FigT
 */
public enum ThreadContext {

    /**
     * This context represents a synchronous thread execution, that is, on the main thread of the application
     */
    SYNC,

    /**
     * This context represents an asynchronous thread execution, that is, on a separate thread from the main thread of the application
     */
    ASYNC;
}