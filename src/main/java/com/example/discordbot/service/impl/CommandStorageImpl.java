package com.example.discordbot.service.impl;

import com.example.discordbot.service.CommandStorage;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import org.springframework.stereotype.Service;

@Service
public class CommandStorageImpl implements CommandStorage {

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
    public ApplicationCommandRequest deleteAllCommands() {
        return ApplicationCommandRequest.builder()
                .name("delete_commands")
                .description("delete all commands")
                .build();
    }
}
