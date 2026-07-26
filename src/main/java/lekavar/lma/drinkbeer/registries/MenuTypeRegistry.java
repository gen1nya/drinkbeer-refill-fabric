package lekavar.lma.drinkbeer.registries;

import lekavar.lma.drinkbeer.DrinkBeer;
import lekavar.lma.drinkbeer.fabric.DeferredRegister;
import lekavar.lma.drinkbeer.gui.BeerBarrelMenu;
import lekavar.lma.drinkbeer.gui.TradeBoxMenu;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;

import java.util.function.Supplier;


public class MenuTypeRegistry {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, DrinkBeer.MOD_ID);

    // Оба меню передают клиенту только BlockPos блока — на Fabric это
    // ExtendedScreenHandlerType с кодеком данных вместо NeoForge IMenuTypeExtension.
    public static final Supplier<MenuType<BeerBarrelMenu>> beerBarrelContainer =
            MENUS.register("beer_barrel_container", () -> new ExtendedScreenHandlerType<>(BeerBarrelMenu::new, BlockPos.STREAM_CODEC));
    public static final Supplier<MenuType<TradeBoxMenu>> tradeBoxContainer =
            MENUS.register("trade_box_normal_container", () -> new ExtendedScreenHandlerType<>(TradeBoxMenu::new, BlockPos.STREAM_CODEC));

    // Экраны привязываются в DrinkBeerClient (MenuScreens вместо RegisterMenuScreensEvent).
}
