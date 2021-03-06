package cback.commands;

import cback.GamingBot;
import cback.GamingRoles;
import cback.Util;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;

import java.util.Arrays;
import java.util.List;

public class CommandLog implements Command {
    @Override
    public String getName() {
        return "addlog";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("log");
    }

    @Override
    public void execute(GamingBot bot, IDiscordClient client, String[] args, IGuild guild, IMessage message, boolean isPrivate) {
        if (message.getAuthor().getRolesForGuild(guild).contains(guild.getRoleByID(GamingRoles.STAFF.id))) {

            Util.botLog(message);

            if (args.length >= 1) {
                List<IRole> userRoles = message.getAuthor().getRolesForGuild(guild);
                if (userRoles.contains(guild.getRoleByID(GamingRoles.HELPER.id)) || userRoles.contains(guild.getRoleByID(GamingRoles.ADMIN.id)) || userRoles.contains(guild.getRoleByID(GamingRoles.MOD.id))) {
                    String finalText = message.getFormattedContent().split(" ", 2)[1];
                    Util.sendLog(message, finalText);
                    Util.sendMessage(message.getChannel(), "Log added. " + guild.getChannelByID(GamingBot.LOG_CHANNEL_ID).mention());
                    Util.deleteMessage(message);
                } else {
                    Util.sendMessage(message.getChannel(), "You don't have permission to add logs.");
                }
            } else {
                Util.sendMessage(message.getChannel(), "Usage: !addlog <text>");
            }
        }
    }

}
