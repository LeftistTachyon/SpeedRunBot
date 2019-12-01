package com.github.leftisttachyon.speedrunbot;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Consumer;

/**
 * A POJO that represents a bot command.
 *
 * @author Jed Wang
 * @since 0.9.0
 */
public class Command {

    /**
     * The logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(Command.class);

    /**
     * The prefix this bot will use
     */
    static final String PREFIX = "!!";

    /**
     * The code that the function executes
     */
    private final Consumer<MessageReceivedEvent> function;

    /**
     * A description of the command
     */
    private final String description;

    /**
     * A list of aliases (or nicknames or shorthand) that can call this command.<br>
     * For example, {@code resume} could be an alias of {@code unpause}
     */
    private final List<String> aliases;

    /**
     * Creates a new Command
     *
     * @param function    the code that the command will execute
     * @param description a description of the command
     * @param aliases     aliases of the command; the first one in the array will always be the primary alias
     */
    public Command(Consumer<MessageReceivedEvent> function, String description, String[] aliases) {
        this.function = function;
        this.description = description;
        if (aliases.length == 0) {
            throw new IllegalArgumentException("A command must have at least one alias");
        }
        for (int i = 0; i < aliases.length; i++) {
            aliases[i] = aliases[i].toLowerCase();
        }
        this.aliases = List.of(aliases);
    }

    /**
     * Creates a new Command
     *
     * @param function    the code to execute when this command is invoked
     * @param description the description of the command
     * @param aliases     an immutable list of the aliases of this command
     */
    protected Command(Consumer<MessageReceivedEvent> function, String description, List<String> aliases) {
        this.function = function;
        this.description = description;
        this.aliases = aliases;
    }

    /**
     * Returns the description of the command
     *
     * @return the description of the command
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the aliases of this command
     *
     * @return the aliases of this command
     */
    public List<String> getAliases() {
        return aliases;
    }

    /**
     * Returns the primary alias of this command
     *
     * @return the primary alias of this command
     */
    public String getPrimaryAlias() {
        return aliases.get(0);
    }

    /**
     * Determines whether this command should be invoked by the given string command.
     *
     * @param s the entire command
     * @return whether this command should be invoked
     */
    public boolean shouldInvoke(String s) {
        String[] data = s.split("\\s+");
        return shouldInvoke(data);
    }

    /**
     * Given the parsed message, determines whether this command should be invoked
     *
     * @param data the parsed command
     * @return whether this command should be invoked
     */
    protected boolean shouldInvoke(String[] data) {
        logger.trace("shouldInvoke for {}: {} {} {}", getPrimaryAlias(), data.length > 0, data[0].startsWith(PREFIX),
                aliases.contains(data[0].substring(PREFIX.length())));
        return data.length > 0 && data[0].startsWith(PREFIX) && aliases.contains(data[0].substring(PREFIX.length()));
    }

    /**
     * Invokes this command with the given MessageReceivedEvent
     *
     * @param event the event that gives the information needed to invoke this command
     */
    public void invoke(MessageReceivedEvent event) {
        function.accept(event);
    }

    /**
     * Determines whether the given string is an alias of this command
     *
     * @param alias the possible alias
     * @return whether the given string is an alias of this command
     */
    public boolean isAlias(String alias) {
        return aliases.contains(alias);
    }
}
