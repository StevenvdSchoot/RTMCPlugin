package com.rushteamc.RTMCPlugin.PlayerList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.rushteamc.RTMCPlugin.Main;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class PlayerList
{
	private static JSONArray playerList = null;
	private static Map<String, Integer> playerIDList = null;
	private static boolean update = false;
	private static UpdatePlayerData updateTask = null;
	private static int tickCounter = 0;
	private static String playerDataFile = "/dev/shm/RTMCPlugin/playerData.json";
	private static EventListener eventListener = null;
	
	public static void init()
	{
		FileConfiguration config = Main.plugin.getConfig();
		
		if(!config.getBoolean("userdata.enable", false))
			return;
		
		playerDataFile = config.getString("userdata.path",playerDataFile);
		
		if(playerIDList == null)
			playerIDList = new HashMap<String, Integer>();
		
		if(playerList == null)
		{
			String JSONString;

			Path path = Paths.get(playerDataFile);
			if(path.toFile().exists())
			{
				try {
					JSONString = new String(Files.readAllBytes(path));
				} catch (IOException e) {
					JSONString = "[]";
					e.printStackTrace();
				}
			}
			else
			{
				JSONString = "[]";
			}
			playerList = JSONArray.fromObject(JSONString);

			for (int i=0; i<playerList.size(); i++)
			{
				playerIDList.put(playerList.getJSONObject(i).getString("Name"), i);
			}
		}

		if(eventListener == null)
		{
			eventListener = new EventListener();
			Main.plugin.getServer().getPluginManager().registerEvents(eventListener, Main.plugin);
		}
		
		if(updateTask == null)
		{
			updateTask = new UpdatePlayerData();
			updateTask.runTaskTimerAsynchronously(Main.plugin, 50, 2); // Give the server about 2.5 sec. startup time.
		}
		
	}
	
	public static void unload()
	{
		if(updateTask != null)
		{
			updateTask.cancel();
			updateTask.run(); // Do a last save
		}
		
		if(eventListener != null)
			Main.plugin.getServer().getPluginManager().registerEvents(eventListener, Main.plugin);
		
		updateTask = null;
		playerList = null;
		playerIDList = null;
	}
	
	public static void Tick()
	{
		if(tickCounter == 0)
		{
			if(update)
			{
				try {
					Files.write(Paths.get(playerDataFile), playerList.toString().getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				update = false;
				tickCounter = 5; //Only update ones every 2*5 ticks (2 time a second)
			}
		}
		else
		{
			tickCounter--;
		}
	}
	
	private static int getPlayerID(String playerName)
	{
		if(playerIDList == null)
			playerIDList = new HashMap<String, Integer>();
		
		Integer result = playerIDList.get(playerName);
		
		if(result==null)
		{
			JSONObject obj = new JSONObject();
			
			Player player = Bukkit.getPlayerExact(playerName);
			obj.element("Name", playerName);
			if(player == null)
			{
				obj.element("Online", false);
				obj.element("LastLogin", 0);
				obj.element("SpawnDistance", 0);
				obj.element("Health", 0);
				obj.element("Exp", 0);
				obj.element("World", "");
			}
			else
			{
				obj.element("Online", player.isOnline());
				obj.element("LastLogin", player.getLastPlayed());
				obj.element("SpawnDistance", player.getLocation().distance(player.getWorld().getSpawnLocation()) );
				obj.element("Health", player.getHealth());
				obj.element("Exp", player.getLevel());
				obj.element("World", player.getWorld().getName());
			}
			
			playerList.add(obj);
			result = playerList.indexOf(obj);
			playerIDList.put(playerName, result);
		}
		
		return result;
	}
	
	private static void updatePlayerSetting(String playerName, String key, Object value)
	{
		playerList.getJSONObject(getPlayerID(playerName)).element(key, value);
		update = true;
	}
	
	public static void updatePlayerOnline(String playerName, boolean online)
	{
		updatePlayerSetting(playerName, "Online", online);
	}
	
	public static void updatePlayerLastLogin(String playerName, long LastLogin)
	{
		updatePlayerSetting(playerName, "LastLogin", LastLogin);
	}
	
	public static void updatePlayerSpawnDistance(String playerName, double d)
	{
		updatePlayerSetting(playerName, "SpawnDistance", d);
	}
	
	public static void updatePlayerHealth(String playerName, double d)
	{
		updatePlayerSetting(playerName, "Health", d);
	}
	
	public static void updatePlayerExp(String playerName, int exp)
	{
		updatePlayerSetting(playerName, "Exp", exp);
	}
	
	public static void updatePlayerWorld(String playerName, String world)
	{
		updatePlayerSetting(playerName, "World", world);
	}
	
	public static class UpdatePlayerData extends BukkitRunnable
	{
		public void run()
		{
			PlayerList.Tick();
		}
	}
}
