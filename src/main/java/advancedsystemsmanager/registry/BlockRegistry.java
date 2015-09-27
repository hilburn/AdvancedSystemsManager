package advancedsystemsmanager.registry;

import advancedsystemsmanager.blocks.*;
import advancedsystemsmanager.compatibility.appliedenergistics.AECompat;
import advancedsystemsmanager.compatibility.rf.RFCompat;
import advancedsystemsmanager.items.blocks.*;
import advancedsystemsmanager.recipes.*;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class BlockRegistry
{
    public static int BLOCKS_TO_REGISTER = 4;
    public static BlockTileElement[] cableElements;

    public static void registerBlocks()
    {
        cableElements = new BlockTileElement[BLOCKS_TO_REGISTER];
        for (int i = 0; i < BLOCKS_TO_REGISTER; i++)
        {
            GameRegistry.registerBlock(cableElements[i] = new BlockTileElement(i), ItemTileElement.class, "element" + i);
        }
    }


    public static void registerRecipes()
    {
        GameRegistry.addRecipe(ClusterRegistry.MANAGER.getItemStack(),
                "III",
                "IRI",
                "SPS",
                'R', Blocks.redstone_block,
                'P', Blocks.piston,
                'I', Items.iron_ingot,
                'S', Blocks.stone
        );

        ItemStack cable = ClusterRegistry.CABLE.getItemStack();
        cable.stackSize = 8;

        GameRegistry.addRecipe(cable,
                "GPG",
                "IRI",
                "GPG",
                'R', Items.redstone,
                'G', Blocks.glass,
                'I', Items.iron_ingot,
                'P', Blocks.light_weighted_pressure_plate
        );

        cable = ClusterRegistry.CABLE.getItemStack();

        GameRegistry.addShapelessRecipe(ClusterRegistry.RELAY.getItemStack(),
                cable,
                Blocks.hopper
        );

        GameRegistry.addShapelessRecipe(ClusterRegistry.EMITTER.getItemStack(),
                cable,
                Items.redstone,
                Items.redstone,
                Items.redstone
        );


        GameRegistry.addShapelessRecipe(ClusterRegistry.RECEIVER.getItemStack(),
                cable,
                Items.redstone
        );

        GameRegistry.addShapelessRecipe(ClusterRegistry.RELAY.getItemStack(1),
                ClusterRegistry.RELAY.getItemStack(),
                new ItemStack(Items.dye, 1, 4)
        );

        GameRegistry.addShapelessRecipe(ClusterRegistry.VALVE.getItemStack(),
                cable,
                Blocks.hopper,
                Blocks.hopper,
                Blocks.dropper
        );

        GameRegistry.addShapelessRecipe(ClusterRegistry.BUD.getItemStack(),
                cable,
                Items.quartz,
                Items.quartz,
                Items.quartz
        );


        GameRegistry.addShapelessRecipe(ClusterRegistry.BLOCK_GATE.getItemStack(),
                cable,
                Items.iron_pickaxe,
                Blocks.dispenser
        );

        GameRegistry.addShapelessRecipe(ClusterRegistry.FLUID_GATE.getItemStack(),
                cable,
                Items.bucket,
                Blocks.dispenser
        );

        GameRegistry.addShapelessRecipe(ClusterRegistry.VALVE.getItemStack(1),
                ClusterRegistry.RECEIVER.getItemStack(),
                Items.gold_ingot
        );

        GameRegistry.addShapelessRecipe(ClusterRegistry.CLUSTER.getItemStack(),
                cable,
                Items.ender_pearl,
                Items.slime_ball,
                Items.ender_pearl
        );

        GameRegistry.addRecipe(new ShapelessOreRecipe(ClusterRegistry.CAMO.getItemStack(), cable, "wool", "wool", "wool"));

        GameRegistry.addShapelessRecipe(ClusterRegistry.CAMO.getItemStack(1),
                ClusterRegistry.CAMO.getItemStack(),
                ClusterRegistry.CAMO.getItemStack(),
                Blocks.iron_bars,
                Blocks.iron_bars
        );

        GameRegistry.addShapelessRecipe(ClusterRegistry.CAMO.getItemStack(2),
                ClusterRegistry.CAMO.getItemStack(1),
                Blocks.sticky_piston
        );

        GameRegistry.addShapelessRecipe(ClusterRegistry.SIGN.getItemStack(),
                cable,
                new ItemStack(Items.dye, 0),
                Items.feather
        );

        GameRegistry.addShapelessRecipe(ClusterRegistry.VOID.getItemStack(), cable, Items.magma_cream, Items.blaze_rod);

        if (RFCompat.RF != null)
        {
            GameRegistry.addRecipe(RFCompat.RF.getItemStack(), "RRR", "RCR", "RRR", 'R', Items.redstone, 'C', cable);
        }
        if (AECompat.AE != null)
        {
            ItemStack aeInterface = new ItemStack(GameRegistry.findBlock("appliedenergistics2", "tile.BlockInterface"));
            Item quartz = GameRegistry.findItem("appliedenergistics2", "item.ItemMultiMaterial");
            ItemStack fluix = new ItemStack(quartz, 1, 12);
            ItemStack certus = new ItemStack(quartz, 1, 10);
            Block fluidBlock = GameRegistry.findBlock("extracells", "ecbaseblock");
            GameRegistry.addRecipe(AECompat.AE.getItemStack(), "FRQ", "ACB", "QRF", 'R', Blocks.redstone_block, 'C', cable, 'A', aeInterface, 'B', fluidBlock == null ? aeInterface : new ItemStack(fluidBlock), 'F', fluix, 'Q', certus);
        }

        GameRegistry.addRecipe(new ClusterUpgradeRecipe());
        GameRegistry.addRecipe(new ClusterRecipe());
        GameRegistry.addRecipe(new ClusterUncraftingRecipe());
        GameRegistry.addRecipe(new QuantumCraftingRecipe());
        GameRegistry.addRecipe(new QuantumPairingRecipe());
    }

    public static void registerTiles()
    {
        for (BlockTileElement block : cableElements)
        {
            block.clearFactories();
            block.setFactories(ClusterRegistry.getFactories());
        }
        registerRecipes();
        ItemRegistry.registerRecipes();
    }
}
