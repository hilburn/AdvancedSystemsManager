package advancedsystemsmanager.registry;

import advancedsystemsmanager.blocks.BlockCableAE;
import advancedsystemsmanager.blocks.BlockCableRF;
import advancedsystemsmanager.helpers.Config;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.tileentities.TileEntityAENode;
import advancedsystemsmanager.tileentities.TileEntityRFNode;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class BlockRegistry
{
    public static BlockCableRF cableRFNode;
    public static BlockCableAE cableAENode;

    public static void registerBlocks()
    {
        GameRegistry.registerTileEntity(TileEntityRFNode.class, Names.CABLE_RF);
        GameRegistry.registerBlock(cableRFNode = new BlockCableRF(), Names.CABLE_RF);
        ClusterRegistry.register(TileEntityRFNode.class, cableRFNode);

        if (Config.aeIntegration)
        {
            GameRegistry.registerTileEntity(TileEntityAENode.class, Names.CABLE_AE);
            GameRegistry.registerBlock(cableAENode = new BlockCableAE(), Names.CABLE_AE);
            ClusterRegistry.register(TileEntityAENode.class, cableAENode);
        }
    }

    public static void registerRecipes()
    {
        GameRegistry.addRecipe(new ItemStack(cableRFNode), "RRR", "RCR", "RRR", 'R', new ItemStack(Items.redstone), 'C', new ItemStack(ModBlocks.blockCable));
        if (cableAENode != null)
        {
            ItemStack aeInterface = new ItemStack(GameRegistry.findBlock("appliedenergistics2", "tile.BlockInterface"));
            Item quartz = GameRegistry.findItem("appliedenergistics2", "item.ItemMultiMaterial");
            ItemStack redstone = new ItemStack(Blocks.redstone_block);
            ItemStack cable = new ItemStack(ModBlocks.blockCable);
            ItemStack fluix = new ItemStack(quartz, 1, 12);
            ItemStack certus = new ItemStack(quartz, 1, 10);
            Block fluidBlock = GameRegistry.findBlock("extracells", "ecbaseblock");
            GameRegistry.addRecipe(new ItemStack(cableAENode), "FRQ", "ACB", "QRF", 'R', redstone, 'C', cable, 'A', aeInterface, 'B', fluidBlock == null ? aeInterface : new ItemStack(fluidBlock), 'F', fluix, 'Q', certus);
            GameRegistry.addRecipe(new ItemStack(cableAENode), "QRF", "ACB", "FRQ", 'R', redstone, 'C', cable, 'A', aeInterface, 'B', fluidBlock == null ? aeInterface : new ItemStack(fluidBlock), 'F', fluix, 'Q', certus);
        }
    }
}
