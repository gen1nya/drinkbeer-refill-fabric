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

    /** Ingredient.EMPTY в 1.21.11 убрали, поэтому список собираем сами. */
    private static NonNullList<Ingredient> toNonNullList(List<Ingredient> list) {
        NonNullList<Ingredient> result = NonNullList.createWithCapacity(list.size());
        result.addAll(list);
        return result;
    }

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
            // С 1.21.2 ванильный HolderSet-кодек не принимает тег ВНУТРИ списка альтернатив.
            // У апстрима слот вида [{"item":"minecraft:wheat"},{"tag":"c:crops/wheat"}] — тег там
            // и так покрывает предмет, поэтому при наличии тега отдаём только его.
            List<T> elements = asList.get().toList();
            for (T element : elements) {
                var tagOnly = ops.get(element, "tag").flatMap(ops::getStringValue).result();
                if (tagOnly.isPresent()) {
                    return DataResult.success(ops.createString("#" + tagOnly.get()));
                }
            }
            List<T> converted = new ArrayList<>();
            for (T element : elements) {
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
        return DataResult.success(toNonNullList(list));
    }, nonNullList -> nonNullList);

    public final static Codec<NonNullList<Ingredient>> NON_NULL_LIST_4_INGREDIENT_CODEC = INGREDIENT_COMPAT_CODEC.listOf().comapFlatMap((list) -> {
        if(list.size()!=4) return DataResult.error(()->"Must be 4 ingredients", toNonNullList(list));
        return DataResult.success(toNonNullList(list));
    }, nonNullList -> nonNullList);
}
