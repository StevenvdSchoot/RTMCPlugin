package com.rushteamc.RTMCPlugin.sync.message;

import com.rushteamc.RTMCPlugin.sync.Synchronizer;

public class FakePlayerLogin extends Message
{
	private static final long serialVersionUID = 1L;
	
	private String playername;
	
	public FakePlayerLogin(String playername)
	{
		System.out.println("[RTMCPlugin][SYNC] About to remote login player: " + playername);
		this.playername = playername;
	}
	
	@Override
	public void run()
	{
		System.out.println("[RTMCPlugin][SYNC] About to login player: " + playername);
		Synchronizer.addPlayer(playername);
	}
	
}
