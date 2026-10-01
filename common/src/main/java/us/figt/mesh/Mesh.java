package us.figt.mesh;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.figt.mesh.utils.TaskBackend;
import us.figt.mesh.utils.ThreadContext;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static us.figt.mesh.utils.TaskBackend.NO_DELAY;
import static us.figt.mesh.utils.ThreadContext.ASYNC;
import static us.figt.mesh.utils.ThreadContext.SYNC;

/**
 * @author FigT
 */
@SuppressWarnings("unused") // i don't want IntelliJ to yell at me
public class Mesh<T> {

    private final @NotNull TaskBackend backend;
    private final @NotNull CompletableFuture<T> completableFuture; // the backing CompletableFuture
    private final @NotNull AtomicBoolean hasBeenSupplied = new AtomicBoolean(false);
    private final @NotNull AtomicBoolean isCancelled = new AtomicBoolean(false);

    protected Mesh(@NotNull TaskBackend backend, @NotNull CompletableFuture<T> completableFuture) {
        this.backend = backend;
        this.completableFuture = completableFuture;
    }

    protected Mesh(@NotNull TaskBackend backend, @NotNull CompletableFuture<T> completableFuture, boolean supplied, boolean cancelled) {
        this(backend, completableFuture);

        this.hasBeenSupplied.set(supplied);
        this.isCancelled.set(cancelled);
    }

    // TODO: add more comments







    // ~~~ ASYNC BELOW ~~~


    /**
     * Supplies this Mesh with a value <strong>asynchronously</strong>.
     *
     * @param supplier the value to supply
     * @return the supplied Mesh instance
     */
    public @NotNull Mesh<T> supplyAsync(@NotNull Supplier<T> supplier) {
        return supply(supplier, ASYNC, NO_DELAY);
    }

    /**
     * Executes a runnable <strong>asynchronously</strong>.
     *
     * @param runnable the runnable to run
     * @return this Mesh instance
     */
    public @NotNull Mesh<Void> runAsync(@NotNull Runnable runnable) {
        return applyRun(runnable, ASYNC, NO_DELAY);
    }

    /**
     * Applies a function to this Mesh <strong>asynchronously</strong>.
     *
     * @param function the function to apply
     * @param <R>      the type of the function's result
     * @return this Mesh instance with the applied function
     */
    public <R> @NotNull Mesh<R> applyAsync(@NotNull Function<? super T, ? extends R> function) {
        return apply(function, ASYNC, NO_DELAY);
    }

    /**
     * Executes an action <strong>asynchronously</strong> with Void return type.
     *
     * @param consumer the action to run
     * @return this Mesh instance with Void return type
     */
    public @NotNull Mesh<Void> acceptAsync(@NotNull Consumer<T> consumer) {
        return accept(consumer, ASYNC, NO_DELAY);
    }

    /**
     * Executes an operation <strong>asynchronously</strong> if an exception occurred.
     *
     * @param function the function to execute
     * @return this Mesh instance with the applied function
     */
    public @NotNull Mesh<T> exceptionallyAsync(@NotNull Function<Throwable, ? extends T> function) {
        return exceptionally(function, ASYNC, NO_DELAY);
    }

    /**
     * Creates a new Mesh that, when this Mesh completes normally, is executed (<strong>asynchronously</strong>) with this Mesh's result as the argument to the supplied function.
     *
     * @param function the function to execute
     * @param <R>      the type of the returned Mesh's result
     * @return the new Mesh instance
     */
    public <R> @NotNull Mesh<R> composeAsync(@NotNull Function<? super T, ? extends Mesh<R>> function) {
        return compose(function, ASYNC, NO_DELAY);
    }

    /**
     * Supplies this Mesh with a value (given by a Callable) <strong>asynchronously</strong>.
     *
     * @param callable the value to supply
     * @return the supplied Mesh instance
     */
    public @NotNull Mesh<T> supplyCallableAsync(@NotNull Callable<T> callable) {
        return supplyCallable(callable, ASYNC, NO_DELAY);
    }


    // ~~~ DELAYED ASYNC BELOW ~~~


    /**
     * Supplies this Mesh with a value <strong>asynchronously</strong>.
     *
     * @param supplier the value to supply
     * @param delay    the delay (<strong>in ticks</strong>) to wait to supply this Mesh
     * @return the supplied Mesh instance
     */
    public @NotNull Mesh<T> supplyAsyncDelayed(@NotNull Supplier<T> supplier, long delay) {
        return supply(supplier, ASYNC, delay);
    }

