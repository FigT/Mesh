package us.figt.mesh;

import org.jetbrains.annotations.ApiStatus;
import us.figt.mesh.utils.TaskBackend;
import us.figt.mesh.utils.ThreadContext;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author FigT
 */
@ApiStatus.Internal
final class MeshRunnables {

    private MeshRunnables() {
        throw new AssertionError("Container class cannot be instantiated"); // seal
    }

    public static abstract class WrappedRunnable<T> implements Runnable {

        final Mesh<? super T> mesh;

        private WrappedRunnable(Mesh<? super T> mesh) {
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


    public static class AppliableRunnable<T> extends WrappedRunnable<T> {

        private final Runnable runnable;

        AppliableRunnable(Mesh<T> mesh, Runnable runnable) {
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


    public static class FunctionalRunnable<R, T> extends WrappedRunnable<R> {

        private final Function<? super T, ? extends R> function;
        private final T value;

        FunctionalRunnable(Mesh<R> mesh, Function<? super T, ? extends R> function, T value) {
            super(mesh);
            this.function = function;
            this.value = value;
        }

        @Override
        public R getCompleteValue() {
            return this.function.apply(value);
        }
    }

    public static class ConsumableRunnable<R, T> extends WrappedRunnable<R> {

        private final Consumer<T> consumer;
        private final T value;

        ConsumableRunnable(Mesh<R> mesh, Consumer<T> consumer, T value) {
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

    public static class SuppliableRunnable<T> extends WrappedRunnable<T> {

        private final Supplier<T> supplier;

        SuppliableRunnable(Mesh<T> mesh, Supplier<T> supplier) {
            super(mesh);
            this.supplier = supplier;
        }


        @Override
        public T getCompleteValue() {
            return this.supplier.get();
        }
    }

    public static class CallableRunnable<T> extends WrappedRunnable<T> {

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

    public static class ComposableRunnable<R, T> extends WrappedRunnable<R> {

        private final Function<? super T, ? extends Mesh<R>> function;
        private final T value;
        private final ThreadContext threadContext;


        ComposableRunnable(Mesh<R> mesh, Function<? super T, ? extends Mesh<R>> function, T value, ThreadContext threadContext) {
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
                applied.accept(mesh::complete, threadContext, TaskBackend.NO_DELAY);
            }

            return applied == null;
        }
    }
}
