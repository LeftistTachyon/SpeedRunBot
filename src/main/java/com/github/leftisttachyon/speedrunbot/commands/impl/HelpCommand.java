package com.github.leftisttachyon.speedrunbot.commands.impl;

import com.github.leftisttachyon.speedrunbot.commands.Command;
import com.github.leftisttachyon.speedrunbot.commands.Commands;
import com.github.leftisttachyon.speedrunbot.commands.Subcommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

/**
 * The {@code Command} object that handles the {@code !!help} command.
 *
 * @author Jed Wang
 * @since 0.9.0
 */
public class HelpCommand extends Command {
    /**
     * The only HelpCommand object to be created for this program
     */
    private static final HelpCommand singleton = new HelpCommand();

    /**
     * Returns an instance of an HelpCommand.
     *
     * @return an instance of an HelpCommand.
     */
    public static HelpCommand getHelpCommand() {
        return singleton;
    }

    /**
     * Creates a new HelpCommand, but since this constructor is private, HelpCommand objects can only be created in this class.
     */
    private HelpCommand() {
        super("Lists commands or, if specified, gives a detailed entry on a command.",
                new String[]{"help", "halp"});
    }

    /**
     * {@inheritDoc}<br>
     * For the {@code !!help} command, this method sends the help message.
     *
     * @param event the event that generated the help message
     */
    @Override
    public void invoke(MessageReceivedEvent event) {
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
                for (String module : Commands.getCommands().keySet()) {
                    // for the body of the field
                    StringBuilder sBuilder = new StringBuilder();
                    for (Command c : Commands.getCommands().get(module)) {
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
        for (List<Command> cc : Commands.getCommands().values()) {
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
        if (command_.getAliases().length > 1) {
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
