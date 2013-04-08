package com.rushteamc.RTMCPlugin.sync;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_5_R2.CraftServer;

import net.minecraft.server.v1_5_R2.EntityPlayer;
import net.minecraft.server.v1_5_R2.INetworkManager;
import net.minecraft.server.v1_5_R2.MinecraftServer;
import net.minecraft.server.v1_5_R2.Packet;
import net.minecraft.server.v1_5_R2.PlayerConnection;
import net.minecraft.server.v1_5_R2.PlayerInteractManager;
import net.minecraft.server.v1_5_R2.World;

public class FakeEntityPlayer extends EntityPlayer
{
	public FakeEntityPlayer(String playername)
	{
		super(((CraftServer) Bukkit.getServer()).getServer(),  ((CraftServer) Bukkit.getServer()).getServer().worlds.get(0), playername, new PlayerInteractManager(((CraftServer) Bukkit.getServer()).getServer().worlds.get(0)));
		init();
	}
	
	private FakeEntityPlayer(MinecraftServer arg0, World arg1, String arg2, PlayerInteractManager arg3)
	{
		super(arg0, arg1, arg2, arg3);
		init();
	}
	
	private void init()
	{
		this.playerConnection = new FakePlayerConnection(this);
	}
	
	private class FakePlayerConnection extends PlayerConnection
	{
		public FakePlayerConnection(EntityPlayer entityplayer)
		{
			super(((CraftServer) Bukkit.getServer()).getServer(), new FakeINetworkManager(), entityplayer);
		}
		
		public FakePlayerConnection(MinecraftServer minecraftserver, EntityPlayer entityplayer)
		{
			super(minecraftserver, new FakeINetworkManager(), entityplayer);
		}
		
		public FakePlayerConnection(MinecraftServer minecraftserver, INetworkManager inetworkmanager, EntityPlayer entityplayer)
		{
			super(minecraftserver, inetworkmanager, entityplayer);
		}
		
		public void sendPacket(Packet packet)
		{
			/**
			 * TODO: We don't want the data to be actually sended. Instead the data should be filtered. Data that is usefull for the actual player sended to him using the synchronizer, data that might be usefull for others will b adjusted on all servers using the synchronizer.
			 */
			
			// System.out.println("Haha, server tried to send data to a fakeplayer :P");
		}
		
	}
	
}
