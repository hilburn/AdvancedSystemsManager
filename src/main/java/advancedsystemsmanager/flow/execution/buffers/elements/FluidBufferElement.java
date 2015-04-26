package advancedsystemsmanager.flow.execution.buffers.elements;

import advancedsystemsmanager.api.execution.IBufferElement;
import advancedsystemsmanager.flow.setting.Setting;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class FluidBufferElement extends BufferElementBase<Fluid>
{
    protected IFluidHandler tank;
    protected ForgeDirection direction;

    public FluidBufferElement(int id, IFluidHandler tank, ForgeDirection direction, int amount, Fluid fluid, Setting<Fluid> setting, boolean whitelist)
    {
        this(id, tank, direction, amount, fluid);
        this.setting = setting;
        this.whitelist = whitelist;
    }

    public FluidBufferElement(int id, IFluidHandler tank, ForgeDirection direction, int amount, Fluid fluid)
    {
        super(id);
        this.amount = amount;
        this.content = fluid;
        this.tank = tank;
        this.direction = direction;
    }

    @Override
    public void remove()
    {
    }

    @Override
    public void onUpdate()
    {
    }

    @Override
    public int getSizeLeft()
    {
        FluidStack stack = tank.drain(direction, new FluidStack(content, amount), false);
        return stack == null ? 0 : getMaxWithSetting(stack.amount);
    }

    @Override
    public int reduceBufferAmount(int amount)
    {
        amount = Math.min(amount, this.amount);
        FluidStack stack = tank.drain(direction, new FluidStack(content, amount), true);
        return stack == null ? 0 : stack.amount;
    }

    @Override
    public IBufferElement<Fluid> getSplitElement(int elementAmount, int id, boolean fair)
    {
        FluidBufferElement element = new FluidBufferElement(this.id, this.tank, this.direction, this.amount, this.content);
        int oldAmount = getSizeLeft();
        int amount = oldAmount / elementAmount;
        if (!fair)
        {
            int amountLeft = oldAmount % elementAmount;
            if (id < amountLeft)
            {
                amount++;
            }
        }
        element.amount = amount;
        return element;
    }
}
