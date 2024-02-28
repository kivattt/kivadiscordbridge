package com.kiva.kivadiscordbridge;

import com.fox2code.foxloader.loader.ServerMod;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Config {
    public void loadFromFile(final String filename) {
        Scanner scanner;
        try {
            scanner = new Scanner(new File(filename));
        } catch (FileNotFoundException e){
            ServerMod.getGameInstance().logWarning(KivaDiscordBridgeServer.loggingPrefix + "Failed to read config file " + filename);
            return;
        }

        int lineNum = 0;
        while (scanner.hasNextLine()) {
            ++lineNum;
            String line = scanner.nextLine();

            if (line.startsWith("#") || line.isEmpty())
                continue;

            int separatorIndex = line.indexOf("=");
            if (separatorIndex == -1) {
                ServerMod.getGameInstance().logWarning(KivaDiscordBridgeServer.loggingPrefix + "Failed to find '=' separator on line " + lineNum + " in config file " + filename);
                continue;
            }

            String value = line.substring(separatorIndex + 1).trim();

            if (line.startsWith("bot-token="))
                KivaDiscordBridgeServer.discordAPI.setToken(value);
            else if (line.startsWith("channel-id="))
                KivaDiscordBridgeServer.discordAPI.setChannel(value);
        }
    }
}
