package advancedsystemsmanager.tileentities;

import advancedsystemsmanager.api.tileentities.ITileEntityInterface;
import advancedsystemsmanager.gui.ContainerVoid;
import advancedsystemsmanager.gui.GuiVoid;
import advancedsystemsmanager.util.ClusterMethodRegistration;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import java.util.EnumSet;

public class TileEntityVoid extends TileEntityClusterElement implements IInventory, IFluidHandler, ITileEntityInterface
{

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
    {
        return resource == null ? 0 : resource.amount;
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain)
    {
        return null;
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
        return false;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from)
    {
        return new FluidTankInfo[0];
    }

    @Override
    public int getSizeInventory()
    {
        return 1;
    }

    @Override
    public ItemStack getStackInSlot(int slot)
    {
        return null;
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount)
    {
        return null;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int p_70304_1_)
    {
        return null;
    }

    @Override
    public void setInventorySlotContents(int p_70299_1_, ItemStack p_70299_2_)
    {

    }

    @Override
    public String getInventoryName()
    {
        return null;
    }

    @Override
    public boolean hasCustomInventoryName()
    {
        return false;
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player)
    {
        return player.getDistanceSq(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64;
    }

    @Override
    public void openInventory()
    {
    }

    @Override
    public void closeInventory()
    {
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack)
    {
        return true;
    }

    @Override
    protected EnumSet<ClusterMethodRegistration> getRegistrations()
    {
        return EnumSet.noneOf(ClusterMethodRegistration.class);
    }

    @Override
    public Container getContainer(EntityPlayer player)
    {
        return new ContainerVoid(this, player.inventory);
    }

    @Override
    public GuiScreen getGui(EntityPlayer player)
    {
        return new GuiVoid((ContainerVoid)getContainer(player));
    }

    @Override
    public void readData(ByteBuf buf, EntityPlayer player)
    {
    }

    @Override
    public void writeNetworkComponent(ByteBuf buf)
    {
    }
}
