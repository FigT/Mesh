package us.figt.mesh.bukkit.example;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.figt.mesh.Mesh;
import us.figt.mesh.bukkit.BukkitMesh;
import us.figt.mesh.bukkit.BukkitTaskBackend;

import java.util.ArrayList;
import java.util.List;

public final class ExamplePlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // enable debug mode for the task backend for this plugin
        BukkitTaskBackend.of(this).setDebugMode(true);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("mesh-example")) {
            return true;
        }

        BukkitMesh.createCompletedMesh(this)
                .runSync(() -> sender.sendMessage("this is running on the main thread"));

        Mesh<List<Player>> test2 = BukkitMesh.createCompletedMesh(this)
                .runSync(() -> sender.sendMessage("this is running sync and will message all players in 10s"))
                .applyAsync(players -> new ArrayList<>(getServer().getOnlinePlayers()));

        test2.acceptSyncDelayed(players -> players.forEach(p -> p.sendMessage(p.getName())), 10L * 20L);

        Mesh<String> test1 = BukkitMesh.createSupplyingAsyncMesh(
                this,
                () -> doSomething(sender.getName()));

        test1.acceptSync(s -> sender.sendMessage("your data: " + s));

        BukkitMesh.createCompletedMesh(this)
                .runSync(this::printCurrentThread)
                .runAsync(this::printCurrentThread)
                .runAsyncDelayed(this::printCurrentThread, 150)
                .runSyncDelayed(this::printCurrentThread, 100);

        BukkitMesh.createSupplyingSyncMesh(this, () -> {
           throw new RuntimeException("this is a test exception");
        }).exceptionallySync(e -> {
            getLogger().severe("Caught an exception: " + e.getMessage());
            return null;
        });

        return true;
    }

    private String doSomething(String name) {
        String toReturn = name + "-ADDED";

        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            getLogger().severe("Thread was interrupted while sleeping: " + e.getMessage());
        }

        return toReturn;
    }

    private void printCurrentThread() {
        Thread current = Thread.currentThread();

        getLogger().info(String.format("Current thread: %s, ThreadContext: %s",
                current.getName(),
                BukkitTaskBackend.getThreadContext(current).name())
        );
    }
}