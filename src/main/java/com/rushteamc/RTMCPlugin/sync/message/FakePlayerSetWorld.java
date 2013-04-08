package com.rushteamc.RTMCPlugin.sync.message;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_5_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_5_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class FakePlayerSetWorld implements Message
{
	private static final long serialVersionUID = 1L;
	
	private String playername;
	private String worldname;
	
	public FakePlayerSetWorld(String playername, String worldname)
	{
		this.playername = playername;
		this.worldname = worldname;
	}
	
	@Override
	public void execute()
	{
		Player player = Bukkit.getPlayer(playername);
		if(player != null)
		{
			World world = Bukkit.getWorld(worldname);
			if(world != null)
				((CraftPlayer)player).getHandle().world = ((CraftWorld)world).getHandle();
		}
	}
	
}
