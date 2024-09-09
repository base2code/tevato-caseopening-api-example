package de.base2code.example;

import de.base2code.caseopening.CaseAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class ApiExample extends JavaPlugin {
    private CaseAPI caseApi;

    @Override
    public void onEnable() {
        // Plugin startup logic
        RegisteredServiceProvider<CaseAPI> provider = getServer().getServicesManager().getRegistration(CaseAPI.class);
        if (provider == null) {
            getLogger().severe("CaseAPI not found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        getLogger().info("CaseAPI class loaded by: " + provider.getProvider().getClass().getClassLoader());
        this.caseApi = provider.getProvider();

        this.getCommand("casegive").setExecutor(this);

    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            caseApi.addJewelry((Player) sender, 1);
            sender.sendMessage("You received 1 jewelry!");
            // The jewelry count is updated asynchronously, so we need to wait a bit before checking it
            Bukkit.getScheduler().runTaskLater(this, () -> {
                caseApi.getJewelry((Player) sender).thenAccept(jewelry -> sender.sendMessage("You have " + jewelry + " jewelry!"));
            }, 20L); // 1 second later
        } else {
            sender.sendMessage("You must be a player to execute this command!");
        }
        return true;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
