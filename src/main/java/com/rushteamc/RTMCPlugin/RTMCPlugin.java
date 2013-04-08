package com.rushteamc.RTMCPlugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.rushteamc.RTMCPlugin.ChatManager.ChatManager;
import com.rushteamc.RTMCPlugin.PermissionsManager.PermissionsManager;
import com.rushteamc.RTMCPlugin.Reporter.Reporter;
import com.rushteamc.RTMCPlugin.RestrictionManager.RestrictionManager;
import com.rushteamc.RTMCPlugin.adminChat.adminChatMain;
import com.rushteamc.RTMCPlugin.sync.Synchronizer;

public class RTMCPlugin extends JavaPlugin
{
	public static RTMCPlugin rtmcplugin;
	
	public void onLoad()
	{
		rtmcplugin = this;
	}
	
	public void onEnable()
	{
		Synchronizer.init();
		adminChatMain.init();
		ChatManager.init();
		PermissionsManager.init();
		RestrictionManager.init();
		Reporter.init();
	}
	
	public void onDisable()
	{
		Synchronizer.unload();
	}
	
	private String joinArguments(String[] args)
	{
		String msg = "";
		for(String arg : args)
			msg += arg + " ";
		if(msg.length()>0)
			msg = msg.substring(0,msg.length()-1);
		return msg;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		switch(cmd.getName())
		{
		case "rtmctest":
			//Synchronizer.fp.chat("HOI!");
			// TODO: remove this command.
			//ChatManager.sendMessageFormatted("STSc", "world", "HI");
			//for(Player player : Bukkit.getOnlinePlayers())
			//	Permissions.updateUserPermissions(player);
			//PermissionsManager.test(args[0]);
			//System.out.println("Has permission modifyworld.blocks.destroy.27591:?*: " + ((Bukkit.getPlayer(args[0]).hasPermission("modifyworld.blocks.destroy.27591:?*") == true)?"true":"false") );
			return true;
		case "rtmcsync":
			int i = -1;
			do
			{
				i++;
				if(i>=args.length)
				{
					sender.sendMessage("usage:\n" + cmd.getUsage() );
					return true;
				}
			} while( args[i].equals("") );
			switch(args[i])
			{
			case "reset":
				do
				{
					i++;
					if(i>=args.length)
					{
						sender.sendMessage("usage:\n" + cmd.getUsage() );
						return true;
					}
				} while( args[i].equals("") );
				switch(args[i])
				{
				case "in":
					/* TODO: remove this command
					if(sync == null)
						sync = new syncMain(this);
					else
						sync.resetIn();
					*/
					break;
				case "out":
					/* TODO: remove this command
					if(sync == null)
						sync = new syncMain(this);
					else
						sync.resetOut();
					*/
					break;
				default:
					sender.sendMessage(args[i] + " is not a valid action for reload, usage:\n" + cmd.getUsage() + " [in|out]" );
				}
				break;
			default:
				sender.sendMessage(args[i] + " is not a valid action, usage:\n" + cmd.getUsage() );
			}
			return true;
		case "atoggle":
			adminChatMain.togleAdminChat(sender.getName());
			return true;
		case "amsg":
			adminChatMain.sendAdminChat(sender.getName(), joinArguments(args) );
			return true;
		case "msg":
			//adminChat.sendAdminChat(sender.getName(), joinArguments(args) );
			return false;
		case "reply":
			//adminChat.sendAdminChat(sender.getName(), joinArguments(args) );
			return false;
		case "mail":
			//adminChat.sendAdminChat(sender.getName(), joinArguments(args) );
			return false;
		}
		return false;
	}
	
}
