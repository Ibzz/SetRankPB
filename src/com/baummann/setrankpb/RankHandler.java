package com.baummann.setrankpb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.player.SpoutPlayer;

public class RankHandler {
	public File permissionsFile = new File("plugins/PermissionsBukkit/config.yml");
	public static SetRankPB plugin;

	public RankHandler(SetRankPB instance) {
		plugin = instance;
	}

	public FileConfiguration config() {
		if (!permissionsFile.exists()) {
			try {
				permissionsFile.getParentFile().createNewFile();
				permissionsFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		FileConfiguration config = new YamlConfiguration();
		try {
			config.load(permissionsFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
		return config;
	}

	public void setRank(Player player, String rank) {
		FileConfiguration config = config();
		plugin.loadConfig();
		List<String> group = new ArrayList<String>();
		group.add(rank);
		config.set("users." + player.getName() + ".groups", group);
		try {
			config.save(permissionsFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		plugin.reloadPermissions();
	}

	public void setOfflineRank(String player, String rank) {
		FileConfiguration config = config();
		plugin.loadConfig();
		List<String> group = new ArrayList<String>();
		group.add(rank);
		config.set("users." + player + ".groups", group);
		try {
			config.save(permissionsFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		plugin.reloadPermissions();
	}

	@SuppressWarnings("unchecked")
	public String getRank(Player player) {
		FileConfiguration config = config();
		plugin.loadConfig();
		ArrayList<String> groups = (ArrayList<String>) config.get("users." + player.getName() + ".groups");
		try {
			config.save(permissionsFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String g = "";
		for (String s : groups) {
			g = s;
		}
		return g;
	}

	public void triggerAchievement(Player player, String rank) {
		try {
			if (player instanceof SpoutPlayer && plugin.achievements && plugin.useSpout) {
				try {
					((SpoutPlayer) player).sendNotification("SetRankPB", "You are now " + rank, Material.getMaterial(plugin.icon.toUpperCase().replace(" ", "_")));
				} catch (NullPointerException e) {
					System.out.println("[SetRankPB] WARNING: The material " + plugin.icon.toUpperCase().replace(" ", "_") + " does not exist. Using default.");
					((SpoutPlayer) player).sendNotification("SetRankPB", "You are now " + rank, Material.DIAMOND_PICKAXE);
				} catch (Exception e) {
				}
			}
		} catch (NoClassDefFoundError e) {
		}
	}
}
