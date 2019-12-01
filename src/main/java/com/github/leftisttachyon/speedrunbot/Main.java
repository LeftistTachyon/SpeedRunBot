package com.github.leftisttachyon.speedrunbot;

import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

import static com.github.leftisttachyon.speedrunbot.Command.PREFIX;

/**
 * The main class for this application.
 *
 * @author LeftistTachyon
 * @since 0.9.0
 */
public class Main extends ListenerAdapter {

    /**
     * The logger for this application
     */
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    /**
     * A Map of all commands, sorted by category
     */
    private TreeMap<String, List<Command>> commands;

    /**
     * Creates a new Main object
     */
    public Main() {
        commands = new TreeMap<>();

        // a list of "Meta" commands
        List<Command> metaCommands = new ArrayList<>();

        // the "!!ping" command
        metaCommands.add(new Command(event -> {
            OffsetDateTime time = event.getMessage().getTimeCreated(),
                    now = OffsetDateTime.now();
            Duration delay = Duration.between(now, time);
            long millis = delay.toMillis();

            event.getChannel().sendMessageFormat("Pong! The delay is %,d milliseconds.", millis).queue();
        }, "Pings me!", new String[]{"ping"}));

        // the "!!help" command
        metaCommands.add(new Command(this::help,
                "Lists commands or, if specified, gives a detailed entry on a command.",
                new String[]{"help", "halp"}));

        // add the "Meta" commands
        commands.put("Meta", metaCommands);
    }

    /**
     * The main method
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        try {
            JDABuilder builder = new JDABuilder(AccountType.BOT);
            builder.setToken(Token.getToken());
            builder.addEventListeners(new Main());
            logger.info("JDABuilder initialized");
            builder.build();
            logger.info("JDABuilder#build() invoked");
        } catch (LoginException ex) {
            logger.error("Could not log in successfully", ex);
        }
    }

    /**
     * Called when a message is received on any accessible text channel.
     *
     * @param event a {@code MessageReceivedEvent}F that describes the event
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        logger.info("Message received from {}: \"{}\"", event.getAuthor().getName(),
                event.getMessage().getContentDisplay());

        if (event.getAuthor().isBot()) {
            return;
        }

        String message = event.getMessage().getContentRaw();

        String[] data = message.split("\\s+");
        logger.trace("data: {}", Arrays.toString(data));
        if (data.length == 0 || !data[0].startsWith(PREFIX)) {
            return;
        }

        outer:
        for (List<Command> commandList : commands.values()) {
            for (Command c : commandList) {
                logger.trace("Checking for invokation of {}{}", PREFIX, c.getPrimaryAlias());
                if (c.shouldInvoke(message)) {
                    c.invoke(event);
                    break outer;
                }
            }
        }
    }

    /**
     * Sends the help message.
     *
     * @param event the event that generated the help message
     */
    private void help(MessageReceivedEvent event) {
        EmbedBuilder builder = new EmbedBuilder();
        // color: 245, 197, 83
        builder.setColor(new Color(245, 197, 83));
        String message = event.getMessage().getContentRaw();
        String[] data = message.substring(PREFIX.length()).split("\\s+");
        // logger.trace("data: {}", Arrays.toString(data));
        assert data.length > 0 : "data.length is less than or equal to 0 " + Arrays.toString(data);

        String failMessage = "I can't seem to find what you're looking for.\n\"" +
                message.substring(message.indexOf(' ') + 1) + "\" doesn't match anything of what I have.";
        switch (data.length) {
            case 1:
                // title: "Help"
                builder.setTitle("Help");
                // description: "..."
                builder.setDescription("To get a more detailed description of one of the below commands, say `" + PREFIX +
                        "help <command>`. For a subcommand, say `" + PREFIX + "help <command> <subcommand>`. To run a command, say `" +
                        PREFIX + "<command>`.");
                // thumbnail: bot icon, but better
                builder.setThumbnail("https://cdn.discordapp.com/app-assets/647269383345012736/650784079859548160.png");
                // go through entries of command map
                for (String module : commands.keySet()) {
                    // for the body of the field
                    StringBuilder sBuilder = new StringBuilder();
                    for (Command c : commands.get(module)) {
                        if (c instanceof Subcommand) {
                            continue;
                        }
                        sBuilder.append("`");
                        sBuilder.append(PREFIX);
                        sBuilder.append(c.getPrimaryAlias());
                        sBuilder.append("`, ");
                    }
                    String commandList = sBuilder.toString();
                    commandList = commandList.substring(0, commandList.length() - 2);
                    builder.addField(module, commandList, false);
                }
                break;
            case 2:
                String command = data[1];
                embedSpecificHelp(event, command, builder, failMessage);
                break;
            case 3:
                command = data[1] + " " + data[2];
                embedSpecificHelp(event, command, builder, failMessage);
                break;
            default:
                event.getChannel().sendMessage(failMessage).queue();
                return;
        }

        event.getChannel().sendMessage(builder.build()).queue();

    }

    /**
     * Creates a specific help embed
     *
     * @param event       the event that triggered the creation of a specific help embed
     * @param command     the command to create a specific help embed for
     * @param builder     the EmbedBuilder to add on to
     * @param failMessage the message to send if the command cannot be found
     */
    private void embedSpecificHelp(MessageReceivedEvent event, String command,
                                   EmbedBuilder builder, String failMessage) {
        Command command_ = null;
        outer:
        for (List<Command> cc : commands.values()) {
            for (Command c : cc) {
                if (c.isAlias(command)) {
                    command_ = c;
                    break outer;
                }
            }
        }
        if (command_ == null) {
            event.getChannel().sendMessage(failMessage).queue();
            return;
        }

        // title: "Help on `!!{}`"
        builder.setTitle("Help on `" + PREFIX + command + "`");

        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append("```markdown\n# ");
        sBuilder.append(PREFIX);
        sBuilder.append(command);
        sBuilder.append("\n");
        sBuilder.append(command_.getDescription());
        sBuilder.append("\n");
        if (command_.getAliases().size() > 1) {
            sBuilder.append("Other aliases: ");
            for (String alias : command_.getAliases()) {
                if (!alias.equals(command)) {
                    sBuilder.append("_");
                    sBuilder.append(PREFIX);
                    sBuilder.append(alias);
                    sBuilder.append("_, ");
                }
            }
            sBuilder.delete(sBuilder.length() - 2, sBuilder.length());
            sBuilder.append("\n");
        }
        sBuilder.append("```");

        builder.setDescription(sBuilder.toString());
    }

}
