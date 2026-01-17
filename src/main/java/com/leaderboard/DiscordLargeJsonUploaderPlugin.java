package com.leaderboard;

import com.google.common.base.Splitter;
import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import okhttp3.*;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.Instant;
import java.util.List;

import static net.runelite.http.api.RuneLiteAPI.GSON;

@Slf4j
@PluginDescriptor(
	name = "Discord Boss KC JSON Uploader"
)
public class DiscordLargeJsonUploaderPlugin extends Plugin
{
	private static final String[] bossNames = {"Corporeal Beast",
			"TzTok-Jad",
			"Kalphite Queen",
			"Chaos Elemental",
			"Grotesque Guardians",
			"Crazy Archaeologist",
			"Deranged Archaeologist",
			"Giant Mole",
			"Vet'ion",
			"Calvar'ion",
			"Venenatis",
			"King Black Dragon",
			"Vorkath",
			"Abyssal Sire",
			"Thermonuclear Smoke Devil",
			"Cerberus",
			"TzKal-Zuk",
			"Alchemical Hydra",
			"Commander Zilyana",
			"K'ril Tsutsaroth",
			"Kree'arra",
			"General Graardor",
			"Dagannoth Supreme",
			"Dagannoth Rex",
			"Dagannoth Prime",
			"Wintertodt",
			"Barrows Chests",
			"Herbiboar",

			//Chambers of Xeric
			"Chambers of Xeric",
			"Chambers of Xeric Solo",
			"Chambers of Xeric 2 players",
			"Chambers of Xeric 3 players",
			"Chambers of Xeric 4 players",
			"Chambers of Xeric 5 players",
			"Chambers of Xeric 6 players",
			"Chambers of Xeric 7 players",
			"Chambers of Xeric 8 players",
			"Chambers of Xeric 9 players",
			"Chambers of Xeric 10 players",
			"Chambers of Xeric 11-15 players",
			"Chambers of Xeric 16-23 players",
			"Chambers of Xeric 24+ players",

			//Chambers of Xeric Challenge Mode
			"Chambers of Xeric Challenge Mode",
			"Chambers of Xeric Challenge Mode Solo",
			"Chambers of Xeric Challenge Mode 2 players",
			"Chambers of Xeric Challenge Mode 3 players",
			"Chambers of Xeric Challenge Mode 4 players",
			"Chambers of Xeric Challenge Mode 5 players",
			"Chambers of Xeric Challenge Mode 6 players",
			"Chambers of Xeric Challenge Mode 7 players",
			"Chambers of Xeric Challenge Mode 8 players",
			"Chambers of Xeric Challenge Mode 9 players",
			"Chambers of Xeric Challenge Mode 10 players",
			"Chambers of Xeric Challenge Mode 11-15 players",
			"Chambers of Xeric Challenge Mode 16-23 players",
			"Chambers of Xeric Challenge Mode 24+ players",

			//TOB
			"Theatre of Blood",
			"Theatre of Blood Solo",
			"Theatre of Blood 2 players",
			"Theatre of Blood 3 players",
			"Theatre of Blood 4 players",
			"Theatre of Blood 5 players",
			"Theatre of Blood Entry Mode",
			"Theatre of Blood Hard Mode Solo",
			"Theatre of Blood Hard Mode 2 players",
			"Theatre of Blood Hard Mode 3 players",
			"Theatre of Blood Hard Mode 4 players",
			"Theatre of Blood Hard Mode 5 players",

			//TOA
			"Tombs of Amascut",
			"Tombs of Amascut Solo",
			"Tombs of Amascut 2 players",
			"Tombs of Amascut 3 players",
			"Tombs of Amascut 4 players",
			"Tombs of Amascut 5 players",
			"Tombs of Amascut 6 players",
			"Tombs of Amascut 7 players",
			"Tombs of Amascut 8 players",
			"Tombs of Amascut Entry Mode",
			"Tombs of Amascut Entry Mode Solo",
			"Tombs of Amascut Entry Mode 2 players",
			"Tombs of Amascut Entry Mode 3 players",
			"Tombs of Amascut Entry Mode 4 players",
			"Tombs of Amascut Entry Mode 5 players",
			"Tombs of Amascut Entry Mode 6 players",
			"Tombs of Amascut Entry Mode 7 players",
			"Tombs of Amascut Entry Mode 8 players",
			"Tombs of Amascut Expert Mode",
			"Tombs of Amascut Expert Mode Solo",
			"Tombs of Amascut Expert Mode 2 players",
			"Tombs of Amascut Expert Mode 3 players",
			"Tombs of Amascut Expert Mode 4 players",
			"Tombs of Amascut Expert Mode 5 players",
			"Tombs of Amascut Expert Mode 6 players",
			"Tombs of Amascut Expert Mode 7 players",
			"Tombs of Amascut Expert Mode 8 players",
			"Gauntlet",
			"Corrupted Gauntlet",
			"Nightmare",
			"Phosani's Nightmare",

			//Hallowed Sepulcher is a boss, right?
			"Hallowed Sepulchre",
			"Hallowed Sepulchre Floor 1",
			"Hallowed Sepulchre Floor 2",
			"Hallowed Sepulchre Floor 3",
			"Hallowed Sepulchre Floor 4",
			"Hallowed Sepulchre Floor 5",
			"Guardians of the Rift",
			"Tempoross",
			"Phantom Muspah",

			//dt2 bosses
			"Leviathan",
			"Duke Sucellus",
			"Whisperer",
			"Vardorvis",
			"Leviathan (awakened)",
			"Duke Sucellus (awakened)",
			"Whisperer (awakened)",
			"Vardorvis (awakened)",

			//Varlamore bosses
			"Lunar Chest",
			"Sol Heredit",
			"Colosseum Glory",
			"Amoxliatl",
			"Hueycoatl",
			"Doom of Mokhaiotl"
	};

