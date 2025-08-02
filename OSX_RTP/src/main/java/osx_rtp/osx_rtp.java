package osx_rtp;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class osx_rtp {

    private final JavaPlugin plugin;
    private final Queue<Player> queue = new LinkedList<>();
    private final Random random = new Random();
    private final int radius = 5000;
    private boolean isProcessing = false;

    public osx_rtp(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void addToQueue(Player player) {
        if (queue.contains(player)) {
            player.sendMessage("§eYou're already in the RTP queue!");
            return;
        }

        queue.add(player);
        player.sendMessage("§aAdded to RTP queue. Please wait...");
        processQueue();
    }

    private void processQueue() {
        if (isProcessing || queue.isEmpty()) return;

        isProcessing = true;

        new BukkitRunnable() {
            @Override
            public void run() {
                Player player = queue.poll();
                if (player == null || !player.isOnline()) {
                    isProcessing = false;
                    return;
                }

                World world = player.getWorld();

                for (int i = 0; i < 10; i++) {
                    int x = random.nextInt(radius * 2) - radius;
                    int z = random.nextInt(radius * 2) - radius;
                    int y = world.getHighestBlockYAt(x, z) + 1;

                    Location loc = new Location(world, x, y, z);

                    if (world.getBlockAt(loc).isEmpty()) {
                        player.teleport(loc);
                        player.sendMessage("§aTeleported to random location: §e" + x + " " + y + " " + z);
                        break;
                    }
                }

                isProcessing = false;

                // Process the next player after a small delay (e.g., 2 seconds)
                if (!queue.isEmpty()) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            processQueue();
                        }
                    }.runTaskLater(plugin, 40L); // 40 ticks = 2 seconds
                }
            }
        }.runTask(plugin);
    }
}
