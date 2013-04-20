package com.rushteamc.RTMCPlugin.sync.message;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class FakePlayerPeformCommand extends Message
{
	private static final long serialVersionUID = 1L;
	
	private String playername;
	private String command;
	
	public FakePlayerPeformCommand(String playername, String command)
	{
		this.playername = playername;
		this.command = command;
	}
	
	@Override
	public void run()
	{
		Player player = Bukkit.getPlayer(playername);
		if(player != null)
		{
			System.out.println("peforming command: \"" + command + "\"");
			player.performCommand(command);
		}
	}

}
