package advancedsystemsmanager.compatibility.rf.buffer;

import advancedsystemsmanager.api.execution.IBufferElement;
import advancedsystemsmanager.api.execution.Key;
import advancedsystemsmanager.flow.execution.buffers.elements.BufferElementBase;
import cofh.api.energy.IEnergyProvider;
import net.minecraftforge.common.util.ForgeDirection;

public class RFBufferElement extends BufferElementBase<Integer>
{
    private IEnergyProvider source;
    private ForgeDirection direction;

    public RFBufferElement(int id, IEnergyProvider provider, ForgeDirection direction, int amount)
    {
        super(id);
        this.source = provider;
        this.direction = direction;
        this.amount = amount;
    }

    @Override
    public Key<Integer> getKey()
    {
        return new Key.RFKey(source, id);
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
        return source.extractEnergy(direction, amount, true);
    }

    @Override
    public int reduceBufferAmount(int amount)
    {
        amount = Math.min(amount, getSizeLeft());
        amount = source.extractEnergy(direction, amount, false);
        this.amount -= amount;
        return amount;
    }

    @Override
    public int getMaxWithSetting(int amount)
    {
        return Math.min(amount, this.amount);
    }

    @Override
    public IBufferElement<Integer> getSplitElement(int elementAmount, int id, boolean fair)
    {
        return this;
    }
}
