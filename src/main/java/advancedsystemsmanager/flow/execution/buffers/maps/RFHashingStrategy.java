package advancedsystemsmanager.flow.execution.buffers.maps;

import advancedsystemsmanager.api.execution.IBufferElement;
import gnu.trove.strategy.HashingStrategy;

public class RFHashingStrategy implements HashingStrategy<IBufferElement<?, Integer>>
{
    @Override
    public int computeHashCode(IBufferElement<?, Integer> object)
    {
        return object.getSubElement().getContainer().hashCode();
    }

    @Override
    public boolean equals(IBufferElement<?, Integer> o1, IBufferElement<?, Integer> o2)
    {
        return o1.getSubElement().getContainer().equals(o2.getSubElement().getContainer());
    }
}
