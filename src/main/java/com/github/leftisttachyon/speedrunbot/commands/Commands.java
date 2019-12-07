package com.github.leftisttachyon.speedrunbot.commands;

import com.github.leftisttachyon.speedrunbot.commands.impl.HelpCommand;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * A utility class that deals with Commands
 *
 * @author Jed Wang
 * @since 0.9.0
 */
public class Commands {
    /**
     * A Map of all commands, sorted by category
     */
    private static final TreeMap<String, List<Command>> commands;

    static {
        commands = new TreeMap<>();

        // a list of "Meta" commands
        List<Command> metaCommands = new ArrayList<>();

        // the "!!ping" command
        metaCommands.add(new ConsumerCommand(event -> {
            OffsetDateTime time = event.getMessage().getTimeCreated(),
                    now = OffsetDateTime.now();
            Duration delay = Duration.between(now, time);
            long millis = delay.toMillis();

            event.getChannel().sendMessageFormat("Pong! The delay is %,d milliseconds.", millis).queue();
        }, "Pings me!", new String[]{"ping"}));

        // the "!!help" command
        metaCommands.add(HelpCommand.getHelpCommand());

        // add the "Meta" commands
        commands.put("Meta", metaCommands);
    }

    /**
     * Returns the TreeMap of Commands that are used in this bot.
     *
     * @return all commands
     * @see Command
     */
    public static TreeMap<String, List<Command>> getCommands() {
        return commands;
    }
}
