package advancedsystemsmanager.registry;

import advancedsystemsmanager.blocks.*;
import advancedsystemsmanager.items.blocks.ItemCamouflage;
import advancedsystemsmanager.items.blocks.ItemCluster;
import advancedsystemsmanager.items.blocks.ItemIntake;
import advancedsystemsmanager.items.blocks.ItemRelay;
import advancedsystemsmanager.recipes.ClusterRecipe;
import advancedsystemsmanager.recipes.ClusterUncraftingRecipe;
import advancedsystemsmanager.recipes.ClusterUpgradeRecipe;
import advancedsystemsmanager.reference.Mods;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.tileentities.*;
import advancedsystemsmanager.tileentities.manager.TileEntityManager;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import hilburnlib.registry.Register;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class BlockRegistry
{
//    Manager
    @Register(tileEntity = TileEntityManager.class, name = Names.MANAGER)
    public static BlockManager blockManager;

    @Register(name = Names.CABLE)
    public static BlockCable blockCable;

//    Cluster Elements
    @Register(tileEntity = TileEntityCluster.class, itemBlock = ItemCluster.class, name = Names.CABLE_CLUSTER)
    public static BlockCableCluster blockCableCluster;
    @Register(tileEntity = TileEntityRelay.class, itemBlock = ItemRelay.class, name = Names.CABLE_RELAY)
    public static BlockCableRelay blockCableRelay;
    @Register(tileEntity = TileEntityOutput.class, name = Names.CABLE_OUTPUT)
    public static BlockCableOutput blockCableOutput;
    @Register(tileEntity = TileEntityInput.class, name = Names.CABLE_INPUT)
    public static BlockCableInput blockCableInput;
    @Register(tileEntity = TileEntityCreative.class, name = Names.CABLE_CREATIVE)
    public static BlockCableCreative blockCableCreative;
    @Register(tileEntity = TileEntityIntake.class, itemBlock = ItemIntake.class, name = Names.CABLE_INTAKE)
    public static BlockCableIntake blockCableIntake;
    @Register(tileEntity = TileEntityBUD.class, name = Names.CABLE_BUD)
    public static BlockCableBUD blockCableBUD;
    @Register(tileEntity = TileEntityBreaker.class, name = Names.CABLE_BREAKER)
    public static BlockCableBreaker blockCableBreaker;
    @Register(tileEntity = TileEntityCamouflage.class, itemBlock = ItemCamouflage.class, name = Names.CABLE_CAMOUFLAGE, SBRH = RenderCamouflage.class)
    public static BlockCableCamouflages blockCableCamouflage;
    @Register(tileEntity = TileEntitySignUpdater.class, name = Names.CABLE_SIGN)
    public static BlockCableSign blockCableSign;

//    Mod Cluster Elements
    @Register(tileEntity = TileEntityAENode.class, name = Names.CABLE_AE, dependency = Mods.APPLIEDENERGISTICS2)
    public static BlockCableAE cableAENode;
    @Register(tileEntity = TileEntityRFNode.class, name = Names.CABLE_RF, dependency = Mods.RF_API)
    public static BlockCableRF cableRFNode;

    public static void registerRecipes()
    {
        GameRegistry.addRecipe(new ItemStack(blockManager),
                "III",
                "IRI",
                "SPS",
                'R', Blocks.redstone_block,
                'P', Blocks.piston,
                'I', Items.iron_ingot,
                'S', Blocks.stone
        );

        GameRegistry.addRecipe(new ItemStack(blockCable, 8),
                "GPG",
                "IRI",
                "GPG",
                'R', Items.redstone,
                'G', Blocks.glass,
                'I', Items.iron_ingot,
                'P', Blocks.light_weighted_pressure_plate
        );

        GameRegistry.addShapelessRecipe(new ItemStack(blockCableRelay, 1),
                blockCable,
                Blocks.hopper
        );

        GameRegistry.addShapelessRecipe(new ItemStack(blockCableOutput, 1),
                blockCable,
                Items.redstone,
                Items.redstone,
                Items.redstone
        );


        GameRegistry.addShapelessRecipe(new ItemStack(blockCableInput, 1),
                blockCable,
                Items.redstone
        );

        GameRegistry.addShapelessRecipe(new ItemStack(blockCableRelay, 1, 8),
                new ItemStack(blockCableRelay, 1, 0),
                new ItemStack(Items.dye, 1, 4)
        );

        GameRegistry.addShapelessRecipe(new ItemStack(blockCableIntake, 1, 0),
                blockCable,
                Blocks.hopper,
                Blocks.hopper,
                Blocks.dropper
        );

        GameRegistry.addShapelessRecipe(new ItemStack(blockCableBUD, 1),
                blockCable,
                Items.quartz,
                Items.quartz,
                Items.quartz
        );


        GameRegistry.addShapelessRecipe(new ItemStack(blockCableBreaker, 1),
                blockCable,
                Items.iron_pickaxe,
                Blocks.dispenser
        );

        GameRegistry.addShapelessRecipe(new ItemStack(blockCableIntake, 1, 8),
                new ItemStack(blockCableIntake, 1, 0),
                Items.gold_ingot
        );

        GameRegistry.addShapelessRecipe(new ItemStack(blockCableCluster, 1),
                blockCable,
                Items.ender_pearl,
                Items.ender_pearl,
                Items.ender_pearl
        );

        GameRegistry.addShapelessRecipe(new ItemStack(blockCableCamouflage, 1, 0),
                blockCable,
                new ItemStack(Blocks.wool, 1, 14),
                new ItemStack(Blocks.wool, 1, 13),
                new ItemStack(Blocks.wool, 1, 11)
        );

        GameRegistry.addShapelessRecipe(new ItemStack(blockCableCamouflage, 1, 1),
                new ItemStack(blockCableCamouflage, 1, 0),
                new ItemStack(blockCableCamouflage, 1, 0),
                Blocks.iron_bars,
                Blocks.iron_bars
        );

        GameRegistry.addShapelessRecipe(new ItemStack(blockCableCamouflage, 1, 2),
                new ItemStack(blockCableCamouflage, 1, 1),
                Blocks.sticky_piston
        );

        GameRegistry.addShapelessRecipe(new ItemStack(blockCableSign, 1),
                blockCable,
                new ItemStack(Items.dye, 0),
                Items.feather
        );

        GameRegistry.addRecipe(new ItemStack(cableRFNode), "RRR", "RCR", "RRR", 'R', Items.redstone, 'C', new ItemStack(blockCable));
        if (cableAENode != null)
        {
            ItemStack aeInterface = new ItemStack(GameRegistry.findBlock("appliedenergistics2", "tile.BlockInterface"));
            Item quartz = GameRegistry.findItem("appliedenergistics2", "item.ItemMultiMaterial");
            ItemStack cable = new ItemStack(blockCable);
            ItemStack fluix = new ItemStack(quartz, 1, 12);
            ItemStack certus = new ItemStack(quartz, 1, 10);
            Block fluidBlock = GameRegistry.findBlock("extracells", "ecbaseblock");
            GameRegistry.addRecipe(new ItemStack(cableAENode), "FRQ", "ACB", "QRF", 'R', Blocks.redstone_block, 'C', cable, 'A', aeInterface, 'B', fluidBlock == null ? aeInterface : new ItemStack(fluidBlock), 'F', fluix, 'Q', certus);
            GameRegistry.addRecipe(new ItemStack(cableAENode), "QRF", "ACB", "FRQ", 'R', Blocks.redstone_block, 'C', cable, 'A', aeInterface, 'B', fluidBlock == null ? aeInterface : new ItemStack(fluidBlock), 'F', fluix, 'Q', certus);
        }

        GameRegistry.addRecipe(new ClusterUpgradeRecipe());
        GameRegistry.addRecipe(new ClusterRecipe());

        ClusterUncraftingRecipe uncrafting = new ClusterUncraftingRecipe();
        GameRegistry.addRecipe(uncrafting);
        FMLCommonHandler.instance().bus().register(uncrafting);
    }
}
