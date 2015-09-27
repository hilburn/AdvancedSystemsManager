package advancedsystemsmanager.tileentities;

import advancedsystemsmanager.api.tileentities.ITileInterfaceProvider;
import advancedsystemsmanager.api.tileentities.IActivateListener;
import advancedsystemsmanager.containers.ContainerVoid;
import advancedsystemsmanager.client.gui.GuiVoid;
import advancedsystemsmanager.network.ASMPacket;
import advancedsystemsmanager.reference.Mods;
import cofh.api.energy.IEnergyReceiver;
import cpw.mods.fml.common.Optional;
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
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;

@Optional.InterfaceList({
        @Optional.Interface(iface = "cofh.api.energy.IEnergyReceiver", modid = Mods.COFH_ENERGY),
        @Optional.Interface(iface = "thaumcraft.api.aspects.IAspectContainer", modid = Mods.THAUMCRAFT)})
public class TileEntityVoid extends TileEntityElementBase implements IInventory, IFluidHandler, ITileInterfaceProvider, IEnergyReceiver, IAspectContainer, IActivateListener
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
    public boolean writeData(ASMPacket packet)
    {
        return false;
    }    @Override
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
    public boolean readData(ASMPacket buf, EntityPlayer player)
    {
        return false;
    }


    @Override
    @Optional.Method(modid = Mods.COFH_ENERGY)
    public int receiveEnergy(ForgeDirection forgeDirection, int amount, boolean simulate)
    {
        return amount;
    }

    @Override
    @Optional.Method(modid = Mods.COFH_ENERGY)
    public int getEnergyStored(ForgeDirection forgeDirection)
    {
        return 0;
    }

    @Override
    @Optional.Method(modid = Mods.COFH_ENERGY)
    public int getMaxEnergyStored(ForgeDirection forgeDirection)
    {
        return Integer.MAX_VALUE;
    }

    @Override
    @Optional.Method(modid = Mods.COFH_ENERGY)
    public boolean canConnectEnergy(ForgeDirection forgeDirection)
    {
        return true;
    }

    @Override
    @Optional.Method(modid = Mods.THAUMCRAFT)
    public AspectList getAspects()
    {
        return new AspectList();
    }

    @Override
    @Optional.Method(modid = Mods.THAUMCRAFT)
    public void setAspects(AspectList aspectList)
    {

    }

    @Override
    @Optional.Method(modid = Mods.THAUMCRAFT)
    public boolean doesContainerAccept(Aspect aspect)
    {
        return true;
    }

    @Override
    @Optional.Method(modid = Mods.THAUMCRAFT)
    public int addToContainer(Aspect aspect, int i)
    {
        return 0;
    }

    @Override
    @Optional.Method(modid = Mods.THAUMCRAFT)
    public boolean takeFromContainer(Aspect aspect, int i)
    {
        return false;
    }

    @Override
    @Optional.Method(modid = Mods.THAUMCRAFT)
    public boolean takeFromContainer(AspectList aspectList)
    {
        return false;
    }

    @Override
    @Optional.Method(modid = Mods.THAUMCRAFT)
    public boolean doesContainerContainAmount(Aspect aspect, int i)
    {
        return false;
    }

    @Override
    @Optional.Method(modid = Mods.THAUMCRAFT)
    public boolean doesContainerContain(AspectList aspectList)
    {
        return false;
    }

    @Override
    @Optional.Method(modid = Mods.THAUMCRAFT)
    public int containerContains(Aspect aspect)
    {
        return 0;
    }
}
