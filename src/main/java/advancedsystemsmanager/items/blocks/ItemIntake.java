package advancedsystemsmanager.items.blocks;

import advancedsystemsmanager.AdvancedSystemsManager;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.registry.BlockRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemIntake extends ItemBlock
{


    public ItemIntake(Block block)
    {
        super(block);
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @Override
    public String getUnlocalizedName(ItemStack item)
    {
        return "tile." + AdvancedSystemsManager.UNLOCALIZED_START + (BlockRegistry.blockCableIntake.isAdvanced(item.getItemDamage()) ? Names.CABLE_INSTANT_INTAKE_UNLOCALIZED_NAME : Names.CABLE_INTAKE_UNLOCALIZED_NAME);
    }

}
