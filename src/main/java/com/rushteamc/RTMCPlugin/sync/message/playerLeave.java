package com.rushteamc.RTMCPlugin.sync.message;

public class playerLeave implements message
{
	public String playerName;
	public String message;
	
	public playerLeave(String playerName, String message)
	{
		this.playerName = playerName;
		this.message = message;
	}
}
