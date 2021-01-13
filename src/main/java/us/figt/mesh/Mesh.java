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
@SuppressWarnings("unused")
public class Mesh<T> {

    private static boolean debugMode = false;
    private static final Consumer<Throwable> DEBUG_EXCEPTION_CONSUMER = throwable -> {
        PluginUtil.getPlugin().getLogger().warning("DEBUG_EXCEPTION_CONSUMER has caught a " + throwable.getClass().getSimpleName());
        throwable.printStackTrace();
    };


    private final CompletableFuture<T> completableFuture; // the backing CompletableFuture

    private final AtomicBoolean hasBeenSupplied = new AtomicBoolean(false);
    private final AtomicBoolean isCancelled = new AtomicBoolean(false);

    private Mesh(CompletableFuture<T> completableFuture) {
        this.completableFuture = completableFuture;
    }


    // TODO: add TONS more comments and docs


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
    public static <T> Mesh createCompletedMesh(T value) {
        Mesh mesh = new Mesh<>(CompletableFuture.completedFuture(value));
        mesh.hasBeenSupplied.set(true);

        return mesh;
    }

    /**
     * Creates a fresh Mesh instance that you can then supply, & complete later.
     *
     * @param <T> the type of this Mesh
     * @return the Mesh instance
     */
    public static <T> Mesh<T> createMesh() {
        return new Mesh<>(new CompletableFuture<>());
    }


    // ASYNC BELOW:

    public Mesh<T> supplyAsync(Supplier<T> supplier) {
        return supply(supplier, ASYNC, NO_DElAY);
    }

    public Mesh<Void> runAsync(Runnable runnable) {
        return applyRun(runnable, ASYNC, NO_DElAY);
    }

    public <R> Mesh<R> applyAsync(Function<? super T, ? extends R> function) {
        return apply(function, ASYNC, NO_DElAY);
    }

    public Mesh<Void> acceptAsync(Consumer<T> consumer) {
        return accept(consumer, ASYNC, NO_DElAY);
    }

    public Mesh<T> exceptionallyAsync(Function<Throwable, ? extends T> function) {
        return exceptionally(function, ASYNC, NO_DElAY);
    }

    public <R> Mesh<R> composeAsync(Function<? super T, ? extends Mesh<R>> function) {
        return compose(function, ASYNC, NO_DElAY);
    }

    // DELAYED ASYNC BELOW

    public Mesh<T> supplyAsyncDelayed(Supplier<T> supplier, long delay) {
        return supply(supplier, ASYNC, delay);
    }

    public Mesh<Void> runAsyncDelayed(Runnable runnable, long delay) {
        return applyRun(runnable, ASYNC, delay);
    }

    public <R> Mesh<R> applyAsyncDelayed(Function<? super T, ? extends R> function, long delay) {
        return apply(function, ASYNC, delay);
    }

    public Mesh<Void> acceptAsyncDelayed(Consumer<T> consumer, long delay) {
        return accept(consumer, ASYNC, delay);
    }

    public Mesh<T> exceptionallyAsyncDelayed(Function<Throwable, ? extends T> function, long delay) {
        return exceptionally(function, ASYNC, delay);
    }

    public <R> Mesh<R> composeAsyncDelayed(Function<? super T, ? extends Mesh<R>> function, long delay) {
        return compose(function, ASYNC, delay);
    }


    // SYNC BELOW:

    public Mesh<T> supplySync(Supplier<T> supplier) {
        return supply(supplier, SYNC, NO_DElAY);
    }

    public Mesh<Void> runSync(Runnable runnable) {
        return applyRun(runnable, SYNC, NO_DElAY);
    }

    public <R> Mesh<R> applySync(Function<? super T, ? extends R> function) {
        return apply(function, SYNC, NO_DElAY);
    }

    public Mesh<Void> acceptSync(Consumer<T> consumer) {
        return accept(consumer, SYNC, NO_DElAY);
    }

    public Mesh<T> exceptionallySync(Function<Throwable, ? extends T> function) {
        return exceptionally(function, SYNC, NO_DElAY);
    }

    public <R> Mesh<R> composeSync(Function<? super T, ? extends Mesh<R>> function) {
        return compose(function, SYNC, NO_DElAY);
    }

    // DELAYED SYNC BELOW

    public Mesh<T> supplySyncDelayed(Supplier<T> supplier, long delay) {
        return supply(supplier, SYNC, delay);
    }

    public Mesh<Void> runSyncDelayed(Runnable runnable, long delay) {
        return applyRun(runnable, SYNC, delay);
    }

    public <R> Mesh<R> applySyncDelayed(Function<? super T, ? extends R> function, long delay) {
        return apply(function, SYNC, delay);
    }

    public Mesh<Void> acceptSyncDelayed(Consumer<T> consumer, long delay) {
        return accept(consumer, SYNC, delay);
    }

    public Mesh<T> exceptionallySyncDelayed(Function<Throwable, ? extends T> function, long delay) {
        return exceptionally(function, SYNC, delay);
    }

    public <R> Mesh<R> composeSyncDelayed(Function<? super T, ? extends Mesh<R>> function, long delay) {
        return compose(function, SYNC, delay);
    }


    public void complete(T value) {
        if (!isCancelled.get()) {
            completableFuture.complete(value);
        }
    }

    public void completeExceptionally(Throwable throwable) {
        if (!isCancelled.get()) {
            completableFuture.completeExceptionally(throwable);
        }

        if (Mesh.debugMode) DEBUG_EXCEPTION_CONSUMER.accept(throwable);
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
     * Enables Mesh's debug mode.
     *
     * (ONLY USE THIS IF YOU KNOW WHAT YOU ARE DOING)
     */
    public static void enableDebugMode() {
        Mesh.debugMode = true;
    }
}
