/*
 * MIT License
 *
 * Copyright (c) 2021 FigT
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package us.figt.mesh;

import us.figt.mesh.utils.PluginUtil;
import us.figt.mesh.utils.ThreadContext;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static us.figt.mesh.MeshRunnables.NO_DElAY;
import static us.figt.mesh.utils.ThreadContext.ASYNC;
import static us.figt.mesh.utils.ThreadContext.SYNC;

/**
 * @author FigT
 */
@SuppressWarnings("unused") // i don't want IntelliJ to yell at me
public class Mesh<T> {

    private static boolean debugMode = false;


    private final CompletableFuture<T> completableFuture; // the backing CompletableFuture
    private final AtomicBoolean hasBeenSupplied = new AtomicBoolean(false);
    private final AtomicBoolean isCancelled = new AtomicBoolean(false);

    private Mesh(CompletableFuture<T> completableFuture) {
        this.completableFuture = completableFuture;
    }

    private Mesh(CompletableFuture<T> completableFuture, boolean supplied, boolean cancelled) {
        this(completableFuture);

        this.hasBeenSupplied.set(supplied);
        this.isCancelled.set(cancelled);
    }

    // TODO: add more comments


    /**
     * Creates an already 'completed' Mesh instance.
     *
     * @param <T> the type of this Mesh
     * @return the completed Mesh instance
     */
    public static <T> Mesh<T> createCompletedMesh() {
        Mesh<T> mesh = new Mesh<>(CompletableFuture.completedFuture(null));
        mesh.hasBeenSupplied.set(true);

        return mesh;
    }

    /**
     * Creates an already 'completed' Mesh instance with a supplied value.
     *
     * @param value the value to supply this completed Mesh with
     * @param <T>   the type of this Mesh
     * @return the completed Mesh instance
     */
    public static <T> Mesh<T> createCompletedMesh(T value) {
        Mesh<T> mesh = new Mesh<>(CompletableFuture.completedFuture(value));
        mesh.hasBeenSupplied.set(true);

        return mesh;
    }

    /**
     * Creates a fresh Mesh instance which you can then supply, and complete later.
     *
     * @param <T> the type of this Mesh
     * @return the Mesh instance
     */
    public static <T> Mesh<T> createMesh() {
        return new Mesh<>(new CompletableFuture<>());
    }

    /**
     * Creates a fresh Mesh instance and then supplies it (<strong>synchronously</strong>), which you can then complete later.
     *
     * @param supplier the value to supply
     * @param <T>      the type of this Mesh
     * @return the supplied Mesh instance
     */
    public static <T> Mesh<T> createSupplyingSyncMesh(Supplier<T> supplier) {
        Mesh<T> mesh = createMesh();

        return mesh.supplySync(supplier);
    }

    /**
     * Creates a fresh Mesh instance and then supplies it (<strong>synchronously</strong>) after a delay, which you can then complete later.
     *
     * @param supplier the value to supply
     * @param delay    the delay (<strong>in ticks</strong>) to wait to supply this Mesh
     * @param <T>      the type of this Mesh
     * @return the supplied Mesh instance
     */
    public static <T> Mesh<T> createSupplyingSyncDelayedMesh(Supplier<T> supplier, long delay) {
        Mesh<T> mesh = createMesh();

        return mesh.supplySyncDelayed(supplier, delay);
    }

    /**
     * Creates a fresh Mesh instance and then supplies it (<strong>asynchronously</strong>), which you can then complete later.
     *
     * @param supplier the value to supply
     * @param <T>      the type of this Mesh
     * @return the supplied Mesh instance
     */
    public static <T> Mesh<T> createSupplyingAsyncMesh(Supplier<T> supplier) {
        Mesh<T> mesh = createMesh();

        return mesh.supplyAsync(supplier);
    }

    /**
     * Creates a fresh Mesh instance and then supplies it (<strong>asynchronously</strong>) after a delay, which you can then complete later.
     *
     * @param supplier the value to supply
     * @param delay    the delay (<strong>in ticks</strong>) to wait to supply this Mesh
     * @param <T>      the type of this Mesh
     * @return the supplied Mesh instance
     */
    public static <T> Mesh<T> createSupplyingAsyncDelayedMesh(Supplier<T> supplier, long delay) {
        Mesh<T> mesh = createMesh();

        return mesh.supplyAsyncDelayed(supplier, delay);
    }


    // ASYNC BELOW:

