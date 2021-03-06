package com.rushteamc.RTMCPlugin.synchronizer.FakePlayer;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import net.minecraft.server.v1_4_R1.*;  // v1_4_R1 / v1_5_R3 / v1_6_R1

public class FakeINetworkManager implements INetworkManager
{
	/**
	 * An entirely empty network manager, to prevent any real communication.
	 */

	@Override
	public void a() {
		;
	}

	@Override
	public void a(Connection arg0) {
		;
	}

	@Override
	public void a(String arg0, Object... arg1) {
		;
	}

	@Override
	public void b() {
		;
	}

	@Override
	public void d() {
		;
	}

	@Override
	public int e() {
		return 0;
	}

	@Override
	public SocketAddress getSocketAddress() {
		return new InetSocketAddress("0.0.0.0", 0);
	}

	@Override
	public void queue(Packet arg0) {
		;
	}
}
