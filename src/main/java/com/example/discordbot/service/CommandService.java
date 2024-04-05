package com.example.discordbot.service;

import discord4j.discordjson.json.ApplicationCommandRequest;

import java.util.List;

public interface CommandService {

    ApplicationCommandRequest play();

    List<ApplicationCommandRequest> getAllCommands();

    List<String> getAllCommandsNames();
}
