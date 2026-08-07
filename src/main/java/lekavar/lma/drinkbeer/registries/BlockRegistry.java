package lekavar.lma.drinkbeer.registries;

import lekavar.lma.drinkbeer.DrinkBeer;
import lekavar.lma.drinkbeer.blocks.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import lekavar.lma.drinkbeer.fabric.DeferredRegister;

import java.util.function.Supplier;


public class BlockRegistry {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, DrinkBeer.MOD_ID);

    //general
    public static final Supplier<Block> BEER_BARREL = BLOCKS.register("beer_barrel", key -> new BeerBarrelBlock(BeerBarrelBlock.settings().setId(key)));
    public static final Supplier<Block> BARTENDING_TABLE = BLOCKS.register("bartending_table_normal", key -> new BartendingTableBlock(BartendingTableBlock.settings().setId(key)));
    public static final Supplier<Block> TRADE_BOX = BLOCKS.register("trade_box_normal", key -> new TradeboxBlock(TradeboxBlock.settings().setId(key)));
    public static final Supplier<Block> EMPTY_BEER_MUG = BLOCKS.register("empty_beer_mug", key -> new BeerMugBlock(BeerMugBlock.settings().setId(key)));
    public static final Supplier<Block> IRON_CALL_BELL = BLOCKS.register("iron_call_bell", key -> new CallBellBlock(CallBellBlock.settings().setId(key)));
    public static final Supplier<Block> GOLDEN_CALL_BELL = BLOCKS.register("golden_call_bell", key -> new CallBellBlock(CallBellBlock.settings().setId(key)));
    public static final Supplier<Block> LEKAS_CALL_BELL = BLOCKS.register("lekas_call_bell", key -> new CallBellBlock(CallBellBlock.settings().setId(key)));

    public static final Supplier<Block> RECIPE_BOARD_BEER_MUG = BLOCKS.register("recipe_board_beer_mug", key -> new RecipeBoardBlock(RecipeBoardBlock.settings().setId(key), true));
    public static final Supplier<Block> RECIPE_BOARD_BEER_MUG_BLAZE_STOUT = BLOCKS.register("recipe_board_beer_mug_blaze_stout", key -> new RecipeBoardBlock(RecipeBoardBlock.settings().setId(key), true));
    public static final Supplier<Block> RECIPE_BOARD_BEER_MUG_BLAZE_MILK_STOUT = BLOCKS.register("recipe_board_beer_mug_blaze_milk_stout", key -> new RecipeBoardBlock(RecipeBoardBlock.settings().setId(key), true));
    public static final Supplier<Block> RECIPE_BOARD_BEER_MUG_APPLE_LAMBIC = BLOCKS.register("recipe_board_beer_mug_apple_lambic", key -> new RecipeBoardBlock(RecipeBoardBlock.settings().setId(key), true));
    public static final Supplier<Block> RECIPE_BOARD_BEER_MUG_SWEET_BERRY_KRIEK = BLOCKS.register("recipe_board_beer_mug_sweet_berry_kriek", key -> new RecipeBoardBlock(RecipeBoardBlock.settings().setId(key), true));
    public static final Supplier<Block> RECIPE_BOARD_BEER_MUG_HAARS_ICEY_PALE_LAGER = BLOCKS.register("recipe_board_beer_mug_haars_icey_pale_lager", key -> new RecipeBoardBlock(RecipeBoardBlock.settings().setId(key), true));
    public static final Supplier<Block> RECIPE_BOARD_BEER_MUG_PUMPKIN_KVASS = BLOCKS.register("recipe_board_beer_mug_pumpkin_kvass", key -> new RecipeBoardBlock(RecipeBoardBlock.settings().setId(key), true));
    public static final Supplier<Block> RECIPE_BOARD_BEER_MUG_NIGHT_HOWL_KVASS = BLOCKS.register("recipe_board_beer_mug_night_howl_kvass", key -> new RecipeBoardBlock(RecipeBoardBlock.settings().setId(key), true));
    public static final Supplier<Block> RECIPE_BOARD_BEER_MUG_FROTHY_PINK_EGGNOG = BLOCKS.register("recipe_board_beer_mug_frothy_pink_eggnog", key -> new RecipeBoardBlock(RecipeBoardBlock.settings().setId(key), true));

    public static final Supplier<Block> RECIPE_BOARD_PACKAGE = BLOCKS.register("recipe_board_package", key -> new RecipeBoardPackageBlock(RecipeBoardPackageBlock.settings().setId(key)));

    //beer
    public static final Supplier<Block> BEER_MUG = BLOCKS.register("beer_mug", key -> new BeerMugBlock(BeerMugBlock.settings().setId(key)));
    public static final Supplier<Block> BEER_MUG_BLAZE_STOUT = BLOCKS.register("beer_mug_blaze_stout", key -> new BeerMugBlock(BeerMugBlock.settings().setId(key)));
    public static final Supplier<Block> BEER_MUG_BLAZE_MILK_STOUT = BLOCKS.register("beer_mug_blaze_milk_stout", key -> new BeerMugBlock(BeerMugBlock.settings().setId(key)));
    public static final Supplier<Block> BEER_MUG_APPLE_LAMBIC = BLOCKS.register("beer_mug_apple_lambic", key -> new BeerMugBlock(BeerMugBlock.settings().setId(key)));
    public static final Supplier<Block> BEER_MUG_SWEET_BERRY_KRIEK = BLOCKS.register("beer_mug_sweet_berry_kriek", key -> new BeerMugBlock(BeerMugBlock.settings().setId(key)));
    public static final Supplier<Block> BEER_MUG_HAARS_ICEY_PALE_LAGER = BLOCKS.register("beer_mug_haars_icey_pale_lager", key -> new BeerMugBlock(BeerMugBlock.settings().setId(key)));
    public static final Supplier<Block> BEER_MUG_PUMPKIN_KVASS = BLOCKS.register("beer_mug_pumpkin_kvass", key -> new BeerMugBlock(BeerMugBlock.settings().setId(key)));
    public static final Supplier<Block> BEER_MUG_NIGHT_HOWL_KVASS = BLOCKS.register("beer_mug_night_howl_kvass", key -> new BeerMugBlock(BeerMugBlock.settings().setId(key)));
    public static final Supplier<Block> BEER_MUG_FROTHY_PINK_EGGNOG = BLOCKS.register("beer_mug_frothy_pink_eggnog", key -> new BeerMugBlock(BeerMugBlock.settings().setId(key)));
    public static final Supplier<Block> MIXED_BEER = BLOCKS.register("mixed_beer", key -> new MixedBeerBlock(MixedBeerBlock.settings().setId(key)));

    // Spices
    public static final Supplier<Block> SPICE_BLAZE_PAPRIKA = BLOCKS.register("spice_blaze_paprika", key -> new SpiceBlock(SpiceBlock.settings().setId(key)));
    public static final Supplier<Block> SPICE_DRIED_EGLIA_BUD = BLOCKS.register("spice_dried_eglia_bud", key -> new SpiceBlock(SpiceBlock.settings().setId(key)));
    public static final Supplier<Block> SPICE_SMOKED_EGLIA_BUD = BLOCKS.register("spice_smoked_eglia_bud", key -> new SpiceBlock(SpiceBlock.settings().setId(key)));
    public static final Supplier<Block> SPICE_AMETHYST_NIGELLA_SEEDS = BLOCKS.register("spice_amethyst_nigella_seeds", key -> new SpiceBlock(SpiceBlock.settings().setId(key)));
    public static final Supplier<Block> SPICE_CITRINE_NIGELLA_SEEDS = BLOCKS.register("spice_citrine_nigella_seeds", key -> new SpiceBlock(SpiceBlock.settings().setId(key)));
    public static final Supplier<Block> SPICE_ICE_MINT = BLOCKS.register("spice_ice_mint", key -> new SpiceBlock(SpiceBlock.settings().setId(key)));
    public static final Supplier<Block> SPICE_ICE_PATCHOULI = BLOCKS.register("spice_ice_patchouli", key -> new SpiceBlock(SpiceBlock.settings().setId(key)));
    public static final Supplier<Block> SPICE_STORM_SHARDS = BLOCKS.register("spice_storm_shards", key -> new SpiceBlock(SpiceBlock.settings().setId(key)));
    public static final Supplier<Block> SPICE_ROASTED_RED_PINE_NUTS = BLOCKS.register("spice_roasted_red_pine_nuts", key -> new SpiceBlock(SpiceBlock.settings().setId(key)));
    public static final Supplier<Block> SPICE_GLACE_GOJI_BERRIES = BLOCKS.register("spice_glace_goji_berries", key -> new SpiceBlock(SpiceBlock.settings().setId(key)));
    public static final Supplier<Block> SPICE_FROZEN_PERSIMMON = BLOCKS.register("spice_frozen_persimmon", key -> new SpiceBlock(SpiceBlock.settings().setId(key), SpiceBlock.SPICE_FROZEN_PERSIMMON_SHAPE));
    public static final Supplier<Block> SPICE_ROASTED_PECANS = BLOCKS.register("spice_roasted_pecans", key -> new SpiceBlock(SpiceBlock.settings().setId(key)));
    public static final Supplier<Block> SPICE_SILVER_NEEDLE_WHITE_TEA = BLOCKS.register("spice_silver_needle_white_tea", key -> new SpiceBlock(SpiceBlock.settings().setId(key)));
    public static final Supplier<Block> SPICE_GOLDEN_CINNAMON_POWDER = BLOCKS.register("spice_golden_cinnamon_powder", key -> new SpiceBlock(SpiceBlock.settings().setId(key)));
    public static final Supplier<Block> SPICE_DRIED_SELAGINELLA = BLOCKS.register("spice_dried_selaginella", key -> new SpiceBlock(SpiceBlock.settings().setId(key), SpiceBlock.SPICE_DRIED_SELAGINELLA));

}
