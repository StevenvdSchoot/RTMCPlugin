package com.rushteamc.RTMCPlugin.sync.message;

public class meChat implements message
{
	public String playerName;
	public String message;
	
	public meChat(String playerName, String message)
	{
		this.playerName = playerName;
		this.message = message;
	}
	
}
