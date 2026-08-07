package lekavar.lma.drinkbeer.client.renderers;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;

/**
 * С 1.21.9 рендереры блок-энтити работают через render-state: данные снимаются с блок-энтити
 * в отдельный объект (extractRenderState), а отрисовка идёт уже из него (submit) — без доступа
 * к самому блок-энтити. Здесь состояния для наших двух рендереров.
 */
public final class DrinkBeerRenderStates {

    /** Коктейль, поставленный на землю: своя модель пива + разворот по позиции. */
    public static class MixedBeer extends BlockEntityRenderState {
        public final ItemStackRenderState beer = new ItemStackRenderState();
        public float angle;
        public int lightAbove;
    }

    /** Кружка, стоящая на бартендинг-столе: может отсутствовать. */
    public static class BartendingTable extends BlockEntityRenderState {
        public final ItemStackRenderState beer = new ItemStackRenderState();
        public boolean hasBeer;
        public int lightAbove;
    }

    private DrinkBeerRenderStates() {
    }
}
