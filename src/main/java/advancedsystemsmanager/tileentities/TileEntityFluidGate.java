package advancedsystemsmanager.tileentities;

import advancedsystemsmanager.util.ClusterMethodRegistration;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;

import java.util.EnumSet;

public class TileEntityFluidGate extends TileEntityBaseGate implements IFluidHandler
{
    protected FluidStack tank;
    protected FluidStack cachedStack;

    @Override
    public int getPlaceDirection()
    {
        return getBlockMetadata() % ForgeDirection.VALID_DIRECTIONS.length;
    }

    @Override
    protected EnumSet<ClusterMethodRegistration> getRegistrations()
    {
        return EnumSet.of(ClusterMethodRegistration.ON_BLOCK_PLACED_BY, ClusterMethodRegistration.ON_BLOCK_ACTIVATED, ClusterMethodRegistration.ON_NEIGHBOR_BLOCK_CHANGED);
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
    {
        if (!isBlocked() && resource != null && resource.getFluid().canBePlacedInWorld() && (tank == null || resource.isFluidEqual(tank)))
        {
            int amount = FluidContainerRegistry.BUCKET_VOLUME - (tank == null ? 0 : tank.amount);
            amount = Math.min(amount, resource.amount);
            if (amount > 0 && doFill)
            {
                if (tank == null)
                {
                    tank = resource.copy();
                    tank.amount = 0;
                }
                tank.amount += amount;
            }
            if (tank != null && tank.amount == FluidContainerRegistry.BUCKET_VOLUME)
            {
                Fluid fluid = tank.getFluid();
                ForgeDirection direction = getDirection();
                if (fillBlock(fluid, worldObj, xCoord + direction.offsetX, yCoord + direction.offsetY, zCoord + direction.offsetZ, false) == FluidContainerRegistry.BUCKET_VOLUME)
                    fillBlock(fluid, worldObj, xCoord + direction.offsetX, yCoord + direction.offsetY, zCoord + direction.offsetZ, true);
            }
            return amount;
        }
        return 0;
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain)
    {
        FluidStack tank = getTank(doDrain);
        if (resource != null && tank != null && resource.isFluidEqual(tank))
        {
            FluidStack drain = tank.copy();
            drain.amount = Math.min(tank.amount, resource.amount);
            if (doDrain)
            {
                this.tank.amount -= drain.amount;
                if (this.tank.amount == 0) this.tank = null;
            }
            return drain;
        }
        return null;
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
    {
        FluidStack tank = getTank(doDrain);
        if (tank != null)
        {
            FluidStack drain = tank.copy();
            drain.amount = Math.min(tank.amount, maxDrain);
            if (doDrain)
            {
                this.tank.amount -= drain.amount;
                if (this.tank.amount == 0) this.tank = null;
            }
            return drain;
        }
        return null;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid)
    {
        ForgeDirection direction = getDirection();
        boolean validFluid = fluid != null && fluid.canBePlacedInWorld() && fluid.getBlock() != null && fluid.getBlock().canPlaceBlockAt(worldObj, xCoord + direction.offsetX, yCoord + direction.offsetY, zCoord + direction.offsetZ);
        return !isBlocked() && validFluid && (tank == null || tank.getFluid() == fluid);
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid)
    {
        FluidStack stack = getTank(false);
        return stack != null && stack.getFluid() == fluid;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from)
    {
        return new FluidTankInfo[]{new FluidTankInfo(getTank(false), FluidContainerRegistry.BUCKET_VOLUME)};
    }

    private FluidStack getTank(boolean doDrain)
    {
        if (tank != null && tank.amount > 0) return tank;
        if (doDrain)
            return tank = drainBlock(true);
        return drainBlock(false);
    }

    private FluidStack drainBlock(boolean doDrain)
    {
        ForgeDirection direction = getDirection();
        Block block = worldObj.getBlock(xCoord + direction.offsetX, yCoord + direction.offsetY, zCoord + direction.offsetZ);
        return drainBlock(block, worldObj, xCoord + direction.offsetX, yCoord + direction.offsetY, zCoord + direction.offsetZ, doDrain);
    }

    public int fillBlock(Fluid fluid, World world, int x, int y, int z, boolean doFill)
    {
        Block block = fluid.getBlock();
        if (block.canPlaceBlockAt(world, x, y, z))
        {
            Block worldBlock = world.getBlock(x, y, z);
            boolean sourceBlock = (worldBlock instanceof IFluidBlock || worldBlock instanceof BlockStaticLiquid) && world.getBlockMetadata(x, y, z) == 0;
            if (!sourceBlock)
            {
                if (doFill)
                {
                    world.setBlock(x, y, z, block);
                    tank = null;
                }
                return FluidContainerRegistry.BUCKET_VOLUME;
            }
        }
        return 0;
    }

    public FluidStack drainBlock(Block block, World world, int x, int y, int z, boolean doDrain)
    {
        Fluid fluid = FluidRegistry.lookupFluidForBlock(block);
        FluidStack result;
        if (fluid != null && FluidRegistry.isFluidRegistered(fluid))
        {
            if (block instanceof IFluidBlock)
            {
                IFluidBlock fluidBlock = (IFluidBlock) block;
                if (!fluidBlock.canDrain(world, x, y, z))
                {
                    return cachedStack = null;
                }
                result = fluidBlock.drain(world, x, y, z, doDrain);
                if (doDrain)
                {
                    cachedStack = null;
                }
                else
                {
                    cachedStack = result;
                }
                return result;
            } else
            {
                if (world.getBlockMetadata(x, y, z) != 0)
                {
                    return null;
                }
                result = new FluidStack(fluid, FluidContainerRegistry.BUCKET_VOLUME);
                if (doDrain)
                {
                    cachedStack = null;
                    world.setBlockToAir(x, y, z);
                }else
                {
                    cachedStack = result;
                }

                return result;
            }
        }
        return cachedStack = null;
    }

    @Override
    protected void writeContentToNBT(NBTTagCompound tagCompound)
    {
        if (tank != null) tank.writeToNBT(tagCompound);
    }

    @Override
    protected void readContentFromNBT(NBTTagCompound tagCompound)
    {
        tank = FluidStack.loadFluidStackFromNBT(tagCompound);
    }

    @Override
    public boolean isBlocked()
    {
        return cachedStack != null;
    }

    @Override
    public void onNeighbourBlockChange(World world, int x, int y, int z, Block block)
    {
        drainBlock(false);
    }
}
