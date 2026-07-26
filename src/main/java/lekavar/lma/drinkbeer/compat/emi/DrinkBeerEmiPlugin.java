package lekavar.lma.drinkbeer.compat.emi;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import lekavar.lma.drinkbeer.DrinkBeer;
import lekavar.lma.drinkbeer.recipes.BrewingRecipe;
import lekavar.lma.drinkbeer.registries.ItemRegistry;
import lekavar.lma.drinkbeer.registries.RecipeRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

/**
 * Показывает рецепты варки в EMI. Апстримовский JEI-плагин выброшен — в клиент-паке
 * Kururun стоит EMI. Класс загружается только самим EMI, поэтому на сервере (и у клиентов
 * без EMI) он никогда не читается.
 */
@EmiEntrypoint
public class DrinkBeerEmiPlugin implements EmiPlugin {

    public static final ResourceLocation BREWING_ID =
            ResourceLocation.fromNamespaceAndPath(DrinkBeer.MOD_ID, "brewing");

    public static final EmiRecipeCategory BREWING =
            new EmiRecipeCategory(BREWING_ID, EmiStack.of(ItemRegistry.BEER_BARREL.get()));

    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(BREWING);
        registry.addWorkstation(BREWING, EmiStack.of(ItemRegistry.BEER_BARREL.get()));

        for (RecipeHolder<BrewingRecipe> holder :
                registry.getRecipeManager().getAllRecipesFor(RecipeRegistry.RECIPE_TYPE_BREWING.get())) {
            registry.addRecipe(new BrewingEmiRecipe(holder));
        }
    }
}
