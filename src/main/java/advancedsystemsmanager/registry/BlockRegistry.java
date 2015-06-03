package advancedsystemsmanager.registry;

import advancedsystemsmanager.blocks.*;
import advancedsystemsmanager.items.blocks.*;
import advancedsystemsmanager.recipes.ClusterRecipe;
import advancedsystemsmanager.recipes.ClusterUncraftingRecipe;
import advancedsystemsmanager.recipes.ClusterUpgradeRecipe;
import advancedsystemsmanager.reference.Mods;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.tileentities.*;
import advancedsystemsmanager.tileentities.manager.TileEntityManager;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import thevault.registry.ConfigKey;
import thevault.registry.Register;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class BlockRegistry
{
    //    Manager
    @Register(tileEntity = TileEntityManager.class, itemBlock = ItemManager.class, name = Names.MANAGER)
    public static BlockManager blockManager;

    @Register(name = Names.CABLE)
    public static BlockCable cable;

    //    Cluster Elements
    @Register(tileEntity = TileEntityCluster.class, itemBlock = ItemCluster.class, name = Names.CABLE_CLUSTER)
    public static BlockCableCluster cableCluster;
    @Register(tileEntity = TileEntityRelay.class, itemBlock = ItemAdvanced.class, name = Names.CABLE_RELAY)
    public static BlockCableRelay cableRelay;
    @Register(tileEntity = TileEntityEmitter.class, itemBlock = ItemClusterElement.class, name = Names.CABLE_OUTPUT)
    public static BlockCableOutput cableOutput;
    @Register(tileEntity = TileEntityReceiver.class, itemBlock = ItemClusterElement.class, name = Names.CABLE_INPUT)
    public static BlockCableInput cableInput;
    @Register(tileEntity = TileEntityCreative.class, itemBlock = ItemClusterElement.class, name = Names.CABLE_CREATIVE)
    public static BlockCableCreative cableCreative;
    @Register(tileEntity = TileEntityValve.class, itemBlock = ItemAdvanced.class, name = Names.CABLE_VALVE)
    public static BlockCableValve cableValve;
    @Register(tileEntity = TileEntityBUD.class, itemBlock = ItemClusterElement.class, name = Names.CABLE_BUD)
    public static BlockCableBUD cableBUD;
    @Register(tileEntity = TileEntityBlockGate.class, itemBlock = ItemClusterElement.class, name = Names.CABLE_BLOCK_GATE)
    public static BlockCableBlockGate cableBlockGate;
    @Register(tileEntity = TileEntityFluidGate.class, itemBlock = ItemClusterElement.class, name = Names.CABLE_FLUID_GATE)
    public static BlockCableFluidGate cableFluidGate;
    @Register(tileEntity = TileEntityCamouflage.class, itemBlock = ItemCamouflage.class, name = Names.CABLE_CAMO, SBRH = RenderCamouflage.class)
    public static BlockCableCamouflages cableCamouflage;
    @Register(tileEntity = TileEntitySignUpdater.class, itemBlock = ItemClusterElement.class, name = Names.CABLE_SIGN)
    public static BlockCableSign cableSign;
    @Register(tileEntity = TileEntityVoid.class, itemBlock = ItemClusterElement.class, name = Names.CABLE_VOID)
    public static BlockCableVoid cableVoid;

    //    Mod Cluster Elements
    @ConfigKey(Names.CABLE_AE)
    @Register(tileEntity = TileEntityAENode.class, itemBlock = ItemClusterElement.class, name = Names.CABLE_AE, dependency = Mods.APPLIEDENERGISTICS2)
    public static BlockCableAE cableAENode;
    @Register(tileEntity = TileEntityRFNode.class, itemBlock = ItemClusterElement.class, name = Names.CABLE_RF, dependency = Mods.COFH_ENERGY)
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

        GameRegistry.addRecipe(new ItemStack(cable, 8),
                "GPG",
                "IRI",
                "GPG",
                'R', Items.redstone,
                'G', Blocks.glass,
                'I', Items.iron_ingot,
                'P', Blocks.light_weighted_pressure_plate
        );

        GameRegistry.addShapelessRecipe(new ItemStack(cableRelay, 1),
                cable,
                Blocks.hopper
        );

        GameRegistry.addShapelessRecipe(new ItemStack(cableOutput, 1),
                cable,
                Items.redstone,
                Items.redstone,
                Items.redstone
        );


        GameRegistry.addShapelessRecipe(new ItemStack(cableInput, 1),
                cable,
                Items.redstone
        );

        GameRegistry.addShapelessRecipe(new ItemStack(cableRelay, 1, 8),
                new ItemStack(cableRelay, 1, 0),
                new ItemStack(Items.dye, 1, 4)
        );

        GameRegistry.addShapelessRecipe(new ItemStack(cableValve, 1, 0),
                cable,
                Blocks.hopper,
                Blocks.hopper,
                Blocks.dropper
        );

        GameRegistry.addShapelessRecipe(new ItemStack(cableBUD, 1),
                cable,
                Items.quartz,
                Items.quartz,
                Items.quartz
        );


        GameRegistry.addShapelessRecipe(new ItemStack(cableBlockGate, 1),
                cable,
                Items.iron_pickaxe,
                Blocks.dispenser
        );

        GameRegistry.addShapelessRecipe(new ItemStack(cableValve, 1, 8),
                new ItemStack(cableValve, 1, 0),
                Items.gold_ingot
        );

        GameRegistry.addShapelessRecipe(new ItemStack(cableCluster, 1),
                cable,
                Items.ender_pearl,
                Items.ender_pearl,
                Items.ender_pearl
        );

        GameRegistry.addShapelessRecipe(new ItemStack(cableCamouflage, 1, 0),
                cable,
                new ItemStack(Blocks.wool, 1, 14),
                new ItemStack(Blocks.wool, 1, 13),
                new ItemStack(Blocks.wool, 1, 11)
        );

        GameRegistry.addShapelessRecipe(new ItemStack(cableCamouflage, 1, 1),
                new ItemStack(cableCamouflage, 1, 0),
                new ItemStack(cableCamouflage, 1, 0),
                Blocks.iron_bars,
                Blocks.iron_bars
        );

        GameRegistry.addShapelessRecipe(new ItemStack(cableCamouflage, 1, 2),
                new ItemStack(cableCamouflage, 1, 1),
                Blocks.sticky_piston
        );

        GameRegistry.addShapelessRecipe(new ItemStack(cableSign, 1),
                cable,
                new ItemStack(Items.dye, 0),
                Items.feather
        );

        GameRegistry.addRecipe(new ItemStack(cableRFNode), "RRR", "RCR", "RRR", 'R', Items.redstone, 'C', new ItemStack(cable));
        if (cableAENode != null)
        {
            ItemStack aeInterface = new ItemStack(GameRegistry.findBlock("appliedenergistics2", "tile.BlockInterface"));
            Item quartz = GameRegistry.findItem("appliedenergistics2", "item.ItemMultiMaterial");
            ItemStack cable = new ItemStack(BlockRegistry.cable);
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
