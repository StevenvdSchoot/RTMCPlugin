package com.rushteamc.RTMCPlugin.sync.message;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.rushteamc.RTMCPlugin.ChatManager.ChatFormatter;

public class FormattedMessage implements MessageNew
{
	private String playername;
	private String playerworld;
	private String message;
	
	public FormattedMessage(Player player, String message)
	{
		this.playername = player.getName();
		this.playerworld = player.getWorld().getName();
		this.message = message;
	}

	@Override
	public void execute()
	{
		String msg = ChatFormatter.format(playername, playerworld, message);
		Player source = Bukkit.getPlayer(playername);
		for( Player player : Bukkit.getOnlinePlayers())
			player.sendMessage(msg);
	}
	
}
