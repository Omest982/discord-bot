package com.example.discordbot.service;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import org.reactivestreams.Publisher;

public interface CommandHandler {
    Publisher<Void> handlePlay(ChatInputInteractionEvent event);

    Publisher<Void> handleDeleteAllCommands(ChatInputInteractionEvent event);

    Publisher<Void> handle(ChatInputInteractionEvent event);
}
