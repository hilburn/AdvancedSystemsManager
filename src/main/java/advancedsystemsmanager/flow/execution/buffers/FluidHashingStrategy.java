package advancedsystemsmanager.flow.execution.buffers;

import advancedsystemsmanager.api.execution.IFluidBufferElement;
import gnu.trove.strategy.HashingStrategy;

public class FluidHashingStrategy implements HashingStrategy<IFluidBufferElement>
{
    @Override
    public int computeHashCode(IFluidBufferElement object)
    {
        return object.getKey().hashCode();
    }

    @Override
    public boolean equals(IFluidBufferElement o1, IFluidBufferElement o2)
    {
        return o1.getKey() == o2.getKey();
    }
}
