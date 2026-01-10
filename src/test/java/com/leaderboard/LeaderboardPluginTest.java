package com.leaderboard;

import com.leaderboard.LeaderboardPlugin;
import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class LeaderboardPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(LeaderboardPlugin.class);
		RuneLite.main(args);
	}
}