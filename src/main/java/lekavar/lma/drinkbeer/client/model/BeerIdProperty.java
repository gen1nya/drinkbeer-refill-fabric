package lekavar.lma.drinkbeer.client.model;

import com.mojang.serialization.MapCodec;
import lekavar.lma.drinkbeer.managers.MixedBeerManager;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemStack;

/**
 * Числовое свойство item-модели: отдаёт id базового пива коктейля.
 * <p>
 * До 1.21.4 это делалось через {@code ItemProperties.register("beer_id", …)} и предикаты
 * в {@code models/item/mixed_beer.json}. Оба механизма вырезаны: теперь вид предмета
 * описывается декларативно в {@code assets/drinkbeer/items/mixed_beer.json}, а своё
 * свойство надо положить в {@code RangeSelectItemModelProperties.ID_MAPPER}.
 * <p>
 * Возвращаем id как есть (1..9), а не делённый на 100, как было у апстрима, — пороги в
 * модели читаются человеком, а не подбираются по сотым.
 */
public record BeerIdProperty() implements RangeSelectItemModelProperty {

    public static final MapCodec<BeerIdProperty> MAP_CODEC = MapCodec.unit(new BeerIdProperty());

    @Override
    public float get(ItemStack stack, ClientLevel level, ItemOwner owner, int seed) {
        return MixedBeerManager.getBeerId(stack);
    }

    @Override
    public MapCodec<? extends RangeSelectItemModelProperty> type() {
        return MAP_CODEC;
    }
}
