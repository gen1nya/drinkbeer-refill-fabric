package lekavar.lma.drinkbeer.compat.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import lekavar.lma.drinkbeer.recipes.BrewingRecipe;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Раскладка одного рецепта варки в EMI:
 * <pre>
 *   ▣ ▣        ↦     ▣        4 ингредиента слева, стрелка со временем варки,
 *   ▣ ▣   ▣          готовое пиво справа, под ингредиентами — слот кружек.
 * </pre>
 */
public class BrewingEmiRecipe implements EmiRecipe {

    private static final int SLOT = 18;

    private final ResourceLocation id;
    private final List<EmiIngredient> inputs;
    private final EmiStack output;
    private final EmiIngredient cup;
    private final int brewingTime;

    public BrewingEmiRecipe(RecipeHolder<BrewingRecipe> holder) {
        BrewingRecipe recipe = holder.value();
        this.id = holder.id();
        this.brewingTime = recipe.getBrewingTime();
        this.cup = EmiStack.of(recipe.getBeerCup(), recipe.getBeerCup().getCount());
        this.output = EmiStack.of(recipe.getResultItem(null));

        this.inputs = new ArrayList<>();
        for (Ingredient ingredient : recipe.getIngredients()) {
            inputs.add(EmiIngredient.of(ingredient));
        }
        // кружки — тоже расходник, EMI должен учитывать их в дереве рецептов
        inputs.add(cup);
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return DrinkBeerEmiPlugin.BREWING;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return inputs;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return List.of(output);
    }

    @Override
    public int getDisplayWidth() {
        return 124;
    }

    @Override
    public int getDisplayHeight() {
        return 46;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        // 2×2 сетка ингредиентов
        for (int i = 0; i < 4 && i < inputs.size() - 1; i++) {
            int x = (i % 2) * SLOT;
            int y = (i / 2) * SLOT;
            widgets.addSlot(inputs.get(i), x, y);
        }

        // слот пустых кружек
        widgets.addSlot(cup, 44, 9 + SLOT / 2);

        // стрелка заполнения: время варки EMI ждёт в миллисекундах
        widgets.addFillingArrow(70, 9 + SLOT / 2 + 1, brewingTime * 50);
        widgets.addText(Component.translatable("drinkbeer.emi.brewing_time", formatTime(brewingTime))
                        .withStyle(ChatFormatting.GRAY),
                70, 4, -1, false);

        widgets.addSlot(output, 100, 9 + SLOT / 2).recipeContext(this);
    }

    private static String formatTime(int ticks) {
        int seconds = ticks / 20;
        return "%d:%02d".formatted(seconds / 60, seconds % 60);
    }
}
