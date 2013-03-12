package com.rushteamc.RTMCPlugin.sync;

import java.io.*;
import java.nio.*;
import java.nio.channels.Pipe;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

import org.bukkit.configuration.file.FileConfiguration;

import com.rushteamc.RTMCPlugin.RTMCPlugin;
import com.rushteamc.RTMCPlugin.sync.message.message;

public class syncMain
{
	public static final String PIPE_PATH = "/dev/shm/RTMCPlugin/pipes/server_"; // TODO: make this configurable
	
	public int syncReceiverPort; // TODO: remove this variable
	public int[] syncTransmitterPorts = new int[0]; // TODO: remove this variable
	
	private syncListener listener; // TODO: Improve to allow more then two servers
	private syncSender sender; // TODO: Improve to allow more than two server
	private Socket clientSocket; // TODO: remove variable
	private ObjectOutputStream oos; // TODO: remove variable

	private int port; // TODO: Improve to allow more then two server
	
	public syncMain(RTMCPlugin main)
	{
		/**
		 * TODO: The config reding should change to a system where you only give the number
		 * of servers running parallel, in the config. The current ID of the server will
		 * then be determined by checking the current existing pipes and assuming every
		 * pipe is actualy used.
		 * The name of the pipe will be formated as follow:
		 * PIPE_PREFIX + "_" + listening server ID + "_" + sending server ID
		 */
		FileConfiguration config = main.getConfig();
		int recvPort = 1;
		if(config.isInt("sync.ports.own"))
			recvPort = (int)config.get("sync.ports.own");

		int sendPort[] = {0};
		if(config.isInt("sync.ports.others"))
			sendPort = new int[] {(int)config.get("sync.ports.others")};
		else if(config.isList("sync.ports.others"))
		{
			ArrayList list = (ArrayList)config.get("sync.ports.others");
			sendPort = new int[ list.size() ];
			for( int i = 0 ; i < list.size() ; i++ )
			{
				Object obj = list.get(i);
				if( obj instanceof Integer )
				{
					sendPort[i] = (int)obj;
				}
				else if( obj instanceof String )
				{
					sendPort[i] = Integer.parseInt((String)obj);
				}
				else
				{
					System.out.println("[RTMCPlugin][SYNC] Invalid class of type: " + list.get(i).getClass().getCanonicalName() );
				}
			}
		}

		port = sendPort[0];
		
		try {
			System.out.println("[RTMCPlugin][sync] Creating listener...");
			listener = new syncListener(recvPort);
			System.out.println("[RTMCPlugin][sync] Starting listener...");
			listener.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("[RTMCPlugin][sync] Creating listener...");
		sender = new syncSender(PIPE_PATH + port);
		System.out.println("[RTMCPlugin][sync] Starting listener...");
		sender.start();

		main.getServer().getPluginManager().registerEvents(new eventListener(this, config), main);
	}

	public void resetIn()
	{
		// TODO: remove empty method
	}

	public void resetOut()
	{
		// TODO: remove empty method
	}
	
	public void unload()
	{
		System.out.println("[RTMCPlugin][SYNC] Clossing listening thread...");
		closeListener();
		System.out.println("[RTMCPlugin][SYNC] Closing writing ports...");
		closeSocket();
	}
	
	private void closeListener()
	{
		if(listener != null)
			listener.kill();
		try {
			if( oos != null)
				oos.close();
			if( clientSocket != null)
				clientSocket.close();
		} catch (IOException e) {
			;
		}
		try {
			if( listener != null && listener.isAlive() )
			{
				listener.join();
				listener = null;
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void closeSocket()
	{
		// TODO: remove empty method
	}
	
	private void openSocket() throws IOException
	{
		// TODO: remove empty method
	}
	
	public void sendMessage(message message)
	{
		// TODO: Extend for infinit servers (more than two)
		sender.sendMessage(message);
	}
	
}
