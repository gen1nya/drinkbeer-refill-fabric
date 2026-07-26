package lekavar.lma.drinkbeer;

import lekavar.lma.drinkbeer.client.renderers.BartendingTableBlockEntityRenderer;
import lekavar.lma.drinkbeer.client.renderers.MixedBeerBlockEntityRenderer;
import lekavar.lma.drinkbeer.gui.BeerBarrelScreen;
import lekavar.lma.drinkbeer.gui.TradeBoxScreen;
import lekavar.lma.drinkbeer.managers.MixedBeerManager;
import lekavar.lma.drinkbeer.registries.BlockEntityRegistry;
import lekavar.lma.drinkbeer.registries.ItemRegistry;
import lekavar.lma.drinkbeer.registries.MenuTypeRegistry;
import lekavar.lma.drinkbeer.registries.ParticleTypeRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.client.particle.HeartParticle;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;

public class DrinkBeerClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockEntityRenderers.register(BlockEntityRegistry.MIXED_BEER_TILEENTITY.get(), MixedBeerBlockEntityRenderer::new);
        BlockEntityRenderers.register(BlockEntityRegistry.BARTENDING_TABLE_TILEENTITY.get(), BartendingTableBlockEntityRenderer::new);

        ParticleFactoryRegistry.getInstance().register(ParticleTypeRegistry.MIXED_BEER_DEFAULT.get(), FlameParticle.Provider::new);
        ParticleFactoryRegistry.getInstance().register(ParticleTypeRegistry.CALL_BELL_TINKLE_PAW.get(), HeartParticle.AngryVillagerProvider::new);

        MenuScreens.register(MenuTypeRegistry.beerBarrelContainer.get(), BeerBarrelScreen::new);
        MenuScreens.register(MenuTypeRegistry.tradeBoxContainer.get(), TradeBoxScreen::new);

        ItemProperties.register(ItemRegistry.MIXED_BEER.get(), ResourceLocation.withDefaultNamespace("beer_id"),
                (stack, level, living, id) -> MixedBeerManager.getBeerId(stack) / 100.0f);
    }
}
