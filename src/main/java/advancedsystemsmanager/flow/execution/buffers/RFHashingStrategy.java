package advancedsystemsmanager.flow.execution.buffers;

import advancedsystemsmanager.api.execution.IEnergyBufferElement;
import gnu.trove.strategy.HashingStrategy;

public class RFHashingStrategy implements HashingStrategy<IEnergyBufferElement>
{
    @Override
    public int computeHashCode(IEnergyBufferElement object)
    {
        return object.getSubElement().getContainer().hashCode();
    }

    @Override
    public boolean equals(IEnergyBufferElement o1, IEnergyBufferElement o2)
    {
        return o1.getSubElement().getContainer().equals(o2.getSubElement().getContainer());
    }
}
