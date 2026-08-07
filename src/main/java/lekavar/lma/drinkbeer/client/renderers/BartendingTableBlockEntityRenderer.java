package lekavar.lma.drinkbeer.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import lekavar.lma.drinkbeer.blockentities.BartendingTableBlockEntity;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class BartendingTableBlockEntityRenderer
        implements BlockEntityRenderer<BartendingTableBlockEntity, DrinkBeerRenderStates.BartendingTable> {

    private final ItemModelResolver itemModelResolver;

    public BartendingTableBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public DrinkBeerRenderStates.BartendingTable createRenderState() {
        return new DrinkBeerRenderStates.BartendingTable();
    }

    @Override
    public void extractRenderState(BartendingTableBlockEntity blockEntity, DrinkBeerRenderStates.BartendingTable state,
                                   float partialTick, Vec3 cameraPos,
                                   ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTick, cameraPos, crumblingOverlay);
        ItemStack beerStack = blockEntity.takeBeer(true);
        state.hasBeer = !beerStack.isEmpty();
        state.lightAbove = LevelRenderer.getLightColor(blockEntity.getLevel(), blockEntity.getBlockPos().above());
        if (state.hasBeer) {
            itemModelResolver.updateForTopItem(state.beer, beerStack, ItemDisplayContext.GROUND,
                    blockEntity.getLevel(), null, 0);
        } else {
            state.beer.clear();
        }
    }

    @Override
    public void submit(DrinkBeerRenderStates.BartendingTable state, PoseStack poseStack,
                       SubmitNodeCollector collector, CameraRenderState cameraState) {
        if (!state.hasBeer) {
            return;
        }
        poseStack.pushPose();
        poseStack.translate(0.5, 1.25, 0.5);
        state.beer.submit(poseStack, collector, state.lightAbove, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();
    }
}
