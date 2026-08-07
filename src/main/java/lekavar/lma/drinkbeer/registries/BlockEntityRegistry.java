package lekavar.lma.drinkbeer.registries;

import lekavar.lma.drinkbeer.DrinkBeer;
import lekavar.lma.drinkbeer.blockentities.BartendingTableBlockEntity;
import lekavar.lma.drinkbeer.blockentities.BeerBarrelBlockEntity;
import lekavar.lma.drinkbeer.blockentities.MixedBeerBlockEntity;
import lekavar.lma.drinkbeer.blockentities.TradeBoxBlockEntity;
import net.minecraft.core.registries.Registries;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.world.level.block.entity.BlockEntityType;
import lekavar.lma.drinkbeer.fabric.DeferredRegister;

import java.util.function.Supplier;

public class BlockEntityRegistry {
    public static final DeferredRegister<BlockEntityType<?>> BLOKC_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, DrinkBeer.MOD_ID);
    public static final Supplier<BlockEntityType<BeerBarrelBlockEntity>> BEER_BARREL_TILEENTITY = BLOKC_ENTITIES.register("beer_barrel_blockentity", () -> FabricBlockEntityTypeBuilder.create(BeerBarrelBlockEntity::new, BlockRegistry.BEER_BARREL.get()).build());
    public static final Supplier<BlockEntityType<BartendingTableBlockEntity>> BARTENDING_TABLE_TILEENTITY = BLOKC_ENTITIES.register("bartending_table_normal_blockentity", () -> FabricBlockEntityTypeBuilder.create(BartendingTableBlockEntity::new, BlockRegistry.BARTENDING_TABLE.get()).build());
    public static final Supplier<BlockEntityType<TradeBoxBlockEntity>> TRADE_BOX_TILEENTITY = BLOKC_ENTITIES.register("trade_box_normal_blockentity", () -> FabricBlockEntityTypeBuilder.create(TradeBoxBlockEntity::new, BlockRegistry.TRADE_BOX.get()).build());
    public static final Supplier<BlockEntityType<MixedBeerBlockEntity>> MIXED_BEER_TILEENTITY = BLOKC_ENTITIES.register("mixed_beer_blockentity", () -> FabricBlockEntityTypeBuilder.create(MixedBeerBlockEntity::new, BlockRegistry.MIXED_BEER.get()).build());

    // Рендереры блок-энтити регистрируются в DrinkBeerClient (Fabric: клиентский entrypoint,
    // а не EntityRenderersEvent).
}
