package dev.hephaestus.csspottedplant;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.particle.BlockDustParticle;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;

public class CPPNetworking implements ClientModInitializer {
    public static final Identifier SPAWN_PLANT_BREAK_PARTICLES = new Identifier("css-potted-plant", "break");

    @Override
    public void onInitializeClient() {
        ClientPlayConnectionEvents.INIT.register((handler, client) -> {
            ClientPlayNetworking.registerReceiver(SPAWN_PLANT_BREAK_PARTICLES, CPPNetworking::spawnPlantBreakParticles);
        });
    }

    public static void spawnPlantBreakParticles(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        double x = buf.readDouble();
        double y = buf.readDouble();
        double z = buf.readDouble();
        BlockPos pos = new BlockPos(x, y, z);

        client.execute(() -> {
            VoxelShape voxelShape = PlantBlock.OUTLINE;
            voxelShape.forEachBox((xMin, yMin, zMin, xMax, yMax, zMax) -> {
                double xSize = Math.min(1.0D, xMax - xMin);
                double ySize = Math.min(1.0D, yMax - yMin);
                double zSize = Math.min(1.0D, zMax - zMin);
                int xSpread = Math.max(2, MathHelper.ceil(xSize / 0.25D));
                int ySpread = Math.max(2, MathHelper.ceil(ySize / 0.25D));
                int zSpread = Math.max(2, MathHelper.ceil(zSize / 0.25D));

                for(int p = 0; p < xSpread; ++p) {
                    for(int q = 0; q < ySpread; ++q) {
                        for(int r = 0; r < zSpread; ++r) {
                            double a = ((double)p + 0.5D) / (double)xSpread;
                            double b = ((double)q + 0.5D) / (double)ySpread;
                            double c = ((double)r + 0.5D) / (double)zSpread;
                            double dX = a * xSize + xMin;
                            double dY = b * ySize + yMin;
                            double dZ = c * zSize + zMin;

                            client.particleManager.addParticle((new BlockDustParticle(client.world, x + dX, y + dY, z + dZ, a - 0.5D, b - 0.5D, c - 0.5D, CPP.PLANT_BLOCK.getDefaultState())).setBlockPos(pos));
                        }
                    }
                }
            });
        });
    }
}
