package advancedsystemsmanager.api.items;

import advancedsystemsmanager.api.ITileFactory;
import net.minecraft.item.ItemStack;

public interface IElementItem
{
    ITileFactory getTileFactory(ItemStack stack);
}
