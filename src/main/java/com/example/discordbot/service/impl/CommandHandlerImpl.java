package com.example.discordbot.service.impl;

import com.example.discordbot.service.CommandHandler;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import org.reactivestreams.Publisher;

public class CommandHandlerImpl implements CommandHandler {

    @Override
    public void handlePlay() {

    }

    @Override
    public Publisher<Void> handle(ChatInputInteractionEvent event) {
        return null;
    }
}
