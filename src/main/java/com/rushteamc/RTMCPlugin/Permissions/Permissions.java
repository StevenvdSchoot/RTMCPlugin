package com.rushteamc.RTMCPlugin.Permissions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.yaml.snakeyaml.Yaml;

import com.rushteamc.RTMCPlugin.RTMCPlugin;

public class Permissions
{
	private static File file;
	private static Map<String, Object> permissionData;
	private static Map<String,PermissionAttachment> permissionAttachments = new HashMap<String,PermissionAttachment>();
	private static String defaultGroup = null; // TODO: Read this value from permissions yaml
	
	public Permissions()
	{
		;
	}
	
	public static void init(RTMCPlugin rtmcplugin)
	{
		String permissionsFileName = "permissions.yml"; // TODO: make this configurable
		file = new File(rtmcplugin.getDataFolder(), permissionsFileName);
		if(file.isFile())
		{
			try {
				InputStream permissionsFile = new FileInputStream( file );
				Yaml permissionsYaml = new Yaml();
				for ( Object object : permissionsYaml.loadAll(permissionsFile) )
				{
					Map<String, Object> data = (Map<String, Object>)object;
					if(permissionData==null)
						permissionData = data;
					else
						permissionData.putAll(data); // TODO: Create a merger instead of putAll
					System.out.println(data);
					
					if(data.containsKey("groups"))
					{
						Map<String, Object> groups = (Map<String, Object>)data.get("groups");
						Iterator<Entry<String, Object>> it = groups.entrySet().iterator();
						while(it.hasNext())
						{
							Entry<String, Object> group = it.next();
							if(((Map<String, Object>) group.getValue()).containsKey("default"))
							{
								Object deflt = ((Map<String, Object>) group.getValue()).get("default");
								if(deflt instanceof Boolean)
									if((Boolean)deflt == true)
										if(defaultGroup==null)
											defaultGroup = group.getKey();
										else
											System.out.println("WARNING: Got multiple default groups! Group " + group.getKey() + " was assigned default, but already got " + defaultGroup + " as default group.");
							}
						}
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
		
	}
	
	private static Map<String, Object> mergeMaps(Map<String, Object> baseMap, Map<String, Object> additionalMap)
	{
		System.out.println(" * Merging " + additionalMap.toString() + " into " + baseMap.toString());
		Iterator<Entry<String, Object>> it = additionalMap.entrySet().iterator();
		while(it.hasNext())
		{
			Entry<String, Object> entry = it.next();
			if(entry.getValue() instanceof Map<?,?>)
			{
				System.out.println("Merging map "+entry.getKey());
				if(baseMap.containsKey(entry.getKey()))
				{
					Object origin = baseMap.get(entry.getKey());
					
					if(origin instanceof Map<?,?>)
					{
						baseMap.put(entry.getKey(), mergeMaps((Map<String, Object>)origin, (Map<String, Object>)entry.getValue()));
					}
					else
					{
						baseMap.put(entry.getKey(), entry.getValue());
					}
				}
				else
				{
					baseMap.put(entry.getKey(), entry.getValue());
				}
			}
			else if(entry.getValue() instanceof List<?>)
			{
				System.out.println("Merging list " + entry.getKey());
				if(baseMap.containsKey(entry.getKey()))
				{
					Object origin = baseMap.get(entry.getKey());
					
					if(origin instanceof List<?>)
					{
						System.out.println("Merging lists " + entry.getValue().toString() + " into  + " + origin.toString());
						List<Object> result = (List<Object>)origin; //.addAll( (Collection)((List<?>)entry.getValue()) );
						List<Object> value = (List<Object>)entry.getValue();
						for(int i = 0;i<value.size();i++)
						{
							System.out.println("i: " + i + " | size: " + value.size());
							Object obj = value.get(i);
							if(obj instanceof String)
							{
								String objString = (String)obj;
								if(objString.startsWith("-") )
									if( result.contains(objString.substring(objString.indexOf('-'))) )
									{
										System.out.println("Removing " + objString.substring(objString.indexOf('-')) + " as of " + objString);
										result.remove(objString.substring(objString.indexOf('-')));
									}
									else
									{
										System.out.println("Adding " + objString);
										result.add(objString);
									}
								else
									if( result.contains("-" + objString) )
									{
										System.out.println("Removing " + "-" + objString + " as of " + objString);
										result.remove("-" + objString);
									}
									else
									{
										System.out.println("Adding " + objString);
										result.add(objString);
									}
							}
							else
							{
								if(!result.contains(obj))
									result.add(obj); //.add(obj);
							}
						}
						baseMap.put(entry.getKey(), result);
					}
					else
					{
						baseMap.put(entry.getKey(), entry.getValue());
					}
				}
				else
				{
					baseMap.put(entry.getKey(), entry.getValue());
				}
			}
			else
			{
				System.out.println("Merging object "+entry.getKey());
				baseMap.put(entry.getKey(), entry.getValue());
			}
		}
		
		System.out.println(" * Merge result: " + baseMap.toString());
		return baseMap;
	}
	
	public static void updateUserPermissions(Player player)
	{
		updateUserPermissions(player.getName(), userDetails(player.getName()));
	}
	
	public static void updateUserPermissions(String userName)
	{
		updateUserPermissions(userName, userDetails(userName));
	}
	
	public static void updateUserPermissions(String userName, Map<String, Object> userDetails)
	{
		if(userDetails == null)
			return;
		
		if(!userDetails.containsKey("permissions"))
			return;
		
		Object permissionsObj = userDetails.get("permissions");
		if( !(permissionsObj instanceof List<?>) )
			return;
		List<String> permissions = (List<String>)permissionsObj;
		
		PermissionAttachment attachment = getPermissionAttachment(userName);

		Iterator<String> it = permissions.iterator();
		while(it.hasNext())
		{
			String perm = it.next();
			if(perm.startsWith("-"))
			{
				attachment.unsetPermission(perm.substring(perm.indexOf('-')));
				System.out.println("Removing permision " + perm.substring(perm.indexOf('-')) + " from player " + userName);
			}
			else
			{
				attachment.setPermission(perm, true);
				System.out.println("Adding permision " + perm + " to player " + userName);
			}
		}
	}
	
	public static Map<String, Object> userDetails(String userName)
	{
		if(!permissionData.containsKey("users"))
			return defaultPermissions();
		
		Map<String, Object> users = (Map<String, Object>)permissionData.get("users");
		
		if(!users.containsKey(userName))
			return defaultPermissions();
		
		Map<String, Object> user = (Map<String, Object>)users.get(userName);
		Map<String, Object> result = user;
		
		Player player = Bukkit.getPlayer(userName);
		String worldName = (player==null)?"":player.getWorld().getName();
		
		if(user.containsKey("group"))
		{
			List<String> groups = (List<String>)user.get("group");
			
			Iterator<String> it = groups.iterator();
			while(it.hasNext())
			{
				Map<String, Object> group = groupDetails(it.next());
				mergeMaps(result, group);
				if(group.containsKey("world"))
				{
					Map<String, Object> worlds = (Map<String, Object>) group.get("world");
					if(worlds.containsKey(worldName))
						; // TODO: Add world permissions
				}
			}
		}
		else if(defaultGroup != null)
		{
			mergeMaps(result, groupDetails(defaultGroup));
		}
		
		mergeMaps(result, user);
		return result;
	}
	
	public static Map<String, Object> groupDetails(String groupName)
	{
		if(!permissionData.containsKey("groups"))
			return null;
		
		Map<String, Object> groups = (Map<String, Object>)permissionData.get("groups");
		
		if(!groups.containsKey(groupName))
			return null;
		
		return (Map<String, Object>)groups.get(groupName);
	}
	
	private static Map<String, Object> defaultPermissions()
	{
		if(defaultGroup == null)
			return null;
		return groupDetails(defaultGroup);
	}
	
	private static PermissionAttachment getPermissionAttachment(String playerName)
	{
		if(permissionAttachments.containsKey(playerName))
			return (PermissionAttachment)permissionAttachments.get(playerName);
		

		PermissionAttachment attachment = Bukkit.getPlayer(playerName).addAttachment(RTMCPlugin.rtmcplugin);
		permissionAttachments.put(playerName, attachment);
		return attachment;
	}
	
}
