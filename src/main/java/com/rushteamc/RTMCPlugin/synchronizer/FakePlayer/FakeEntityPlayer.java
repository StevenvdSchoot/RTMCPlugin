package com.rushteamc.RTMCPlugin.synchronizer.FakePlayer;

import net.minecraft.server.v1_4_R1.*; // v1_4_R1 / v1_5_R3 / v1_6_R1

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_4_R1.CraftServer; // v1_4_R1 / v1_5_R3 / v1_6_R1
import org.bukkit.scheduler.BukkitRunnable;

import com.rushteamc.RTMCPlugin.Main;
import com.rushteamc.RTMCPlugin.synchronizer.Synchronizer;
import com.rushteamc.RTMCPlugin.synchronizer.Messages.SendPlayerPacket;

public class FakeEntityPlayer extends EntityPlayer
{
	private boolean blockPackets = true;
	public int originServerID;
	
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
		//this.locX = 0;
		//this.locY = 0;
		//this.locZ += 30.0;
	}

	private class FakePlayerConnection extends PlayerConnection
	{
		public FakePlayerConnection(EntityPlayer entityplayer)
		{
			super(((CraftServer) Bukkit.getServer()).getServer(), new FakeINetworkManager(), entityplayer);
			init();
		}

		public FakePlayerConnection(MinecraftServer minecraftserver, EntityPlayer entityplayer)
		{
			super(minecraftserver, new FakeINetworkManager(), entityplayer);
			init();
		}

		public FakePlayerConnection(MinecraftServer minecraftserver, INetworkManager inetworkmanager, EntityPlayer entityplayer)
		{
			super(minecraftserver, inetworkmanager, entityplayer);
			init();
		}
		
		private void init()
		{
			new EnablePacketSend().runTaskLater(Main.plugin, 2); // Avoid initial data to be send to the player
		}

		public void sendPacket(Packet packet)
		{
			if(blockPackets)
				return;
			
			// For protocol description see: http://mc.kev009.com/Protocol
			switch(packet.k()) // k() / n() / n()
			{
			case 0x03: // Chat message
				Synchronizer.sendMessage(new SendPlayerPacket(this.getPlayer().getName(), packet));
				break;
			}

		}
		
		public class EnablePacketSend extends BukkitRunnable
		{
			public void run()
			{
				blockPackets = false;
			}
		}

	}
}
