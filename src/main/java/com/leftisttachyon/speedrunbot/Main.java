package com.leftisttachyon.speedrunbot;

import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main class for this application.
 *
 * @author LeftistTachyon
 */
public class Main extends ListenerAdapter {
    
    /**
     * The logger for this class.
     */
    private final static Logger logger = LoggerFactory.getLogger(Main.class);

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
            logger.info("JDABuilder instantiated!");
            builder.build();
            logger.info("JDABuilder running....");
        } catch (LoginException ex) {
            logger.error("The main method threw an exception: " 
                    + ex.getClass().getSimpleName(), ex);
        }
    }

    /**
     * Called when a message is received on any accessible text channel.
     *
     * @param event a {@code MessageReceivedEvent} that describes the event
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        logger.info("Message received from "
                + event.getAuthor().getName() + ": "
                + event.getMessage().getContentDisplay());

        if (event.getAuthor().isBot()) {
            return;
        }

        String[] data = event.getMessage().getContentRaw().split("\\s+");
        if (data.length < 1 || !data[0].startsWith("~")) { // prefix is "~"
            return;
        }

        MessageChannel channel = event.getChannel();
        switch (data[0].substring(1)) {
            case "ping":
                channel.sendMessage("Pong!").queue();
                break;
        }
    }
}
