package com.rushteamc.RTMCPlugin.sync;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_5_R2.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.rushteamc.RTMCPlugin.PermissionsManager.PermissionsManager;
import com.rushteamc.RTMCPlugin.sync.message.ChatMessage;
import com.rushteamc.RTMCPlugin.sync.message.FakePlayerChat;
import com.rushteamc.RTMCPlugin.sync.message.FakePlayerKick;
import com.rushteamc.RTMCPlugin.sync.message.FakePlayerLogin;
import com.rushteamc.RTMCPlugin.sync.message.FakePlayerLogout;
import com.rushteamc.RTMCPlugin.sync.message.FakePlayerPeformCommand;
import com.rushteamc.RTMCPlugin.sync.message.FakePlayerSetWorld;

public class EventListener implements Listener
{
	public String format;
	
	public EventListener(FileConfiguration config)
	{
		format = config.getString("chat.format.default").replace('&', ChatColor.COLOR_CHAR);
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onAsyncPlayerChatSeccond(AsyncPlayerChatEvent event)
	{
		CraftPlayer player = (CraftPlayer) event.getPlayer();
		if( !( player.getHandle() instanceof FakeEntityPlayer) )
		{
			Synchronizer.sendMessage(new FakePlayerChat(event.getPlayer().getName(), event.getMessage()));
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		CraftPlayer player = (CraftPlayer)event.getPlayer();
		if( !( player.getHandle() instanceof FakeEntityPlayer) )
		{
			Synchronizer.sendMessage(new FakePlayerLogin(event.getPlayer().getName()));
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		Synchronizer.sendMessage(new FakePlayerLogout(event.getPlayer().getName()));
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerKick(PlayerKickEvent event)
	{
		Synchronizer.sendMessage(new FakePlayerKick(event.getPlayer().getName(),event.getReason()));
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerDeath(PlayerDeathEvent event)
	{
		//TODO: Use new FakePlayer
		Synchronizer.sendMessage( new ChatMessage(event.getDeathMessage()) );
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerChangedWorld(PlayerChangedWorldEvent event)
	{
		Synchronizer.sendMessage( new FakePlayerSetWorld(event.getPlayer().getName(), event.getPlayer().getWorld().getName()) );
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		//TODO: Use new FakePlayer
		if (event.isCancelled())
			return;
		
		String command = event.getMessage();
		if (command == null)
			return;

		CraftPlayer player = (CraftPlayer)event.getPlayer();
		if( !( player.getHandle() instanceof FakeEntityPlayer) )
		{
			if (command.toLowerCase().startsWith("/me ")) {
				Synchronizer.sendMessage(new FakePlayerPeformCommand(event.getPlayer().getName(), event.getMessage().substring(1)));
			} else if (command.toLowerCase().startsWith("/pex ")) {
				if(!PermissionsManager.managePermissions)
					Synchronizer.sendMessage(new FakePlayerPeformCommand(player.getName(), event.getMessage().substring(1)));
			}
		}
	}
	
}
