package lekavar.lma.drinkbeer.networking.client;

import lekavar.lma.drinkbeer.networking.RefreshTradeBoxPayload;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.core.BlockPos;

/** Отправка C2S-пакетов. Вынесено из NetWorking, чтобы клиентские классы не тянулись на сервер. */
@Environment(EnvType.CLIENT)
public class ClientNetWorking {

    public static void sendRefreshTradebox(BlockPos pos) {
        ClientPlayNetworking.send(new RefreshTradeBoxPayload(pos));
    }
}
