package advancedsystemsmanager.tileentities;

import advancedsystemsmanager.api.execution.IBufferElement;
import advancedsystemsmanager.api.execution.IInternalInventory;
import advancedsystemsmanager.flow.execution.ConditionSettingChecker;
import advancedsystemsmanager.flow.menus.MenuItem;
import advancedsystemsmanager.flow.setting.Setting;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.util.ClusterMethodRegistration;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;


public class TileEntityCreative extends TileEntityClusterElement implements IInternalInventory, IFluidHandler
{
    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
    {
        return resource == null ? 0 : resource.amount;
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain)
    {
        return resource == null ? null : resource.copy();
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
    {
        return null;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid)
    {
        return true;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid)
    {
        return true;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from)
    {
        return new FluidTankInfo[0];
    }

    @Override
    protected EnumSet<ClusterMethodRegistration> getRegistrations()
    {
        return EnumSet.noneOf(ClusterMethodRegistration.class);
    }

    @Override
    public int getAmountToInsert(ItemStack stack)
    {
        return 0;
    }

    @Override
    public void insertItemStack(ItemStack stack)
    {
    }

    @Override
    public List<IBufferElement<ItemStack>> getSubElements(int id, MenuItem menuItem)
    {
        //TODO: Things
        return null;
    }

    @Override
    public void isItemValid(Collection<Setting> settings, Map<Integer, ConditionSettingChecker> conditionSettingCheckerMap)
    {
        //TODO: Things
    }
}
