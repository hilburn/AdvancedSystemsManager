package advancedsystemsmanager.items.blocks;

import advancedsystemsmanager.reference.Names;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemAdvanced extends ItemBlock
{
    public ItemAdvanced(Block block)
    {
        super(block);
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        return super.getUnlocalizedName(stack) + ((stack.getItemDamage() & 8) != 0 ? Names.ADVANCED_SUFFIX : "");
    }
}
