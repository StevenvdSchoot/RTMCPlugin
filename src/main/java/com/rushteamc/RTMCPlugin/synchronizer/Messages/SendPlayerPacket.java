package com.rushteamc.RTMCPlugin.synchronizer.Messages;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.server.v1_4_R1.*; // v1_4_R1 / v1_5_R3 / v1_6_R1

import com.rushteamc.RTMCPlugin.synchronizer.Synchronizer;

public class SendPlayerPacket extends Message
{
	private static final long serialVersionUID = 1L;

	private String playername;
	private byte packetData[];
	private int packetID;
	
	public SendPlayerPacket(String playername, Packet packet)
	{
		this.playername = playername;
		this.packetID = packet.k(); // k() / n() / n()

		ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
		try {
			packet.a(new DataOutputStream(dataStream));
			packetData = dataStream.toByteArray();
		} catch (IOException e) {
			packetData = new byte[0];
			e.printStackTrace();
		}
	}

	@Override
	public void run()
	{
		ByteArrayInputStream byteStream = new ByteArrayInputStream(packetData);
		DataInputStream dataStream = new DataInputStream(byteStream);
		//IConsoleLogManager iconsolelogmanager = null; // Only 1.5+
		Packet packet;
		try {
			packet = Packet.d(packetID); // d(packetID) / a(iconsolelogmanager, packetID) / a(iconsolelogmanager, packetID)
			if(packet == null)
				return;
			packet.a(dataStream);
			Synchronizer.sendPacketToPlayer(playername, packet);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
