package com.rushteamc.RTMCPlugin.sync.message;

public class chatAdmin implements message
{
	private static final long serialVersionUID = 1L;
	public String playername;
	public String message;
	
	public chatAdmin(String playername, String message)
	{
		this.playername = playername;
		this.message = message;
	}
}
