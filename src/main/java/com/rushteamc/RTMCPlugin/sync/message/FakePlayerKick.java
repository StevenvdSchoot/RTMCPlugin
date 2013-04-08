package com.rushteamc.RTMCPlugin.sync.message;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class FakePlayerKick implements Message
{
	private static final long serialVersionUID = 1L;
	
	private String playername;
	private String kickmessage;
	
	public FakePlayerKick(String playername, String kickmessage)
	{
		this.playername = playername;
		this.kickmessage = kickmessage;
	}
	
	@Override
	public void execute()
	{
		Player player = Bukkit.getPlayer(playername);
		if(player != null)
		{
			player.kickPlayer(kickmessage);
		}
	}

}
