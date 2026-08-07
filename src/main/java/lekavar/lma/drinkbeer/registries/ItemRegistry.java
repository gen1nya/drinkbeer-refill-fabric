package lekavar.lma.drinkbeer.registries;

import lekavar.lma.drinkbeer.DrinkBeer;
import lekavar.lma.drinkbeer.items.BeerMugItem;
import lekavar.lma.drinkbeer.items.MixedBeerBlockItem;
import lekavar.lma.drinkbeer.items.SpiceBlockItem;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import lekavar.lma.drinkbeer.fabric.DeferredRegister;

import java.util.function.Supplier;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, DrinkBeer.MOD_ID);

    //general
    public static final Supplier<Item> BEER_BARREL = ITEMS.register("beer_barrel", key -> new BlockItem(BlockRegistry.BEER_BARREL.get(), new Item.Properties().useBlockDescriptionPrefix().setId(key)));
    public static final Supplier<Item> BARTENDING_TABLE = ITEMS.register("bartending_table_normal", key -> new BlockItem(BlockRegistry.BARTENDING_TABLE.get(), new Item.Properties().useBlockDescriptionPrefix().setId(key)));
    public static final Supplier<Item> TRADE_BOX = ITEMS.register("trade_box_normal", key -> new BlockItem(BlockRegistry.TRADE_BOX.get(), new Item.Properties().useBlockDescriptionPrefix().setId(key)));
    public static final Supplier<Item> EMPTY_BEER_MUG = ITEMS.register("empty_beer_mug", key -> new BlockItem(BlockRegistry.EMPTY_BEER_MUG.get(), new Item.Properties().useBlockDescriptionPrefix().setId(key)));

    public static final Supplier<Item> IRON_CALL_BELL = ITEMS.register("iron_call_bell", key -> new BlockItem(BlockRegistry.IRON_CALL_BELL.get(), new Item.Properties().useBlockDescriptionPrefix().setId(key)));
    public static final Supplier<Item> GOLDEN_CALL_BELL = ITEMS.register("golden_call_bell", key -> new BlockItem(BlockRegistry.GOLDEN_CALL_BELL.get(), new Item.Properties().useBlockDescriptionPrefix().setId(key)));
    public static final Supplier<Item> LEKAS_CALL_BELL = ITEMS.register("lekas_call_bell", key -> new BlockItem(BlockRegistry.LEKAS_CALL_BELL.get(), new Item.Properties().useBlockDescriptionPrefix().setId(key)));

    public static final Supplier<Item> RECIPE_BOARD_BEER_MUG = ITEMS.register("recipe_board_beer_mug", key -> new BlockItem(BlockRegistry.RECIPE_BOARD_BEER_MUG.get(), new Item.Properties().useBlockDescriptionPrefix().setId(key).stacksTo(1)));
    public static final Supplier<Item> RECIPE_BOARD_BEER_MUG_BLAZE_STOUT = ITEMS.register("recipe_board_beer_mug_blaze_stout", key -> new BlockItem(BlockRegistry.RECIPE_BOARD_BEER_MUG_BLAZE_STOUT.get(), new Item.Properties().useBlockDescriptionPrefix().setId(key).stacksTo(1)));
    public static final Supplier<Item> RECIPE_BOARD_BEER_MUG_BLAZE_MILK_STOUT = ITEMS.register("recipe_board_beer_mug_blaze_milk_stout", key -> new BlockItem(BlockRegistry.RECIPE_BOARD_BEER_MUG_BLAZE_MILK_STOUT.get(), new Item.Properties().useBlockDescriptionPrefix().setId(key).stacksTo(1)));
    public static final Supplier<Item> RECIPE_BOARD_BEER_MUG_APPLE_LAMBIC = ITEMS.register("recipe_board_beer_mug_apple_lambic", key -> new BlockItem(BlockRegistry.RECIPE_BOARD_BEER_MUG_APPLE_LAMBIC.get(), new Item.Properties().useBlockDescriptionPrefix().setId(key).stacksTo(1)));
    public static final Supplier<Item> RECIPE_BOARD_BEER_MUG_SWEET_BERRY_KRIEK = ITEMS.register("recipe_board_beer_mug_sweet_berry_kriek", key -> new BlockItem(BlockRegistry.RECIPE_BOARD_BEER_MUG_SWEET_BERRY_KRIEK.get(), new Item.Properties().useBlockDescriptionPrefix().setId(key).stacksTo(1)));
    public static final Supplier<Item> RECIPE_BOARD_BEER_MUG_HAARS_ICEY_PALE_LAGER = ITEMS.register("recipe_board_beer_mug_haars_icey_pale_lager", key -> new BlockItem(BlockRegistry.RECIPE_BOARD_BEER_MUG_HAARS_ICEY_PALE_LAGER.get(), new Item.Properties().useBlockDescriptionPrefix().setId(key).stacksTo(1)));
    public static final Supplier<Item> RECIPE_BOARD_BEER_MUG_PUMPKIN_KVASS = ITEMS.register("recipe_board_beer_mug_pumpkin_kvass", key -> new BlockItem(BlockRegistry.RECIPE_BOARD_BEER_MUG_PUMPKIN_KVASS.get(), new Item.Properties().useBlockDescriptionPrefix().setId(key).stacksTo(1)));
    public static final Supplier<Item> RECIPE_BOARD_BEER_MUG_NIGHT_HOWL_KVASS = ITEMS.register("recipe_board_beer_mug_night_howl_kvass", key -> new BlockItem(BlockRegistry.RECIPE_BOARD_BEER_MUG_NIGHT_HOWL_KVASS.get(), new Item.Properties().useBlockDescriptionPrefix().setId(key).stacksTo(1)));
    public static final Supplier<Item> RECIPE_BOARD_BEER_MUG_FROTHY_PINK_EGGNOG = ITEMS.register("recipe_board_beer_mug_frothy_pink_eggnog", key -> new BlockItem(BlockRegistry.RECIPE_BOARD_BEER_MUG_FROTHY_PINK_EGGNOG.get(), new Item.Properties().useBlockDescriptionPrefix().setId(key).stacksTo(1)));

    public static final Supplier<Item> RECIPE_BOARD_PACKAGE = ITEMS.register("recipe_board_package", key -> new BlockItem(BlockRegistry.RECIPE_BOARD_PACKAGE.get(), new Item.Properties().useBlockDescriptionPrefix().setId(key).stacksTo(1)));

    //beer
    public static final Supplier<Item> BEER_MUG = ITEMS.register("beer_mug", key -> new BeerMugItem(key, BlockRegistry.BEER_MUG.get(), new MobEffectInstance(MobEffects.HASTE, 1200), 2, true));
    public static final Supplier<Item> BEER_MUG_BLAZE_STOUT = ITEMS.register("beer_mug_blaze_stout", key -> new BeerMugItem(key, BlockRegistry.BEER_MUG_BLAZE_STOUT.get(), new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 1800), 2, true));
    public static final Supplier<Item> BEER_MUG_BLAZE_MILK_STOUT = ITEMS.register("beer_mug_blaze_milk_stout", key -> new BeerMugItem(key, BlockRegistry.BEER_MUG_BLAZE_MILK_STOUT.get(), new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 2400), 2, true));
    public static final Supplier<Item> BEER_MUG_APPLE_LAMBIC = ITEMS.register("beer_mug_apple_lambic", key -> new BeerMugItem(key, BlockRegistry.BEER_MUG_APPLE_LAMBIC.get(), new MobEffectInstance(MobEffects.REGENERATION, 300), 3, true));
    public static final Supplier<Item> BEER_MUG_SWEET_BERRY_KRIEK = ITEMS.register("beer_mug_sweet_berry_kriek", key -> new BeerMugItem(key, BlockRegistry.BEER_MUG_SWEET_BERRY_KRIEK.get(), new MobEffectInstance(MobEffects.REGENERATION, 400), 3, true));
    public static final Supplier<Item> BEER_MUG_HAARS_ICEY_PALE_LAGER = ITEMS.register("beer_mug_haars_icey_pale_lager", key -> new BeerMugItem(key, BlockRegistry.BEER_MUG_HAARS_ICEY_PALE_LAGER.get(), () -> new MobEffectInstance((Holder<MobEffect>) MobEffectRegistry.DRUNK_FROST_WALKER, 1200), 1, true));
    public static final Supplier<Item> BEER_MUG_PUMPKIN_KVASS = ITEMS.register("beer_mug_pumpkin_kvass", key -> new BeerMugItem(key, BlockRegistry.BEER_MUG_PUMPKIN_KVASS.get(), 9, false));
    public static final Supplier<Item> BEER_MUG_NIGHT_HOWL_KVASS = ITEMS.register("beer_mug_night_howl_kvass", key -> new BeerMugItem(key, BlockRegistry.BEER_MUG_NIGHT_HOWL_KVASS.get(), 4, true));
    public static final Supplier<Item> BEER_MUG_FROTHY_PINK_EGGNOG = ITEMS.register("beer_mug_frothy_pink_eggnog", key -> new BeerMugItem(key, BlockRegistry.BEER_MUG_FROTHY_PINK_EGGNOG.get(), new MobEffectInstance(MobEffects.ABSORPTION, 2400), 2, true));
    public static final Supplier<Item> MIXED_BEER = ITEMS.register("mixed_beer", key -> new MixedBeerBlockItem(key, BlockRegistry.MIXED_BEER.get()));

    // Spices
    public static final Supplier<Item> SPICE_BLAZE_PAPRIKA = ITEMS.register("spice_blaze_paprika", key -> new SpiceBlockItem(key, BlockRegistry.SPICE_BLAZE_PAPRIKA.get(), null, 1));
    public static final Supplier<Item> SPICE_DRIED_EGLIA_BUD = ITEMS.register("spice_dried_eglia_bud", key -> new SpiceBlockItem(key, BlockRegistry.SPICE_DRIED_EGLIA_BUD.get(), null, 1));
    public static final Supplier<Item> SPICE_SMOKED_EGLIA_BUD = ITEMS.register("spice_smoked_eglia_bud", key -> new SpiceBlockItem(key, BlockRegistry.SPICE_SMOKED_EGLIA_BUD.get(), null, 1));
    public static final Supplier<Item> SPICE_AMETHYST_NIGELLA_SEEDS = ITEMS.register("spice_amethyst_nigella_seeds", key -> new SpiceBlockItem(key, BlockRegistry.SPICE_AMETHYST_NIGELLA_SEEDS.get(), null, 1));
    public static final Supplier<Item> SPICE_CITRINE_NIGELLA_SEEDS = ITEMS.register("spice_citrine_nigella_seeds", key -> new SpiceBlockItem(key, BlockRegistry.SPICE_CITRINE_NIGELLA_SEEDS.get(), null, 1));
    public static final Supplier<Item> SPICE_ICE_MINT = ITEMS.register("spice_ice_mint", key -> new SpiceBlockItem(key, BlockRegistry.SPICE_ICE_MINT.get(), null, 1));
    public static final Supplier<Item> SPICE_ICE_PATCHOULI = ITEMS.register("spice_ice_patchouli", key -> new SpiceBlockItem(key, BlockRegistry.SPICE_ICE_PATCHOULI.get(), null, 1));
    public static final Supplier<Item> SPICE_STORM_SHARDS = ITEMS.register("spice_storm_shards", key -> new SpiceBlockItem(key, BlockRegistry.SPICE_STORM_SHARDS.get(), null, 1));
    public static final Supplier<Item> SPICE_ROASTED_RED_PINE_NUTS = ITEMS.register("spice_roasted_red_pine_nuts", key -> new SpiceBlockItem(key, BlockRegistry.SPICE_ROASTED_RED_PINE_NUTS.get(), null, 2));
    public static final Supplier<Item> SPICE_GLACE_GOJI_BERRIES = ITEMS.register("spice_glace_goji_berries", key -> new SpiceBlockItem(key, BlockRegistry.SPICE_GLACE_GOJI_BERRIES.get(), null, 1));
    public static final Supplier<Item> SPICE_FROZEN_PERSIMMON = ITEMS.register("spice_frozen_persimmon", key -> new SpiceBlockItem(key, BlockRegistry.SPICE_FROZEN_PERSIMMON.get(), null, 1));
    public static final Supplier<Item> SPICE_ROASTED_PECANS = ITEMS.register("spice_roasted_pecans", key -> new SpiceBlockItem(key, BlockRegistry.SPICE_ROASTED_PECANS.get(), null, 2));
    public static final Supplier<Item> SPICE_SILVER_NEEDLE_WHITE_TEA = ITEMS.register("spice_silver_needle_white_tea", key -> new SpiceBlockItem(key, BlockRegistry.SPICE_SILVER_NEEDLE_WHITE_TEA.get(), null, 2));
    public static final Supplier<Item> SPICE_GOLDEN_CINNAMON_POWDER = ITEMS.register("spice_golden_cinnamon_powder", key -> new SpiceBlockItem(key, BlockRegistry.SPICE_GOLDEN_CINNAMON_POWDER.get(), null, 2));
    public static final Supplier<Item> SPICE_DRIED_SELAGINELLA = ITEMS.register("spice_dried_selaginella", key -> new SpiceBlockItem(key, BlockRegistry.SPICE_DRIED_SELAGINELLA.get(), null, 2));
}
