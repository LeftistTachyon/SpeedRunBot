package com.leftisttachyon.speedrunbot;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * The main class for this application.
 *
 * @author LeftistTachyon
 */
public class Main extends ListenerAdapter {

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
            builder.build();
        } catch (LoginException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Called when a message is received on any accessible text channel.
     *
     * @param event a {@code MessageReceivedEvent}F that describes the event
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        System.out.println("We received a message from "
                + event.getAuthor().getName() + ": "
                + event.getMessage().getContentDisplay());

        if (event.getAuthor().isBot()) {
            return;
        }

        String[] data = event.getMessage().getContentRaw().split("\\s+");
        if (data.length < 1 || !data[0].startsWith("~")) {
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
