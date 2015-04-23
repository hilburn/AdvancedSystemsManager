package advancedsystemsmanager.items.blocks;

import advancedsystemsmanager.AdvancedSystemsManager;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.registry.BlockRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;


public class ItemRelay extends ItemBlock
{


    public ItemRelay(Block block)
    {
        super(block);
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @Override
    public String getUnlocalizedName(ItemStack item)
    {
        return "tile." + AdvancedSystemsManager.UNLOCALIZED_START + (BlockRegistry.blockCableRelay.isAdvanced(item.getItemDamage()) ? Names.CABLE_ADVANCED_RELAY_UNLOCALIZED_NAME : Names.CABLE_RELAY_UNLOCALIZED_NAME);
    }

}
