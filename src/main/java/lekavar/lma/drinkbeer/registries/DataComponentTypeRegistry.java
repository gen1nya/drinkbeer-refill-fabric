package lekavar.lma.drinkbeer.registries;

import com.mojang.serialization.Codec;
import lekavar.lma.drinkbeer.DrinkBeer;
import lekavar.lma.drinkbeer.fabric.DeferredRegister;
import lekavar.lma.drinkbeer.utils.dataComponent.SpiceData;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;

import java.util.function.Supplier;

public class DataComponentTypeRegistry {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, DrinkBeer.MOD_ID);

    public static final Supplier<DataComponentType<Integer>> BEER_ID_COMPONENT = DATA_COMPONENTS.register(
            "beer_id", () -> DataComponentType.<Integer>builder()
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.INT)
                    .build()
    );

    public static final Supplier<DataComponentType<SpiceData>> SPICE_COMPONENT = DATA_COMPONENTS.register(
            "spice", () -> DataComponentType.<SpiceData>builder()
                    .persistent(SpiceData.CODEC)
                    .networkSynchronized(SpiceData.STREAM_CODEC)
                    .build()
    );
}
