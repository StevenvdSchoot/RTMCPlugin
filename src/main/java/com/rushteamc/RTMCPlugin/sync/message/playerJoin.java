package com.rushteamc.RTMCPlugin.sync.message;

public class playerJoin implements message
{
	public String playerName;
	public String message;
	
	public playerJoin(String playerName, String message)
	{
		this.playerName = playerName;
		this.message = message;
	}
}
