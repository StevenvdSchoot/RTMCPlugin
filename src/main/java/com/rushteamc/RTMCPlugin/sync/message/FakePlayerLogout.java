package com.rushteamc.RTMCPlugin.sync.message;

import com.rushteamc.RTMCPlugin.sync.Synchronizer;

public class FakePlayerLogout extends Message
{
	private static final long serialVersionUID = 1L;
	
	private String playername;
	
	public FakePlayerLogout(String playername)
	{
		System.out.println("[RTMCPlugin][SYNC] About to remote logout player: " + playername);
		this.playername = playername;
	}
	
	@Override
	public void run()
	{
		System.out.println("[RTMCPlugin][SYNC] About to logout player: " + playername);
		Synchronizer.removePlayer(playername);
	}
	
}
