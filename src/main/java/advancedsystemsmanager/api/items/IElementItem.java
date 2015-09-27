package advancedsystemsmanager.api.items;

import advancedsystemsmanager.api.tileentities.ITileFactory;
import net.minecraft.item.ItemStack;

public interface IElementItem
{
    ITileFactory getTileFactory(ItemStack stack);
}
