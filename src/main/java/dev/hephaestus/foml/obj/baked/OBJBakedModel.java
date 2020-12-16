package dev.hephaestus.foml.obj.baked;

import dev.hephaestus.foml.obj.OBJBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Quaternion;
import net.minecraft.world.BlockRenderView;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class OBJBakedModel implements BakedModel, FabricBakedModel {
    private final Mesh mesh;
    private final ModelTransformation transformation;
    private final Sprite sprite;

    public OBJBakedModel(OBJBuilder builder, ModelTransformation transformation, Sprite sprite) {
        this.mesh = builder.build();
        this.transformation = transformation;
        this.sprite = sprite;
    }

    @Override
    public List<BakedQuad> getQuads(BlockState blockState, Direction direction, Random random) {
        List<BakedQuad>[] bakedQuads = ModelHelper.toQuadLists(mesh);
        return bakedQuads[direction == null ? 6 : direction.getId()];
    }

    @Override
    public void emitBlockQuads(BlockRenderView blockRenderView, BlockState blockState, BlockPos blockPos, Supplier<Random> supplier, RenderContext context) {
        if (blockState.getProperties().contains(Properties.HORIZONTAL_FACING)) {
            Quaternion rotate = Vector3f.POSITIVE_Y.getDegreesQuaternion(angle(blockState));
            RenderContext.QuadTransform transform = mv -> {
                Vector3f tmp = new Vector3f();

                for (int i = 0; i < 4; i++) {
                    // Transform the position (center of rotation is 0.5, 0.5, 0.5)
                    mv.copyPos(i, tmp);
                    tmp.add(-0.5f, -0.5f, -0.5f);
                    tmp.rotate(rotate);
                    tmp.add(0.5f, 0.5f, 0.5f);
                    mv.pos(i, tmp);

                    // Transform the normal
                    if (mv.hasNormal(i)) {
                        mv.copyNormal(i, tmp);
                        tmp.rotate(rotate);
                        mv.normal(i, tmp);
                    }
                }

                mv.nominalFace(blockState.get(Properties.HORIZONTAL_FACING));
                return true;
            };

            context.pushTransform(transform);
            context.meshConsumer().accept(mesh);
            context.popTransform();
        } else {
            context.meshConsumer().accept(mesh);
        }
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
        if (mesh != null) {
            context.meshConsumer().accept(mesh);
        }
    }

    @Override
    public boolean useAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean hasDepth() {
        return true;
    }

    @Override
    public boolean isSideLit() {
        return false;
    }

    @Override
    public boolean isBuiltin() {
        return false;
    }

    @Override
    public Sprite getSprite() {
        return sprite;
    }

    @Override
    public ModelTransformation getTransformation() {
        return transformation;
    }

    @Override
    public ModelOverrideList getOverrides() {
        return ModelOverrideList.EMPTY;
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    public static float angle(BlockState state) { switch (state.get(Properties.HORIZONTAL_FACING)) {
            case NORTH: return 0;
            case EAST: return 270;
            case SOUTH: return 180;
            case WEST: return 90;
            default: throw new RuntimeException();
        }
    }
}
