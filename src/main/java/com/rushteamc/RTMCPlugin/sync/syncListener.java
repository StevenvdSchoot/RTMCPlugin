package com.rushteamc.RTMCPlugin.sync;

import java.io.*;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.rushteamc.RTMCPlugin.adminChat.adminChatMain;
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
				message msg = (message)objectInputStream.readObject();
				switch(msg.type)
				{
				case CHAT_PUBLIC:
					publicChat pcm = (publicChat)msg;
					// Bukkit.getServer().broadcastMessage( String.format(pcm.format,pcm.playerName,pcm.message) );
					sendToAllOnlinePlayers(String.format(pcm.format,pcm.playerName,pcm.message));
					break;
				case CHAT_ADMIN:
					adminChat amc = (adminChat)msg;
					adminChatMain.sendAdminChatMessage(amc.playerName, amc.message);
					break;
				case CHAT_ME:
					meChat mc = (meChat)msg;
					sendToAllOnlinePlayers( "* " + mc.playerName + " " + mc.message );
					break;
				case PLAYER_JOIN:
					playerJoin pj = (playerJoin)msg;
					sendToAllOnlinePlayers( pj.message );
					break;
				case PLAYER_LEAVE:
					playerLeave pl = (playerLeave)msg;
					sendToAllOnlinePlayers( pl.message );
					break;
				case PLAYER_KICK:
					playerKick pk = (playerKick)msg;
					sendToAllOnlinePlayers( pk.message );
					break;
				case PLAYER_DEATH:
					playerDeath pd = (playerDeath)msg;
					sendToAllOnlinePlayers( pd.message );
					break;
				default:
					sendToAllOnlinePlayers("[RTMCPlugin][SYNC] Received message with unknown type!");
				}
			} catch (IOException e) {
				try {
					objectInputStream.close();
					inputStream.close();
				} catch(IOException e2) {
					e2.printStackTrace();
				}
				objectInputStream = null;
				inputStream = null;
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
