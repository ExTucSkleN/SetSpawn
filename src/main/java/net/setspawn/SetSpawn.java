package net.setspawn;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class SetSpawn extends JavaPlugin {

    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacyAmpersand();

    @Override
    public void onEnable() {
        saveDefaultConfig();

        SetSpawnCommand commandHandler = new SetSpawnCommand(this);

        PluginCommand setSpawnCmd = getCommand("setspawn");
        if (setSpawnCmd != null) {
            setSpawnCmd.setExecutor(commandHandler);
        } else {
            getLogger().severe("Command /setspawn not found in plugin.yml!");
        }

        PluginCommand spawnCmd = getCommand("spawn");
        if (spawnCmd != null) {
            spawnCmd.setExecutor(commandHandler);
        } else {
            getLogger().severe("Command /spawn not found in plugin.yml!");
        }

        getServer().getPluginManager().registerEvents(new PlayerMoveListener(this), this);

        getLogger().info("SetSpawn plugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("SetSpawn plugin disabled.");
    }

    public Component getMessage(String path, String... placeholders) {
        boolean usePrefix = getConfig().getBoolean("settings.use-prefix", true);

        String prefix = "";
        if (usePrefix) {
            prefix = getConfig().getString("messages.prefix", "");
        }

        String message = getConfig().getString("messages." + path, "&cMessage not found: " + path);

        for (int i = 0; i < placeholders.length; i += 2) {
            if (i + 1 < placeholders.length) {
                message = message.replace(placeholders[i], placeholders[i + 1]);
            }
        }

        return LEGACY.deserialize(prefix + message);
    }
}
