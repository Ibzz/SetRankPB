package com.baummann.setrankpb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Properties;

import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.baummann.setrankpb.commands.CommandPromotion;
import com.baummann.setrankpb.commands.CommandRank;
import com.iCo6.iConomy;
import com.platymuus.bukkit.permissions.PermissionsPlugin;

public class SetRankPB extends JavaPlugin {
	protected PermissionsPlugin plugin;
	public Properties props = new Properties();
	public String md = "plugins/SetRankPB";
	public File config = new File(md + "/config.yml");
	public boolean canUse = false;
	public boolean useSpout = false;
	public boolean achievements;
	public String icon;
	public static String version = "1.3.1";
	final Plugin setRankPB = this;
	public String noPermission = ChatColor.RED + "You don't have Permission to use this!";
	public iConomy iConomy;
	public boolean useiConomy = false;
	public boolean useEconomy = false;
	public boolean rankOffline = true;
	public LinkedHashMap<String, Double> prices = new LinkedHashMap<String, Double>();
	public HashMap<Player, String> confirming = new HashMap<Player, String>();
	private final SRPBPlayerListener playerListener = new SRPBPlayerListener(this);

	public void onEnable() {
		System.out.println("[SetRankPB] Enabling...");
		if (!new File("plugins/SetRankPB").exists())
			new File("plugins/SetRankPB").mkdir();
		if (!config.exists()) {
			try {
				config.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		registerEvents();
		loadConfig();
		Plugin permBukkit = getServer().getPluginManager().getPlugin("PermissionsBukkit");
		Plugin iconomy = getServer().getPluginManager().getPlugin("iConomy");
		if (iconomy != null) {
			iConomy = (iConomy) iconomy;
			System.out.println("[SetRankPB] Found iConomy.");
			useiConomy = true;
		} else {
			System.out.println("[SetRankPB] Couldn't find iConomy");
			useiConomy = false;
		}
		if (permBukkit != null) {
			canUse = true;
			plugin = (PermissionsPlugin) permBukkit;
		} else
			canUse = false;
		Plugin spout = getServer().getPluginManager().getPlugin("Spout");
		if (spout != null) {
			System.out.println("[SetRankPB] Found Spout.");
			useSpout = true;
		} else {
			System.out.println("[SetRankPB] Couldn't find Spout, not using achievements.");
			achievements = false;
		}
		getCommand("promotion").setExecutor(new CommandPromotion(this));
		getCommand("rank").setExecutor(new CommandRank(this));
		System.out.println("[SetRankPB] Enabled. Version " + version);
	}

	public void registerEvents() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(playerListener, this);
	}

	public void onDisable() {
		System.out.println("[SetRankPB] Disabling...");
		getServer().getScheduler().cancelTasks(this);
		System.out.println("[SetRankPB] Disabled.");
	}

	public RankHandler getHandler() {
		return new RankHandler(this);
	}

	@SuppressWarnings("unchecked")
	public void loadConfig() {
		try {
			FileConfiguration config = getConfig();
			config.load(this.config);
			config.addDefault("SetRankPB.iConomy.use", true);
			config.addDefault("SetRankPB.Spout.use-achievements", true);
			config.addDefault("SetRankPB.Spout.achievement-icon", "diamond pickaxe");
			config.addDefault("SetRankPB.rank-offline-players", true);

			useEconomy = config.getBoolean("SetRankPB.iConomy.use");
			achievements = config.getBoolean("SetRankPB.Spout.use-achievements");
			icon = config.getString("SetRankPB.Spout.achievement-icon", "diamond pickaxe");
			rankOffline = config.getBoolean("SetRankPB.rank-offline-players");
			prices = (LinkedHashMap<String, Double>) config.get("SetRankPB.iConomy.prices");
			if (prices == null) {
				prices = new LinkedHashMap<String, Double>();
				prices.put("exampleRank", 3.0);
				config.set("SetRankPB.iConomy.prices", prices);
			}
			config.options().copyDefaults(true);
			config.save(this.config);
		} catch (Exception e) {
		}
	}

	@SuppressWarnings("rawtypes")
	public void reloadPermissions() {
		if (!canUse)
			return;
		try {
			plugin.getConfig().load(new File("plugins/PermissionsBukkit/config.yml"));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (InvalidConfigurationException e1) {
			e1.printStackTrace();
		}
		Class pluginClass = plugin.getClass();
		Method[] methods = pluginClass.getDeclaredMethods();
		for (Method method : methods) {
			if (method.getName().equals("refreshPermissions")) {
				method.setAccessible(true);
				try {
					method.invoke(plugin);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
