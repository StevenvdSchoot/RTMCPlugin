package com.rushteamc.RTMCPlugin.sync.message;

public class chatPublicFormatted implements message
{
	private static final long serialVersionUID = 1L;
	public String playername;
	public String worldname;
	public String message;
	
	public chatPublicFormatted(String playername, String worldname, String message)
	{
		this.playername = playername;
		this.worldname = worldname;
		this.message = message;
	}
}
