package advancedsystemsmanager.api.tileentities;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IPlaceListener
{
    void onBlockPlacedBy(EntityLivingBase entity, ItemStack item);
}
