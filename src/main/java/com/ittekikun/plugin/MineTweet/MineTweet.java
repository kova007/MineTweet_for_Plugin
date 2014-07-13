package com.ittekikun.plugin.MineTweet;

import com.ittekikun.plugin.MineTweet.Command.BaseCommand;
import com.ittekikun.plugin.MineTweet.Command.ConfigReloadCommand;
import com.ittekikun.plugin.MineTweet.Command.HelpCommand;
import com.ittekikun.plugin.MineTweet.Command.TweetCommand;
import com.ittekikun.plugin.MineTweet.Config.MineTweetConfig;
import com.ittekikun.plugin.MineTweet.Listener.RegistrationListener;
import com.ittekikun.plugin.MineTweet.Twitter.TwitterManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class MineTweet extends JavaPlugin
{
	public static MineTweet instance;
    public static Logger log;
	public MineTweetConfig mtConfig;
	public TwitterManager twitterManager;

	private List<BaseCommand> commands = new ArrayList<BaseCommand>();

	public static final String KEYWORD_USER = "$user";
	public static final String KEYWORD_REASON = "$reason";
	public static final String KEYWORD_SENDER = "$sender";
	public static final String KEYWORD_NUMBER = "$number";
	public static final String KEYWORD_CHANNEL = "$channel";
	public static final String KEYWORD_MESSAGE = "$message";
	public static final String KEYWORD_ACHIEVEMENT = "$achievement";

    @Override
    public void onEnable()
    {
	    instance = this;

	    mtConfig = new MineTweetConfig(this);
	    mtConfig.loadConfig();

	    twitterManager = new TwitterManager(this);
	    twitterManager.startSetup();

	    RegistrationListener.registrationListener(instance);
	    registerCommands();

        log =  Logger.getLogger("Minecraft");
    }

	private void registerCommands()
	{
		commands.add(new HelpCommand());
		commands.add(new TweetCommand());
		commands.add(new ConfigReloadCommand());
	}


	public File getPluginJarFile()
	{
		return this.getFile();
	}

	public List<BaseCommand> getCommands()
	{
		return commands;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String args[])
	{
		if (cmd.getName().equalsIgnoreCase("mt") || cmd.getName().equalsIgnoreCase("minetweet"))
		{
			if(args.length == 0)
			{
				// 引数ゼロはヘルプ表示
				args = new String[]{"help"};
			}

			outer:
			for (BaseCommand command : commands.toArray(new BaseCommand[0]))
			{
				String[] cmds = command.getName().split(" ");
				for (int i = 0; i < cmds.length; i++){
					if (i >= args.length || !cmds[i].equalsIgnoreCase(args[i])){
						continue outer;
					}
					// 実行
					return command.run(this, sender, args, commandLabel);
				}
			}
			// 有効コマンドなし ヘルプ表示
			new HelpCommand().run(this, sender, args, commandLabel);
			return true;
		}
//		詰んだ
//		else if(cmd.getName().equalsIgnoreCase("tw") || cmd.getName().equalsIgnoreCase("tweet"))
//		{
//			new TweetCommand().run(this, sender, args, commandLabel);
//			return true;
//		}
		return false;
	}
}