    /**
     * Supplies this Mesh with a value <strong>asynchronously</strong>.
     *
     * @param supplier the value to supply
     * @return the supplied Mesh instance
     */
    public Mesh<T> supplyAsync(Supplier<T> supplier) {
        return supply(supplier, ASYNC, NO_DElAY);
    }

    /**
     * Executes a runnable <strong>asynchronously</strong>.
     *
     * @param runnable the runnable to run
     * @return this Mesh instance
     */
    public Mesh<Void> runAsync(Runnable runnable) {
        return applyRun(runnable, ASYNC, NO_DElAY);
    }

    /**
     * Applies a function to this Mesh <strong>asynchronously</strong>.
     *
     * @param function the function to apply
     * @param <R>      the type of the function's result
     * @return this Mesh instance with the applied function
     */
    public <R> Mesh<R> applyAsync(Function<? super T, ? extends R> function) {
        return apply(function, ASYNC, NO_DElAY);
    }

    /**
     * Executes an action <strong>asynchronously</strong> with Void return type.
     *
     * @param consumer the action to run
     * @return this Mesh instance with Void return type
     */
    public Mesh<Void> acceptAsync(Consumer<T> consumer) {
        return accept(consumer, ASYNC, NO_DElAY);
    }

    /**
     * Executes an operation <strong>asynchronously</strong> if an exception occurred.
     *
     * @param function the function to execute
     * @return this Mesh instance with the applied function
     */
    public Mesh<T> exceptionallyAsync(Function<Throwable, ? extends T> function) {
        return exceptionally(function, ASYNC, NO_DElAY);
    }

    /**
     * Creates a new Mesh that, when this Mesh completes normally, is executed (<strong>asynchronously</strong>) with this Mesh's result as the argument to the supplied function.
     * <p>
     * (alt description: When this Mesh completes normally, the returned Mesh will execute (<strong>asynchronously</strong>) with this Mesh's result as the argument to the supplied function.)
     *
     * @param function the function to execute
     * @param <R>      the type of the returned Mesh's result
     * @return the new Mesh instance
     */
    public <R> Mesh<R> composeAsync(Function<? super T, ? extends Mesh<R>> function) {
        return compose(function, ASYNC, NO_DElAY);
    }

    // DELAYED ASYNC BELOW

    /**
     * Supplies this Mesh with a value <strong>asynchronously</strong>.
     *
     * @param supplier the value to supply
     * @param delay    the delay (<strong>in ticks</strong>) to wait to supply this Mesh
     * @return the supplied Mesh instance
     */
    public Mesh<T> supplyAsyncDelayed(Supplier<T> supplier, long delay) {
        return supply(supplier, ASYNC, delay);
    }

