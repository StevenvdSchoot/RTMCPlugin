package com.rushteamc.RTMCPlugin.synchronizer;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Map;

import net.minecraft.server.v1_4_R1.Packet; // v1_4_R1 / v1_5_R3 / v1_6_R1

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_4_R1.CraftServer; // v1_4_R1 / v1_5_R3 / v1_6_R1
import org.bukkit.craftbukkit.v1_4_R1.entity.CraftPlayer; // v1_4_R1 / v1_5_R3 / v1_6_R1
import org.bukkit.entity.Player;

import com.rushteamc.RTMCPlugin.Main;
import com.rushteamc.RTMCPlugin.synchronizer.FakePlayer.FakeEntityPlayer;
import com.rushteamc.RTMCPlugin.synchronizer.Messages.Message;

public class Synchronizer
{
	private static EventListener eventListener = null;
	
	private static String pipe_prefix;
	private static int numServers;
	private static int serverID;
	private static SynchronizerListener listeners[];
	private static SynchronizerSender senders[];

	public static void init()
	{
		FileConfiguration config = Main.plugin.getConfig();

		if(config.isString("sync.path"))
			pipe_prefix = (String)config.get("sync.path");
		else
			pipe_prefix = "/dev/shm/RTMCPlugin/pipes/server";

		if(config.isInt("sync.servers"))
			numServers = (int)config.get("sync.servers");
		else if(config.isString("sync.servers"))
			numServers = Integer.parseInt((String)config.get("sync.servers"));
		else
			numServers = 0;

		if(numServers > 1)
		{
			listeners = new SynchronizerListener[numServers-1];
			senders = new SynchronizerSender[numServers-1];

			serverID = 0;
			while(new File(pipe_prefix + "_" + String.valueOf(serverID) + "_" + String.valueOf(numServers-1)).exists())
				serverID++;
			if(serverID == numServers-1)
				if(new File(pipe_prefix + "_" + String.valueOf(serverID) + "_0").exists())
					serverID++;
			if(serverID>=numServers)
			{
				System.out.println("[RTMCPlugin][SYNC] All server IDs in use! Could not setup synchronizer.");
				// TODO: Do some better error handling here. Pipes should all pipes should be removed and recreated by the servers. This way the unused pipes are not recreated and can be used by this server.
				numServers = 0;
				listeners = new SynchronizerListener[0];
				senders = new SynchronizerSender[0];
				return;
			}

			int i;
			for(i = 0;i<serverID;i++)
			{
				System.out.println("[RTMCPlugin][SYNC] Creating listener on " + pipe_prefix + "_" + String.valueOf(serverID) + "_" + String.valueOf(i));
				listeners[i] = new SynchronizerListener(pipe_prefix + "_" + String.valueOf(serverID) + "_" + String.valueOf(i) );
				listeners[i].start();
			}
			for(i=serverID+1;i<numServers;i++)
			{
				System.out.println("[RTMCPlugin][SYNC] Creating listener on " + pipe_prefix + "_" + String.valueOf(serverID) + "_" + String.valueOf(i));
				listeners[i-1] = new SynchronizerListener(pipe_prefix + "_" + String.valueOf(serverID) + "_" + String.valueOf(i) );
				listeners[i-1].start();
			}

			for(i = 0;i<serverID;i++)
			{
				System.out.println("[RTMCPlugin][SYNC] Creating sender on " + pipe_prefix + "_" + String.valueOf(i) + "_" + String.valueOf(serverID));
				senders[i] = new SynchronizerSender(pipe_prefix + "_" + String.valueOf(i) + "_" + String.valueOf(serverID) );
				senders[i].start();
			}
			for(i = serverID+1;i<numServers;i++)
			{
				System.out.println("[RTMCPlugin][SYNC] Creating sender on " + pipe_prefix + "_" + String.valueOf(i) + "_" + String.valueOf(serverID));
				senders[i-1] = new SynchronizerSender(pipe_prefix + "_" + String.valueOf(i) + "_" + String.valueOf(serverID) );
				senders[i-1].start();
			}
		}
		else
		{
			listeners = new SynchronizerListener[0];
			senders = new SynchronizerSender[0];
		}

		if(eventListener == null)
		{
			eventListener = new EventListener();
			Main.plugin.getServer().getPluginManager().registerEvents(eventListener, Main.plugin);
		}
	}

	public static void unload()
	{
		if(eventListener != null)
			Main.plugin.getServer().getPluginManager().registerEvents(eventListener, Main.plugin);
		
		for(SynchronizerListener listener : listeners)
			listener.kill();
		for(SynchronizerListener listener : listeners)
			try {
				listener.join();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		for(SynchronizerSender sender : senders)
			sender.kill();
		for(SynchronizerSender sender : senders)
			try {
				sender.join();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		
		eventListener = null;
		listeners = null;
		senders = null;
	}

	public static void sendMessageToServer(int serverID, Message formattedMessage)
	{
		if(senders.length <= serverID || serverID == Synchronizer.serverID)
			return;
		
		if(serverID > Synchronizer.serverID)
			serverID--;
		
		senders[serverID].sendMessage2(formattedMessage);
	}

	public static void sendMessage(Message formattedMessage)
	{
		for(SynchronizerSender sender : senders)
			// TODO: Do null pointer check (and solve null pointers...)
			sender.sendMessage2(formattedMessage);
	}
	
	public static int getServerID()
	{
		return serverID;
	}

	@SuppressWarnings("unchecked")
	public static void addFakePlayer(String playername, int originServerID)
	{
		System.out.println("[RTMCPlugin][SYNC] Spawning alter ego of " + playername);

		FakeEntityPlayer fakePlayer = new FakeEntityPlayer(playername);
		fakePlayer.originServerID = originServerID;
		
		((CraftServer) Bukkit.getServer()).getServer().getPlayerList().c(fakePlayer); // c() / c() / c()
		
		Field hiddenPlayersField;
		try {
			hiddenPlayersField = CraftPlayer.class.getDeclaredField("hiddenPlayers");
			hiddenPlayersField.setAccessible(true);
			
			Player players[] = Bukkit.getOnlinePlayers();
			for(Player player : players)
			{
		        try {
					if (!((Map<String, Player>)hiddenPlayersField.get((CraftPlayer)player)).containsKey(fakePlayer.getName()))
						((Map<String, Player>)hiddenPlayersField.get((CraftPlayer)player)).put(fakePlayer.getName(), fakePlayer.getBukkitEntity());
				} catch (IllegalArgumentException | IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		} catch (NoSuchFieldException | SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}

	public static void removeFakePlayer(String playername)
	{
		System.out.println("[RTMCPlugin][SYNC] Despawning alter ego of " + playername);
		Player player = Bukkit.getPlayerExact(playername);
		if(player != null)
		{
			((CraftServer) Bukkit.getServer()).getServer().getPlayerList().disconnect(((CraftPlayer)player).getHandle());
		}
		
		// TODO: Remove player from hidden list
	}
	
	public static void sendPacketToPlayer(String playername, Packet packet)
	{
		Player player = Bukkit.getPlayerExact(playername);
		if(player != null)
		{
			((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
		}
	}
}
