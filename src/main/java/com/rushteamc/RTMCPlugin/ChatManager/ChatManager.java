package com.rushteamc.RTMCPlugin.ChatManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import com.rushteamc.RTMCPlugin.RTMCPlugin;

import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class ChatManager
{
	private static PermissionManager permissions;
	private static FileConfiguration config;
	public static String BaseFormat;
	
	public static void setup(RTMCPlugin rtmcplugin)
	{
		ChatManager.config = rtmcplugin.getConfig();
		ChatManager.permissions = PermissionsEx.getPermissionManager();
		ChatManager.BaseFormat = config.getString("chat.format.default").replace('&', ChatColor.COLOR_CHAR);
		
		rtmcplugin.getServer().getPluginManager().registerEvents(new EventListener(), rtmcplugin);
	}
	
	public static void sendMessage(String msg)
	{
		for(Player player : Bukkit.getOnlinePlayers())
			player.sendMessage(msg);
	}
	
	public static void sendMessage(String msg, Permission[] permissions)
	{
		for(Player player : Bukkit.getOnlinePlayers())
		{
			boolean permited = true;
			for(Permission perm : permissions)
				if(!player.hasPermission(perm))
				{
					permited = false;
					break;
				}
			if(permited)
				player.sendMessage(msg);
		}
	}
	
	public static void sendMessage(String msg, String[] permissions)
	{
		for(Player player : Bukkit.getOnlinePlayers())
		{
			boolean permited = true;
			for(String perm : permissions)
				if(!player.hasPermission(perm))
				{
					permited = false;
					break;
				}
			if(permited)
				player.sendMessage(msg);
		}
	}
	
	public static void sendMessageFormatted(String playerName, String worldName, String message)
	{
		sendMessage(format(BaseFormat, playerName, worldName, message));
	}
	
	public static void sendMessageFormatted(String playerName, String worldName, String message, Permission[] permissions)
	{
		sendMessage(format(BaseFormat, playerName, worldName, message), permissions);
	}
	
	public static void sendMessageFormatted(String playerName, String worldName, String message, String[] permissions)
	{
		sendMessage(format(BaseFormat, playerName, worldName, message), permissions);
	}
	
	public static String format(String format, Player player, String worldName, String message)
	{
		return format(format, player.getName(), player.getDisplayName(), worldName, message);
	}
	
	public static String format(String format, String playerName, String worldName, String message)
	{
		return format(format, playerName, Bukkit.getPlayer(playerName).getDisplayName() , worldName, message);
	}
	
	public static String format(String format,String playerName, String playerNameString, String worldName, String message)
	{
		PermissionUser user = permissions.getUser(playerName);
		PermissionGroup[] userGroups = user.getGroups(worldName);
		PermissionGroup userGroup = userGroups[0];
		int maxRank = userGroups[0].getRank();
		for(PermissionGroup group : userGroups )
		{
			if(group.getRank() > maxRank)
				userGroup = group;
		}
		if( config.isString("chat.worlds." + worldName ) )
			worldName = config.getString("chat.worlds." + worldName ).replace('&', ChatColor.COLOR_CHAR);
		
		return format.replace("{WORLD}", worldName + ChatColor.RESET ).replace("{RANK}", userGroup.getName() + ChatColor.RESET).replace("{PLAYERNAME}", playerNameString + ChatColor.RESET).replace("{MESSAGE}", message + ChatColor.RESET);
	}
}