	private DiscordLargeJsonUploaderPanel panel;
	private NavigationButton navButton;

	@Inject
	private Client client;

	@Inject
	private DiscordLargeJsonUploaderConfig config;

	@Inject
	private OkHttpClient okHttpClient;

	@Inject
	private ConfigManager configManager;

	@Inject
	private ClientToolbar clientToolbar;

	@Override
	protected void startUp() throws Exception
	{
		panel = new DiscordLargeJsonUploaderPanel(config);
		final BufferedImage icon = ImageUtil.loadImageResource(DiscordLargeJsonUploaderPlugin.class, "/leaderboard_panel_icon.png");
		navButton = NavigationButton.builder()
				.tooltip("Tile Packs")
				.icon(icon)
				.priority(7)
				.panel(panel)
				.build();
		clientToolbar.addNavigation(navButton);
	}

	@Override
	protected void shutDown() throws Exception
	{
	}

	@Provides
	DiscordLargeJsonUploaderConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(DiscordLargeJsonUploaderConfig.class);
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGIN_SCREEN)
		{
			String leaderboard = compileLeaderboard();
			WebhookBody webhookBody = new WebhookBody();
			webhookBody.setContent(client.getLocalPlayer().getName());
			sendWebhook(webhookBody);
		}
	}

	private String compileLeaderboard()
	{
		StringBuilder leaderboard = new StringBuilder();
		leaderboard.append("{");
        for (String bossName : bossNames) {
            leaderboard.append("\"").append(bossName).append("\": {");
            leaderboard.append("\"count\": ").append(getKc(bossName)).append(",");
            leaderboard.append("\"pb\": ").append(getPb(bossName)).append("},");
        }
		leaderboard.setLength(leaderboard.length() - 1); //Removes the last comma
		leaderboard.append("}"); //Cap off the JSON
		return leaderboard.toString();
	}

	private int getKc(String boss)
	{
		Integer killCount = configManager.getRSProfileConfiguration("killcount", boss.toLowerCase(), int.class);
		return killCount == null ? 0 : killCount;
	}

	private double getPb(String boss)
	{
		Double personalBest = configManager.getRSProfileConfiguration("personalbest", boss.toLowerCase(), double.class);
		return personalBest == null ? 0 : personalBest;
	}
	
	private void sendWebhook(WebhookBody webhookBody)
	{
		MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder()
				.setType(MultipartBody.FORM)
				.addFormDataPart("payload_json", GSON.toJson(webhookBody))
				.addFormDataPart("file",String.format("%s-%s.json", webhookBody.getContent(), Instant.now().toString()),
						RequestBody.create(MediaType.parse("application/json"),compileLeaderboard()));

		MultipartBody requestBody = requestBodyBuilder.build();

		List<String> urls = Splitter.on("\n")
				.omitEmptyStrings()
				.trimResults()
				.splitToList(config.webhook());
		for (String url : urls)
		{
			HttpUrl u = HttpUrl.parse(url);
			if (u == null)
			{
				log.info("Malformed webhook url {}", url);
				continue;
			}

			Request request = new Request.Builder()
					.url(url)
					.post(requestBody)
					.build();
			okHttpClient.newCall(request).enqueue(new Callback()
			{
				@Override
				public void onFailure(Call call, IOException e)
				{
					log.debug("Error submitting webhook", e);
				}

				@Override
				public void onResponse(Call call, Response response) throws IOException
				{
					response.close();
				}
			});
		}
	}
}
