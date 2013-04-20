package com.rushteamc.RTMCPlugin.sync.message;

import com.rushteamc.RTMCPlugin.ChatManager.ChatManager;

public class ChatMessage_Permmissions extends Message
{
	private static final long serialVersionUID = 1L;
	
	private String message;
	private String[] permmisions;
	
	public ChatMessage_Permmissions(String message, String[] permmisions)
	{
		this.message = message;
		this.permmisions = permmisions;
	}

	@Override
	public void run()
	{
		ChatManager.sendMessageWithouSync(message, permmisions);
	}
	
}