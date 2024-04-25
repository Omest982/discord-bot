package com.example.discordbot.service.impl;


import com.example.discordbot.service.CommandService;
import com.example.discordbot.service.CommandStorage;
import discord4j.core.GatewayDiscordClient;
import discord4j.discordjson.json.ApplicationCommandData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommandServiceImpl implements CommandService {
    private final CommandStorage commandStorage;

    @Cacheable("allCommands")
    @Override
    public List<ApplicationCommandRequest> getAllCommands()  {
        List<Method> allMethods = Arrays.stream(CommandStorage.class.getMethods()).toList();

        return allMethods.stream()
                .map(method -> {
                    try {
                        return (ApplicationCommandRequest) method.invoke(commandStorage);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList());
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
