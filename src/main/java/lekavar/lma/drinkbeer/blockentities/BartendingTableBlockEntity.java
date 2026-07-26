package lekavar.lma.drinkbeer.blockentities;

import lekavar.lma.drinkbeer.items.BeerMugItem;
import lekavar.lma.drinkbeer.items.MixedBeerBlockItem;
import lekavar.lma.drinkbeer.items.SpiceBlockItem;
import lekavar.lma.drinkbeer.managers.MixedBeerManager;
import lekavar.lma.drinkbeer.registries.BlockEntityRegistry;
import lekavar.lma.drinkbeer.utils.beer.Beers;
import lekavar.lma.drinkbeer.utils.mixedbeer.Spices;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class BartendingTableBlockEntity extends BlockEntity implements WorldlyContainer {
    // Виртуальная раскладка слотов для автоматизации (как в апстримовском
    // BartendingTableInvWrapper): 0 — вход пива, 1 — готовый коктейль, 2 — вход специи.
    private static final int SLOT_BEER = 0;
    private static final int SLOT_RESULT = 1;
    private static final int SLOT_SPICE = 2;

    private final SimpleContainer inv = new OneItemContainer(2);

    public BartendingTableBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.BARTENDING_TABLE_TILEENTITY.get(), pos, state);
    }

    public boolean placeBeer(ItemStack itemStack) {
        if (!inv.isEmpty())
            return false;
        Item beerItem = itemStack.getItem();
        if (!(beerItem instanceof MixedBeerBlockItem) && !(beerItem instanceof BeerMugItem))
            return false;
        var spiceList = MixedBeerManager.getSpiceList(itemStack);
        if (spiceList.size() >= 3)
            return false;
        inv.setItem(0, itemStack);
        markDirty();
        return true;
    }

    public boolean putSpice(ItemStack itemStack) {
        if (!(itemStack.getItem() instanceof SpiceBlockItem))
            return false;
        if (inv.isEmpty())
            return false;
        if (!inv.getItem(1).isEmpty()) {
            var spiceList = MixedBeerManager.getSpiceList(inv.getItem(1));
            if (spiceList.size() >= 3)
                return false;
        }
        ItemStack beerItem = inv.getItem(0);
        if (beerItem.isEmpty())
            beerItem = inv.getItem(1);
        var beerId = beerItem.getItem() instanceof MixedBeerBlockItem ? MixedBeerBlockItem.getBeerId(beerItem) : Beers.byItem(beerItem.getItem()).getId();
        var spiceList = MixedBeerManager.getSpiceList(beerItem);
        spiceList.add(Spices.byItem(itemStack.getItem()).getId());
        ItemStack flavoredBeer = MixedBeerManager.genMixedBeerItemStack(beerId, spiceList);
        inv.setItem(0, ItemStack.EMPTY);
        inv.setItem(1, flavoredBeer);
        markDirty();
        return true;
    }

    public ItemStack takeBeer(boolean simulate) {
        var ret = inv.getItem(0).copy();
        if (ret.isEmpty())
            ret = inv.getItem(1).copy();
        if (!simulate && !ret.isEmpty()) {
            inv.clearContent();
            markDirty();
        }
        return ret;
    }


    public void markDirty() {
        var pos = getBlockPos();
        var bs = level.getBlockState(pos);
        level.sendBlockUpdated(pos, bs, bs, Block.UPDATE_CLIENTS);
        setChanged();
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        // The packet uses the CompoundTag returned by #getUpdateTag. An alternative overload of #create exists
        // that allows you to specify a custom update tag, including the ability to omit data the client might not need.
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        ContainerHelper.saveAllItems(tag, this.inv.getItems(), true, registries);
        return tag;
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag,registries);
        ContainerHelper.saveAllItems(tag, this.inv.getItems(), true, registries);
    }

    /**
     * ФИКС ОТНОСИТЕЛЬНО АПСТРИМА: {@code ContainerHelper.loadAllItems} только записывает
     * присланные слоты и НЕ чистит остальные (ванильные контейнеры поэтому пересоздают
     * список перед загрузкой — см. ChestBlockEntity#loadAdditional). Апстрим прикрывал это
     * NeoForge-патчем handleUpdateTag; на ванильном пути клиент получал «Items:[]» и
     * продолжал показывать снятый предмет. Для стола это выглядело как «пиво со стола
     * невозможно взять»: кружка остаётся нарисованной, хотя на сервере её уже нет.
     */
    @Override
    public void loadAdditional(@Nonnull CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag,registries);
        this.inv.clearContent();
        ContainerHelper.loadAllItems(tag, this.inv.getItems(), registries);
    }

    // ─── Автоматизация ──────────────────────────────────────────────────────────
    // Апстрим отдаёт хопперам IItemHandler c тремя виртуальными слотами
    // (BartendingTableInvWrapper). Тот же контракт на Fabric — через WorldlyContainer:
    // положить пиво в слот 0, специю в слот 2 (превращается в коктейль),
    // забрать готовое из слота 1. Слот 2 всегда пустой, это «воронка» для специи.

    @Override
    public int[] getSlotsForFace(@NotNull Direction side) {
        return new int[]{SLOT_BEER, SLOT_RESULT, SLOT_SPICE};
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, @NotNull ItemStack stack, @Nullable Direction direction) {
        return canPlaceItem(index, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int index, @NotNull ItemStack stack, @NotNull Direction direction) {
        return index == SLOT_RESULT && !inv.getItem(SLOT_RESULT).isEmpty();
    }

    @Override
    public boolean canPlaceItem(int index, @NotNull ItemStack stack) {
        if (index == SLOT_BEER) {
            Item item = stack.getItem();
            if (!(item instanceof MixedBeerBlockItem) && !(item instanceof BeerMugItem))
                return false;
            if (!inv.isEmpty())
                return false;
            return MixedBeerManager.getSpiceList(stack).size() < 3;
        }
        if (index == SLOT_SPICE) {
            if (!(stack.getItem() instanceof SpiceBlockItem))
                return false;
            if (inv.isEmpty())
                return false;
            if (!inv.getItem(SLOT_RESULT).isEmpty())
                return MixedBeerManager.getSpiceList(inv.getItem(SLOT_RESULT)).size() < 3;
            return true;
        }
        return false;
    }

    @Override
    public int getContainerSize() {
        return 3;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return inv.isEmpty();
    }

    @NotNull
    @Override
    public ItemStack getItem(int index) {
        if (index == SLOT_SPICE) return ItemStack.EMPTY;
        return inv.getItem(index);
    }

    @Override
    public void setItem(int index, @NotNull ItemStack stack) {
        switch (index) {
            case SLOT_BEER -> placeBeer(stack);
            // Специя не хранится: она сразу «уходит» в коктейль.
            case SLOT_SPICE -> putSpice(stack);
            default -> inv.setItem(index, stack);
        }
    }

    @NotNull
    @Override
    public ItemStack removeItem(int index, int count) {
        if (index != SLOT_RESULT || inv.getItem(SLOT_RESULT).isEmpty()) return ItemStack.EMPTY;
        return takeBeer(false);
    }

    @NotNull
    @Override
    public ItemStack removeItemNoUpdate(int index) {
        return removeItem(index, 1);
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        inv.clearContent();
    }

    static class OneItemContainer extends SimpleContainer {
        public OneItemContainer(int pSize) {
            super(pSize);
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }
    }
}
