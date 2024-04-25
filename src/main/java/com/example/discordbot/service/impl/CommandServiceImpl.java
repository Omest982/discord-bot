package com.example.discordbot.service.impl;


import com.example.discordbot.service.CommandService;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.discordjson.json.ApplicationCommandData;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.service.ApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CommandServiceImpl implements CommandService {
    @Override
    public ApplicationCommandRequest deleteAllCommands() {
        return ApplicationCommandRequest.builder()
                .name("delete_commands")
                .description("delete all commands")
                .build();
    }

    @Override
    public ApplicationCommandRequest play() {
        System.out.println("Started!");
        return ApplicationCommandRequest.builder()
                .name("play")
                .description("plays")
                .addOption(ApplicationCommandOptionData.builder()
                        .name("query")
                        .description("The search query")
                        .type(ApplicationCommandOption.Type.STRING.getValue())
                        .required(true)
                        .build())
                .build();
    }

    @Override
    public List<ApplicationCommandRequest> getAllCommands()  {

        Method[] allMethods = CommandServiceImpl.class.getMethods();

        try {
            System.out.println("invoking");
            allMethods[0].invoke(this);
            System.out.println("finished");
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        return List.of(
                play(),
                deleteAllCommands()
        );
    }

    @Override
    public List<String> getAllCommandsNames() {

        List<ApplicationCommandRequest> allCommands = getAllCommands();

        return allCommands.stream()
                .map(ApplicationCommandRequest::name)
                .collect(Collectors.toList());
    }

    @Override
    public void registerNewCommands(GatewayDiscordClient client) {

        if (client == null){
            return;
        }

        List<ApplicationCommandRequest> allCommands = getAllCommands();

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
