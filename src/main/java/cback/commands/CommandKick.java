package cback.commands;

import cback.GamingBot;
import cback.GamingRoles;
import cback.Util;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.internal.DiscordUtils;
import sx.blah.discord.handle.obj.*;

import java.awt.*;
import java.util.EnumSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandKick implements Command {
    @Override
    public String getName() {
        return "kick";
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public void execute(GamingBot bot, IDiscordClient client, String[] args, IGuild guild, IMessage message, boolean isPrivate) {
        if (message.getAuthor().getRolesForGuild(guild).contains(guild.getRoleByID(GamingRoles.STAFF.id))) {
            String text = message.getContent();
            IUser mod = message.getAuthor();
            IChannel logChannel = guild.getChannelByID("262465858079555585");
            try {
                DiscordUtils.checkPermissions(message.getChannel().getModifiedPermissions(mod), EnumSet.of(Permissions.KICK));
                Pattern pattern = Pattern.compile("^!kick <@!?(\\d+)> ?(.+)?");
                Matcher matcher = pattern.matcher(text);
                if (matcher.find()) {
                    String userInput = matcher.group(1);
                    String reason = matcher.group(2);
                    if (reason != null) {
                        IUser user = guild.getUserByID(userInput);
                        if (user.getID().equals(mod.getID())) {
                            Util.sendMessage(message.getChannel(), "You know you can just leave right?");
                        } else {
                            try {
                                guild.kickUser(user);
                                Util.sendLog(message, "Banned " + user.getDisplayName(guild) + "\n**Reason:** " + reason, Color.red);
                                Util.sendMessage(message.getChannel(), user.getDisplayName(guild) + " has been kicked. Check " + guild.getChannelByID(GamingBot.LOG_CHANNEL_ID).mention() + " for more info");
                            } catch (Exception e) {
                                e.printStackTrace();
                                Util.sendMessage(message.getChannel(), "Internal error - cback has been notified");
                                Util.sendPrivateMessage(client.getUserByID("73416411443113984"), "Error running CommandKick - check stacktrace");
                            }
                        }
                    } else {
                        Util.sendPrivateMessage(mod, "**Error Kicking**: Reason required");
                    }
                } else {
                    Util.sendMessage(message.getChannel(), "Invalid arguments. Usage: ``!kick @user reason``");
                }
            } catch (Exception e) {
            }

            Util.botLog(message);
            Util.deleteMessage(message);
        }
    }

}
