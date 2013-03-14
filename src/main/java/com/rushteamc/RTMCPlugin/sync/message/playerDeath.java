package com.rushteamc.RTMCPlugin.sync.message;

public class playerDeath implements message
{
	public String playerName;
	public String message;
	
	public playerDeath(String playerName, String message)
	{
		this.playerName = playerName;
		this.message = message;
	}
}
