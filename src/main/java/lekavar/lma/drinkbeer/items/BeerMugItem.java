package lekavar.lma.drinkbeer.items;

import lekavar.lma.drinkbeer.effects.DrunkStatusEffect;
import lekavar.lma.drinkbeer.effects.NightHowlStatusEffect;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import lekavar.lma.drinkbeer.registries.SoundEventRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;

import net.minecraft.world.item.Item;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;
import java.util.ArrayList;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public class BeerMugItem extends BeerBlockItem {

    /**
     * С 1.21.2 эффекты еды живут не в FoodProperties, а в компоненте CONSUMABLE
     * как ConsumeEffect. Собираем питьё с одним эффектом (или без него).
     */
    static Consumable drinkWithEffect(@Nullable MobEffectInstance effect) {
        Consumable.Builder builder = Consumable.builder()
                .sound(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEventRegistry.DRINKING_BEER.get()));
        if (effect != null) {
            builder.onConsume(new ApplyStatusEffectsConsumeEffect(effect, 1.0F));
        }
        return builder.build();
    }

    private final static double MAX_PLACE_DISTANCE = 2.0D;
    private final boolean hasExtraTooltip;

    public BeerMugItem(ResourceKey<Item> key, Block block, int nutrition, boolean hasExtraTooltip) {
        super(block, new Item.Properties().useBlockDescriptionPrefix().setId(key).stacksTo(16)
                .food(new FoodProperties.Builder().nutrition(nutrition).alwaysEdible().build(),
                        drinkWithEffect(null)));
        this.hasExtraTooltip = hasExtraTooltip;
    }

    public BeerMugItem(ResourceKey<Item> key, Block block, @Nullable MobEffectInstance statusEffectInstance, int nutrition, boolean hasExtraTooltip) {
        super(block, new Item.Properties().useBlockDescriptionPrefix().setId(key).stacksTo(16)
                .food(new FoodProperties.Builder().nutrition(nutrition).alwaysEdible().build(),
                        drinkWithEffect(statusEffectInstance)));
        this.hasExtraTooltip = hasExtraTooltip;
    }

    public BeerMugItem(ResourceKey<Item> key, Block block, Supplier<MobEffectInstance> statusEffectInstance, int nutrition, boolean hasExtraTooltip) {
        super(block, new Item.Properties().useBlockDescriptionPrefix().setId(key).stacksTo(16)
                .food(new FoodProperties.Builder().nutrition(nutrition).alwaysEdible().build(),
                        drinkWithEffect(statusEffectInstance == null ? null : statusEffectInstance.get())));
        this.hasExtraTooltip = hasExtraTooltip;
    }

    @Override
    protected boolean canPlace(BlockPlaceContext context, BlockState state) {
        if ((context.getClickLocation().distanceTo(context.getPlayer().position()) > MAX_PLACE_DISTANCE))
            return false;
        else {
            return super.canPlace(context, state);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display,
                                Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag) {
        List<Component> tooltipComponents = new ArrayList<>();
        String name = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
        if (hasEffectNoticeTooltip()) {
            tooltipComponents.add(Component.translatable("item.drinkbeer." + name + ".tooltip").setStyle(Style.EMPTY.applyFormat(ChatFormatting.BLUE)));
        }
        String hunger = String.valueOf(stack.get(DataComponents.FOOD).nutrition());
        tooltipComponents.add(Component.translatable("drinkbeer.restores_hunger").setStyle(Style.EMPTY.applyFormat(ChatFormatting.BLUE)).append(hunger));
    }

    private boolean hasEffectNoticeTooltip() {
        return this.hasExtraTooltip;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity user) {
        //Give Drunk status effect
        DrunkStatusEffect.addStatusEffect(user);
        //Give Night Vision status effect if drank Night Howl Kvass
        NightHowlStatusEffect.addStatusEffect(stack, world, user);
        //Give empty mug back
        giveEmptyMugBack(user);

        return super.finishUsingItem(stack, world, user);
    }
}
