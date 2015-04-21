package com.ittekikun.plugin.MineTweet.Twitter;

import com.ittekikun.plugin.MineTweet.Config.MineTweetConfig;
import com.ittekikun.plugin.MineTweet.MineTweet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import com.ittekikun.plugin.MineTweet.Utility;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import twitter4j.TwitterException;

public class BotManager
{
	public MineTweet plugin;
	public TwitterManager twitterManager;
	public MineTweetConfig mtConfig;
	public List<String> botMessageList;
	public BukkitScheduler bukkitScheduler;
	public int time;

	public BotManager(MineTweet plugin)
	{
		this.plugin = plugin;
		this.mtConfig = plugin.mtConfig;
		this.twitterManager = plugin.twitterManager;
	}

	public void botSetup()
	{
		if (this.mtConfig.useBot)
		{
			this.bukkitScheduler = Bukkit.getServer().getScheduler();

			this.botMessageList = new ArrayList(this.mtConfig.botMessageList);
			this.bukkitScheduler.runTaskTimer(this.plugin, new BotTweetTask(this.botMessageList), 0L, convertSecondToTick(this.mtConfig.tweetCycle));

			//ツイート可能な文字数をここで出しておく
			//面倒くさいけど仕方ないね♂
			this.time = (140 - Utility.timeGetter(this.mtConfig.dateformat).length() + 2);
		}
	}

	public void taskCancel()
	{
		if (this.mtConfig.useBot)
		{
			bukkitScheduler.cancelTasks(this.plugin);
		}
	}

	//簡単だけど分かりやすくするために
	public int convertSecondToTick(int second)
	{
		return second * 20;
	}

	public class BotTweetTask implements Runnable
	{
		public List<String> botMessageList;

		public BotTweetTask(List<String> botMessageList)
		{
			this.botMessageList = botMessageList;
		}

		public void run()
		{
			Collections.rotate(this.botMessageList, 1);

			if (this.botMessageList.get(0).length() <= time)
			{
				try
				{
					String message = replaceKeywords(this.botMessageList.get(0));
					BotManager.this.twitterManager.tweet(message);
				}
				catch (TwitterException e)
				{
					MineTweet.log.severe("[BOT]下記のメッセージは何らかの理由でツイートされませんでした。");
					MineTweet.log.severe(BotManager.this.mtConfig.botMessageList.get(0));
					e.printStackTrace();
					return;
				}
			}
			else
			{
				MineTweet.log.severe("[BOT]下記のメッセージは" + time + "字以上の為ツイートできません。");
				MineTweet.log.severe(BotManager.this.mtConfig.botMessageList.get(0));
			}
		}

		private String replaceKeywords(String source)
		{
			String result = source;
			if (result.contains(MineTweet.KEYWORD_NUMBER))
			{
				ArrayList players = Utility.getOnlinePlayers();
				String number = Integer.toString((players.size()));

				result = result.replace(MineTweet.KEYWORD_NUMBER, number);
			}
			if (result.contains(MineTweet.KEYWORD_NEWLINE))
			{
				result = result.replace(MineTweet.KEYWORD_NEWLINE, MineTweet.SOURCE_NEWLINE);
			}
			if (result.contains(MineTweet.KEYWORD_TIME))
			{
				String time = Utility.timeGetter(mtConfig.dateformat);

				result = result.replace(MineTweet.KEYWORD_TIME, time);
			}
			return result;
		}
	}
}