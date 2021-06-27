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

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author FigT
 */
final class MeshRunnables {

    static final long NO_DElAY = 0L; // const

    private MeshRunnables() {
        throw new AssertionError("Container class cannot be instantiated"); // seal
    }

    static void run(Runnable runnable, ThreadContext context, long delay) {
        // no delay check
        if (delay > NO_DElAY) {
            runLater(runnable, context, delay);
            return;
        }

        switch (context) {
            case SYNC:
                if (ThreadContext.getThreadContext(Thread.currentThread()) == ThreadContext.SYNC) {
                    runnable.run(); // if current thread is main thread, just run the runnable
                } else {
                    PluginUtil.getPlugin().getServer().getScheduler().runTask(PluginUtil.getPlugin(), runnable); // else run the task sync via BukkitScheduler
                }
                break;

            case ASYNC:
                PluginUtil.getPlugin().getServer().getScheduler().runTaskAsynchronously(PluginUtil.getPlugin(), runnable);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + context);
        }
    }

    private static void runLater(Runnable runnable, ThreadContext context, long delay) {
        switch (context) {
            case SYNC:
                PluginUtil.getPlugin().getServer().getScheduler().runTaskLater(PluginUtil.getPlugin(), runnable, delay); // has delay
                break;
            case ASYNC:
                PluginUtil.getPlugin().getServer().getScheduler().runTaskLaterAsynchronously(PluginUtil.getPlugin(), runnable, delay);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + context);
        }
    }

    public static abstract class AbstractWrappedRunnable<T> implements Runnable {

        final Mesh<? super T> mesh;

        private AbstractWrappedRunnable(Mesh<? super T> mesh) {
            this.mesh = mesh;
        }

        public abstract T getCompleteValue() throws Exception;

        void onComplete() {

        }

        boolean shouldNormalComplete() {
            return true;
        }

        @Override
        public void run() {
            if (!mesh.isCancelled()) {
                try {
                    onComplete();
                    if (shouldNormalComplete()) mesh.complete(getCompleteValue());
                } catch (Throwable throwable) {
                    mesh.completeExceptionally(throwable);
                }
            }
        }

    }


    public static class WrappedRunnable<T> extends AbstractWrappedRunnable<T> {

        private final Runnable runnable;

        WrappedRunnable(Mesh<T> mesh, Runnable runnable) {
            super(mesh);
            this.runnable = runnable;
        }

        @Override
        void onComplete() {
            super.onComplete();
            this.runnable.run();
        }

        @Override
        public T getCompleteValue() {
            return null;
        }
    }


    public static class FunctionRunnable<R, T> extends AbstractWrappedRunnable<R> {

        private final Function<? super T, ? extends R> function;
        private final T value;

        FunctionRunnable(Mesh<R> mesh, Function<? super T, ? extends R> function, T value) {
            super(mesh);
            this.function = function;
            this.value = value;
        }

        @Override
        public R getCompleteValue() {
            return this.function.apply(value);
        }
    }

    public static class ConsumerRunnable<R, T> extends AbstractWrappedRunnable<R> {

        private final Consumer<T> consumer;
        private final T value;

        ConsumerRunnable(Mesh<R> mesh, Consumer<T> consumer, T value) {
            super(mesh);
            this.consumer = consumer;
            this.value = value;
        }

        @Override
        public R getCompleteValue() {
            return null;
        }

        @Override
        void onComplete() {
            super.onComplete();
            this.consumer.accept(value);
        }
    }

    public static class SupplierRunnable<T> extends AbstractWrappedRunnable<T> {

        private final Supplier<T> supplier;

        SupplierRunnable(Mesh<T> mesh, Supplier<T> supplier) {
            super(mesh);
            this.supplier = supplier;
        }


        @Override
        public T getCompleteValue() {
            return this.supplier.get();
        }
    }

    public static class CallableRunnable<T> extends AbstractWrappedRunnable<T> {

        private final Callable<T> callable;

        CallableRunnable(Mesh<T> mesh, Callable<T> callable) {
            super(mesh);
            this.callable = callable;
        }


        @Override
        public T getCompleteValue() throws Exception {
            return this.callable.call();
        }
    }

    public static class ComposeRunnable<R, T> extends AbstractWrappedRunnable<R> {

        private final Function<? super T, ? extends Mesh<R>> function;
        private final T value;
        private final ThreadContext threadContext;


        ComposeRunnable(Mesh<R> mesh, Function<? super T, ? extends Mesh<R>> function, T value, ThreadContext threadContext) {
            super(mesh);
            this.function = function;
            this.value = value;
            this.threadContext = threadContext;
        }

        @Override
        public R getCompleteValue() {
            return null;
        }

        @Override
        boolean shouldNormalComplete() {
            Mesh<R> applied = function.apply(value);

            if (applied != null) {
                applied.accept(mesh::complete, threadContext, NO_DElAY);
            }

            return applied == null;
        }
    }
}
