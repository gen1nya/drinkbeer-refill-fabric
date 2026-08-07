package lekavar.lma.drinkbeer.blockentities;

import lekavar.lma.drinkbeer.managers.MixedBeerManager;
import lekavar.lma.drinkbeer.registries.BlockEntityRegistry;
import lekavar.lma.drinkbeer.registries.DataComponentTypeRegistry;
import lekavar.lma.drinkbeer.utils.dataComponent.SpiceData;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class MixedBeerBlockEntity extends BlockEntity {
    private int beerId;
    private List<Integer> spiceList = new ArrayList<>();

    public MixedBeerBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.MIXED_BEER_TILEENTITY.get(), pos, state);
    }

    public MixedBeerBlockEntity(BlockPos pos, BlockState state, int beerId, List<Integer> spiceList) {
        super(BlockEntityRegistry.MIXED_BEER_TILEENTITY.get(), pos, state);
        this.beerId = beerId;
        this.spiceList.clear();
        this.spiceList.addAll(spiceList);
    }

    /**
     * @see MixedBeerManager#genMixedBeerItemStack(int, List)
     */
    @Override
    public void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);

        ValueOutput descriptor = output.child("MixedBeer");
        descriptor.putInt("beerId", getBeerId());
        descriptor.putIntArray("spiceList", getSpiceList().stream().mapToInt(Integer::intValue).toArray());
    }

    @Override
    public void loadAdditional(@Nonnull ValueInput input) {
        super.loadAdditional(input);

        ValueInput descriptor = input.childOrEmpty("MixedBeer");
        this.beerId = descriptor.getIntOr("beerId", 0);
        this.spiceList.clear();
        for (int spice : descriptor.getIntArray("spiceList").orElse(new int[0])) {
            this.spiceList.add(spice);
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        saveAdditional(tag,registries);

        return tag;
    }

    public ItemStack getPickStack() {
        //Generate mixed beer item stack for dropping
        ItemStack resultStack = MixedBeerManager.genMixedBeerItemStack(this.beerId, this.spiceList);
        return resultStack;
    }

    public List<Integer> getSpiceList() {
        return spiceList;
    }

    public int getBeerId() {
        return beerId;
    }

    /**
     * ФИКС ОТНОСИТЕЛЬНО АПСТРИМА: при установке коктейля на землю в блок-энтити никто не
     * писал ни beerId, ни специи — апстримовский {@code MixedBeerBlockItem#placeBlock}
     * только зовёт super, а конструктор с параметрами (pos, state, beerId, spiceList)
     * не вызывается нигде. В итоге у поставленной кружки beerId = 0: рендерер подставлял
     * generic-модель («кружка наебнулась»), а {@link #getPickStack()} возвращал коктейль
     * с нулевым базовым пивом, который выглядел пустым, хотя пить его можно.
     * <p>
     * Реализуем штатный ванильный механизм: {@code BlockItem#place} после установки зовёт
     * {@code BlockEntity#applyComponentsFromItemStack} и {@code setChanged()}, так что
     * данные приезжают из компонентов предмета и сами уходят клиенту.
     */
    @Override
    protected void applyImplicitComponents(DataComponentInput input) {
        super.applyImplicitComponents(input);

        Integer id = input.get(DataComponentTypeRegistry.BEER_ID_COMPONENT.get());
        if (id != null) {
            this.beerId = id;
        }
        SpiceData spices = input.get(DataComponentTypeRegistry.SPICE_COMPONENT.get());
        if (spices != null) {
            this.spiceList.clear();
            for (int spice : new int[]{spices.spiceA(), spices.spiceB(), spices.spiceC()}) {
                if (spice > 0) {
                    this.spiceList.add(spice);
                }
            }
        }
    }

    /** Обратный путь: middle-click по блоку и {@code saveToItem} дают корректный коктейль. */
    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        builder.set(DataComponentTypeRegistry.BEER_ID_COMPONENT.get(), this.beerId);
        builder.set(DataComponentTypeRegistry.SPICE_COMPONENT.get(), SpiceData.fromSpiceList(this.spiceList));
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

}