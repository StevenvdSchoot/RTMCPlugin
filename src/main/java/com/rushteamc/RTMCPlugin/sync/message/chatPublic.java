package com.rushteamc.RTMCPlugin.sync.message;

public class chatPublic implements message
{
	private static final long serialVersionUID = 1L;
	public String message;
	
	public chatPublic(String message)
	{
		this.message = message;
	}
}
