package advancedsystemsmanager.nei;

import advancedsystemsmanager.blocks.BlockTileElement;
import advancedsystemsmanager.reference.Mods;
import advancedsystemsmanager.registry.BlockRegistry;
import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import cpw.mods.fml.common.Optional;
import net.minecraft.item.ItemStack;

@Optional.Interface(modid = Mods.NEI, iface = "codechicken.nei.api.IConfigureNEI")
public class NEIConfig implements IConfigureNEI
{
    @Override
    public void loadConfig()
    {
        API.registerRecipeHandler(new NEIQuantumRecipes());
    }

    public static void hideBlocks()
    {
        for (BlockTileElement block : BlockRegistry.cableElements)
        {
            boolean hide = true;
            for (int i = 0; i < 16 ; i++)
            {
                if (block.getTileFactory(i) != null)
                {
                    hide = false;
                    break;
                }
            }
            if (hide)
            {
                API.hideItem(new ItemStack(block));
            }
        }
    }

    @Override
    public String getName()
    {
        return "AdvancedSystemsManager NEI support";
    }

    @Override
    public String getVersion()
    {
        return "v1";
    }
}
