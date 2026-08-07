package lekavar.lma.drinkbeer.items;

import lekavar.lma.drinkbeer.registries.ItemRegistry;
import lekavar.lma.drinkbeer.registries.SoundEventRegistry;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.joml.Vector3d;

import static java.lang.Math.pow;
import static net.minecraft.util.Mth.sqrt;


public class BeerBlockItem extends BlockItem {
    protected final static float MAX_PLACE_DISTANCE = (float) 2;

    public BeerBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    // Звук питья с 1.21.2 задаётся в компоненте Consumable (см. BeerMugItem#drinkWithEffect),
    // Item#getEatingSound больше не существует.

    public float getDistance(Vector3d p1, Vector3d p2) {
        return sqrt((float) (pow(p1.x - p2.x, 2) + pow(p1.y - p2.y, 2) + pow(p1.z - p2.z, 2)));
    }

    public void giveEmptyMugBack(LivingEntity user) {
        if (!(user instanceof Player) || !((Player) user).isCreative()) {
            ItemStack emptyMugItemStack = new ItemStack(ItemRegistry.EMPTY_BEER_MUG.get(), 1);
            if (user instanceof Player) {
                if (!((Player) user).addItem(emptyMugItemStack))
                    ((Player) user).drop(emptyMugItemStack, false);
            } else {
                if (user.level() instanceof ServerLevel serverLevel) user.spawnAtLocation(serverLevel, emptyMugItemStack);
            }
        }
    }
}
