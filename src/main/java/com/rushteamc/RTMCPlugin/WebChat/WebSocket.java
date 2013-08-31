package com.rushteamc.RTMCPlugin.WebChat;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import com.rushteamc.RTMCPlugin.Main;

public class WebSocket extends WebSocketServer
{
	/**
	 * TODO:
	 * 	1) Get the authNumber auth of the database.
	 *  2) Generate new auth number on disconnect (and put in database).
	 *  3) Cache auth numbers and offset for active connections.
	 *  4) spawn fakeplayer on connection open and despawn on connection close.
	 *  5) run user commands on fakeplayer.
	 *  6) handle errors properly.
	 *  7) write JavaScript side of the code :)
	 */
	private Map<String, AuthenticationNumber> authenticationNumbers = new HashMap<String, AuthenticationNumber>();
	
	public WebSocket(int port) throws UnknownHostException
	{
		super(new InetSocketAddress(Main.plugin.getConfig().getInt("webchat.port", 8000)));
	}

	@Override
	public void onOpen(org.java_websocket.WebSocket webSocket, ClientHandshake handshake)
	{
		System.out.println("[RTMCPlugin][WebChat]: " + "client " + webSocket.getRemoteSocketAddress().getAddress().getHostAddress() + " connected." );
	}

	@Override
	public void onClose(org.java_websocket.WebSocket webSocket, int code, String reason, boolean remote)
	{
		System.out.println("[RTMCPlugin][WebChat]: " + "client " + webSocket.getRemoteSocketAddress().getAddress().getHostAddress()  + (remote?" disconnected":" was disconnected by server") + ". ({" + code + "}" + reason + ")" );
	}

	@Override
	public void onMessage(org.java_websocket.WebSocket webSocket, String message)
	{
		
		JSONObject result = new JSONObject();
		AuthenticationNumber authenticationNumber;
		
		try {
			StringAuthenticationNumber data = authenticate(webSocket, message);
			authenticationNumber = data.authenticationNumber;
			
			// TODO: run the command in data.string
			
			result.element("error", 0);
		} catch (AuthenticationException e) {
			authenticationNumber = e.getAuthenticationNumber();
			result.element("error", e.getCode());
			e.printStackTrace();
		}
		result.element("authOffset", authenticationNumber.offset);
		
		webSocket.send(result.toString());
	}
	
	private StringAuthenticationNumber authenticate(org.java_websocket.WebSocket webSocket, String message) throws AuthenticationException
	{
		try
		{
			JSONObject messageData = JSONObject.fromObject(message);
			if( messageData.containsKey("user") && messageData.containsKey("message") && messageData.containsKey("hash") )
			{
				String userStr = messageData.getString("user");
				String messageStr = messageData.getString("message");
				String hashStr = messageData.getString("hash");
				
				AuthenticationNumber authenticationNumber = authenticationNumbers.get(userStr);
				if(authenticationNumber == null)
					throw new AuthenticationException(4, webSocket, authenticationNumber);
				
				if( hash(userStr + String.valueOf(authenticationNumber.number + authenticationNumber.offset) + messageStr) != hashStr)
					throw new AuthenticationException(3, webSocket, authenticationNumber);
				
				authenticationNumber.offset++;
				authenticationNumbers.put(userStr, authenticationNumber);
				
				return new StringAuthenticationNumber(messageStr, authenticationNumber);
			}
			else
			{
				throw new AuthenticationException(2, webSocket);
			}
		}
		catch (JSONException e)
		{
			System.out.println("[RTMCPlugin][WebChat]: " + "got invalid message from client " + webSocket.getRemoteSocketAddress().getAddress().getHostAddress());
			throw new AuthenticationException(1, webSocket);
		}
	}
	
	private String hash(String str)
	{
		// TODO: Return actual hash..
		return "";
	}

	@Override
	public void onError(org.java_websocket.WebSocket webSocket, Exception arg1)
	{
		// TODO Auto-generated method stub
		
	}
	
	private final class AuthenticationNumber
	{
		public long number;
		public long offset;
	}
	
	private final class StringAuthenticationNumber
	{
		public String string;
		public AuthenticationNumber authenticationNumber;
		
		public StringAuthenticationNumber(String string, AuthenticationNumber authenticationNumber)
		{
			this.string = string;
			this.authenticationNumber = authenticationNumber;
		}
	}
	
	private final class AuthenticationException extends Exception
	{
		private static final long serialVersionUID = 1L;
		
		private Integer code = 0; 
		private AuthenticationNumber authenticationNumber = null;
		
		public AuthenticationException(Integer code, org.java_websocket.WebSocket webSocket)
		{
			super("User " + webSocket.getRemoteSocketAddress().getAddress().getHostAddress() + " could not authenticate.");
			this.code = code;
		}
		
		public AuthenticationException(Integer code, org.java_websocket.WebSocket webSocket, AuthenticationNumber authenticationNumber)
		{
			this(code, webSocket);
			this.authenticationNumber = authenticationNumber;
		}

		public Integer getCode() {
			return code;
		}
		
		public AuthenticationNumber getAuthenticationNumber() {
			return authenticationNumber;
		}
	}
	
}
