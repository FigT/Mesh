package us.figt.mesh.velocity;

import org.jetbrains.annotations.NotNull;
import us.figt.mesh.Mesh;
import us.figt.mesh.utils.TaskBackend;

import java.util.concurrent.CompletableFuture;

/**
 * An implementation of the {@link Mesh} class for Velocity plugins.
 * @param <T> the type of the value that this Mesh will supply
 *
 * @author FigT
 */
public class VelocityMesh<T> extends Mesh<T> {


    private VelocityMesh(@NotNull TaskBackend backend, @NotNull CompletableFuture<T> completableFuture) {
        super(backend, completableFuture);
    }

    private VelocityMesh(@NotNull TaskBackend backend, @NotNull CompletableFuture<T> completableFuture, boolean supplied, boolean cancelled) {
        super(backend, completableFuture, supplied, cancelled);
    }
}
