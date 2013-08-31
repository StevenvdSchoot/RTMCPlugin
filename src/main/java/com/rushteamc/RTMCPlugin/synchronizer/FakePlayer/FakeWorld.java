package com.rushteamc.RTMCPlugin.synchronizer.FakePlayer;

import net.minecraft.server.v1_4_R1.*;  // v1_4_R1 / v1_5_R3 / v1_6_R1

import org.bukkit.craftbukkit.v1_4_R1.*; // v1_4_R1 / v1_5_R3 / v1_6_R1
import org.bukkit.generator.ChunkGenerator;

public class FakeWorld extends CraftWorld
{
	public FakeWorld(WorldServer world, ChunkGenerator gen, Environment env)
	{
		super(world, gen, env);
	}
	
}