    /**
     * Executes a runnable <strong>asynchronously</strong>.
     *
     * @param runnable the runnable to run
     * @param delay    the delay (<strong>in ticks</strong>) to wait to execute the runnable
     * @return this Mesh instance
     */
    public Mesh<Void> runAsyncDelayed(Runnable runnable, long delay) {
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
    public <R> Mesh<R> applyAsyncDelayed(Function<? super T, ? extends R> function, long delay) {
        return apply(function, ASYNC, delay);
    }

    /**
     * Executes an action <strong>asynchronously</strong> with Void return type.
     *
     * @param consumer the action to run
     * @param delay    the delay (<strong>in ticks</strong>) to wait to run the action
     * @return this Mesh instance with Void return type
     */
    public Mesh<Void> acceptAsyncDelayed(Consumer<T> consumer, long delay) {
        return accept(consumer, ASYNC, delay);
    }

    /**
     * Executes an operation <strong>asynchronously</strong> if an exception occurred.
     *
     * @param function the function to execute
     * @param delay    the delay (<strong>in ticks</strong>) to wait to execute the function
     * @return this Mesh instance with the applied function
     */
    public Mesh<T> exceptionallyAsyncDelayed(Function<Throwable, ? extends T> function, long delay) {
        return exceptionally(function, ASYNC, delay);
    }

    /**
     * Creates a new Mesh that, when this Mesh completes normally, is executed (<strong>asynchronously</strong>) with this Mesh's result as the argument to the supplied function.
     * <p>
     * (alt description: When this Mesh completes normally, the returned Mesh will execute (<strong>asynchronously</strong>) with this Mesh's result as the argument to the supplied function.)
     *
     * @param function the function to execute
     * @param delay    the delay (<strong>in ticks</strong>) to wait to execute the function
     * @param <R>      the type of the returned Mesh's result
     * @return the new Mesh instance
     */
    public <R> Mesh<R> composeAsyncDelayed(Function<? super T, ? extends Mesh<R>> function, long delay) {
        return compose(function, ASYNC, delay);
    }


    // SYNC BELOW:

    /**
     * Supplies this Mesh with a value <strong>synchronously</strong>.
     *
     * @param supplier the value to supply
     * @return the supplied Mesh instance
     */
    public Mesh<T> supplySync(Supplier<T> supplier) {
        return supply(supplier, SYNC, NO_DElAY);
    }

    /**
     * Executes a runnable <strong>synchronously</strong>.
     *
     * @param runnable the runnable to run
     * @return this Mesh instance
     */
    public Mesh<Void> runSync(Runnable runnable) {
        return applyRun(runnable, SYNC, NO_DElAY);
    }

    /**
     * Applies a function to this Mesh <strong>synchronously</strong>.
     *
     * @param function the function to apply
     * @param <R>      the type of the function's result
     * @return this Mesh instance with the applied function
     */
    public <R> Mesh<R> applySync(Function<? super T, ? extends R> function) {
        return apply(function, SYNC, NO_DElAY);
    }

    /**
     * Executes an action <strong>synchronously</strong> with Void return type.
     *
     * @param consumer the action to run
     * @return this Mesh instance with Void return type
     */
    public Mesh<Void> acceptSync(Consumer<T> consumer) {
        return accept(consumer, SYNC, NO_DElAY);
    }

    /**
     * Executes an operation <strong>synchronously</strong> if an exception occurred.
     *
     * @param function the function to execute
     * @return this Mesh instance with the applied function
     */
    public Mesh<T> exceptionallySync(Function<Throwable, ? extends T> function) {
        return exceptionally(function, SYNC, NO_DElAY);
    }

    /**
     * Creates a new Mesh that, when this Mesh completes normally, is executed (<strong>synchronously</strong>) with this Mesh's result as the argument to the supplied function.
     * <p>
     * (alt description: When this Mesh completes normally, the returned Mesh will execute (<strong>synchronously</strong>) with this Mesh's result as the argument to the supplied function.)
     *
     * @param function the function to execute
     * @param <R>      the type of the returned Mesh's result
     * @return the new Mesh instance
     */
    public <R> Mesh<R> composeSync(Function<? super T, ? extends Mesh<R>> function) {
        return compose(function, SYNC, NO_DElAY);
    }

    // DELAYED SYNC BELOW

    /**
     * Supplies this Mesh with a value <strong>synchronously</strong>.
     *
     * @param supplier the value to supply
     * @param delay    the delay (<strong>in ticks</strong>) to wait to supply this Mesh
     * @return the supplied Mesh instance
     */
    public Mesh<T> supplySyncDelayed(Supplier<T> supplier, long delay) {
        return supply(supplier, SYNC, delay);
    }

    /**
     * Executes a runnable <strong>synchronously</strong>.
     *
     * @param runnable the runnable to run
     * @param delay    the delay (<strong>in ticks</strong>) to wait to execute the runnable
     * @return this Mesh instance
     */
    public Mesh<Void> runSyncDelayed(Runnable runnable, long delay) {
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
    public <R> Mesh<R> applySyncDelayed(Function<? super T, ? extends R> function, long delay) {
        return apply(function, SYNC, delay);
    }

    /**
     * Executes an action <strong>synchronously</strong> with Void return type.
     *
     * @param consumer the action to run
     * @param delay    the delay (<strong>in ticks</strong>) to wait to run the action
     * @return this Mesh instance with Void return type
     */
    public Mesh<Void> acceptSyncDelayed(Consumer<T> consumer, long delay) {
        return accept(consumer, SYNC, delay);
    }

    /**
     * Executes an operation <strong>synchronously</strong> if an exception occurred.
     *
     * @param function the function to execute
     * @param delay    the delay (<strong>in ticks</strong>) to wait to execute the function
     * @return this Mesh instance with the applied function
     */
    public Mesh<T> exceptionallySyncDelayed(Function<Throwable, ? extends T> function, long delay) {
        return exceptionally(function, SYNC, delay);
    }

    /**
     * Creates a new Mesh that, when this Mesh completes normally, is executed (<strong>synchronously</strong>) with this Mesh's result as the argument to the supplied function.
     * <p>
     * (alt description: When this Mesh completes normally, the returned Mesh will execute (<strong>synchronously</strong>) with this Mesh's result as the argument to the supplied function.)
     *
     * @param function the function to execute
     * @param delay    the delay (<strong>in ticks</strong>) to wait to execute the function
     * @param <R>      the type of the returned Mesh's result
     * @return the new Mesh instance
     */
    public <R> Mesh<R> composeSyncDelayed(Function<? super T, ? extends Mesh<R>> function, long delay) {
        return compose(function, SYNC, delay);
    }


    /**
     * If not already completed or cancelled, completes this Mesh with the given value.
     *
     * @param value the value to complete this Mesh with
     */
    public void complete(T value) {
        if (!isCancelled.get()) {
            completableFuture.complete(value);
        }
    }

    /**
     * If not already completed or cancelled, completes this Mesh with the given exception.
     *
     * @param throwable the exception
     */
    public void completeExceptionally(Throwable throwable) {
        if (!isCancelled.get()) {
            completableFuture.completeExceptionally(throwable);
        }

        if (Mesh.debugMode) PluginUtil.debugException(throwable); // debug exception
    }


    public CompletableFuture<T> toCompletableFuture() {
        return completableFuture.thenApply(Function.identity());
    }


    private void setHasBeenSupplied() {
        if (!hasBeenSupplied.compareAndSet(false, true)) {
            throw new AssertionError("This can only be supplied once, and this Mesh has already been supplied");
        }
    }


    private Mesh<T> supply(Supplier<T> supplier, ThreadContext threadContext, long delay) {
        setHasBeenSupplied();
        MeshRunnables.run(new MeshRunnables.SupplierRunnable<>(this, supplier), threadContext, delay);

        return this;
    }

    private <R> Mesh<R> apply(Function<? super T, ? extends R> function, ThreadContext threadContext, long delay) {
        Mesh<R> newMesh = new Mesh<>(new CompletableFuture<>());

        completableFuture.whenComplete((value, throwable) -> {
            if (throwable == null) {
                MeshRunnables.run(new MeshRunnables.FunctionRunnable<>(newMesh, function, value), threadContext, delay);
            } else {
                newMesh.completeExceptionally(throwable);
            }
        });

        return newMesh;
    }

    private <R> Mesh<R> applyRun(Runnable runnable, ThreadContext threadContext, long delay) {
        Mesh<R> newMesh = new Mesh<>(new CompletableFuture<>());

        completableFuture.whenComplete((value, throwable) -> {
            if (throwable == null) {
                MeshRunnables.run(new MeshRunnables.WrappedRunnable<>(newMesh, runnable), threadContext, delay);
            } else {
                newMesh.completeExceptionally(throwable);
            }
        });

        return newMesh;
    }

    <R> Mesh<R> accept(Consumer<T> consumer, ThreadContext threadContext, long delay) {
        Mesh<R> newMesh = new Mesh<>(new CompletableFuture<>());

        completableFuture.whenComplete((value, throwable) -> {
            if (throwable == null) {
                MeshRunnables.run(new MeshRunnables.ConsumerRunnable<>(newMesh, consumer, value), threadContext, delay);
            } else {
                newMesh.completeExceptionally(throwable);
            }
        });

        return newMesh;
    }

    private Mesh<T> exceptionally(Function<Throwable, ? extends T> function, ThreadContext threadContext, long delay) {
        Mesh<T> newMesh = new Mesh<>(new CompletableFuture<>());

        completableFuture.whenComplete((value, throwable) -> {
            if (throwable == null) {
                newMesh.complete(value);
            } else {
                MeshRunnables.run(new MeshRunnables.FunctionRunnable<>(newMesh, function, throwable), threadContext, delay);
            }
        });

        return newMesh;
    }

    private <R> Mesh<R> compose(Function<? super T, ? extends Mesh<R>> function, ThreadContext threadContext, long delay) {
        Mesh<R> newMesh = new Mesh<>(new CompletableFuture<>());

        completableFuture.whenComplete((value, throwable) -> {
            if (throwable == null) {
                MeshRunnables.run(new MeshRunnables.ComposeRunnable<>(newMesh, function, value, threadContext), threadContext, delay);
            } else {
                newMesh.completeExceptionally(throwable);
            }
        });

        return newMesh;
    }

    public AtomicBoolean getIsCancelled() {
        return isCancelled;
    }

    public AtomicBoolean getHasBeenSupplied() {
        return hasBeenSupplied;
    }

    CompletableFuture<T> getCompletableFuture() {
        return completableFuture;
    }

    /**
     * Sets Mesh's debug mode to the specified value.
     *
     * @param debugMode if debug mode is enabled or not
     */
    public static void setDebugMode(boolean debugMode) {
        Mesh.debugMode = debugMode;
    }
}
