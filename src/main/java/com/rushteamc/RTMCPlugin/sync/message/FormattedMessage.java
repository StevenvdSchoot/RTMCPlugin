package com.rushteamc.RTMCPlugin.sync.message;

import org.bukkit.entity.Player;

import com.rushteamc.RTMCPlugin.ChatManager.ChatManager;

public class FormattedMessage implements MessageNew
{
	private static final long serialVersionUID = 1L;
	
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
		ChatManager.sendMessageFormatted(playername, playerworld, message);
	}
	
}
