package lekavar.lma.drinkbeer.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import lekavar.lma.drinkbeer.blockentities.MixedBeerBlockEntity;
import lekavar.lma.drinkbeer.registries.ItemRegistry;
import lekavar.lma.drinkbeer.utils.beer.Beers;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import com.mojang.math.Axis;

public class MixedBeerBlockEntityRenderer
        implements BlockEntityRenderer<MixedBeerBlockEntity, DrinkBeerRenderStates.MixedBeer> {

    private final ItemModelResolver itemModelResolver;

    public MixedBeerBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public DrinkBeerRenderStates.MixedBeer createRenderState() {
        return new DrinkBeerRenderStates.MixedBeer();
    }

    @Override
    public void extractRenderState(MixedBeerBlockEntity blockEntity, DrinkBeerRenderStates.MixedBeer state,
                                   float partialTick, Vec3 cameraPos,
                                   ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTick, cameraPos, crumblingOverlay);
        BlockPos pos = blockEntity.getBlockPos();
        state.angle = getRandomAngleByPos(pos);
        state.lightAbove = LevelRenderer.getLightColor(blockEntity.getLevel(), pos.above());
        itemModelResolver.updateForTopItem(state.beer, getBeerStack(blockEntity.getBeerId()),
                ItemDisplayContext.GROUND, blockEntity.getLevel(), null, 0);
    }

    @Override
    public void submit(DrinkBeerRenderStates.MixedBeer state, PoseStack poseStack,
                       SubmitNodeCollector collector, CameraRenderState cameraState) {
        poseStack.pushPose();
        //Move beer
        poseStack.translate(0.5, 0.25, 0.5);
        //Rotate beer
        poseStack.mulPose(Axis.YP.rotationDegrees(state.angle));
        state.beer.submit(poseStack, collector, state.lightAbove, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();
    }

    private ItemStack getBeerStack(int beerId) {
        if (beerId > Beers.EMPTY_BEER_ID) {
            Item item = Beers.byId(beerId).getBeerItem();
            return new ItemStack(item, 1);
        }
        return new ItemStack(ItemRegistry.MIXED_BEER.get().asItem(), 1);
    }

    private float getRandomAngleByPos(BlockPos pos) {
        int sum = Math.abs(pos.getX()) + Math.abs(pos.getZ()) + Math.abs(pos.getY());
        return 360 * ((float) Mth.abs(sum) % 8 / 8);
    }
}
