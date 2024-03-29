package com.ittekikun.plugin.MineTweet.Listener;

import com.ittekikun.plugin.MineTweet.Config.MineTweetConfig;
import com.ittekikun.plugin.MineTweet.MineTweet;
import com.ittekikun.plugin.MineTweet.Twitter.TwitterManager;
import com.ittekikun.plugin.MineTweet.Utility;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import twitter4j.TwitterException;

//例のアレ
public class ZyariEvent implements Listener
{
	MineTweet plugin;
	MineTweetConfig mtConfig;
	TwitterManager twitterManager;

	public  ZyariEvent(MineTweet plugin)
	{
		this.plugin = plugin;
		this.mtConfig = plugin.mtConfig;
		this.twitterManager = plugin.twitterManager;
	}

	@EventHandler
	public void onBlockBreakEvent(BlockBreakEvent event) throws TwitterException
	{
		if(event.getBlock().getType() == Material.GRAVEL)
		{
			String name = event.getPlayer().getName();
			String message = replaceKeywords("あぁ＾〜$userのこころがじゃりじゃりするんじゃあ＾〜【自動投稿】", name);
			twitterManager.tweet(message);
		}
	}

	private String replaceKeywords(String source,String name)
	{
		String result = source;
		if (result.contains(MineTweet.KEYWORD_USER) )
		{
			result = result.replace(MineTweet.KEYWORD_USER, name);
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