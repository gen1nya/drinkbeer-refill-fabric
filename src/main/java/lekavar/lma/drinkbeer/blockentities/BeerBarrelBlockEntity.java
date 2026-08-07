package lekavar.lma.drinkbeer.blockentities;

import lekavar.lma.drinkbeer.gui.BeerBarrelMenu;
import lekavar.lma.drinkbeer.recipes.BrewingRecipe;
import lekavar.lma.drinkbeer.recipes.BrewingRecipeInput;
import lekavar.lma.drinkbeer.recipes.IBrewingInventory;
import lekavar.lma.drinkbeer.registries.BlockEntityRegistry;
import lekavar.lma.drinkbeer.registries.RecipeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.block.state.BlockState;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.WorldlyContainer;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BeerBarrelBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory<BlockPos>, WorldlyContainer {

    private static final int OUTPUT_SLOT = 5;

    private final BrewingInventory brewingInventory = new BrewingInventory(this);
    private int remainingBrewTime;
    // 0 - waiting for ingredient, 1 - brewing, 2 - waiting for pickup product
    private int statusCode;
    public final ContainerData syncData = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> remainingBrewTime;
                case 1 -> statusCode;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> remainingBrewTime = value;
                case 1 -> statusCode = value;
            }
        }

        @Override
        public int getCount() {
            return 1;
        }
    };

    public BeerBarrelBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.BEER_BARREL_TILEENTITY.get(), pos, state);
    }

    public void tickServer() {
        if (statusCode == 0) {
            if (brewingInventory.getIngredients().size() == 4) {
                IBrewingInventory recipeInput = new BrewingRecipeInput(brewingInventory);
                RecipeHolder<BrewingRecipe> recipeholder = level.recipeAccess().getRecipeFor(RecipeRegistry.RECIPE_TYPE_BREWING.get(), recipeInput, this.level).orElse(null);
                if (recipeholder==null) {
                    clearResult();
                    return;
                }
                var recipe = recipeholder.value();
                if (canBrew(recipe, recipeInput)) {
                    displayResult(recipe, recipeInput);
                    if (recipe.isCupQualified(recipeInput)) {
                        for (int i = 0; i < 4; i++) {
                            consumeIngredient(i);
                        }
                        brewingInventory.getItem(4).shrink(recipe.getRequiredCupCount());
                        remainingBrewTime = recipe.getBrewingTime();
                        statusCode = 1;
                        updateBE();
                    }
                }
            }
        }

        else if (statusCode == 1) {
            if (remainingBrewTime > 0) {
                remainingBrewTime--;
            }
            else {
                remainingBrewTime = 0;
                statusCode = 2;
            }
            setChanged();
        }
        else {
            if (brewingInventory.getItem(5).isEmpty()) {
                statusCode = 0;
                setChanged();
            }
        }
    }


    private boolean canBrew(@Nullable BrewingRecipe recipe, IBrewingInventory recipeInput) {
        return recipe.matches(recipeInput, this.level);
    }

    /**
     * ФИКС ОТНОСИТЕЛЬНО АПСТРИМА: апстрим стирал слот ингредиента целиком
     * ({@code setItem(i, EMPTY)}, а для ведра {@code setItem(i, BUCKET)}), поэтому
     * положенный в слот стак пропадал ради одного нужного рецепту предмета, а стак ведёр
     * с водой превращался в одно пустое ведро. Рецепт расходует ровно по одному предмету
     * на слот (те «три пшеницы» — это три отдельных слота), столько и снимаем.
     */
    private void consumeIngredient(int slot) {
        ItemStack ingredient = brewingInventory.getItem(slot);
        if (ingredient.isEmpty()) {
            return;
        }
        boolean returnsBucket = shouldReturnBucket(ingredient);

        ingredient.shrink(1);
        if (ingredient.isEmpty()) {
            brewingInventory.setItem(slot, ItemStack.EMPTY);
        }
        if (!returnsBucket) {
            return;
        }

        ItemStack emptyBucket = Items.BUCKET.getDefaultInstance();
        if (brewingInventory.getItem(slot).isEmpty()) {
            brewingInventory.setItem(slot, emptyBucket);
        } else {
            // Ванильные ведра нештабелируемые, так что сюда попасть нельзя; ветка —
            // страховка на случай модового ведра с maxStackSize > 1, чтобы пустое не пропало.
            Containers.dropItemStack(level, worldPosition.getX() + 0.5, worldPosition.getY() + 1.0,
                    worldPosition.getZ() + 0.5, emptyBucket);
        }
    }

    private boolean shouldReturnBucket(ItemStack item) {
        return item.getItem() instanceof BucketItem || item.is(Items.MILK_BUCKET);
    }

    private void clearResult() {
        if (!brewingInventory.getItem(5).isEmpty()) {
            brewingInventory.setItem(5, ItemStack.EMPTY);
            remainingBrewTime = 0;
            updateBE();
        }
    }

    private void displayResult(BrewingRecipe recipe, IBrewingInventory recipeInput) {
        var result = recipe.assemble(recipeInput, level.registryAccess());
        if (!ItemStack.matches(result, brewingInventory.getItem(5))) {
            brewingInventory.setItem(5, recipe.assemble(recipeInput, level.registryAccess()));
            remainingBrewTime = recipe.getBrewingTime();
            updateBE();
        }
    }

    public BrewingInventory getBrewingInventory() {
        return brewingInventory;
    }

    public void updateBE() {
        var pos = getBlockPos();
        var bs = level.getBlockState(pos);
        level.sendBlockUpdated(pos, bs, bs, Block.UPDATE_ALL_IMMEDIATE);
        setChanged();
    }

    @Override
    public void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, brewingInventory.getItems());
        output.putInt("RemainingBrewTime", this.remainingBrewTime);
        output.putInt("statusCode", this.statusCode);
    }

    @Override
    public void loadAdditional(@Nonnull ValueInput input) {
        super.loadAdditional(input);
        this.remainingBrewTime = input.getIntOr("RemainingBrewTime", 0);
        this.statusCode = input.getIntOr("statusCode", 0);
        // чистим перед загрузкой — loadAllItems снятые предметы не убирает (см. стол)
        brewingInventory.clearContent();
        ContainerHelper.loadAllItems(input, brewingInventory.getItems());
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.drinkbeer.beer_barrel");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new BeerBarrelMenu(id, brewingInventory, syncData, inventory, this);
    }

    @Override
    public BlockPos getScreenOpeningData(ServerPlayer player) {
        return getBlockPos();
    }

    // ─── Автоматизация ──────────────────────────────────────────────────────────
    // Апстрим отдаёт хопперам IItemHandler (BarrelInvWrapper): виден только слот
    // результата (5), забирать можно лишь когда statusCode == 2, кладут — никогда.
    // На Fabric ровно та же семантика выражается через WorldlyContainer, который
    // понимают и ванильные хопперы, и Transfer API.

    @Override
    public int[] getSlotsForFace(@NotNull Direction side) {
        return new int[]{OUTPUT_SLOT};
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, @NotNull ItemStack stack, @Nullable Direction direction) {
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int index, @NotNull ItemStack stack, @NotNull Direction direction) {
        return index == OUTPUT_SLOT && statusCode == 2;
    }

    @Override
    public boolean canPlaceItem(int index, @NotNull ItemStack stack) {
        return false;
    }

    @Override
    public int getContainerSize() {
        return brewingInventory.getContainerSize();
    }

    @Override
    public boolean isEmpty() {
        return brewingInventory.isEmpty();
    }

    @NotNull
    @Override
    public ItemStack getItem(int index) {
        // Пока варка не закончена, результат для автоматизации не существует.
        if (index == OUTPUT_SLOT && statusCode != 2) return ItemStack.EMPTY;
        return brewingInventory.getItem(index);
    }

    @NotNull
    @Override
    public ItemStack removeItem(int index, int count) {
        if (index == OUTPUT_SLOT && statusCode != 2) return ItemStack.EMPTY;
        return brewingInventory.removeItem(index, count);
    }

    @NotNull
    @Override
    public ItemStack removeItemNoUpdate(int index) {
        return brewingInventory.removeItemNoUpdate(index);
    }

    @Override
    public void setItem(int index, @NotNull ItemStack stack) {
        brewingInventory.setItem(index, stack);
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return brewingInventory.stillValid(player);
    }

    @Override
    public void clearContent() {
        brewingInventory.clearContent();
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        ContainerHelper.saveAllItems(tag,brewingInventory.getItems(),registries);
        return tag;
    }

    /**
     * Хранилище кега. Сознательно НЕ реализует IBrewingInventory (RecipeInput) —
     * см. комментарий в {@link BrewingRecipeInput}: на Fabric это ломает ремаппинг.
     */
    public static class BrewingInventory extends SimpleContainer {
        BeerBarrelBlockEntity be;

        public BrewingInventory(BeerBarrelBlockEntity be) {
            super(6);
            this.be = be;
        }

        @NotNull
        public List<ItemStack> getIngredients() {
            List<ItemStack> ret = new ArrayList<>();
            if (isEmpty()) return ret;
            for (int i = 0; i < 4; i++) {
                if (!getItem(i).isEmpty()) ret.add(getItem(i).copy());
            }
            return ret;
        }

        @NotNull
        public ItemStack getCup() {
            return getItem(4);
        }

        @Override
        public boolean canPlaceItem(int pIndex, ItemStack pStack) {
            return super.canPlaceItem(pIndex, pStack);
        }


        @Override
        public boolean stillValid(Player pPlayer) {
            if (be.level.getBlockEntity(be.worldPosition) != be) {
                return false;
            } else {
                return !(pPlayer.distanceToSqr((double) be.worldPosition.getX() + 0.5D, (double) be.worldPosition.getY() + 0.5D, (double) be.worldPosition.getZ() + 0.5D) > 64.0D);
            }
        }
    }

}
