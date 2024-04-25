package com.example.discordbot.service;

import discord4j.discordjson.json.ApplicationCommandRequest;

public interface CommandStorage {
    ApplicationCommandRequest deleteAllCommands();

    ApplicationCommandRequest play();
}
