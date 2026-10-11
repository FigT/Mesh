package us.figt.mesh.sponge;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.plugin.PluginContainer;
import us.figt.mesh.Mesh;
import us.figt.mesh.utils.TaskBackend;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.Supplier;

import static us.figt.mesh.sponge.SpongeTaskBackend.of;

/**
 * An implementation of the {@link Mesh} class for Sponge plugins.
 * @param <T> the type of the value that this Mesh will supply
 *
 * @author FigT
 */
public class SpongeMesh<T> extends Mesh<T> {

    private SpongeMesh(@NotNull TaskBackend backend, @NotNull CompletableFuture<T> completableFuture) {
        super(backend, completableFuture);
    }

    private SpongeMesh(@NotNull TaskBackend backend, @NotNull CompletableFuture<T> completableFuture, boolean supplied, boolean cancelled) {
        super(backend, completableFuture, supplied, cancelled);
    }

    /**
     * Creates an already 'completed' Mesh instance.
     *
     * @param plugin the providing plugin for the {@link SpongeTaskBackend} for this Mesh
     * @param <T> the type of this Mesh
     * @return the completed Mesh instance
     */
    public static <T> @NotNull Mesh<T> createCompletedMesh(@NotNull PluginContainer plugin) {
        return Mesh.createCompletedMesh(of(plugin));
    }

    /**
     * Creates an already 'completed' Mesh instance with a supplied value.
     *
     * @param plugin the providing plugin for the {@link SpongeTaskBackend} for this Mesh
     * @param value the value to supply this completed Mesh with
     * @param <T>   the type of this Mesh
     * @return the completed Mesh instance
     */
    public static <T> @NotNull Mesh<T> createCompletedMesh(@NotNull PluginContainer plugin, T value) {
        return Mesh.createCompletedMesh(of(plugin), value);
    }

    /**
     * Creates a fresh Mesh instance which you can then supply, and complete later.
     *
     * @param plugin the providing plugin for the {@link SpongeTaskBackend} for this Mesh
     * @param <T> the type of this Mesh
     * @return the Mesh instance
     */
    public static <T> @NotNull Mesh<T> createMesh(@NotNull PluginContainer plugin) {
        return Mesh.createMesh(of(plugin));
    }

    /**
     * Creates a fresh Mesh instance and then supplies it (<strong>synchronously</strong>), which you can then complete later.
     *
     * @param plugin the providing plugin for the {@link SpongeTaskBackend} for this Mesh
     * @param supplier the value to supply
     * @param <T>      the type of this Mesh
     * @return the supplied Mesh instance
     */
    public static <T> @NotNull Mesh<T> createSupplyingSyncMesh(@NotNull PluginContainer plugin, @NotNull Supplier<T> supplier) {
        return Mesh.createSupplyingSyncMesh(of(plugin), supplier);
    }

    /**
     * Creates a fresh Mesh instance and then supplies it (<strong>synchronously</strong>) after a delay, which you can then complete later.
     *
     * @param plugin the providing plugin for the {@link SpongeTaskBackend} for this Mesh
     * @param supplier the value to supply
     * @param delay    the delay (<strong>in ticks</strong>) to wait to supply this Mesh
     * @param <T>      the type of this Mesh
     * @return the supplied Mesh instance
     */
    public static <T> @NotNull Mesh<T> createSupplyingSyncDelayedMesh(@NotNull PluginContainer plugin, @NotNull Supplier<T> supplier, long delay) {
        return Mesh.createSupplyingSyncDelayedMesh(of(plugin), supplier, delay);
    }

    /**
     * Creates a fresh Mesh instance and then supplies it (<strong>asynchronously</strong>), which you can then complete later.
     *
     * @param plugin the providing plugin for the {@link SpongeTaskBackend} for this Mesh
     * @param supplier the value to supply
     * @param <T>      the type of this Mesh
     * @return the supplied Mesh instance
     */
    public static <T> @NotNull Mesh<T> createSupplyingAsyncMesh(@NotNull PluginContainer plugin, @NotNull Supplier<T> supplier) {
        return Mesh.createSupplyingAsyncMesh(of(plugin), supplier);
    }

    /**
     * Creates a fresh Mesh instance and then supplies it (<strong>asynchronously</strong>) after a delay, which you can then complete later.
     *
     * @param plugin the providing plugin for the {@link SpongeTaskBackend} for this Mesh
     * @param supplier the value to supply
     * @param delay    the delay (<strong>in ticks</strong>) to wait to supply this Mesh
     * @param <T>      the type of this Mesh
     * @return the supplied Mesh instance
     */
    public static <T> @NotNull Mesh<T> createSupplyingAsyncDelayedMesh(@NotNull PluginContainer plugin, @NotNull Supplier<T> supplier, long delay) {
        return Mesh.createSupplyingAsyncDelayedMesh(of(plugin), supplier, delay);
    }


    /**
     * Creates a Mesh based on the given Future.
     *
     * @param plugin the providing plugin for the {@link SpongeTaskBackend} for this Mesh
     * @param future the Future to base this Mesh on
     * @param <R>    the type of the given Future and this Mesh
     * @return the new Mesh instance
     */
    public static <R> @NotNull Mesh<R> fromFuture(@NotNull PluginContainer plugin, @NotNull Future<R> future) {
        return Mesh.fromFuture(of(plugin), future);
    }

}
