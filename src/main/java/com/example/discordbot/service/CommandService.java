package com.example.discordbot.service;

import discord4j.core.GatewayDiscordClient;
import discord4j.discordjson.json.ApplicationCommandRequest;

import java.util.List;

public interface CommandService {

    void registerNewCommands(GatewayDiscordClient client);

    List<ApplicationCommandRequest> getAllCommands();

    List<String> getAllCommandsNames();
}
