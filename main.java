package me.maro.silentwatch;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

public class Main extends JavaPlugin {

    private final Set<Player> vanished = new HashSet<>();

    @Override
    public void onEnable() {
        getLogger().info("SilentWatch Enabled");
    }

    @Override
    public void onDisable() {
        getLogger().info("SilentWatch Disabled");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player admin)) {
            sender.sendMessage("Players only.");
            return true;
        }

        if (!admin.hasPermission("silentwatch.use")) {
            admin.sendMessage("§cليس لديك صلاحية.");
            return true;
        }

        // /watch <player>
        if (cmd.getName().equalsIgnoreCase("watch")) {

            if (args.length != 1) {
                admin.sendMessage("§eالاستخدام: /watch <player>");
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                admin.sendMessage("§cاللاعب غير متصل.");
                return true;
            }

            // إخفاء الأدمن من الجميع
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.hidePlayer(this, admin);
            }

            admin.setGameMode(GameMode.SPECTATOR);
            admin.teleport(target);
            admin.setReducedDebugInfo(true); // إخفاء الإحداثيات

            vanished.add(admin);

            admin.sendMessage("§aتم دخول المراقبة الخفية");
            return true;
        }

        // /watchoff
        if (cmd.getName().equalsIgnoreCase("watchoff")) {

            if (!vanished.contains(admin)) {
                admin.sendMessage("§cأنت لست في وضع المراقبة.");
                return true;
            }

            // إظهار الأدمن
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.showPlayer(this, admin);
            }

            admin.setGameMode(GameMode.SURVIVAL);
            admin.setReducedDebugInfo(false);

            // Teleport للاسبون
            Location spawn = admin.getWorld().getSpawnLocation();
            admin.teleport(spawn);

            vanished.remove(admin);

            admin.sendMessage("§cتم الخروج من المراقبة والرجوع للاسبون");
            return true;
        }

        return true;
    }
}
