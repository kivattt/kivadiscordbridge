package com.kiva.kivadiscordbridge.server.mixins;

import com.fox2code.foxloader.loader.ModLoader;
import com.kiva.kivadiscordbridge.KivaDiscordBridgeServer;
import net.minecraft.src.game.entity.player.EntityPlayerMP;
import net.minecraft.src.server.packets.NetServerHandler;
import net.minecraft.src.server.packets.Packet3Chat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.lang.reflect.Field;
import java.util.HashMap;

@Mixin(NetServerHandler.class)
public class MixinNetServerHandler {
    @Shadow private EntityPlayerMP playerEntity;

    @Inject(method = "handleChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/server/ServerConfigurationManager;sendPacketToAllPlayers(Lnet/minecraft/src/server/packets/Packet;)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    public void onHandleChat(Packet3Chat packet3Chat, CallbackInfo ci, String var2, int var3){
        Field KSUPlayerPronouns = null;
        try {
            KSUPlayerPronouns = ModLoader.getModContainer("kivaserverutils").getServerMod().getClass().getSuperclass().getDeclaredField("playerPronouns");
        } catch (NoSuchFieldException | NullPointerException ignored) {}

        // Server does not have KivaServerUtils installed
        if (KSUPlayerPronouns == null){
            int firstSpace = var2.indexOf(" ");
            String msgWithoutUsername = null;
            try {
                msgWithoutUsername = var2.substring(firstSpace + 1);
            } catch (IndexOutOfBoundsException ignored){}

            if (firstSpace == -1 || msgWithoutUsername == null) {
                // Fall back to just sending the raw message if something went wrong
                KivaDiscordBridgeServer.discordAPI.sendMessage(var2);
            } else {
                // Custom message format
                KivaDiscordBridgeServer.discordAPI.sendMessage("`" + this.playerEntity.username + "` " + msgWithoutUsername, 0, this.playerEntity.username.length()+1);
            }

            return;
        }

        // Server has KivaServerUtils installed
        final String KSUMsgPrefix = "§r> ";
        int beginningOfMsg = var2.indexOf(KSUMsgPrefix);
        String msgWithoutUsername = null;
        try {
            msgWithoutUsername = var2.substring(beginningOfMsg + KSUMsgPrefix.length());
        } catch (IndexOutOfBoundsException ignored){}

        if (beginningOfMsg == -1 || msgWithoutUsername == null) {
            // Fall back to just sending the raw message if something went wrong
            KivaDiscordBridgeServer.discordAPI.sendMessage(var2);
        } else {
            // Custom message format
            HashMap<String, String> pronouns = new HashMap<>();
            try {
                pronouns = (HashMap<String, String>) KSUPlayerPronouns.get(this);
            } catch (IllegalAccessException ignored){}
            String playerPronouns = pronouns.get(this.playerEntity.username);
            if (playerPronouns == null)
                KivaDiscordBridgeServer.discordAPI.sendMessage("`" + this.playerEntity.username + "` " + msgWithoutUsername, 0, this.playerEntity.username.length()+1);
            else
                KivaDiscordBridgeServer.discordAPI.sendMessage("`" + this.playerEntity.username + " [" + playerPronouns + "]` " + msgWithoutUsername, 0, this.playerEntity.username.length()+2+playerPronouns.length()+2);
        }
    }
}
