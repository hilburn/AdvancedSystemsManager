package advancedsystemsmanager.api.items;

import advancedsystemsmanager.api.tileentities.IClusterElement;
import net.minecraft.item.ItemStack;

public interface IClusterItem
{
    IClusterElement getClusterElement(ItemStack stack);
}
