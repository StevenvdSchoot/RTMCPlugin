package com.rushteamc.RTMCPlugin.PlayerList;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventListener implements Listener
{
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		PlayerList.updatePlayerOnline(event.getPlayer().getName(), true);
		PlayerList.updatePlayerLastLogin(event.getPlayer().getName(), event.getPlayer().getLastPlayed());
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		PlayerList.updatePlayerOnline(event.getPlayer().getName(), false);
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerKick(PlayerKickEvent event)
	{
		PlayerList.updatePlayerOnline(event.getPlayer().getName(), false);
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerLevelChangeEvent(PlayerLevelChangeEvent event)
	{
		PlayerList.updatePlayerExp(event.getPlayer().getName(), event.getPlayer().getLevel());
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityRegainHealthEvent(EntityRegainHealthEvent event)
	{
		Entity entity = event.getEntity();
		
		if(!(entity instanceof Player))
			return;
		
		Player player = (Player)entity;
		PlayerList.updatePlayerHealth(player.getName(), player.getHealth());
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityDamageEvent(EntityDamageEvent event)
	{
		Entity entity = event.getEntity();
		
		if(!(entity instanceof Player))
			return;
		
		Player player = (Player)entity;
		PlayerList.updatePlayerHealth(player.getName(), player.getHealth());
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerMoveEvent(PlayerMoveEvent event)
	{
		Player player = event.getPlayer();
		PlayerList.updatePlayerSpawnDistance(player.getName(), player.getLocation().distance(player.getWorld().getSpawnLocation()));
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerChangedWorldEvent(PlayerChangedWorldEvent event)
	{
		Player player = event.getPlayer();
		
		PlayerList.updatePlayerWorld(player.getName(), player.getWorld().getName());
		PlayerList.updatePlayerSpawnDistance(player.getName(), player.getLocation().distance(player.getWorld().getSpawnLocation()));
	}
}
