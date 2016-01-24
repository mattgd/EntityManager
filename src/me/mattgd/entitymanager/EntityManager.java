package me.mattgd.entitymanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class EntityManager extends JavaPlugin implements CommandExecutor {

	private static LivingEntity selectedEntity = null;
	
	@Override
	public void onEnable() {
		getCommand("entitymanager").setExecutor(this); // Setup command
	}
	
	@Override
	public void onDisable() {}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("entitymanager") || cmd.getName().equalsIgnoreCase("em")) {
			if (sender.hasPermission("entitymanager.use")) {
				if (args.length == 1) {
					if (args[0].equalsIgnoreCase("findduplicate") || args[0].equalsIgnoreCase("fd")) {
						World world = getWorld(sender);
						
						if (world == null) {
							message(sender, "&cCould not get world.");
							return true;
						}
						
						List<UUID> entityUUIDs = new ArrayList<UUID>();
						Map<UUID, Location> duplicateEntities = new HashMap<UUID, Location>();
						for (LivingEntity le : world.getLivingEntities()) {
							if (entityUUIDs.contains(le.getUniqueId())) {
								duplicateEntities.put(le.getUniqueId(), le.getLocation());
							}
							entityUUIDs.add(le.getUniqueId());
						}
						
						if (duplicateEntities.isEmpty()) {
							sender.sendMessage(ChatColor.RED + "Could not find any duplicate entities.");
						} else {
							String message = "";
							for (Entry<UUID, Location> e : duplicateEntities.entrySet()) {
								message += "Entity " + e.getKey() + " at x:" + (int) e.getValue().getX() + ", y:" + (int) e.getValue().getY() + ", z:" + (int) e.getValue().getZ() + "\n";
							}
							
							sender.sendMessage(ChatColor.GREEN + "Duplicate entities:\n" + message);
						}
						
						return true;
					}
				}
				
				if (args.length >= 2) {
					if (args[0].equalsIgnoreCase("get")) {
						UUID uuid = UUID.fromString(args[1]);
						if (uuid == null) {
							sender.sendMessage(ChatColor.RED + "Invalid UUID.");
						} else {
							World world = getWorld(sender);
							
							if (world == null) {
								message(sender, "&cCould not get world.");
								return true;
							}
							
							for (LivingEntity le : world.getLivingEntities()) {
								if (le.getUniqueId().equals(uuid)) {
									Location loc = le.getLocation();
									selectedEntity = le;
									message(sender, "&aEntity location: x:" + (int) loc.getX() + ", y:" + loc.getY() + ", z:" + loc.getZ());
									return true;
								}
							}
							
							message(sender, "&cNo valid entity found.");
						}
					} else if (args[0].equalsIgnoreCase("highlight") || args[0].equalsIgnoreCase("h")) {
						if (selectedEntity == null) {
							message(sender, "&cNo entity selected.");
						} else {
							Firework firework = selectedEntity.getWorld().spawn(selectedEntity.getLocation(), Firework.class);
							FireworkMeta data = (FireworkMeta) firework.getFireworkMeta();
					        data.addEffects(FireworkEffect.builder().withColor(Color.LIME).with(Type.BALL).build());
					        data.setPower(0);
					        firework.setFireworkMeta(data);
					        message(sender, "&aEntity highlighted.");
						}
					}
				} else {
					message(sender, "&cInvalid subcommand.");
				}
			
			} else {
				sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
			}
			
			return true;
		}
		
		return false; 
	}
	
	private void message(CommandSender sender, String msg) {
		msg = ChatColor.translateAlternateColorCodes('&', msg);
		sender.sendMessage(msg);
	}
	
	private World getWorld(CommandSender sender) {
		World world = null;
		if (sender instanceof Player) {
			world = ((Player) sender).getWorld();
		} else {
			for (World w : Bukkit.getWorlds()) {
				if (w.getWorldType().equals(WorldType.NORMAL)) {
					world = w;
					break;
				}
			}
		}
		
		return world;
	}
}
