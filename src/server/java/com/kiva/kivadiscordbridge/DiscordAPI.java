package com.kiva.kivadiscordbridge;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DiscordAPI {
    //private final String BASEURL = "https://discord.com/api";
    private String token = null;
    private String channel = null;

    private void postRequest(final String endpoint, final String body) {
        try {
            URL url = new URL("https://discord.com/api" + endpoint);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", "curl/7.74.0");
            con.setRequestProperty("Authorization", "Bot " + token); // Who the fuck named this "property" instead of header? A landlord?
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoInput(true);
            con.setDoOutput(true);

            try(OutputStream os = con.getOutputStream()) {
                byte[] input = body.getBytes();
                os.write(input, 0, input.length);
            }

            con.getInputStream();
            con.disconnect();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    private String getMessageSanitizedForJSONStringValue(final String message) {
        // https://www.json.org/json-en.html
        String disallowedCharactersForJSON = "\"\\";

        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < message.length(); i++){
            if (disallowedCharactersForJSON.indexOf(message.charAt(i)) != -1)
                ret.append('\\');

            ret.append(message.charAt(i));
        }

        return ret.toString();
    }

    // Handrolled because I don't care
    private String messageToJSONPostBody(final String message) {
        String ret = "{\"content\": \"";
        ret += getMessageSanitizedForJSONStringValue(message);
        return ret + "\"}";
    }

    // Adds backslashes before characters like * and _
    private String escapeForMarkdown(final String str, Integer allowMarkdownStart, Integer allowMarkDownEnd) {
        // https://spec.commonmark.org/0.31.2/#ascii-punctuation-character
        String charsToEscape = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";

        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            if (allowMarkdownStart != null && allowMarkDownEnd != null){
                if (i >= allowMarkdownStart && i <= allowMarkDownEnd) {
                    ret.append(str.charAt(i));
                    continue;
                }
            }

            if (charsToEscape.indexOf(str.charAt(i)) != -1)
                ret.append('\\');
            ret.append(str.charAt(i));
        }

        return ret.toString();
    }

    private String removeColorCodes(final String str) {
        StringBuilder ret = new StringBuilder();

        boolean ignoreNextChar = false;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);

            if (c == '§' && i != str.length() - 1) {
                ignoreNextChar = true;
                continue;
            }

            if (!ignoreNextChar) {
                ret.append(c);
            } else {
                ignoreNextChar = false;
            }
        }

        return ret.toString();
    }

    public void setToken(final String newToken) {
        token = newToken;
    }

    public void setChannel(final String newChannel) {
        channel = newChannel;
    }

    public void sendMessage(final String message) {
        if (token == null || channel == null)
            return;

        Thread sendThread = new Thread(() -> postRequest("/channels/" + channel + "/messages", messageToJSONPostBody(escapeForMarkdown(removeColorCodes(message), null, null))));
        sendThread.start();
    }

    public void sendMessage(final String message, int allowMarkDownStart, int allowMarkDownEnd) {
        if (token == null || channel == null)
            return;

        Thread sendThread = new Thread(() -> postRequest("/channels/" + channel + "/messages", messageToJSONPostBody(escapeForMarkdown(removeColorCodes(message), allowMarkDownStart, allowMarkDownEnd))));
        sendThread.start();
    }
}
