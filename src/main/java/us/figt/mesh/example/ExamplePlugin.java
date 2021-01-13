package us.figt.mesh.example;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import us.figt.mesh.Mesh;
import us.figt.mesh.utils.ThreadContext;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author FigT
 */
public class ExamplePlugin extends JavaPlugin {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("meshexample") && sender instanceof Player) {
            Player player = (Player) sender;




            Mesh.createCompletedMesh().runSync(() -> player.sendMessage("This works SYNC"));

            Mesh<List<Player>> test2 = Mesh.createCompletedMesh()
                    .runSync(() -> player.sendMessage("this is running sync and will message all players in 10s"))
                    .applyAsync(players -> new ArrayList<>(getServer().getOnlinePlayers()));

            test2.acceptSyncDelayed(players -> players.forEach(p -> p.sendMessage(p.getName())), 360L);


            Mesh<String> test1 = Mesh.createMesh();

            test1.supplyAsync(() -> doSomething(player.getUniqueId()));
            test1.acceptSync(s -> player.sendMessage("Your data: " + s));

            Mesh.createCompletedMesh()
                    .runSync(this::printCurThread)
                    .runAsync(this::printCurThread)
                    .runAsyncDelayed(this::printCurThread, 150)
                    .runSyncDelayed(this::printCurThread, 100);
        }

        return true;
    }

    private String doSomething(UUID uuid) {
        String toReturn = uuid + "-ADDED";

        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return toReturn;
    }

    private void printCurThread() {
        Thread curThread = Thread.currentThread();

        System.out.println(String.format("%s-%s (%s) [%s]", curThread.getName(), curThread.getId(), curThread.getState().name(), ThreadContext.getThreadContext(curThread).name()));
    }
}
