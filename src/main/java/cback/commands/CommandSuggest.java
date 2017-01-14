package cback.commands;

import cback.GamingBot;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;

import java.util.Arrays;
import java.util.List;

public class CommandSuggest implements Command {
    @Override
    public String getName() {
        return "suggest";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("idea","suggestion");
    }

    @Override
    public void execute(GamingBot bot, IDiscordClient client, String[] args, IGuild guild, IMessage message, boolean isPrivate) {
        if (message.getChannel().getID().equals("262454712111071233")) {
            try {
                message.getChannel().pin(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
