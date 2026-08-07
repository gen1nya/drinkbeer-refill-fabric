package lekavar.lma.drinkbeer.utils;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class DrinkBeerCodes {

    /**
     * Ингредиент в ванильном ИЛИ в NeoForge-формате.
     * <p>
     * Рецепты мода написаны так, как их понимает NeoForge:
     * {@code {"item": "minecraft:wheat"}}, {@code {"tag": "c:crops/wheat"}} и списки из них.
     * Ванильный {@link Ingredient#CODEC_NONEMPTY} ждёт {@code "minecraft:wheat"} и
     * {@code "#c:crops/wheat"}. Кодек сначала пробует ванильный разбор, а если не вышло —
     * переписывает JSON в ванильную форму и повторяет. Так рецепты апстрима работают
     * без правок, но датапаки в ванильном формате тоже принимаются.
     */
    public final static Codec<Ingredient> INGREDIENT_COMPAT_CODEC = new Codec<>() {
        @Override
        public <T> DataResult<Pair<Ingredient, T>> decode(DynamicOps<T> ops, T input) {
            DataResult<Pair<Ingredient, T>> vanilla = Ingredient.CODEC.decode(ops, input);
            if (vanilla.result().isPresent()) {
                return vanilla;
            }
            return toVanillaForm(ops, input).flatMap(converted -> Ingredient.CODEC.decode(ops, converted));
        }

        @Override
        public <T> DataResult<T> encode(Ingredient input, DynamicOps<T> ops, T prefix) {
            return Ingredient.CODEC.encode(input, ops, prefix);
        }
    };

    private static <T> DataResult<T> toVanillaForm(DynamicOps<T> ops, T input) {
        var asList = ops.getStream(input).result();
        if (asList.isPresent()) {
            List<T> converted = new ArrayList<>();
            for (T element : asList.get().toList()) {
                DataResult<T> single = toVanillaForm(ops, element);
                if (single.result().isEmpty()) {
                    return single;
                }
                converted.add(single.result().get());
            }
            return DataResult.success(ops.createList(converted.stream()));
        }

        var item = ops.get(input, "item").flatMap(ops::getStringValue).result();
        if (item.isPresent()) {
            return DataResult.success(ops.createString(item.get()));
        }

        var tag = ops.get(input, "tag").flatMap(ops::getStringValue).result();
        if (tag.isPresent()) {
            return DataResult.success(ops.createString("#" + tag.get()));
        }

        return DataResult.error(() -> "Не ингредиент ни в ванильном, ни в NeoForge-формате: " + input);
    }

    public final static Codec<NonNullList<Ingredient>> NON_NULL_LIST_INGREDIENT_CODEC = INGREDIENT_COMPAT_CODEC.listOf().comapFlatMap((list) -> {
        Ingredient[] allingredient = list.toArray(Ingredient[]::new);
        return DataResult.success(NonNullList.of(Ingredient.EMPTY, allingredient));
    }, nonNullList -> nonNullList);

    public final static Codec<NonNullList<Ingredient>> NON_NULL_LIST_4_INGREDIENT_CODEC = INGREDIENT_COMPAT_CODEC.listOf().comapFlatMap((list) -> {
        Ingredient[] allingredient = list.toArray(Ingredient[]::new);
        if(allingredient.length!=4) return DataResult.error(()->"Must be 4 ingredients", NonNullList.of(Ingredient.EMPTY, allingredient));
        return DataResult.success(NonNullList.of(Ingredient.EMPTY, allingredient));
    }, nonNullList -> nonNullList);
}
