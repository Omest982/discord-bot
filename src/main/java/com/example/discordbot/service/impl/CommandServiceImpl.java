package com.example.discordbot.service.impl;


import com.example.discordbot.service.CommandService;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommandServiceImpl implements CommandService {
    @Override
    public ApplicationCommandRequest play() {
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
    public List<ApplicationCommandRequest> getAllCommands() {
        return List.of(
                play()
        );
    }

    @Override
    public List<String> getAllCommandsNames() {
        return List.of(
                play().name()
        );
    }
}
