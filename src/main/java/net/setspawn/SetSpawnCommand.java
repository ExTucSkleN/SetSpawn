package net.setspawn;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SetSpawnCommand implements CommandExecutor {

    private final SetSpawn plugin;
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    public SetSpawnCommand(SetSpawn plugin) {
        this.plugin = plugin;
    }

    // Добавлены @NotNull перед параметрами
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getMessage("player-only"));
            return true;
        }

        // --- КОМАНДА /setspawn ---
        if (command.getName().equalsIgnoreCase("setspawn")) {
            if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                if (!player.hasPermission("setspawn.reload")) {
                    player.sendMessage(plugin.getMessage("no-permission"));
                    return true;
                }
                plugin.reloadConfig();
                player.sendMessage(plugin.getMessage("reload-success"));
                return true;
            }

            if (!player.hasPermission("setspawn.set")) {
                player.sendMessage(plugin.getMessage("no-permission"));
                return true;
            }

            Location loc = player.getLocation();
            player.getWorld().setSpawnLocation(loc);
            player.sendMessage(plugin.getMessage("setspawn-success"));
            plugin.getLogger().info(player.getName() + " установил спавн на " + loc);
            return true;
        }

        // --- КОМАНДА /spawn ---
        if (command.getName().equalsIgnoreCase("spawn")) {
            if (!player.hasPermission("setspawn.use")) {
                player.sendMessage(plugin.getMessage("no-permission"));
                return true;
            }

            long cooldownSeconds = plugin.getConfig().getLong("settings.spawn-cooldown", 10);
            if (cooldowns.containsKey(player.getUniqueId())) {
                long timeLeft = (cooldowns.get(player.getUniqueId()) / 1000) + cooldownSeconds - (System.currentTimeMillis() / 1000);
                if (timeLeft > 0) {
                    player.sendMessage(plugin.getMessage("spawn-cooldown", "%seconds%", String.valueOf(timeLeft)));
                    return true;
                }
            }

            int warmupSeconds = plugin.getConfig().getInt("settings.spawn-warmup", 3);
            boolean cancelOnMove = plugin.getConfig().getBoolean("settings.cancel-on-move", true);

            if (warmupSeconds > 0 && cancelOnMove) {
                PlayerMoveListener.frozenPlayers.put(player.getUniqueId(), player.getLocation());

                player.sendMessage(plugin.getMessage("spawn-warmup", "%seconds%", String.valueOf(warmupSeconds)));

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (PlayerMoveListener.frozenPlayers.containsKey(player.getUniqueId())) {
                            PlayerMoveListener.frozenPlayers.remove(player.getUniqueId());
                            teleportToSpawn(player, cooldownSeconds);
                        }
                    }
                }.runTaskLater(plugin, warmupSeconds * 20L);

            } else {
                teleportToSpawn(player, cooldownSeconds);
            }
            return true;
        }

        return false;
    }

    private void teleportToSpawn(Player player, long cooldownSeconds) {
        Location spawn = player.getWorld().getSpawnLocation();
        spawn.add(0.5, 0, 0.5);

        player.teleport(spawn);
        player.sendMessage(plugin.getMessage("spawn-success"));
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);

        if (cooldownSeconds > 0) {
            cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
        }
    }
}