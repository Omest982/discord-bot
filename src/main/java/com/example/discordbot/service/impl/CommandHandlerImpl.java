package com.example.discordbot.service.impl;

import com.example.discordbot.service.CommandHandler;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.rest.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommandHandlerImpl implements CommandHandler {
    private final GatewayDiscordClient discordClient;

    @Override
    public Mono<Void> handlePlay(ChatInputInteractionEvent event) {
        log.info("play command handles!");
        return Mono.empty();
    }

    @Override
    public Mono<Void> handleDeleteAllCommands(ChatInputInteractionEvent event) {
        Long applicationId = discordClient.getRestClient().getApplicationId().block();
        ApplicationService applicationService = discordClient.getRestClient().getApplicationService();

        return applicationService.getGlobalApplicationCommands(applicationId)
                .flatMap(command -> applicationService.deleteGlobalApplicationCommand(applicationId, command.id().asLong()))
                .then();
    }

    @Override
    public Publisher<Void> handle(ChatInputInteractionEvent event) {
        switch (event.getCommandName()) {
            case ("delete_commands"):
                handleDeleteAllCommands(event);
                log.info("All commands was deleted!");
                return event.reply("Command worked!");

            case ("play"):
                return handlePlay(event);
        }
        log.error("Could not found the appropriate handler!");
        return Mono.empty();
    }
}
