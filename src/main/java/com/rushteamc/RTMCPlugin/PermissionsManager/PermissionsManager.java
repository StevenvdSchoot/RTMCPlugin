package com.rushteamc.RTMCPlugin.PermissionsManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.Plugin;
import org.yaml.snakeyaml.Yaml;

import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import com.rushteamc.RTMCPlugin.RTMCPlugin;
import com.rushteamc.RTMCPlugin.ChatManager.EventListener;

public class PermissionsManager
{
	private static PermissionManager permissions;
	private static File file;
	private static Map<String,PermissionAttachment> permissionAttachments = new HashMap<String,PermissionAttachment>();
	
	private static String defaultGroup = null;
	private static Map<String, details> players = new HashMap<String, details>();
	private static Map<String, details> groups = new HashMap<String, details>();
	
	public static boolean managePermissions = false;
	
	public static void init()
	{
		Plugin plugin = Bukkit.getPluginManager().getPlugin("PermissionsEx");
		if(plugin != null)
		{
			System.out.println("Detected PeX");
			managePermissions = false;
			permissions = PermissionsEx.getPermissionManager();
		}
		else
		{
			System.out.println("No compatible permission manager found, using owne permissions manager!");
			managePermissions = true;
			reloadPermissions();
			RTMCPlugin.rtmcplugin.getServer().getPluginManager().registerEvents(new EventListener(), RTMCPlugin.rtmcplugin);
		}
	}
	
	public static String[] getPlayerGroups(String playername)
	{
		Player player = Bukkit.getPlayer(playername);
		return (player==null)?new String[0]:getPlayerGroups(player);
	}
	
	public static String[] getPlayerGroups(Player player)
	{
		if(managePermissions)
		{
			details details = getPlayerDetails(player);
			if(details==null)
				return new String[0];
			if (details.parents==null)
				return new String[]{defaultGroup};
			try {
				return (String[])details.parents.toArray();
			} catch(ClassCastException e) {
				return new String[]{defaultGroup};
			}
		}
		else
		{
			PermissionUser user = permissions.getUser(player.getName());
			if(user==null)
				return new String[0];
			
			List<PermissionGroup> groupList = new LinkedList<PermissionGroup>();
			
			Map<String, PermissionGroup[]> groups = user.getAllGroups();
			PermissionGroup[] permissionGroups = groups.get(null);
			for(PermissionGroup permissionGroup : permissionGroups)
			{
				groupList.add(permissionGroup);
			}
			if(groups.containsKey(player.getWorld().getName()))
			{
				permissionGroups = groups.get(player.getWorld().getName());
				for(PermissionGroup permissionGroup : permissionGroups)
				{
					if(!groupList.contains(permissionGroup.getName()))
						groupList.add(permissionGroup);
				}
			}
			
			String[] result = new String[groupList.size()];
			int rank;
			int use;
			for(int i = 0;i<result.length;i++)
			{
				rank = 0;
				use = 0;
				for(int i2 = 0;i2<groupList.size();i2++)
				{
					if(groupList.get(i2).getRank() > rank)
					{
						use = i2;
					}
				}
				result[i] = groupList.get(use).getName();
				groupList.remove(use);
			}
			
			return result;
		}
	}
	
	public static Map<String, String> getPlayerOptions(String playername)
	{
		Player player = Bukkit.getPlayer(playername);
		return (player==null)?new HashMap<String, String>():getPlayerOptions(player);
	}
	
