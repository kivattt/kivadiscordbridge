# KivaDiscordBridge
**Currently, this mod only sends messages in-game to a Discord channel. It does not pass on Discord messages to the server.** 

## Installation
Once you've put the mod .jar file in your `mods` folder, start the server and then stop it with the `stop` command.\
You should see a new file appear in `mods/KivaDiscordBridge/config.txt`

Add your bot token after the `bot-token=` line\
Add the channel ID after the `channel-id=` line

Now when you start the server, it should post in-game chat messages to the channel you specified.

## Notes
This mod hardcodes the API url as `https://discord.com/api`
should this URL change in the future, open an issue or pull request

This mod integrates well with [KivaServerUtils](https://github.com/kivattt/kivaserverutils).
It will show the player pronouns next to their username in Discord if it's also installed.