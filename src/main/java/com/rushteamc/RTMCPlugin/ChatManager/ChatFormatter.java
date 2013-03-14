package com.rushteamc.RTMCPlugin.ChatManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class ChatFormatter
{
	private static PermissionManager permissions;
	private static String format;
	private static FileConfiguration config;
	
	public ChatFormatter(FileConfiguration config)
	{
		ChatFormatter.config = config;
		ChatFormatter.permissions = PermissionsEx.getPermissionManager();
		ChatFormatter.format = config.getString("chat.format.default").replace('&', ChatColor.COLOR_CHAR);
	}
	
	public static String format(String playerName, String worldName, String message)
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
		
		return format.replace("{WORLD}", worldName + ChatColor.RESET ).replace("{RANK}", userGroup.getName() + ChatColor.RESET).replace("{PLAYERNAME}", Bukkit.getPlayer(playerName).getDisplayName() + ChatColor.RESET).replace("{MESSAGE}", message + ChatColor.RESET);
	}
}
