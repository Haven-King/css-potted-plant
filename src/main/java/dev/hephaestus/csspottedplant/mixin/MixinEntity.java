package dev.hephaestus.csspottedplant.mixin;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Entity.class)
public abstract class MixinEntity {
//    @Shadow public World world;
//
//    @Shadow public abstract BlockPos getBlockPos();
//
//    @Unique private BlockPos.Mutable mut = null;
//
//    @Inject(method = "checkBlockCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;onBlockCollision(Lnet/minecraft/block/BlockState;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
//    private void captureCollisionPos(CallbackInfo ci, Box box, BlockPos pos, BlockPos blockPos2, BlockPos.Mutable mut) {
//        this.mut = mut;
//    }
//
//    @SuppressWarnings("ConstantConditions")
//    @Inject(method = "onBlockCollision", at = @At("TAIL"))
//    private void breakPots(BlockState state, CallbackInfo ci) {
//        if ((Object) this instanceof FallingBlockEntity && ((FallingBlockEntity) (Object) this).getBlockState().isOf(CssPottedPlant.PLANT_BLOCK)) {
//                if (!state.getCollisionShape(this.world, mut).isEmpty()) {
//                FallingBlockEntity falling = ((FallingBlockEntity) (Object) this);
//                FallingBlock block = (FallingBlock) falling.getBlockState().getBlock();
//
//                block.onDestroyedOnLanding(this.world, this.getBlockPos(), falling);
//                falling.remove();
//            }
//        }
//    }
}
