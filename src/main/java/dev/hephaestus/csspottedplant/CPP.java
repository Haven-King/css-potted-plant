package dev.hephaestus.csspottedplant;

import dev.hephaestus.foml.obj.OBJLoader;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Block;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class CPP implements ModInitializer {
    public static final SoundEvent PLANT_BREAK_SOUND = Registry.register(Registry.SOUND_EVENT, new Identifier("css-potted-plant", "pottery_break"), new SoundEvent( new Identifier("css-potted-plant", "pottery_break")));
    public static final BlockSoundGroup PLANT_SOUNDGROUP = new BlockSoundGroup(1.0F, 1.0F, PLANT_BREAK_SOUND, SoundEvents.BLOCK_GLASS_STEP, SoundEvents.BLOCK_GLASS_PLACE, SoundEvents.BLOCK_GLASS_HIT, SoundEvents.BLOCK_GLASS_FALL);
    public static final PlantBlock PLANT_BLOCK = Registry.register(Registry.BLOCK, new Identifier("css-potted-plant", "plant"), new PlantBlock(Block.Settings.of(Material.PLANT, MaterialColor.GREEN).sounds(PLANT_SOUNDGROUP).nonOpaque()));
    public static final Item PLANT_ITEM = Registry.register(Registry.ITEM, new Identifier("css-potted-plant", "plant"), new PlantBlockItem(PLANT_BLOCK, new Item.Settings().group(ItemGroup.DECORATIONS).rarity(Rarity.RARE).maxCount(1)));

    @Override
    public void onInitialize() {
        OBJLoader.INSTANCE.registerDomain("css-potted-plant");
        DispenserBlock.registerBehavior(() -> PLANT_ITEM, new ItemDispenserBehavior() {
            @Override
            protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
                World world = pointer.getWorld();
                Position position = DispenserBlock.getOutputLocation(pointer);
                Direction direction = pointer.getBlockState().get(DispenserBlock.FACING);
                Direction facing = direction.getAxis() == Direction.Axis.Y ? Direction.NORTH : direction;
                FallingBlockEntity plant = new FallingBlockEntity(world, position.getX(), position.getY(), position.getZ(), PLANT_BLOCK.getDefaultState().with(Properties.HORIZONTAL_FACING, facing));
                PLANT_BLOCK.configureFallingBlockEntity(plant);
                Vec3d velocity = Vec3d.of(direction.getVector());
                plant.setVelocity(velocity.multiply(0.1));

                if (direction == Direction.UP) {
                    plant.addVelocity(0, 1.5, 0);
                }

                world.spawnEntity(plant);
                stack.decrement(1);
                return stack;
            }
        });
    }
}
