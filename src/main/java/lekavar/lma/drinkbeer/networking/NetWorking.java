package lekavar.lma.drinkbeer.networking;

import lekavar.lma.drinkbeer.blockentities.TradeBoxBlockEntity;
import lekavar.lma.drinkbeer.gui.TradeBoxMenu;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;

public class NetWorking {

    public static void init() {
        PayloadTypeRegistry.playC2S().register(RefreshTradeBoxPayload.TYPE, RefreshTradeBoxPayload.STREAM_CODEC);

        // Fabric вызывает обработчик уже в серверном потоке — обёртка
        // MainThreadPayloadHandler из NeoForge не нужна.
        ServerPlayNetworking.registerGlobalReceiver(RefreshTradeBoxPayload.TYPE, (payload, context) -> {
            ServerPlayer player = context.player();
            if (!(player.containerMenu instanceof TradeBoxMenu)) {
                return;
            }
            BlockPos pos = payload.pos();
            if (!player.level().hasChunkAt(pos)) {
                return;
            }
            if (player.level().getBlockEntity(pos) instanceof TradeBoxBlockEntity tradeboxEntity) {
                tradeboxEntity.screenHandler.setTradeboxCooling();
                tradeboxEntity.setChanged();
            }
        });
    }
}
