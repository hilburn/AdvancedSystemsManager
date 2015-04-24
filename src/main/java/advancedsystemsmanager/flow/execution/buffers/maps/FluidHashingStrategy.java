package advancedsystemsmanager.flow.execution.buffers.maps;

import advancedsystemsmanager.api.execution.IBufferElement;
import gnu.trove.strategy.HashingStrategy;
import net.minecraftforge.fluids.Fluid;

public class FluidHashingStrategy implements HashingStrategy<IBufferElement<?, Fluid>>
{
    @Override
    public int computeHashCode(IBufferElement<?, Fluid> object)
    {
        return object.getKey().hashCode();
    }

    @Override
    public boolean equals(IBufferElement<?, Fluid> o1, IBufferElement<?, Fluid> o2)
    {
        return o1.getKey() == o2.getKey();
    }
}
