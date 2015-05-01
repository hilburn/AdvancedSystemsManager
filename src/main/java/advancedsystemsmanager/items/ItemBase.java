package advancedsystemsmanager.items;

import advancedsystemsmanager.AdvancedSystemsManager;
import advancedsystemsmanager.reference.Reference;
import net.minecraft.item.Item;

public class ItemBase extends Item
{
    public ItemBase(String name)
    {
        this.setCreativeTab(AdvancedSystemsManager.creativeTab);
        this.setUnlocalizedName(name);
        this.setTextureName(Reference.RESOURCE_LOCATION + ":" + name.replace("system_", ""));
        this.setMaxStackSize(1);
    }
}
