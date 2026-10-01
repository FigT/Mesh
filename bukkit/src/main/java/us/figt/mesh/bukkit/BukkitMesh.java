package us.figt.mesh.bukkit;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import us.figt.mesh.Mesh;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * An implementation of the {@link Mesh} class for Bukkit plugins.
 * @param <T> the type of the value that this Mesh will supply
 *
 * @author FigT
 */
public class BukkitMesh<T> extends Mesh<T> {

    private BukkitMesh(@NotNull Plugin plugin, @NotNull CompletableFuture<T> completableFuture) {
        super(BukkitTaskBackend.of(plugin), completableFuture);
    }

    private BukkitMesh(@NotNull Plugin plugin, @NotNull CompletableFuture<T> completableFuture, boolean supplied, boolean cancelled) {
        super(BukkitTaskBackend.of(plugin), completableFuture, supplied, cancelled);
    }

    /**
     * Creates an already 'completed' Mesh instance.
     *
     * @param plugin the providing plugin for the {@link BukkitTaskBackend} for this Mesh
     * @param <T> the type of this Mesh
     * @return the completed Mesh instance
     */
    public static <T> @NotNull Mesh<T> createCompletedMesh(@NotNull Plugin plugin) {
        BukkitMesh<T> mesh = new BukkitMesh<>(plugin, CompletableFuture.completedFuture(null));
        mesh.getHasBeenSupplied().set(true);

        return mesh;
    }

    /**
     * Creates an already 'completed' Mesh instance with a supplied value.
     *
     * @param plugin the providing plugin for the {@link BukkitTaskBackend} for this Mesh
     * @param value the value to supply this completed Mesh with
     * @param <T>   the type of this Mesh
     * @return the completed Mesh instance
     */
    public static <T> @NotNull Mesh<T> createCompletedMesh(@NotNull Plugin plugin, T value) {
        BukkitMesh<T> mesh = new BukkitMesh<>(plugin, CompletableFuture.completedFuture(value));
        mesh.getHasBeenSupplied().set(true);

        return mesh;
    }

    /**
     * Creates a fresh Mesh instance which you can then supply, and complete later.
     *
     * @param plugin the providing plugin for the {@link BukkitTaskBackend} for this Mesh
     * @param <T> the type of this Mesh
     * @return the Mesh instance
     */
    public static <T> @NotNull Mesh<T> createMesh(@NotNull Plugin plugin) {
        return new BukkitMesh<>(plugin, new CompletableFuture<>());
    }

    /**
     * Creates a fresh Mesh instance and then supplies it (<strong>synchronously</strong>), which you can then complete later.
     *
     * @param plugin the providing plugin for the {@link BukkitTaskBackend} for this Mesh
     * @param supplier the value to supply
     * @param <T>      the type of this Mesh
     * @return the supplied Mesh instance
     */
    public static <T> @NotNull Mesh<T> createSupplyingSyncMesh(@NotNull Plugin plugin, @NotNull Supplier<T> supplier) {
        Mesh<T> mesh = createMesh(plugin);

        return mesh.supplySync(supplier);
    }

    /**
     * Creates a fresh Mesh instance and then supplies it (<strong>synchronously</strong>) after a delay, which you can then complete later.
     *
     * @param plugin the providing plugin for the {@link BukkitTaskBackend} for this Mesh
     * @param supplier the value to supply
     * @param delay    the delay (<strong>in ticks</strong>) to wait to supply this Mesh
     * @param <T>      the type of this Mesh
     * @return the supplied Mesh instance
     */
    public static <T> @NotNull Mesh<T> createSupplyingSyncDelayedMesh(@NotNull Plugin plugin, @NotNull Supplier<T> supplier, long delay) {
        Mesh<T> mesh = createMesh(plugin);

        return mesh.supplySyncDelayed(supplier, delay);
    }

    /**
     * Creates a fresh Mesh instance and then supplies it (<strong>asynchronously</strong>), which you can then complete later.
     *
     * @param plugin the providing plugin for the {@link BukkitTaskBackend} for this Mesh
     * @param supplier the value to supply
     * @param <T>      the type of this Mesh
     * @return the supplied Mesh instance
     */
    public static <T> @NotNull Mesh<T> createSupplyingAsyncMesh(@NotNull Plugin plugin, @NotNull Supplier<T> supplier) {
        Mesh<T> mesh = createMesh(plugin);

        return mesh.supplyAsync(supplier);
    }

    /**
     * Creates a fresh Mesh instance and then supplies it (<strong>asynchronously</strong>) after a delay, which you can then complete later.
     *
     * @param plugin the providing plugin for the {@link BukkitTaskBackend} for this Mesh
     * @param supplier the value to supply
     * @param delay    the delay (<strong>in ticks</strong>) to wait to supply this Mesh
     * @param <T>      the type of this Mesh
     * @return the supplied Mesh instance
     */
    public static <T> @NotNull Mesh<T> createSupplyingAsyncDelayedMesh(@NotNull Plugin plugin, @NotNull Supplier<T> supplier, long delay) {
        Mesh<T> mesh = createMesh(plugin);

        return mesh.supplyAsyncDelayed(supplier, delay);
    }


    /**
     * Creates a Mesh based on the given Future.
     *
     * @param plugin the providing plugin for the {@link BukkitTaskBackend} for this Mesh
     * @param future the Future to base this Mesh on
     * @param <R>    the type of the given Future and this Mesh
     * @return the new Mesh instance
     */
    @SuppressWarnings("unchecked")
    public static <R> @NotNull Mesh<R> fromFuture(@NotNull Plugin plugin, @NotNull Future<R> future) {
        // TODO: add comments to this method

        if (future instanceof CompletableFuture<?>) {
            return new BukkitMesh<>(plugin, ((CompletableFuture<R>) future).thenApply(Function.identity()), true, future.isCancelled());
        }

        if (future instanceof CompletionStage<?>) {
            CompletionStage<R> stage = (CompletionStage<R>) future;

            return new BukkitMesh<>(plugin, stage.toCompletableFuture().thenApply(Function.identity()));
        }

        if (future.isDone()) {
            try {
                // if the future is done, just create a completed mesh based on the future's value
                return createCompletedMesh(plugin, future.get());
            } catch (ExecutionException e) {
                // if the computation threw an exception, create a new CompletableFuture
                CompletableFuture<R> newFuture = new CompletableFuture<>();

                // complete exceptionally using the ExecutionException thrown
                newFuture.completeExceptionally(e);


                // return a new Mesh based on that future
                return new BukkitMesh<>(plugin, newFuture, true, false);
            } catch (InterruptedException e) {
                // uh-oh
                BukkitTaskBackend.of(plugin).debugException(e);
                throw new RuntimeException(e);
            }
        }

        Mesh<R> newMesh = createMesh(plugin);
        return newMesh.supplyCallableAsync(future::get);
    }
}
