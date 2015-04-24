package advancedsystemsmanager.flow.execution;

import advancedsystemsmanager.compatibility.appliedenergistics.AEHelper;
import advancedsystemsmanager.tileentities.TileEntityAENode;
import appeng.api.storage.data.IAEFluidStack;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;

public class AEFluidBufferElement extends StackTankHolder
{
    public IAEFluidStack fluid;
    public TileEntityAENode node;
    public int sizeLeft;

    public AEFluidBufferElement(IAEFluidStack fluid, TileEntityAENode node)
    {
        super(null, null, null);
        this.fluid = fluid;
        this.node = node;
        this.sizeLeft = (int)fluid.getStackSize();
    }

    @Override
    public FluidStack getFluidStack()
    {
        return fluid.getFluidStack();
    }

    @Override
    public IFluidHandler getTank()
    {
        return node.getTank();
    }

    @Override
    public ForgeDirection getSide()
    {
        return ForgeDirection.UNKNOWN;
    }

    @Override
    public void reduceAmount(int val)
    {
        node.helper.extract(fluid.copy().setStackSize(val));
    }

    @Override
    public int getSizeLeft()
    {
        return (int)Math.min(this.fluid.getStackSize(), this.sizeLeft);
    }

    @Override
    public StackTankHolder getSplitElement(int elementAmount, int id, boolean fair)
    {
        AEFluidBufferElement element = new AEFluidBufferElement(this.fluid, this.node);
        int oldAmount = this.getSizeLeft();
        int amount = oldAmount / elementAmount;
        if (!fair)
        {
            int amountLeft = oldAmount % elementAmount;
            if (id < amountLeft)
            {
                ++amount;
            }
        }
        element.sizeLeft = amount;
        return element;
    }
}
