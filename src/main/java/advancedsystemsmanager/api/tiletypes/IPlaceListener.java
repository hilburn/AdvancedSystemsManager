package advancedsystemsmanager.api.tiletypes;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IPlaceListener
{
    void onBlockPlacedBy(EntityLivingBase entity, ItemStack item);
}
