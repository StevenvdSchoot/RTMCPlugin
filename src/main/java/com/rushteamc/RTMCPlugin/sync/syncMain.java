package com.rushteamc.RTMCPlugin.sync;

import java.io.*;

import org.bukkit.configuration.file.FileConfiguration;

import com.rushteamc.RTMCPlugin.RTMCPlugin;
import com.rushteamc.RTMCPlugin.sync.message.MessageNew;
import com.rushteamc.RTMCPlugin.sync.message.message;

public class syncMain
{
	public int syncReceiverPort; // TODO: remove this variable
	public int[] syncTransmitterPorts = new int[0]; // TODO: remove this variable
	
	private String pipe_prefix;
	private int numServers;
	private int serverID;
	private syncListener listeners[];
	private syncSender senders[];
	
	public syncMain(RTMCPlugin main)
	{
		FileConfiguration config = main.getConfig();
		
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
			listeners = new syncListener[numServers-1];
			senders = new syncSender[numServers-1];
			
			serverID = 0;
			while(new File(pipe_prefix + "_" + String.valueOf(serverID) + "_" + String.valueOf(numServers-1)).exists())
				serverID++;
			if(serverID == numServers-1)
				if(new File(pipe_prefix + "_" + String.valueOf(serverID) + "_0").exists())
					serverID++;
			if(serverID>=numServers)
			{
				System.out.println("[RTMCPlugin][SYNC] All server IDs in use! Could not setup synchronizer.");
				return;
			}
			
			int i;
			for(i = 0;i<serverID;i++)
			{
				listeners[i] = new syncListener(pipe_prefix + "_" + String.valueOf(serverID) + "_" + String.valueOf(i) );
				listeners[i].start();
			}
			for(i=serverID+1;i<numServers;i++)
			{
				listeners[i-1] = new syncListener(pipe_prefix + "_" + String.valueOf(serverID) + "_" + String.valueOf(i) );
				listeners[i-1].start();
			}
			
			for(i = 0;i<serverID;i++)
			{
				senders[i] = new syncSender(pipe_prefix + "_" + String.valueOf(i) + "_" + String.valueOf(serverID) );
				senders[i].start();
			}
			for(i = serverID+1;i<numServers;i++)
			{
				senders[i-1] = new syncSender(pipe_prefix + "_" + String.valueOf(i) + "_" + String.valueOf(serverID) );
				senders[i-1].start();
			}
		}

		main.getServer().getPluginManager().registerEvents(new eventListener(this, config), main);
	}
	
	public void unload()
	{
		for(syncListener listener : listeners)
			listener.kill();
		for(syncListener listener : listeners)
			try {
				listener.join();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		for(syncSender sender : senders)
			sender.kill();
		for(syncSender sender : senders)
			try {
				sender.join();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	}
	
	public void sendMessage(message message)
	{
		// TODO: Extend for infinit servers (more than two)
		for(syncSender sender : senders)
			sender.sendMessage(message);
	}

	public void sendMessage2(MessageNew formattedMessage)
	{
		for(syncSender sender : senders)
			sender.sendMessage2(formattedMessage);
	}
	
}
