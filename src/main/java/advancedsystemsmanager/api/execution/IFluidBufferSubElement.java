package advancedsystemsmanager.api.execution;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;

public interface IFluidBufferSubElement extends IBufferSubElement<IFluidBufferSubElement, Fluid, IFluidHandler>
{
    ForgeDirection getSide();
}