    /**
     * Executes a runnable <strong>asynchronously</strong>.
     *
     * @param runnable the runnable to run
     * @param delay    the delay (<strong>in ticks</strong>) to wait to execute the runnable
     * @return this Mesh instance
     */
    public @NotNull Mesh<Void> runAsyncDelayed(@NotNull Runnable runnable, long delay) {
        return applyRun(runnable, ASYNC, delay);
    }

    /**
     * Applies a function to this Mesh <strong>asynchronously</strong>.
     *
     * @param function the function to apply
     * @param delay    the delay (<strong>in ticks</strong>) to wait to apply the function
     * @param <R>      the type of the function's result
     * @return this Mesh instance with the applied function
     */
    public <R> @NotNull Mesh<R> applyAsyncDelayed(@NotNull Function<? super T, ? extends R> function, long delay) {
        return apply(function, ASYNC, delay);
    }

    /**
     * Executes an action <strong>asynchronously</strong> with Void return type.
     *
     * @param consumer the action to run
     * @param delay    the delay (<strong>in ticks</strong>) to wait to run the action
     * @return this Mesh instance with Void return type
     */
    public @NotNull Mesh<Void> acceptAsyncDelayed(@NotNull Consumer<T> consumer, long delay) {
        return accept(consumer, ASYNC, delay);
    }

    /**
     * Executes an operation <strong>asynchronously</strong> if an exception occurred.
     *
     * @param function the function to execute
     * @param delay    the delay (<strong>in ticks</strong>) to wait to execute the function
     * @return this Mesh instance with the applied function
     */
    public @NotNull Mesh<T> exceptionallyAsyncDelayed(@NotNull Function<Throwable, ? extends T> function, long delay) {
        return exceptionally(function, ASYNC, delay);
    }

    /**
     * Creates a new Mesh that, when this Mesh completes normally, is executed (<strong>asynchronously</strong>) with this Mesh's result as the argument to the supplied function.
     *
     * @param function the function to execute
     * @param delay    the delay (<strong>in ticks</strong>) to wait to execute the function
     * @param <R>      the type of the returned Mesh's result
     * @return the new Mesh instance
     */
    public <R> @NotNull Mesh<R> composeAsyncDelayed(@NotNull Function<? super T, ? extends Mesh<R>> function, long delay) {
        return compose(function, ASYNC, delay);
    }

    /**
     * Supplies this Mesh with a value (given by a Callable) <strong>asynchronously</strong>.
     *
     * @param callable the value to supply
     * @param delay    the delay (<strong>in ticks</strong>) to wait to supply this Mesh
     * @return the supplied Mesh instance
     */
    public @NotNull Mesh<T> supplyCallableAsyncDelayed(@NotNull Callable<T> callable, long delay) {
        return supplyCallable(callable, ASYNC, delay);
    }


    // ~~~ SYNC BELOW ~~~


    /**
     * Supplies this Mesh with a value <strong>synchronously</strong>.
     *
     * @param supplier the value to supply
     * @return the supplied Mesh instance
     */
    public @NotNull Mesh<T> supplySync(@NotNull Supplier<T> supplier) {
        return supply(supplier, SYNC, NO_DELAY);
    }

    /**
     * Executes a runnable <strong>synchronously</strong>.
     *
     * @param runnable the runnable to run
     * @return this Mesh instance
     */
    public @NotNull Mesh<Void> runSync(@NotNull Runnable runnable) {
        return applyRun(runnable, SYNC, NO_DELAY);
    }

    /**
     * Applies a function to this Mesh <strong>synchronously</strong>.
     *
     * @param function the function to apply
     * @param <R>      the type of the function's result
     * @return this Mesh instance with the applied function
     */
    public <R> @NotNull Mesh<R> applySync(@NotNull Function<? super T, ? extends R> function) {
        return apply(function, SYNC, NO_DELAY);
    }

    /**
     * Executes an action <strong>synchronously</strong> with Void return type.
     *
     * @param consumer the action to run
     * @return this Mesh instance with Void return type
     */
    public @NotNull Mesh<Void> acceptSync(@NotNull Consumer<T> consumer) {
        return accept(consumer, SYNC, NO_DELAY);
    }

