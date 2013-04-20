package com.rushteamc.RTMCPlugin.sync.message;

import com.rushteamc.RTMCPlugin.ChatManager.ChatManager;

public class ChatMessage extends Message
{
	private static final long serialVersionUID = 1L;
	
	private String message;
	
	public ChatMessage(String message)
	{
		this.message = message;
	}

	@Override
	public void run()
	{
		ChatManager.sendMessageWithouSync(message);
	}
	
}
