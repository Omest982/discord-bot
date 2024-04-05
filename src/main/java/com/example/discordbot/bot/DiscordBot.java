package com.example.discordbot.bot;

import com.example.discordbot.service.CommandService;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.discordjson.json.ApplicationCommandData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DiscordBot {

    @Value("${bot.token}")
    private String botToken;

    private final CommandService commandService;

    @Bean
    public GatewayDiscordClient createClient() {
        GatewayDiscordClient client = DiscordClientBuilder.create(botToken)
                .build()
                .login()
                .block();

        registerNewCommands(client);

        return client;
    }

    private void registerNewCommands(GatewayDiscordClient client) {

        List<ApplicationCommandRequest> allCommands = commandService.getAllCommands();

        ApplicationService applicationService = client.getRestClient().getApplicationService();
        Long applicationId = client.getRestClient().getApplicationId().block();

        applicationService
                .getGlobalApplicationCommands(applicationId)
                .collectList()
                .flatMapMany(registeredCommands -> {
                    Set<String> registeredCommandsNames = registeredCommands.stream()
                            .map(ApplicationCommandData::name)
                            .collect(Collectors.toSet());

                    return Flux.fromIterable(allCommands)
                            .filter(command -> !registeredCommandsNames
                                    .contains(command.name()))
                            .flatMap(command -> applicationService
                                    .createGlobalApplicationCommand(applicationId, command)
                                    .doOnSuccess(result -> log.info(String.format("Successfully registered command: %s", result.name())))
                                    .doOnError(error -> log.error("Failed to register command", error))
                            );
                }).subscribe();
    }
}
