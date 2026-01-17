package com.leaderboard;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("leaderboard")
public interface DiscordLargeJsonUploaderConfig extends Config
{
	@ConfigItem(
		keyName = "webhook",
		name = "Webhook URL",
		description = "The Discord Webhook URL to send messages to"
	)
	String webhook();
}