    /**
     * Executes an operation <strong>synchronously</strong> if an exception occurred.
     *
     * @param function the function to execute
     * @return this Mesh instance with the applied function
     */
    public @NotNull Mesh<T> exceptionallySync(@NotNull Function<Throwable, ? extends T> function) {
        return exceptionally(function, SYNC, NO_DELAY);
    }

    /**
     * Creates a new Mesh that, when this Mesh completes normally, is executed (<strong>synchronously</strong>) with this Mesh's result as the argument to the supplied function.
     *
     * @param function the function to execute
     * @param <R>      the type of the returned Mesh's result
     * @return the new Mesh instance
     */
    public <R> @NotNull Mesh<R> composeSync(@NotNull Function<? super T, ? extends Mesh<R>> function) {
        return compose(function, SYNC, NO_DELAY);
    }

    /**
     * Supplies this Mesh with a value (given by a Callable) <strong>synchronously</strong>.
     *
     * @param callable the value to supply
     * @return the supplied Mesh instance
     */
    public @NotNull Mesh<T> supplyCallableSync(@NotNull Callable<T> callable) {
        return supplyCallable(callable, SYNC, NO_DELAY);
    }


    // ~~~ DELAYED SYNC BELOW ~~~


    /**
     * Supplies this Mesh with a value <strong>synchronously</strong>.
     *
     * @param supplier the value to supply
     * @param delay    the delay (<strong>in ticks</strong>) to wait to supply this Mesh
     * @return the supplied Mesh instance
     */
    public @NotNull Mesh<T> supplySyncDelayed(@NotNull Supplier<T> supplier, long delay) {
        return supply(supplier, SYNC, delay);
    }

    /**
     * Executes a runnable <strong>synchronously</strong>.
     *
     * @param runnable the runnable to run
     * @param delay    the delay (<strong>in ticks</strong>) to wait to execute the runnable
     * @return this Mesh instance
     */
    public @NotNull Mesh<Void> runSyncDelayed(@NotNull Runnable runnable, long delay) {
        return applyRun(runnable, SYNC, delay);
    }

    /**
     * Applies a function to this Mesh <strong>synchronously</strong>.
     *
     * @param function the function to apply
     * @param delay    the delay (<strong>in ticks</strong>) to wait to apply the function
     * @param <R>      the type of the function's result
     * @return this Mesh instance with the applied function
     */
    public <R> @NotNull Mesh<R> applySyncDelayed(@NotNull Function<? super T, ? extends R> function, long delay) {
        return apply(function, SYNC, delay);
    }

    /**
     * Executes an action <strong>synchronously</strong> with Void return type.
     *
     * @param consumer the action to run
     * @param delay    the delay (<strong>in ticks</strong>) to wait to run the action
     * @return this Mesh instance with Void return type
     */
    public @NotNull Mesh<Void> acceptSyncDelayed(@NotNull Consumer<T> consumer, long delay) {
        return accept(consumer, SYNC, delay);
    }

    /**
     * Executes an operation <strong>synchronously</strong> if an exception occurred.
     *
     * @param function the function to execute
     * @param delay    the delay (<strong>in ticks</strong>) to wait to execute the function
     * @return this Mesh instance with the applied function
     */
    public @NotNull Mesh<T> exceptionallySyncDelayed(@NotNull Function<Throwable, ? extends T> function, long delay) {
        return exceptionally(function, SYNC, delay);
    }

    /**
     * Creates a new Mesh that, when this Mesh completes normally, is executed (<strong>synchronously</strong>) with this Mesh's result as the argument to the supplied function.
     *
     * @param function the function to execute
     * @param delay    the delay (<strong>in ticks</strong>) to wait to execute the function
     * @param <R>      the type of the returned Mesh's result
     * @return the new Mesh instance
     */
    public <R> @NotNull Mesh<R> composeSyncDelayed(@NotNull Function<? super T, ? extends Mesh<R>> function, long delay) {
        return compose(function, SYNC, delay);
    }

    /**
     * Supplies this Mesh with a value (given by a Callable) <strong>synchronously</strong>.
     *
     * @param callable the value to supply
     * @param delay    the delay (<strong>in ticks</strong>) to wait to supply this Mesh
     * @return the supplied Mesh instance
     */
    public @NotNull Mesh<T> supplyCallableSyncDelayed(@NotNull Callable<T> callable, long delay) {
        return supplyCallable(callable, SYNC, delay);
    }


