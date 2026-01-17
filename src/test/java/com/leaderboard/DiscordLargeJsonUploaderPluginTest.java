package com.leaderboard;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class DiscordLargeJsonUploaderPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(DiscordLargeJsonUploaderPlugin.class);
		RuneLite.main(args);
	}
}