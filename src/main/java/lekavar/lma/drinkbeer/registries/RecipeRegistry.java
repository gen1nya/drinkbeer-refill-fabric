package lekavar.lma.drinkbeer.registries;

import lekavar.lma.drinkbeer.DrinkBeer;
import lekavar.lma.drinkbeer.recipes.BrewingRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import lekavar.lma.drinkbeer.fabric.DeferredRegister;

import java.util.function.Supplier;

public class RecipeRegistry {
    public static final DeferredRegister<RecipeBookCategory> RECIPE_BOOK_CATEGORIES =
            DeferredRegister.create(Registries.RECIPE_BOOK_CATEGORY, DrinkBeer.MOD_ID);
    public static final Supplier<RecipeBookCategory> RECIPE_BOOK_CATEGORY_BREWING =
            RECIPE_BOOK_CATEGORIES.register("brewing", RecipeBookCategory::new);

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, DrinkBeer.MOD_ID);
    public static final Supplier<RecipeType<BrewingRecipe>> RECIPE_TYPE_BREWING = RECIPE_TYPES.register("brewing", () -> new RecipeType<BrewingRecipe>() {
        @Override
        public String toString() {
            return DrinkBeer.MOD_ID + ":brewing";
        }
    });
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, DrinkBeer.MOD_ID);
    public static final Supplier<RecipeSerializer<BrewingRecipe>> RECIPE_SERIALIZER_BREWING = RECIPE_SERIALIZERS.register("brewing", BrewingRecipe.Serializer::new);
}
