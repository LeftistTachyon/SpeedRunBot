package com.github.leftisttachyon.speedrunbot.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

/**
 * A POJO that represents a bot command based off of a Consumer object.
 *
 * @author Jed Wang
 * @see Consumer
 * @see Command
 * @since 0.9.0
 */
public class ConsumerCommand extends Command {

    /**
     * The logger for this class
     */
    private static final Logger log = LoggerFactory.getLogger(ConsumerCommand.class);

    /**
     * The code that the function executes
     */
    private final Consumer<MessageReceivedEvent> function;

    /**
     * Creates a new ConsumerCommand
     *
     * @param function    the code that the command will execute
     * @param description a description of the command
     * @param aliases     aliases of the command; the first one in the array will always be the primary alias
     */
    public ConsumerCommand(Consumer<MessageReceivedEvent> function, String description, String[] aliases) {
        super(description, aliases);
        this.function = function;
    }

    @Override
    public void invoke(MessageReceivedEvent event) {
        log.trace("Invoking the function in the command {}{}", PREFIX, getPrimaryAlias());
        function.accept(event);
    }
}
