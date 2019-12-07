package com.github.leftisttachyon.speedrunbot;

import com.github.leftisttachyon.speedrunbot.commands.Command;
import com.github.leftisttachyon.speedrunbot.commands.Commands;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

import static com.github.leftisttachyon.speedrunbot.commands.Command.PREFIX;

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
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    /**
     * Creates a new Main object
     */
    public Main() {
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
            log.info("JDABuilder initialized");
            builder.build();
            log.trace("JDABuilder#build() invoked");
        } catch (LoginException ex) {
            log.error("Could not log in successfully", ex);
        }
    }

    /**
     * Called when a message is received on any accessible text channel.
     *
     * @param event a {@code MessageReceivedEvent}F that describes the event
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        log.trace("Message received from {}: \"{}\"", event.getAuthor().getName(),
                event.getMessage().getContentDisplay());

        if (event.getAuthor().isBot()) {
            return;
        }

        String message = event.getMessage().getContentRaw();

        String[] data = message.split("\\s+");
        log.trace("data: {}", Arrays.toString(data));
        if (data.length == 0 || !data[0].startsWith(PREFIX)) {
            return;
        }

        TreeMap<String, List<Command>> commands = Commands.getCommands();

        outer:
        for (List<Command> commandList : commands.values()) {
            for (Command c : commandList) {
                log.trace("Checking for invokation of {}{}", PREFIX, c.getPrimaryAlias());
                if (c.shouldInvoke(message)) {
                    c.invoke(event);
                    break outer;
                }
            }
        }
    }
}
