package com.rushteamc.RTMCPlugin.sync.message;

public class adminChat implements message
{
	private static final long serialVersionUID = 1L;
	public String playerName;
	public String message;
	
	public adminChat(String playerName, String message)
	{
		this.playerName = playerName;
		this.message = message;
	}
	
}
