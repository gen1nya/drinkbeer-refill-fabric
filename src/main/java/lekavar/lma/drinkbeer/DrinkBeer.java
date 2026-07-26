package lekavar.lma.drinkbeer;

import lekavar.lma.drinkbeer.networking.NetWorking;
import lekavar.lma.drinkbeer.registries.*;
import net.fabricmc.api.ModInitializer;

public class DrinkBeer implements ModInitializer {

    public static final String MOD_ID = "drinkbeer";

    @Override
    public void onInitialize() {
        // На Fabric реестры открыты прямо здесь, поэтому регистрация происходит
        // в статической инициализации *Registry-классов — её достаточно «разбудить».
        // Порядок важен: блоки раньше предметов, потому что BlockItem'ам нужен готовый блок.
        load(MobEffectRegistry.DRUNK);
        load(BlockRegistry.BEER_BARREL);
        load(ItemRegistry.BEER_BARREL);
        load(BlockEntityRegistry.BEER_BARREL_TILEENTITY);
        load(SoundEventRegistry.DRINKING_BEER);
        load(MenuTypeRegistry.beerBarrelContainer);
        load(RecipeRegistry.RECIPE_TYPE_BREWING);
        load(RecipeRegistry.RECIPE_SERIALIZER_BREWING);
        load(ParticleTypeRegistry.MIXED_BEER_DEFAULT);
        load(CreativeTabRegistry.GENERAL);
        load(CreativeTabRegistry.BEER);
        load(DataComponentTypeRegistry.BEER_ID_COMPONENT);

        NetWorking.init();

    }

    /** Обращение к статическому полю форсит инициализацию класса-реестра. */
    private static void load(Object registryEntry) {
    }
}
