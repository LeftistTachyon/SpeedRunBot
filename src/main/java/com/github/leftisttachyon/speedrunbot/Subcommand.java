package com.github.leftisttachyon.speedrunbot;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * A POJO object that represents a subcommand
 *
 * @author Jed Wang
 * @since 0.9.0
 */
public class Subcommand extends Command {
    /**
     * The aliases of this subcommand
     */
    private List<String> subAliases;

    /**
     * Creates a new command
     *
     * @param function    the code that this subcommand will execute
     * @param description a description of this subcommand
     * @param aliases     aliases of the parent command; the first one in the array will always be the primary alias
     * @param subAliases  the aliases of the subcommand; the first one is the primary alias
     */
    public Subcommand(Consumer<MessageReceivedEvent> function, String description, String[] aliases,
                      String[] subAliases) {
        super(function, description, aliases);

        for (int i = 0; i < subAliases.length; i++) {
            subAliases[i] = subAliases[i].toLowerCase();
        }
        this.subAliases = List.of(subAliases);
    }

    /**
     * Creates a subcommand based off of a parent command
     *
     * @param function    the code that invoking this subcommand will execute
     * @param description a description of this subcommand
     * @param subAliases  the aliases of this subcommand
     * @param parent      the parent of this subcommand
     */
    public Subcommand(Consumer<MessageReceivedEvent> function, String description, String[] subAliases,
                      Command parent) {
        super(function, description, parent.getAliases());
        for (int i = 0; i < subAliases.length; i++) {
            subAliases[i] = subAliases[i].toLowerCase();
        }
        this.subAliases = List.of(subAliases);
    }

    @Override
    public List<String> getAliases() {
        List<String> output = new ArrayList<>();
        for (String superAlias : super.getAliases()) {
            for (String subAlias : subAliases) {
                output.add(superAlias + " " + subAlias);
            }
        }
        return output;
    }

    /**
     * Returns the aliases of the parent command
     *
     * @return the aliases of the parent command
     */
    public List<String> getParentAliases() {
        return super.getAliases();
    }

    /**
     * Returns the aliases of this subcommand
     *
     * @return the aliases of this subcommand
     */
    public List<String> getSubAliases() {
        return subAliases;
    }

    @Override
    public boolean shouldInvoke(String s) {
        String[] data = s.split("\\s+");
        return super.shouldInvoke(data) && subAliases.contains(data[1]);
    }

    @Override
    public String getPrimaryAlias() {
        return super.getPrimaryAlias() + " " + subAliases.get(0);
    }

    @Override
    public boolean isAlias(String alias) {
        return super.isAlias(alias) && subAliases.contains(alias);
    }
}
