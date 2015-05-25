package advancedsystemsmanager.compatibility.appliedenergistics;

import advancedsystemsmanager.api.execution.IBufferElement;
import advancedsystemsmanager.flow.execution.buffers.elements.BufferElementBase;
import advancedsystemsmanager.flow.setting.Setting;
import advancedsystemsmanager.tileentities.TileEntityAENode;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class AEFluidBufferElement extends BufferElementBase<Fluid>
{
    protected TileEntityAENode node;

    public AEFluidBufferElement(int id, TileEntityAENode node, int amount, Fluid fluid, Setting<Fluid> setting, boolean whitelist)
    {
        this(id, node, amount, fluid);
        this.setting = setting;
        this.whitelist = whitelist;
    }

    public AEFluidBufferElement(int id, TileEntityAENode node, int amount, Fluid fluid)
    {
        super(id);
        this.node = node;
        this.amount = amount;
        this.content = fluid;
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
        FluidStack stack = node.getTank().drain(ForgeDirection.UNKNOWN, new FluidStack(content, amount), false);
        return stack == null ? 0 : getMaxWithSetting(stack.amount);
    }

    @Override
    public int reduceBufferAmount(int amount)
    {
        amount = Math.min(amount, this.amount);
        FluidStack stack = node.getTank().drain(ForgeDirection.UNKNOWN, new FluidStack(content, amount), true);
        return stack == null ? 0 : stack.amount;
    }

    @Override
    public IBufferElement<Fluid> getSplitElement(int elementAmount, int id, boolean fair)
    {
        AEFluidBufferElement element = new AEFluidBufferElement(this.id, this.node, this.amount, this.content, this.setting, this.whitelist);
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
