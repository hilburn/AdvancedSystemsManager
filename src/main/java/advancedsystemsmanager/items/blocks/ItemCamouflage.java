package advancedsystemsmanager.items.blocks;

import advancedsystemsmanager.tileentities.TileEntityCamouflage;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class ItemCamouflage extends ItemAdvanced
{
    public ItemCamouflage(Block block)
    {
        super(block);
    }

    @Override
    public String getUnlocalizedName(ItemStack item)
    {
        return field_150939_a.getUnlocalizedName() + TileEntityCamouflage.CamouflageType.getByID(item.getItemDamage()).getUnlocalized();
    }
}
