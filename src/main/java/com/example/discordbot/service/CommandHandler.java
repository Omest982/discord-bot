package com.example.discordbot.service;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import org.reactivestreams.Publisher;

public interface CommandHandler {
    void handlePlay();

    Publisher<Void> handle(ChatInputInteractionEvent event);
}
