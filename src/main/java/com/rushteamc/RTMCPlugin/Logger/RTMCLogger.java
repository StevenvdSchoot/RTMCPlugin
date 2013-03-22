package com.rushteamc.RTMCPlugin.Logger;

import java.util.logging.Logger;

import org.bukkit.Bukkit;

public class RTMCLogger
{
	private static Logger logger;
	
	public static void info(String msg)
	{
		logger = Bukkit.getLogger();
		StackTraceElement stackTraceElement = getStackTraceElement();
		logger.info(((stackTraceElement==null)?("[RTMCPlugin][unknown source]: "):("[" + stackTraceElement.getClassName().replace("com.rushteamc.", "").replace(".", "][") + "]: ")) + msg);
	}
	
	public static StackTraceElement getStackTraceElement()
	{
		try {
			throw new Exception();
		} catch (Exception e) {
			StackTraceElement[] stackTrace = e.getStackTrace();
			if(stackTrace.length>2)
				return stackTrace[2];
		}
		return null;
	}
}
