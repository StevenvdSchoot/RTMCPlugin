package com.rushteamc.RTMCPlugin.sync;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;

import com.rushteamc.RTMCPlugin.sync.message.*;

import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class eventListener implements Listener
{
	syncMain syncmain;
	public String format;
	
	public eventListener(syncMain syncmain, FileConfiguration config)
	{
		this.syncmain = syncmain;
		format = config.getString("chat.format.default").replace('&', ChatColor.COLOR_CHAR);
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onAsyncPlayerChatSeccond(AsyncPlayerChatEvent event)
	{
		// publicChat message = new publicChat(event.getFormat(),event.getPlayer().getDisplayName(),event.getMessage());
		// syncmain.sendMessage(message);
		Player player = event.getPlayer();
		syncmain.sendMessage2( new FormattedMessage(player, event.getMessage()) );
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		playerJoin message = new playerJoin(event.getPlayer().getDisplayName(),event.getJoinMessage());
		syncmain.sendMessage(message);
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		playerLeave message = new playerLeave(event.getPlayer().getDisplayName(),event.getQuitMessage());
		syncmain.sendMessage(message);
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerKick(PlayerKickEvent event)
	{
		playerKick message = new playerKick(event.getPlayer().getDisplayName(),event.getLeaveMessage());
		syncmain.sendMessage(message);
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerDeath(PlayerDeathEvent event)
	{
		playerDeath message = new playerDeath(event.getEntity().getDisplayName(),event.getDeathMessage());
		syncmain.sendMessage(message);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		if (event.isCancelled())
			return;
		
		String command = event.getMessage();
		if (command == null)
			return;

		if (command.toLowerCase().startsWith("/me ")) {
			System.out.println("[RTMCPlugin][SYNC] Player name: " + event.getPlayer().getDisplayName());
			System.out.println("[RTMCPlugin][SYNC] Me string: " + command.substring(command.indexOf(" ")).trim());
			meChat msg = new meChat(event.getPlayer().getDisplayName(), command.substring(command.indexOf(" ")).trim());
			syncmain.sendMessage(msg);
		} else if (command.toLowerCase().startsWith("/pex ")) {
			;
		}
	}
	
}
