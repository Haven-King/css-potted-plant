package dev.hephaestus.csspottedplant;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.*;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class PlantBlock extends FallingBlock {
    public static final VoxelShape OUTLINE = Block.createCuboidShape(
            3, 0, 3,
            13, 8, 13
    );

    public PlantBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.HORIZONTAL_FACING);
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(Properties.HORIZONTAL_FACING, ctx.getPlayerFacing().getOpposite());
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return OUTLINE;
    }

    @Override
    public void configureFallingBlockEntity(FallingBlockEntity entity) {
        ((FallingPlantBlock) entity).init();
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (player.getStackInHand(hand).isEmpty()) {
            player.setStackInHand(hand, new ItemStack(CPP.PLANT_ITEM));
            world.setBlockState(pos, Blocks.AIR.getDefaultState());

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    @Override
    public void onDestroyedOnLanding(World world, BlockPos pos, FallingBlockEntity fallingBlockEntity) {
        if (world instanceof ServerWorld) {
            world.playSound(null, pos, CPP.PLANT_BREAK_SOUND, SoundCategory.BLOCKS, 1F, 1F);
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeDouble(fallingBlockEntity.getX()).writeDouble(fallingBlockEntity.getY()).writeDouble(fallingBlockEntity.getZ());

            Packet<?> packet = ServerPlayNetworking.createS2CPacket(CPPNetworking.SPAWN_PLANT_BREAK_PARTICLES, buf);

            PlayerLookup.around((ServerWorld) world, fallingBlockEntity.getPos(), 32).forEach(player -> {
                    player.networkHandler.sendPacket(packet);
                }
            );
        }
    }
}
