package com.rushteamc.RTMCPlugin.sync;

import java.io.*;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.rushteamc.RTMCPlugin.adminChat.adminChatMain;
import com.rushteamc.RTMCPlugin.ChatManager.ChatFormatter;
import com.rushteamc.RTMCPlugin.sync.message.*;

public class syncListener extends Thread
{
	private boolean running = true;
	private String filename;
	private File fd;
	private InputStream inputStream;
	private ObjectInputStream objectInputStream;
	
	public syncListener(String filename)
	{
		setDaemon(true);
		
		this.filename = filename;

		fd = new File(filename);
		if( !fd.mkdirs() )
			; // TODO: Do some error handling...

		if(fd.exists())
			fd.delete();

		try {
			Process p = Runtime.getRuntime().exec("mkfifo --mode=666 " + filename);
			p.waitFor();
		} catch(InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void kill()
	{
		running = false;
		try { // Extremly ugly way to force FileInputStream(PIPE_PATH + pipeNum) to unblock... 
			OutputStream outputStream = new FileOutputStream(filename);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
			objectOutputStream.writeObject( null );
			objectOutputStream.close();
			outputStream.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
		this.interrupt();
		if( fd.exists() )
			fd.delete();
	}
	
	private void sendToAllOnlinePlayers(String str)
	{
		System.out.println("[RTMCPlugin][SYNC] Broadcasting message: " + str);
		if(str == null)
			return;
		for(Player p : Bukkit.getOnlinePlayers()){
			p.sendMessage(str);
		}
	}
	
	public void run()
	{
		System.out.println("[RTMCPlugin][SYNC] Trying to listen to: " + filename);
		while(running)
		{
			try {
				if(objectInputStream==null)
				{
					try {
						inputStream = new FileInputStream(filename);
						objectInputStream = new ObjectInputStream(inputStream);
					} catch(IOException e) {
						e.printStackTrace();
					}
					System.out.println("[RTMCPlugin][SYNC] Connected to: " + filename);
				}
				Object obj = objectInputStream.readObject();
				if( obj instanceof publicChat )
				{
					publicChat msg = (publicChat)obj;
					sendToAllOnlinePlayers(String.format(msg.format,msg.playerName,msg.message));
				}
				else if( obj instanceof adminChat )
				{
					adminChat msg = (adminChat)obj;
					adminChatMain.sendAdminChatMessage(msg.playerName, msg.message);
				}
				else if( obj instanceof meChat )
				{
					meChat msg = (meChat)obj;
					sendToAllOnlinePlayers( "* " + msg.playerName + " " + msg.message );
				}
				else if( obj instanceof playerJoin )
				{
					playerJoin msg = (playerJoin)obj;
					sendToAllOnlinePlayers( msg.message );
				}
				else if( obj instanceof playerLeave )
				{
					playerLeave msg = (playerLeave)obj;
					sendToAllOnlinePlayers( msg.message );
				}
				else if( obj instanceof playerKick )
				{
					playerKick msg = (playerKick)obj;
					sendToAllOnlinePlayers( msg.message );
				}
				else if( obj instanceof playerDeath )
				{
					playerDeath msg = (playerDeath)obj;
					sendToAllOnlinePlayers( msg.message );
				}
				else if( obj instanceof chatAdmin )
				{
					chatAdmin msg = (chatAdmin)obj;
					adminChatMain.sendAdminChatMessage( msg.playername, msg.message );
				}
				else if( obj instanceof chatPublic )
				{
					chatPublic msg = (chatPublic)obj;
					sendToAllOnlinePlayers( msg.message );
				}
				else if( obj instanceof chatPublicFormatted )
				{
					chatPublicFormatted msg = (chatPublicFormatted)obj;
					sendToAllOnlinePlayers( ChatFormatter.format(msg.playername, msg.worldname, msg.message) );
				}
			} catch (IOException e) {
				if(objectInputStream != null)
					try {objectInputStream.close();} catch (IOException e2) {}
				if(inputStream != null)
					try {inputStream.close();} catch (IOException e2) {}
				objectInputStream = null;
				inputStream = null;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				if(objectInputStream != null)
					try {objectInputStream.close();} catch (IOException e2) {}
				if(inputStream != null)
					try {inputStream.close();} catch (IOException e2) {}
				objectInputStream = null;
				inputStream = null;
			}
		}
		System.out.println("[RTMCPlugin][SYNC] Listener closed! Please reopen by typing \"/rtmcplugin sync reset\" in the console.");
		if(objectInputStream != null)
			try {objectInputStream.close();} catch (IOException e) {}
		if(inputStream != null)
			try {inputStream.close();} catch (IOException e) {}
		fd = new File(filename);
		if( fd.exists() )
			fd.delete();

	}
}
