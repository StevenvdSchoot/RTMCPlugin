package com.rushteamc.RTMCPlugin.sync.message;

public class playerKick implements message
{
	public String playerName;
	public String message;
	
	public playerKick(String playerName, String message)
	{
		this.playerName = playerName;
		this.message = message;
	}
}
