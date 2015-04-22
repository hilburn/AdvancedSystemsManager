package advancedsystemsmanager.items.blocks;

import advancedsystemsmanager.AdvancedSystemsManager;
import advancedsystemsmanager.registry.ModBlocks;
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
        return "tile." + AdvancedSystemsManager.UNLOCALIZED_START + (ModBlocks.blockCableRelay.isAdvanced(item.getItemDamage()) ? ModBlocks.CABLE_ADVANCED_RELAY_UNLOCALIZED_NAME : ModBlocks.CABLE_RELAY_UNLOCALIZED_NAME);
    }

}
