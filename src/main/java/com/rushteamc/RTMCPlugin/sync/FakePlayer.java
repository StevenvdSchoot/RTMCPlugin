package com.rushteamc.RTMCPlugin.sync;

import net.minecraft.server.v1_5_R2.EntityPlayer;
import net.minecraft.server.v1_5_R2.MinecraftServer;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_5_R2.CraftServer;
import org.bukkit.craftbukkit.v1_5_R2.entity.CraftPlayer;

public class FakePlayer extends CraftPlayer
{

	public FakePlayer(String playername)
	{
		super((CraftServer) Bukkit.getServer(), new FakeEntityPlayer(playername));
		init();
	}

	private FakePlayer(CraftServer server, EntityPlayer entity)
	{
		super(server, entity);
		init();
	}
	
	@SuppressWarnings("unchecked")
	private void init()
	{
		MinecraftServer mcserver = ((CraftServer) Bukkit.getServer()).getServer();
		mcserver.getPlayerList().players.add(this.entity);
	}
	
}
