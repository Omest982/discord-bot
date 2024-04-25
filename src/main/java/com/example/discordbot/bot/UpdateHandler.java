package com.example.discordbot.bot;

import com.example.discordbot.service.CommandHandler;
import com.example.discordbot.service.CommandService;
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

    //for usual text messages
    public void onMessageReceived(){
        discordClient.on(MessageCreateEvent.class,
                event -> {
                    String message = event.getMessage().getContent();
                    log.info(String.format("Received simple text message  %s", message));

                    if (message.equalsIgnoreCase("!ping")) {
                        return event.getMessage().getChannel()
                                .flatMap(channel -> channel.createMessage("Pong!"))
                                .then();
                    }
                    return Mono.empty();
                }
        ).subscribe();
    }

    //for slash commands
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
