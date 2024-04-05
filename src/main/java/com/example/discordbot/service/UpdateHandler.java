package com.example.discordbot.service;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UpdateHandler {
    private final GatewayDiscordClient discordClient;
    private final CommandService commandService;
    private final CommandHandler commandHandler;

    @PostConstruct
    public void init() {
        onMessageReceived();
        onCommandReceived();
    }

    public void onMessageReceived(){
        discordClient.on(MessageCreateEvent.class,
                event -> {
                    System.out.println("Simple text message " + event.getMessage().getContent());
                    if (event.getMessage().getContent().equalsIgnoreCase("!ping")) {
                        return event.getMessage().getChannel()
                                .flatMap(channel -> channel.createMessage("Pong!"))
                                .then();
                    }
                    return Mono.empty();
                }
        ).subscribe();
    }

    public void onCommandReceived(){
        discordClient.on(ChatInputInteractionEvent.class,
                event -> {
            log.info(String.format("Received command: %s", event.getCommandName()));

            List<String> allCommandsNames = commandService.getAllCommandsNames();

            if (allCommandsNames.contains(event.getCommandName())){
                return commandHandler.handle(event);
            }
            return event.reply("Command not recognized!");
        }).subscribe();
    }

}