	public static Map<String, String> getPlayerOptions(Player player)
	{
		System.out.println("managePermissions = " + ((managePermissions)?"true":"false"));
		if(managePermissions)
		{
			details details = getPlayerDetails(player);
			if(details==null)
				return new HashMap<String, String>();
			return (details.options==null)?new HashMap<String, String>():details.options;
		}
		else
		{
			PermissionUser user = permissions.getUser(player.getName());
			if(user==null)
				return new HashMap<String, String>();
			
			Map<String, String> result;
			
			Map<String, Map<String, String>> options = user.getAllOptions();
			if(options.containsKey(player.getWorld().getName()))
			{
				result = mergeMap(options.get(player.getWorld().getName()), options.get(null));
			}
			else
			{
				result = options.get(null);
			}
			
			String tmp = user.getPrefix();
			if(tmp!=null)
			{
				System.out.println("Prefix = " + tmp);
				result.put("prefix", tmp);
			}
			else
			{
				System.out.println("No prefix");
			}
			
			tmp = user.getSuffix();
			if(tmp!=null)
				result.put("suffix", tmp);
			
			return result;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void reloadPermissions()
	{
		String permissionsFileName = "permissions.yml"; // TODO: make this configurable
		file = new File(RTMCPlugin.rtmcplugin.getDataFolder(), permissionsFileName);
		if(file.isFile())
		{
			try {
				InputStream permissionsFile = new FileInputStream( file );
				Yaml permissionsYaml = new Yaml();
				for ( Object object : permissionsYaml.loadAll(permissionsFile) )
				{
					if(object instanceof Map<?,?>)
					{
						Map<String, Object> data = (Map<String, Object>)object;
						
						if(data.containsKey("users"))
						{
							Object obj = data.get("users");
							if(obj instanceof Map<?,?>)
							{
								Map<String, Object> users = (Map<String, Object>)obj;
								
								Iterator<Entry<String, Object>> it = users.entrySet().iterator();
								while(it.hasNext())
								{
									Entry<String, Object> entry = it.next();
									if( !(entry.getValue() instanceof Map<?,?>) )
										continue;
									
									Map<String, Object> map = (Map<String, Object>)entry.getValue() ;
									PermissionsManager.players.put(entry.getKey(), readUserDetails(map));
								}
							}
							else
							{
								; // TODO: Notify user of malicious permission file.
							}
						}
						
						if(data.containsKey("groups"))
						{
							Object obj = data.get("groups");
							if(obj instanceof Map<?,?>)
							{
								Map<String, Object> groups = (Map<String, Object>)obj;
								
								Iterator<Entry<String, Object>> it = groups.entrySet().iterator();
								while(it.hasNext())
								{
									Entry<String, Object> entry = it.next();
									if( !(entry.getValue() instanceof Map<?,?>) )
										continue;
									
									Map<String, Object> map = (Map<String, Object>)entry.getValue() ;
									
									details details = new details();
									
									// TODO: Change to iteration over data. "permissions" and "groups" are special cases, all other data should be added to options.
									
									Iterator<Entry<String, Object>> it2 = map.entrySet().iterator();
									while(it2.hasNext())
									{
										Entry<String, Object> entry2 = it2.next();
										Object tmp;
										switch(entry2.getKey())
										{
										case "permissions":
											tmp = entry2.getValue();
											if(tmp instanceof List<?>)
											{
												details.permissions = new ArrayList<String>();
												Iterator<String> it3 = ((List<String>)tmp).iterator();
												while(it3.hasNext())
												{
													List<String> tmp3 = permissionSplit(it3.next());
													Iterator<String> it4 = tmp3.iterator();
													while(it4.hasNext())
													{
														details.permissions.add(it4.next());
													}
												}
											}
											else
											{
												; // TODO: Notify user of malicious permission file.
											}
											break;
										case "inheritance":
											tmp = entry2.getValue();
											if(tmp instanceof List<?>)
												details.parents = (List<String>) tmp;
											else
												; // TODO: Notify user of malicious permission file.
											break;
										case "default":
											PermissionsManager.defaultGroup = entry2.getKey();
											break;
										case "options":
											tmp = entry2.getValue();
											if(details.options==null)
												details.options = new HashMap<String, String>();
											if(tmp instanceof Map<?,?>)
											{
												Iterator<Entry<String, String>> it3 = ((Map<String, String>)tmp).entrySet().iterator();
												while(it3.hasNext())
												{
													Entry<String, String> option = it3.next();
													Object value = option.getValue();
													if(value instanceof String)
													{
														details.options.put(option.getKey(), (String)value);
													}
													else
													{
														details.options.put(entry.getKey(), value.toString());
													}
												}
											}
											break;
										default:
											if(details.options==null)
												details.options = new HashMap<String, String>();
											Object value = entry.getValue();
											if(value instanceof String)
											{
												details.options.put(entry.getKey(), (String)value);
											}
											else
											{
												details.options.put(entry.getKey(), value.toString());
											}
										}
									}
									
									PermissionsManager.groups.put(entry.getKey(), details);
								}
							}
							else
							{
								; // TODO: Notify user of malicious permission file.
							}
						}
						
					}
					else
					{
						; // TODO: Notify user of malicious permission file.
					}
					
				}
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		;
		
	}
	
	@SuppressWarnings("unchecked")
	private static details readUserDetails(Map<String, Object> map)
	{
		details details = new details();
				
		Iterator<Entry<String, Object>> it = map.entrySet().iterator();
		while(it.hasNext())
		{
			Entry<String, Object> entry = it.next();
			Object tmp;
			switch(entry.getKey())
			{
			case "permissions":
				tmp = entry.getValue();
				if(tmp instanceof List<?>)
				{
					try {
						details.permissions = new ArrayList<String>();
						Iterator<String> it3 = ((List<String>)tmp).iterator();
						while(it3.hasNext())
						{
							List<String> tmp3 = permissionSplit(it3.next());
							Iterator<String> it4 = tmp3.iterator();
							while(it4.hasNext())
							{
								details.permissions.add(it4.next());
							}
						}
					} catch(ClassCastException e) {
						// TODO: Notify user of malicious permission file.
						e.printStackTrace();
					}
				}
				else
				{
					; // TODO: Notify user of malicious permission file.
				}
				break;
			case "group":
				tmp = entry.getValue();
				if(tmp instanceof List<?>)
				{
					try {
						details.parents = (List<String>) tmp;
					} catch(ClassCastException e) {
						// TODO: Notify user of malicious permission file.
						e.printStackTrace();
					}
				}
				else
					; // TODO: Notify user of malicious permission file.
				break;
			case "worlds":
				details.worlds = new HashMap<String, details>();
				if( !(entry.getValue() instanceof Map<?,?>) )
				{
					// TODO: Inform user of malicious permissions file.
					break;
				}
				Iterator<Entry<String,Object>> it2 = ((Map<String,Object>)entry.getValue()).entrySet().iterator();
				while(it2.hasNext())
				{
					Entry<String,Object> entry2 = it2.next();
					if(entry.getValue() instanceof Map<?,?>)
					{
						try {
							details.worlds.put(entry2.getKey(), readUserDetails((Map<String,Object>)entry2.getValue()));
						} catch(ClassCastException e) {
							// TODO: Notify user of malicious permission file.
							e.printStackTrace();
						}
					}
					else
						; // TODO: Inform user of malicious permissions file.
				}
				break;
			case "options":
				tmp = entry.getValue();
				if(tmp instanceof Map<?,?>)
				{
					if(details.options==null)
						details.options = new HashMap<String, String>();
					
					Iterator<Entry<String, String>> it3 = ((Map<String, String>)tmp).entrySet().iterator();
					while(it3.hasNext())
					{
						String newOption;
						Entry<String, String> obj = it3.next();
						Object value = obj.getValue();
						if(value instanceof String)
							newOption = (String)value;
						else
							newOption = value.toString();
						details.options.put(obj.getKey(), newOption);
					}
				}
				break;
			default:
				if(details.options==null)
					details.options = new HashMap<String, String>();
				String newOption;
				Object value = entry.getValue();
				if(value instanceof String)
					newOption = (String)value;
				else
				{
					newOption = value.toString();
				}
				details.options.put(entry.getKey(), newOption);
			}
		}
		return details;
	}
	
	private static details mergeDetails(details baseDetails, details additionalDetails)
	{		
		details result = new PermissionsManager.details();
		
		if(baseDetails.parents == null)
		{
			if(additionalDetails.parents != null)
			{
				result.parents = additionalDetails.parents;
			}
		}
		else
		{
			if(additionalDetails.parents == null)
			{
				result.parents = baseDetails.parents;
			}
			else
			{
				result.parents = mergeLists(baseDetails.parents,additionalDetails.parents);
			}
		}
		
		if(baseDetails.permissions == null)
		{
			if(additionalDetails.permissions != null)
			{
				result.permissions = additionalDetails.permissions;
			}
		}
		else
		{
			if(additionalDetails.permissions == null)
			{
				result.permissions = baseDetails.permissions;
			}
			else
			{
				result.permissions = mergePermissions(baseDetails.permissions,additionalDetails.permissions);
			}
		}
		
		if(baseDetails.options == null)
		{
			if(additionalDetails.options != null)
			{
				result.options = additionalDetails.options;
			}
		}
		else
		{
			if(additionalDetails.options == null)
			{
				result.options = baseDetails.options;
			}
			else
			{
				result.options =  mergeMap(baseDetails.options, additionalDetails.options);
			}
		}
		
		if(baseDetails.worlds == null)
		{
			if(additionalDetails.worlds != null)
			{
				result.worlds = additionalDetails.worlds;
			}
		}
		else
		{
			if(additionalDetails.worlds == null)
			{
				result.worlds = baseDetails.worlds;
			}
			else
			{
				result.worlds = baseDetails.worlds;
				Iterator<Entry<String,details>> it = additionalDetails.worlds.entrySet().iterator();
				while(it.hasNext())
				{
					Entry<String, details> entry = it.next();
					if(result.worlds.containsKey(entry.getKey()))
					{
						result.worlds.put(entry.getKey(), mergeDetails(result.worlds.get(entry.getKey()),entry.getValue()));
					}
					else
					{
						result.worlds.put(entry.getKey(), entry.getValue());
					}
				}
			}
		}
		
		return result;
	}
	
	private static List<String> mergeLists(List<String> baseList, List<String> additionalList)
	{
		List<String> result = new ArrayList<String>();
		result.addAll(baseList);
		Iterator<String> it = additionalList.iterator();
		while(it.hasNext())
		{
			String currentString = it.next();
			if(!baseList.contains(currentString))
				result.add(currentString);
		}
		return result;
	}
	
	private static List<String> mergePermissions(List<String> baseList, List<String> additionalList)
	{		
		Iterator<String> it = additionalList.iterator();
		while(it.hasNext())
		{
			String currentString = it.next();
			if(currentString.startsWith("-"))
			{
				String currentContraString = currentString.substring(currentString.indexOf('-'));
				if(baseList.contains(currentContraString))
				{
					baseList.remove(currentContraString);
					baseList.add(currentString);
				}
				else
				{
					baseList.add(currentString);
				}
			}
			else
			{
				String currentContraString = "-" + currentString;
				if(baseList.contains(currentContraString))
				{
					baseList.remove(currentContraString);
					baseList.add(currentString);
				}
				else
				{
					baseList.add(currentString);
				}
			}
		}
		return baseList;
	}
	
	private static Map<String, String> mergeMap(Map<String, String> baseList, Map<String, String> additionalList)
	{
		Iterator<Entry<String, String>> it = additionalList.entrySet().iterator();
		while(it.hasNext())
		{
			Entry<String, String> entry = it.next();
			baseList.put(entry.getKey(),entry.getValue());
		}
		return baseList;
	}
	
	public static void test(String playername)
	{
		//System.out.println( getPlayerDetails(playername,"&a").toString() );
		updatePlayer(playername);
		System.out.println("PeX user options: " + getPlayerOptions(playername));
	}
	
	public static void updatePlayer(Player player)
	{
		updatePlayer(player, player.getName());
	}
	
	public static void updatePlayer(String playername)
	{
		Player player = Bukkit.getPlayer(playername);
		if(player!=null)
			updatePlayer(player, playername);
	}
	
	private static void updatePlayer(Player player, String playername)
	{
		details details = getPlayerDetails(player);
		
		if(details.permissions==null)
			return;
		
		PermissionAttachment attachment = getPermissionAttachment(playername);

		Iterator<String> it = details.permissions.iterator();
		while(it.hasNext())
		{
			String perm = it.next();
			if(perm.startsWith("-"))
			{
				attachment.setPermission(perm.substring(perm.indexOf('-')), false);
				//System.out.println("Removing permission " + perm.substring(perm.indexOf('-')) + " from player " + playername);
			}
			else
			{
				attachment.setPermission(perm, true);
				//System.out.println("Adding permission " + perm + " to player " + playername);
			}
		}
		
		String playerDisplayName = player.getName();
		
		if(details.options.containsKey("prefix"))
			playerDisplayName = details.options.get("prefix").replace('&', ChatColor.COLOR_CHAR) + playerDisplayName;
			
		if(details.options.containsKey("suffix"))
			playerDisplayName = playerDisplayName + details.options.get("suffix").replace('&', ChatColor.COLOR_CHAR);
		
		player.setDisplayName(playerDisplayName);
		
	}
	
	private static details getPlayerDetails(Player player)
	{
		return getPlayerDetails(player.getName(),player.getWorld().getName());
	}
	
	private static details getPlayerDetails(String playername, String worldname)
	{
		details details = getPlayerDetailsRaw(playername);
		if(details.worlds != null)
		{
			if(details.worlds.containsKey(worldname))
				details = mergeDetails(details,details.worlds.get(worldname));
		}
		return details;
	}
	
	private static details getPlayerDetailsRaw(String playername)
	{
		details result = new details();
		
		if(!players.containsKey(playername))
			return result;
		
		details player = players.get(playername);
		
		if(player.parents != null)
		{
			Iterator<String> it = player.parents.iterator();
			while(it.hasNext())
				result = mergeDetails(result,getGroupDetails(it.next()));
		}
		else if( defaultGroup != null )
		{
			result = getGroupDetails(defaultGroup);
		}
		
		return mergeDetails(result,player);
	}

	private static details getGroupDetails(String groupname)
	{
		details result = new details();
		
		if(!groups.containsKey(groupname))
			return result;
		
		details group = groups.get(groupname);
		
		if(group.parents!=null)
		{
			Iterator<String> it = group.parents.iterator();
			while(it.hasNext())
			{
				String inheritGroup = it.next();
				if( inheritGroup == groupname ) // Believe me, there are some noobs...
					continue;
				result = mergeDetails(result,getGroupDetails(inheritGroup));
			}
		}
		
		return mergeDetails(result,group);
	}
		
	private static PermissionAttachment getPermissionAttachment(String playerName)
	{
		if(permissionAttachments.containsKey(playerName))
			return (PermissionAttachment)permissionAttachments.get(playerName);
		

		PermissionAttachment attachment = Bukkit.getPlayer(playerName).addAttachment(RTMCPlugin.rtmcplugin);
		permissionAttachments.put(playerName, attachment);
		return attachment;
	}

	public static void releasePlayer(Player player)
	{
		releasePlayer(player, player.getName());
	}

	public static void releasePlayer(String playername)
	{
		Player player = Bukkit.getPlayer(playername);
		if(player != null)
			releasePlayer(player, playername);
	}

	private static void releasePlayer(Player player, String playername)
	{
		if(permissionAttachments.containsKey(playername))
		{
			PermissionAttachment attachment = (PermissionAttachment)permissionAttachments.get(playername);
			player.removeAttachment(attachment);
			permissionAttachments.remove(attachment);
		}
	}
	
	private static List<String> permissionSplit(String permission)
	{
		return permissionSplitHelper(permission.split("\\."));
	}
	
	private static List<String> permissionSplitHelper(String[] permissionArray)
	{
		List<String> result = new ArrayList<String>();
		String permPart = (permissionArray[0]).trim();
		if( permPart.startsWith("(") && permPart.endsWith(")") )
		{
			permPart = permPart.substring(1, permPart.length()-1);
			String[] permArray = permPart.split("\\|");
			if(permissionArray.length>1)
			{
				for( String perm : permArray )
				{
					List<String> newPermissions = permissionSplitHelper( Arrays.copyOfRange(permissionArray, 1, permissionArray.length) );
					Iterator<String> it = newPermissions.iterator();
					while(it.hasNext())
					{
						result.add( perm + "." + it.next());
					}
				}
			}
			else
			{
				for( String perm : permArray )
				{
					result.add(perm);
				}
			}
		}
		else
		{
			if(permissionArray.length>1)
			{
				List<String> newPermissions = permissionSplitHelper( Arrays.copyOfRange(permissionArray, 1, permissionArray.length) );
				Iterator<String> it = newPermissions.iterator();
				while(it.hasNext())
				{
					result.add( permPart + "." + it.next());
				}
			}
			else
			{
				result.add(permPart);
			}
		}
		return result;
	}
	
	private static class details
	{
		public List<String> parents;
		public List<String> permissions;
		public Map<String, String> options;
		public Map<String, details> worlds;
		
		public details()
		{
			this.parents = null;
			this.permissions = null;
			this.options = null;
			this.worlds = null;
		}
		
		@SuppressWarnings("unused")
		public details(List<String> parents, List<String> permissions, Map<String, String> options, Map<String, details> worlds)
		{
			this.parents = parents;
			this.permissions = permissions;
			this.options = options;
			this.worlds = worlds;
		}
		
		public String toString()
		{
			return "{parents=" + ((this.parents==null)?"null":this.parents.toString()) + ", permissions=" + ((this.permissions==null)?"null":this.permissions.toString()) + ", options=" + ((this.options==null)?"null":this.options.toString()) + ", worlds=" + ((this.worlds==null)?"null":this.worlds.toString()) + "}";
		}
	}
	
}