    /**
     * If not already completed or cancelled, completes this Mesh with the given value.
     *
     * @param value the value to complete this Mesh with
     */
    public void complete(@Nullable T value) {
        if (!isCancelled.get()) {
            completableFuture.complete(value);
        }
    }

    /**
     * If not already completed or cancelled, completes this Mesh with the given exception.
     *
     * @param throwable the exception
     */
    public void completeExceptionally(@NotNull Throwable throwable) {
        if (!isCancelled.get()) {
            completableFuture.completeExceptionally(throwable);
        }

        if (backend.isDebugMode()) backend.debugException(throwable); // debug exception
    }


    public @NotNull CompletableFuture<T> toCompletableFuture() {
        return completableFuture.thenApply(Function.identity());
    }


    private void setHasBeenSupplied() {
        if (!hasBeenSupplied.compareAndSet(false, true)) {
            throw new AssertionError("This can only be supplied once, and this Mesh has already been supplied");
        }
    }


    private Mesh<T> supply(Supplier<T> supplier, ThreadContext threadContext, long delay) {
        setHasBeenSupplied();
        backend.run(new MeshRunnables.SuppliableRunnable<>(this, supplier), threadContext, delay);

        return this;
    }

    private <R> Mesh<R> apply(Function<? super T, ? extends R> function, ThreadContext threadContext, long delay) {
        Mesh<R> newMesh = new Mesh<>(backend, new CompletableFuture<>());

        completableFuture.whenComplete((value, throwable) -> {
            if (throwable == null) {
                backend.run(new MeshRunnables.FunctionalRunnable<>(newMesh, function, value), threadContext, delay);
            } else {
                newMesh.completeExceptionally(throwable);
            }
        });

        return newMesh;
    }

    private <R> Mesh<R> applyRun(Runnable runnable, ThreadContext threadContext, long delay) {
        Mesh<R> newMesh = new Mesh<>(backend, new CompletableFuture<>());

        completableFuture.whenComplete((value, throwable) -> {
            if (throwable == null) {
                backend.run(new MeshRunnables.AppliableRunnable<>(newMesh, runnable), threadContext, delay);
            } else {
                newMesh.completeExceptionally(throwable);
            }
        });

        return newMesh;
    }

    @ApiStatus.Internal
     <R> Mesh<R> accept(Consumer<T> consumer, ThreadContext threadContext, long delay) {
        Mesh<R> newMesh = new Mesh<>(backend, new CompletableFuture<>());

        completableFuture.whenComplete((value, throwable) -> {
            if (throwable == null) {
                backend.run(new MeshRunnables.ConsumableRunnable<>(newMesh, consumer, value), threadContext, delay);
            } else {
                newMesh.completeExceptionally(throwable);
            }
        });

        return newMesh;
    }

    private Mesh<T> exceptionally(Function<Throwable, ? extends T> function, ThreadContext threadContext, long delay) {
        Mesh<T> newMesh = new Mesh<>(backend, new CompletableFuture<>());

        completableFuture.whenComplete((value, throwable) -> {
            if (throwable == null) {
                newMesh.complete(value);
            } else {
                backend.run(new MeshRunnables.FunctionalRunnable<>(newMesh, function, throwable), threadContext, delay);
            }
        });

        return newMesh;
    }

    private <R> Mesh<R> compose(Function<? super T, ? extends Mesh<R>> function, ThreadContext threadContext, long delay) {
        Mesh<R> newMesh = new Mesh<>(backend, new CompletableFuture<>());

        completableFuture.whenComplete((value, throwable) -> {
            if (throwable == null) {
                backend.run(new MeshRunnables.ComposableRunnable<>(newMesh, function, value, threadContext), threadContext, delay);
            } else {
                newMesh.completeExceptionally(throwable);
            }
        });

        return newMesh;
    }

    private Mesh<T> supplyCallable(Callable<T> callable, ThreadContext threadContext, long delay) {
        setHasBeenSupplied();
        backend.run(new MeshRunnables.CallableRunnable<>(this, callable), threadContext, delay);

        return this;
    }

    protected @NotNull AtomicBoolean getHasBeenSupplied() {
        return hasBeenSupplied;
    }

    public boolean isCancelled() {
        return isCancelled.get();
    }

    public boolean hasBeenSupplied() {
        return hasBeenSupplied.get();
    }
}
