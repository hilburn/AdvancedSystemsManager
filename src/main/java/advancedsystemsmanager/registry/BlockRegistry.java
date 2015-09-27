package advancedsystemsmanager.registry;

import advancedsystemsmanager.api.tileentities.ITileFactory;
import advancedsystemsmanager.blocks.*;
import advancedsystemsmanager.items.blocks.*;
import advancedsystemsmanager.reference.Names;
import cpw.mods.fml.common.registry.GameRegistry;
import thevault.registry.Register;

import java.util.*;

public class BlockRegistry
{
//    //    Manager
//    @Register(tileEntity = TileEntityManager.class, itemBlock = ItemManager.class, name = Names.MANAGER)
//    public static BlockManager blockManager;
//
    public static final List<BlockTileElement> cableElement = new ArrayList<BlockTileElement>();

//    @Register(tileEntity = TileEntityQuantumCable.class, itemBlock = ItemQuantum.class, name = Names.OLD_CABLE_QUANTUM)
//    public static BlockCableQuantum cableQuantum;
//
//    //    Mod Cluster Elements
//    @ConfigKey(Names.OLD_CABLE_AE)
//    @Register(tileEntity = TileEntityAENode.class, itemBlock = ItemClusterElement.class, name = Names.OLD_CABLE_AE, dependency = Mods.APPLIEDENERGISTICS2)
//    public static BlockCableAE cableAENode;
//    @Register(tileEntity = TileEntityRFNode.class, itemBlock = ItemClusterElement.class, name = Names.OLD_CABLE_RF, dependency = Mods.COFH_ENERGY)
//    public static BlockCableRF cableRFNode;

    public static void registerBlocks()
    {
        Comparator<ITileFactory> alphabetical = new Comparator<ITileFactory>()
        {
            @Override
            public int compare(ITileFactory o1, ITileFactory o2)
            {
                return String.CASE_INSENSITIVE_ORDER.compare(o1.getKey(), o2.getKey());
            }
        };
        List<ITileFactory> factories = new ArrayList<ITileFactory>(ClusterRegistry.getFactories());
        Collections.sort(factories, alphabetical);
        int i = 0;
        for (Iterator<ITileFactory> itr = factories.iterator(); itr.hasNext(); i++)
        {
            BlockTileElement element = new BlockTileElement("element" + i);
            GameRegistry.registerBlock(element, ItemTileElement.class, "element" + i);
            cableElement.add(element);
            element.setFactories(itr);
        }
    }


