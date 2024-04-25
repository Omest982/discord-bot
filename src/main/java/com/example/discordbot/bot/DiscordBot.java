package com.example.discordbot.bot;

import com.example.discordbot.service.CommandService;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

        commandService.registerNewCommands(client);

        return client;
    }
}
