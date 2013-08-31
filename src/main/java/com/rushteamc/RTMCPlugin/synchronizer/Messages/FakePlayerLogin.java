package com.rushteamc.RTMCPlugin.synchronizer.Messages;

import com.rushteamc.RTMCPlugin.synchronizer.Synchronizer;

public class FakePlayerLogin extends Message
{
	private static final long serialVersionUID = 1L;

	private String playername;
	private int serverID;

	public FakePlayerLogin(String playername, int serverID)
	{
		this.playername = playername;
		this.serverID = serverID;
	}

	@Override
	public void run()
	{
		Synchronizer.addFakePlayer(playername, serverID);
	}
}
