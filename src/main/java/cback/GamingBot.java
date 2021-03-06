package cback;

import cback.commands.Command;
import cback.eventFunctions.ChannelChange;
import cback.eventFunctions.MemberChange;
import org.reflections.Reflections;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.shard.DisconnectedEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.modules.Configuration;
import sx.blah.discord.util.DiscordException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("FieldCanBeLocal")
public class GamingBot {

    private static GamingBot instance;
    private IDiscordClient client;

    private ConfigManager configManager;
    private CommandManager commandManager;
    private Scheduler scheduler;

    private List<String> botAdmins = new ArrayList<>();
    public List<Command> registeredCommands = new ArrayList<>();

    private static final Pattern COMMAND_PATTERN = Pattern.compile("^!([^\\s]+) ?(.*)", Pattern.CASE_INSENSITIVE);
    public static final String ANNOUNCEMENT_CHANNEL_ID = "263185438380916737";
    public static final String GENERAL_CHANNEL_ID = "191589587817070593";
    public static final String LOG_CHANNEL_ID = "262465858079555585";
    public static final String BOTLOG_WEBHOOK_URL = "https://ptb.discordapp.com/api/webhooks/263740625755701259/4md6yxY7cUxq5mS5LcfMtU1azF0RYurFdo-sl-YBbnkp-rhHTQais6xjE_ABXAsGdQG-/slack";

    private long startTime;

    public static void main(String[] args) {
        new GamingBot();
    }

    public GamingBot() {

        instance = this;

        //instantiate config manager first as connect() relies on tokens
        configManager = new ConfigManager(this);
        commandManager = new CommandManager(this);

        connect();
        client.getDispatcher().registerListener(this);
        client.getDispatcher().registerListener(new ChannelChange(this));
        client.getDispatcher().registerListener(new MemberChange(this));

        scheduler = new Scheduler(this);

        registerAllCommands();

        botAdmins.add("109109946565537792");
        botAdmins.add("148279556619370496");
        botAdmins.add("73416411443113984");
        botAdmins.add("144412318447435776");

    }

    private void connect() {
        //don't load external modules and don't attempt to create modules folder
        Configuration.LOAD_EXTERNAL_MODULES = false;

        Optional<String> token = configManager.getTokenValue("botToken");
        if (!token.isPresent()) {
            System.out.println("-------------------------------------");
            System.out.println("Insert your bot's token in the config.");
            System.out.println("Exiting......");
            System.out.println("-------------------------------------");
            System.exit(0);
            return;
        }

        ClientBuilder clientBuilder = new ClientBuilder();
        clientBuilder.withToken(token.get());
        clientBuilder.setMaxReconnectAttempts(5);
        try {
            client = clientBuilder.login();
        } catch (DiscordException e) {
            e.printStackTrace();
        }
    }

    @EventSubscriber
    public void onMessageEvent(MessageReceivedEvent event) {
        if (event.getMessage().getAuthor().isBot()) return; //ignore bot messages
        IMessage message = event.getMessage();
        IGuild guild = null;
        boolean isPrivate = message.getChannel().isPrivate();
        if (!isPrivate) guild = message.getGuild();
        String text = message.getContent();
        Matcher matcher = COMMAND_PATTERN.matcher(text);
        if (matcher.matches()) {
            String baseCommand = matcher.group(1).toLowerCase();
            Optional<Command> command = registeredCommands.stream()
                    .filter(com -> com.getName().equalsIgnoreCase(baseCommand) || (com.getAliases() != null && com.getAliases().contains(baseCommand)))
                    .findAny();
            if (command.isPresent()) {
                System.out.println("@" + message.getAuthor().getName() + " issued \"" + text + "\" in " +
                        (isPrivate ? ("@" + message.getAuthor().getName()) : guild.getName()));

                String args = matcher.group(2);
                String[] argsArr = args.isEmpty() ? new String[0] : args.split(" ");
                command.get().execute(this, client, argsArr, guild, message, isPrivate);
            } else if (commandManager.getCommandValue(baseCommand) != null) {

                String response = commandManager.getCommandValue(baseCommand);

                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("``" + message.getAuthor().getDisplayName(guild) + "``\n").append(response);

                Util.sendMessage(message.getChannel(), stringBuilder.toString());

                Util.deleteMessage(message);
            }
        } else {
            String lowerCase = message.getContent().toLowerCase();

            //Check for discord invite link
            if (lowerCase.contains("discord.gg") || lowerCase.contains("discordapp.com/invite/")) {
                if (Util.permissionCheck(message, "Admins") || lowerCase.contains("discord.gg/lounge") || lowerCase.contains("discord.gg/QeuTNRb") || lowerCase.contains("discord.gg/Empn64q")) {
                } else {
                    Util.sendPrivateMessage(message.getAuthor(), "Rule 3, Advertising your server is not allowed!");
                    Util.sendMessage(client.getChannelByID("191751650489270273"), message.getAuthor().mention() + " __might__ have advertised their server in " + message.getChannel().mention() + ". Could a human please investigate?");
                }
            }
        }
    }

    @EventSubscriber
    public void onReadyEvent(ReadyEvent event) {
        System.out.println("Logged in.");

        startTime = System.currentTimeMillis();
    }

    @EventSubscriber
    public void onDisconnectEvent(DisconnectedEvent event) {
        Util.sendWebhook(
                "https://ptb.discordapp.com/api/webhooks/276878075944370177/lyhmQffmqwILMc7vawjk3g2tG-1XCV_oqSShvWq4ugjIQQ0X69ffp_cGaruoZ265w72S/slack",
                client.getApplicationIconURL(),
                client.getApplicationName(),
                client.getUserByID("73416411443113984").mention() + " GamingBot has gone offline."
        );
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public IDiscordClient getClient() {
        return client;
    }

    public List<String> getBotAdmins() {
        return botAdmins;
    }

    private void registerAllCommands() {
        new Reflections("cback.commands").getSubTypesOf(Command.class).forEach(commandImpl -> {
            try {
                Command command = commandImpl.newInstance();
                Optional<Command> existingCommand = registeredCommands.stream().filter(cmd -> cmd.getName().equalsIgnoreCase(command.getName())).findAny();
                if (!existingCommand.isPresent()) {
                    registeredCommands.add(command);
                    System.out.println("Registered command: " + command.getName());
                } else {
                    System.out.println("Attempted to register two commands with the same name: " + existingCommand.get().getName());
                    System.out.println("Existing: " + existingCommand.get().getClass().getName());
                    System.out.println("Attempted: " + commandImpl.getName());
                }
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }

    public String getUptime() {
        long totalSeconds = (System.currentTimeMillis() - startTime) / 1000;
        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours = (totalSeconds / 3600);
        return (hours < 10 ? "0" + hours : hours) + "h " + (minutes < 10 ? "0" + minutes : minutes) + "m " + (seconds < 10 ? "0" + seconds : seconds) + "s";
    }

    public static GamingBot getInstance() {
        return instance;
    }

}
