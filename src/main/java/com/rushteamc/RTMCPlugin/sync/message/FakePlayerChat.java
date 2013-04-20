package com.rushteamc.RTMCPlugin.sync.message;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_5_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class FakePlayerChat extends Message
{
	private static final long serialVersionUID = 1L;
	
	private String playername;
	private String chatmessage;
	
	public FakePlayerChat(String playername, String chatmessage)
	{
		this.playername = playername;
		this.chatmessage = chatmessage;
		System.out.println("About to send message from " + playername);
	}
	
	@Override
	public void run()
	{
		Player player = Bukkit.getPlayer(playername);
		if(player != null)
		{
			((CraftPlayer)player).chat(chatmessage);
		}
	}
	
}
