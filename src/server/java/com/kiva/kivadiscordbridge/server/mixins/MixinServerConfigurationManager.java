package com.kiva.kivadiscordbridge.server.mixins;

import com.kiva.kivadiscordbridge.KivaDiscordBridgeServer;
import net.minecraft.src.server.ServerConfigurationManager;
import net.minecraft.src.server.packets.Packet;
import net.minecraft.src.server.packets.Packet3Chat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerConfigurationManager.class)
public class MixinServerConfigurationManager {
    @Inject(method = "sendPacketToAllPlayers", at = @At("HEAD"))
    public void onSendPacketToAllPlayers(Packet packet, CallbackInfo ci){
        if (!(packet instanceof Packet3Chat))
            return;

        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace.length < 4)
            return;

        // We handle chat msgs in MixinNetServerHandler. This mixin is more for like, death messages
        if (stackTrace[3].getMethodName().equals("handleChat"))
            return;

        KivaDiscordBridgeServer.discordAPI.sendMessage(((Packet3Chat) packet).message);
    }
}
