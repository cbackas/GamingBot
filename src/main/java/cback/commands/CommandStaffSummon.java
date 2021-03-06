package cback.commands;

import cback.GamingBot;
import cback.GamingRoles;
import cback.Util;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.util.List;

public class CommandStaffSummon implements Command {
    @Override
    public String getName() {
        return "staff";
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public void execute(GamingBot bot, IDiscordClient client, String[] args, IGuild guild, IMessage message, boolean isPrivate) {
        if (!bot.getConfigManager().getConfigArray("cantsummon").contains(message.getAuthor().getID())) {
            Util.botLog(message);

            IChannel staff_hq = client.getChannelByID("191751650489270273");
            List<IUser> mods = Util.getUsersByRole(GamingRoles.MOD.id);

            StringBuilder modMentions = new StringBuilder();
            for (IUser u : mods) {
                modMentions.append(" ").append(u.mention());
            }

            Util.sendMessage(message.getChannel(), "Staff have been notified and will come shortly.");
            Util.sendMessage(staff_hq, message.getAuthor().mention() + " has requested staff in " + message.getChannel().mention() + "\n" + modMentions.toString());

            Util.deleteMessage(message);
        }
    }
}
