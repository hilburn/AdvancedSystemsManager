package advancedsystemsmanager.registry;

import advancedsystemsmanager.items.ItemDuplicator;
import advancedsystemsmanager.items.ItemLabeler;
import advancedsystemsmanager.items.ItemRemoteAccessor;
import advancedsystemsmanager.reference.Names;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import thevault.registry.Register;

import java.util.ArrayList;
import java.util.Arrays;

public class ItemRegistry
{
    @Register(name = Names.DUPLICATOR)
    public static ItemDuplicator duplicator;

    @Register(name = Names.LABELER)
    public static ItemLabeler labeler;
    public static ItemStack defaultLabeler;

    @Register(name = Names.REMOTE_ACCESS)
    public static ItemRemoteAccessor remoteAccessor;

    public static void registerRecipes()
    {
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(duplicator), " x ", "xyx", " x ", 'x', "ingotIron", 'y', new ItemStack(BlockRegistry.blockManager)));
        defaultLabeler = new ItemStack(labeler);
        ItemLabeler.saveStrings(defaultLabeler, new ArrayList<String>(Arrays.asList("Energy Receiver", "Energy Provider", "Input Inventory", "Input Tank", "Output Inventory", "Output Tank")));
        GameRegistry.addRecipe(new ShapedOreRecipe(defaultLabeler, "ppp", " i ", "rxr", 'p', new ItemStack(Items.paper), 'i', "dyeBlack", 'r', "dustRedstone", 'x', new ItemStack(Blocks.piston)));
    }
}
