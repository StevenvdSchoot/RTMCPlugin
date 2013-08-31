package com.rushteamc.RTMCPlugin.synchronizer.Messages;

import com.rushteamc.RTMCPlugin.synchronizer.Synchronizer;

public class FakePlayerLogout extends Message
{
	private static final long serialVersionUID = 1L;

	private String playername;

	public FakePlayerLogout(String playername)
	{
		this.playername = playername;
	}

	@Override
	public void run()
	{
		Synchronizer.removeFakePlayer(playername);
	}
}
