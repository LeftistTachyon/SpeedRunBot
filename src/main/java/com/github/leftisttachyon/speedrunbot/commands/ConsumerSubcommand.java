package com.github.leftisttachyon.speedrunbot.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.function.Consumer;

/**
 * A POJO object that represents a subcommand based off a Consumer object.
 *
 * @author Jed Wang
 * @since 0.9.0
 * @see Consumer
 */
public class ConsumerSubcommand extends Subcommand {

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
        super(function, description, aliases);
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
        super(function, description, parent.getAliases());
        for (int i = 0; i < subAliases.length; i++) {
            subAliases[i] = subAliases[i].toLowerCase();
        }
        this.subAliases = subAliases;
    }

    @Override
    public String[] getAliases() {
        String[] parentAliases = super.getAliases(),
                output = new String[parentAliases.length * subAliases.length];
        int cnt = 0;
        for (String superAlias : parentAliases) {
            for (String subAlias : subAliases) {
                output[cnt++] = superAlias + " " + subAlias;
            }
        }
        return output;
    }

    /**
     * Returns the aliases of the parent command
     *
     * @return the aliases of the parent command
     */
    public String[] getParentAliases() {
        return super.getAliases();
    }

    /**
     * Returns the aliases of this subcommand
     *
     * @return the aliases of this subcommand
     */
    public String[] getSubAliases() {
        return subAliases;
    }

    @Override
    public boolean shouldInvoke(String s) {
        String[] data = s.split("\\s+");
        if (!super.shouldInvoke(data)) {
            return false;
        }

        return isAlias(data[1]);
    }

    @Override
    public String getPrimaryAlias() {
        return super.getPrimaryAlias() + " " + subAliases[0];
    }

    /**
     * Determines whether the given string is an alias of this subcommand, not the parent command.
     *
     * @param alias the possible alias
     * @return whether the given string is an alias of this subcommand
     */
    @Override
    public boolean isAlias(String alias) {
        for (String str : subAliases) {
            if (str.equals(alias)) {
                return true;
            }
        }

        return false;
    }
}
