package com.github.leftisttachyon.speedrunbot.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

/**
 * A POJO object that represents a subcommand based off a Consumer object.
 *
 * @author Jed Wang
 * @see Consumer
 * @see Subcommand
 * @since 0.9.0
 */
public class ConsumerSubcommand extends Subcommand {

    /**
     * The logger for this class
     */
    private static final Logger log = LoggerFactory.getLogger(ConsumerSubcommand.class);

    /**
     * The code that the function executes
     */
    private final Consumer<MessageReceivedEvent> function;

    /**
     * Creates a new ConsumerSubcommand
     *
     * @param function    the code that this subcommand will execute
     * @param description a description of this subcommand
     * @param aliases     aliases of the parent command; the first one in the array will always be the primary alias
     * @param subAliases  the aliases of the subcommand; the first one is the primary alias
     */
    public ConsumerSubcommand(Consumer<MessageReceivedEvent> function, String description, String[] aliases,
                              String[] subAliases) {
        super(description, aliases, subAliases);
        this.function = function;
    }

    /**
     * Creates a subcommand based off of a parent command
     *
     * @param function    the code that invoking this subcommand will execute
     * @param description a description of this subcommand
     * @param subAliases  the aliases of this subcommand
     * @param parent      the parent of this subcommand
     */
    public ConsumerSubcommand(Consumer<MessageReceivedEvent> function, String description, String[] subAliases,
                              ConsumerCommand parent) {
        super(description, subAliases, parent);
        this.function = function;
    }

    @Override
    public void invoke(MessageReceivedEvent event) {
        log.trace("Invoking the function in the subcommand {}{}", PREFIX, getPrimaryAlias());
        function.accept(event);
    }
}
