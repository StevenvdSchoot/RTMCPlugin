package com.rushteamc.RTMCPlugin.synchronizer;

import org.bukkit.craftbukkit.v1_4_R1.entity.CraftPlayer; // v1_4_R1 / v1_5_R3 / v1_6_R1
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.rushteamc.RTMCPlugin.synchronizer.FakePlayer.FakeEntityPlayer;
import com.rushteamc.RTMCPlugin.synchronizer.Messages.FakePlayerLogin;
import com.rushteamc.RTMCPlugin.synchronizer.Messages.FakePlayerLogout;

public class EventListener implements Listener
{
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		CraftPlayer player = (CraftPlayer)event.getPlayer();
		if( !( player.getHandle() instanceof FakeEntityPlayer) )
		{
			Synchronizer.sendMessage(new FakePlayerLogin(event.getPlayer().getName(), Synchronizer.getServerID()));
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		CraftPlayer player = (CraftPlayer)event.getPlayer();
		if( !( player.getHandle() instanceof FakeEntityPlayer) )
		{
			Synchronizer.sendMessage(new FakePlayerLogout(event.getPlayer().getName()));
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerKick(PlayerKickEvent event)
	{
		CraftPlayer player = (CraftPlayer)event.getPlayer();
		if( !( player.getHandle() instanceof FakeEntityPlayer) )
		{
			Synchronizer.sendMessage(new FakePlayerLogout(event.getPlayer().getName()));
		}
	}
}
