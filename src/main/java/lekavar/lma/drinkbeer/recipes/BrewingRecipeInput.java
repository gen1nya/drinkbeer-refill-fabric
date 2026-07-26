package lekavar.lma.drinkbeer.recipes;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Вход рецепта варки: обёртка над инвентарём кега.
 * <p>
 * ⚠️ Почему это отдельный класс, а не сам инвентарь (как в апстриме).
 * В Mojang-маппингах {@code Container#getItem(int)} и {@code RecipeInput#getItem(int)}
 * выглядят одинаково, поэтому апстримовское
 * {@code class BrewingInventory extends SimpleContainer implements IBrewingInventory}
 * компилируется и работает на NeoForge (там mojmap в рантайме). На Fabric jar
 * ремапится в интермедиари, где это два РАЗНЫХ метода
 * ({@code method_5438} у Container и {@code method_59984} у RecipeInput), и
 * унаследованный от SimpleContainer {@code getItem} реализацию RecipeInput не даёт:
 * первый же поиск рецепта падает с
 * {@code AbstractMethodError ... does not define or inherit ... method_59984}.
 * <p>
 * Здесь коллизии нет: класс наследует только RecipeInput, поэтому объявленный
 * {@link #getItem(int)} ремапится однозначно, а обращение к контейнеру идёт через
 * поле типа {@link Container}.
 */
public class BrewingRecipeInput implements IBrewingInventory {

    /** Слоты кега: 0..3 — ингредиенты, 4 — пустые кружки, 5 — результат. */
    private static final int INGREDIENT_SLOTS = 4;
    private static final int CUP_SLOT = 4;

    private final Container container;

    public BrewingRecipeInput(Container container) {
        this.container = container;
    }

    @Override
    public ItemStack getItem(int index) {
        return container.getItem(index);
    }

    @Override
    public int size() {
        return container.getContainerSize();
    }

    @Nonnull
    @Override
    public List<ItemStack> getIngredients() {
        List<ItemStack> ingredients = new ArrayList<>();
        for (int i = 0; i < INGREDIENT_SLOTS; i++) {
            if (!container.getItem(i).isEmpty()) {
                ingredients.add(container.getItem(i).copy());
            }
        }
        return ingredients;
    }

    @Nonnull
    @Override
    public ItemStack getCup() {
        return container.getItem(CUP_SLOT);
    }
}
