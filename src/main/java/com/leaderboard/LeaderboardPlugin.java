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
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.List;

import static net.runelite.http.api.RuneLiteAPI.GSON;

@Slf4j
@PluginDescriptor(
	name = "Patience Clan Leaderboard"
)
public class LeaderboardPlugin extends Plugin
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
			// Chambers of Xeric

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
			"Chambers of Xeric 24+ players"};

	@Inject
	private Client client;

	@Inject
	private LeaderboardConfig config;

	@Inject
	private OkHttpClient okHttpClient;

	@Inject
	private ConfigManager configManager;

	@Override
	protected void startUp() throws Exception
	{
	}

	@Override
	protected void shutDown() throws Exception
	{
	}

	@Provides
    LeaderboardConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(LeaderboardConfig.class);
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGIN_SCREEN)
		{
			// Your logic to run on login goes here
			String leaderboard = compileLeaderboard();
			WebhookBody webhookBody = new WebhookBody();
			webhookBody.setContent(leaderboard);
			sendWebhook(webhookBody);
		}
	}

	private String compileLeaderboard()
	{
		StringBuilder leaderboard = new StringBuilder();
		//System.out.println(client.getLocalPlayer());
		//System.out.println(client.getLocalPlayer().getName());
		leaderboard.append("{\n\"").append(client.getLocalPlayer().getName()).append("\": {");
        for (String bossName : bossNames) {
            leaderboard.append("\n\"").append(bossName).append("\": {");
            leaderboard.append("\"count\": ").append(getKc(bossName)).append(",");
            leaderboard.append("\"pb\": ").append(getPb(bossName)).append("},");
        }
		leaderboard.setLength(leaderboard.length() - 1); //removes the last comma
		leaderboard.append("}\n}"); // cap off the JSON
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
				.addFormDataPart("payload_json", GSON.toJson(webhookBody));

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