    public static void registerRecipes()
    {
//        GameRegistry.addRecipe(new ItemStack(blockManager),
//                "III",
//                "IRI",
//                "SPS",
//                'R', Blocks.redstone_block,
//                'P', Blocks.piston,
//                'I', Items.iron_ingot,
//                'S', Blocks.stone
//        );
//
//        GameRegistry.addRecipe(new ItemStack(cable, 8),
//                "GPG",
//                "IRI",
//                "GPG",
//                'R', Items.redstone,
//                'G', Blocks.glass,
//                'I', Items.iron_ingot,
//                'P', Blocks.light_weighted_pressure_plate
//        );
//
//        GameRegistry.addShapelessRecipe(new ItemStack(cableRelay, 1),
//                cable,
//                Blocks.hopper
//        );
//
//        GameRegistry.addShapelessRecipe(new ItemStack(cableOutput, 1),
//                cable,
//                Items.redstone,
//                Items.redstone,
//                Items.redstone
//        );
//
//
//        GameRegistry.addShapelessRecipe(new ItemStack(cableInput, 1),
//                cable,
//                Items.redstone
//        );
//
//        GameRegistry.addShapelessRecipe(new ItemStack(cableRelay, 1, 8),
//                new ItemStack(cableRelay, 1, 0),
//                new ItemStack(Items.dye, 1, 4)
//        );
//
//        GameRegistry.addShapelessRecipe(new ItemStack(cableValve, 1, 0),
//                cable,
//                Blocks.hopper,
//                Blocks.hopper,
//                Blocks.dropper
//        );
//
//        GameRegistry.addShapelessRecipe(new ItemStack(cableBUD, 1),
//                cable,
//                Items.quartz,
//                Items.quartz,
//                Items.quartz
//        );
//
//
//        GameRegistry.addShapelessRecipe(new ItemStack(cableBlockGate, 1),
//                cable,
//                Items.iron_pickaxe,
//                Blocks.dispenser
//        );
//
//        GameRegistry.addShapelessRecipe(new ItemStack(cableFluidGate, 1),
//                cable,
//                Items.bucket,
//                Blocks.dispenser
//        );
//
//        GameRegistry.addShapelessRecipe(new ItemStack(cableValve, 1, 8),
//                new ItemStack(cableValve, 1, 0),
//                Items.gold_ingot
//        );
//
//        GameRegistry.addShapelessRecipe(new ItemStack(cableCluster, 1),
//                cable,
//                Items.ender_pearl,
//                Items.slime_ball,
//                Items.ender_pearl
//        );
//
//        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(cableCamouflage, 1, 0), cable, "wool", "wool", "wool"));
//
//        GameRegistry.addShapelessRecipe(new ItemStack(cableCamouflage, 1, 1),
//                new ItemStack(cableCamouflage, 1, 0),
//                new ItemStack(cableCamouflage, 1, 0),
//                Blocks.iron_bars,
//                Blocks.iron_bars
//        );
//
//        GameRegistry.addShapelessRecipe(new ItemStack(cableCamouflage, 1, 2),
//                new ItemStack(cableCamouflage, 1, 1),
//                Blocks.sticky_piston
//        );
//
//        GameRegistry.addShapelessRecipe(new ItemStack(cableSign, 1),
//                cable,
//                new ItemStack(Items.dye, 0),
//                Items.feather
//        );
//
//        GameRegistry.addShapelessRecipe(new ItemStack(cableVoid), cable, Items.magma_cream, Items.blaze_rod);
//
//        if (cableRFNode != null)
//        {
//            GameRegistry.addRecipe(new ItemStack(cableRFNode), "RRR", "RCR", "RRR", 'R', Items.redstone, 'C', new ItemStack(cable));
//        }
//        if (cableAENode != null)
//        {
//            ItemStack aeInterface = new ItemStack(GameRegistry.findBlock("appliedenergistics2", "tile.BlockInterface"));
//            Item quartz = GameRegistry.findItem("appliedenergistics2", "item.ItemMultiMaterial");
//            ItemStack cable = new ItemStack(BlockRegistry.cable);
//            ItemStack fluix = new ItemStack(quartz, 1, 12);
//            ItemStack certus = new ItemStack(quartz, 1, 10);
//            Block fluidBlock = GameRegistry.findBlock("extracells", "ecbaseblock");
//            GameRegistry.addRecipe(new ItemStack(cableAENode), "FRQ", "ACB", "QRF", 'R', Blocks.redstone_block, 'C', cable, 'A', aeInterface, 'B', fluidBlock == null ? aeInterface : new ItemStack(fluidBlock), 'F', fluix, 'Q', certus);
//            GameRegistry.addRecipe(new ItemStack(cableAENode), "QRF", "ACB", "FRQ", 'R', Blocks.redstone_block, 'C', cable, 'A', aeInterface, 'B', fluidBlock == null ? aeInterface : new ItemStack(fluidBlock), 'F', fluix, 'Q', certus);
//        }
//
//        GameRegistry.addRecipe(new ClusterUpgradeRecipe());
//        GameRegistry.addRecipe(new ClusterRecipe());
//        GameRegistry.addRecipe(new ClusterUncraftingRecipe());
//        GameRegistry.addRecipe(new QuantumCraftingRecipe());
//        GameRegistry.addRecipe(new QuantumPairingRecipe());
    }
}
