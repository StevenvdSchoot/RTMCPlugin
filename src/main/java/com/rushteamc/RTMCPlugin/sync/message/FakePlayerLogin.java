package com.rushteamc.RTMCPlugin.sync.message;

import com.rushteamc.RTMCPlugin.sync.Synchronizer;

public class FakePlayerLogin implements Message
{
	private static final long serialVersionUID = 1L;
	
	private String playername;
	
	public FakePlayerLogin(String playername)
	{
		this.playername = playername;
	}
	
	@Override
	public void execute()
	{
		Synchronizer.addPlayer(playername);
	}
	
}
