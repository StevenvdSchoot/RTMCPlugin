package com.rushteamc.RTMCPlugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.rushteamc.RTMCPlugin.PlayerList.PlayerList;
import com.rushteamc.RTMCPlugin.synchronizer.Synchronizer;

public class Main extends JavaPlugin
{
	public static Main plugin;
	
	public void onLoad()
	{
		plugin = this;
	}
	
	public void onEnable()
	{
		Synchronizer.init();
		PlayerList.init();
	}
	
	public void onDisable()
	{
		Synchronizer.unload();
		PlayerList.unload();
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		return false;
	}
}
