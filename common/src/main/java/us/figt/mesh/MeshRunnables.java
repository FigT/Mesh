package us.figt.mesh;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import us.figt.mesh.utils.TaskBackend;
import us.figt.mesh.utils.ThreadContext;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;

/**
 * @author FigT
 */
@ApiStatus.Internal
final class MeshRunnables {

    static final long NO_DELAY = 0L;

    private MeshRunnables() {
        throw new AssertionError("Container class cannot be instantiated"); // seal
    }


    static void run(@NotNull TaskBackend backend, @NotNull Runnable runnable, @NotNull ThreadContext context, long delay) {
        if (delay <= NO_DELAY) {
            if (context == ThreadContext.ASYNC) {
                backend.runAsync(runnable);
            } else if (context == ThreadContext.SYNC) {
                backend.runSync(runnable);
            }
            return;
        }


        if (context == ThreadContext.ASYNC) {
            backend.runAsyncLater(runnable, delay);
        } else if (context == ThreadContext.SYNC) {
            backend.runSyncLater(runnable, delay);
        }
    }

    static void debugException(@NotNull TaskBackend backend, @NotNull Throwable throwable) {
        backend.log(
                Level.WARNING,
                "Mesh-" + backend.getClass().getSimpleName() + "-Debug - Caught a " + throwable.getClass().getSimpleName()
        );
        // noinspection CallToPrintStackTrace
        throwable.printStackTrace();
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

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            WrappedRunnable<?> that = (WrappedRunnable<?>) o;

            return Objects.equals(mesh, that.mesh);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(mesh);
        }

        @Override
        public String toString() {
            return "WrappedRunnable{" +
                    "mesh=" + mesh +
                    '}';
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

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;

            AppliableRunnable<?> that = (AppliableRunnable<?>) o;
            return Objects.equals(runnable, that.runnable);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), runnable);
        }

        @Override
        public String toString() {
            return "AppliableRunnable{" +
                    "runnable=" + runnable +
                    ", mesh=" + mesh +
                    '}';
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

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;

            FunctionalRunnable<?, ?> that = (FunctionalRunnable<?, ?>) o;
            return Objects.equals(function, that.function)
                    && Objects.equals(value, that.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), function, value);
        }

        @Override
        public String toString() {
            return "FunctionalRunnable{" +
                    "function=" + function +
                    ", value=" + value +
                    ", mesh=" + mesh +
                    '}';
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

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;

            ConsumableRunnable<?, ?> that = (ConsumableRunnable<?, ?>) o;
            return Objects.equals(consumer, that.consumer)
                    && Objects.equals(value, that.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), consumer, value);
        }

        @Override
        public String toString() {
            return "ConsumableRunnable{" +
                    "consumer=" + consumer +
                    ", value=" + value +
                    ", mesh=" + mesh +
                    '}';
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

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;

            SuppliableRunnable<?> that = (SuppliableRunnable<?>) o;
            return Objects.equals(supplier, that.supplier);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), supplier);
        }

        @Override
        public String toString() {
            return "SuppliableRunnable{" +
                    "supplier=" + supplier +
                    ", mesh=" + mesh +
                    '}';
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

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;

            CallableRunnable<?> that = (CallableRunnable<?>) o;
            return Objects.equals(callable, that.callable);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), callable);
        }

        @Override
        public String toString() {
            return "CallableRunnable{" +
                    "callable=" + callable +
                    ", mesh=" + mesh +
                    '}';
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
                applied.accept(mesh::complete, threadContext, NO_DELAY);
            }

            return applied == null;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;

            ComposableRunnable<?, ?> that = (ComposableRunnable<?, ?>) o;
            return Objects.equals(function, that.function)
                    && Objects.equals(value, that.value)
                    && threadContext == that.threadContext;
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), function, value, threadContext);
        }

        @Override
        public String toString() {
            return "ComposableRunnable{" +
                    "function=" + function +
                    ", value=" + value +
                    ", threadContext=" + threadContext +
                    ", mesh=" + mesh +
                    '}';
        }
    }
}
