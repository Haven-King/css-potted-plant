package dev.hephaestus.csspottedplant;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Random;

import static net.minecraft.entity.Entity.squaredHorizontalLength;

public class PlantBlockItem extends BlockItem {
    public PlantBlockItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_EGG_THROW, SoundCategory.PLAYERS, 0.5F, 0.4F / (RANDOM.nextFloat() * 0.4F + 0.8F));

        if (!world.isClient) {
            FallingBlockEntity plant = new FallingBlockEntity(world, user.getX(), user.getEyeY() - 0.10000000149011612D, user.getZ(), CPP.PLANT_BLOCK.getDefaultState());
            CPP.PLANT_BLOCK.configureFallingBlockEntity(plant);

            float k = -MathHelper.sin(user.yaw * 0.017453292F) * MathHelper.cos(user.pitch * 0.017453292F);
            float l = -MathHelper.sin(user.pitch * 0.017453292F);
            float m = MathHelper.cos(user.yaw * 0.017453292F) * MathHelper.cos(user.pitch * 0.017453292F);
            this.setVelocity(plant, world.getRandom(), k, l, m, 1.5F, 1F);
            plant.setVelocity(plant.getVelocity().add(user.getVelocity().x, user.isOnGround() ? 0.0D : user.getVelocity().y, user.getVelocity().z));

            world.spawnEntity(plant);
        }

        user.incrementStat(Stats.USED.getOrCreateStat(this));
        if (!user.abilities.creativeMode) {
            itemStack.decrement(1);
        }

        return TypedActionResult.success(itemStack);
    }



    private void setVelocity(Entity entity, Random random, double d, double e, double f, float g, float h) {
        Vec3d vec3d = (new Vec3d(d, e, f)).normalize().add(random.nextGaussian() * 0.007499999832361937D * (double)h, random.nextGaussian() * 0.007499999832361937D * (double)h, random.nextGaussian() * 0.007499999832361937D * (double)h).multiply(g);
        entity.setVelocity(vec3d);
        float i = MathHelper.sqrt(squaredHorizontalLength(vec3d));
        entity.yaw = (float)(MathHelper.atan2(vec3d.x, vec3d.z) * 57.2957763671875D);
        entity.pitch = (float)(MathHelper.atan2(vec3d.y, i) * 57.2957763671875D);
        entity.prevYaw = entity.yaw;
        entity.prevPitch = entity.pitch;
    }
}
