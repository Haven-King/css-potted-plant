package dev.hephaestus.csspottedplant.mixin;

import dev.hephaestus.csspottedplant.CPP;
import dev.hephaestus.csspottedplant.FallingPlantBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FallingBlockEntity.class)
public abstract class MixinFallingBlockEntity extends Entity implements FallingPlantBlock {
    @Shadow private boolean destroyedOnLanding;

    @Shadow public int timeFalling;

    @Shadow public abstract BlockState getBlockState();

    @Shadow private BlockState block;

    public MixinFallingBlockEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    public void init() {
        this.destroyedOnLanding = true;
        this.timeFalling = 1;
    }

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "tick", at = @At("TAIL"))
    private void destroyOnHorizontalCollision(CallbackInfo ci) {

        if (this.block.isOf(CPP.PLANT_BLOCK)) {
            this.destroyedOnLanding = true;

            if (!this.world.isClient && this.horizontalCollision) {
                FallingBlock block = (FallingBlock) this.getBlockState().getBlock();

                block.onDestroyedOnLanding(this.world, this.getBlockPos(), (FallingBlockEntity) (Object) this);
                this.remove();
            }
        }
    }
}
