package com.rushteamc.RTMCPlugin.sync.message;

public class publicChat implements message
{
	public String playerName;
	public String message;
	public String format;
	
	public publicChat(String format, String playerName, String message)
	{
		this.playerName = playerName;
		this.format = format;
		this.message = message;
	}
	
}
