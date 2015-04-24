package advancedsystemsmanager.flow.execution.buffers.maps;

import gnu.trove.strategy.HashingStrategy;
import net.minecraftforge.fluids.Fluid;

public class FluidHashingStrategy implements HashingStrategy<Fluid>
{

    @Override
    public int computeHashCode(Fluid object)
    {
        return object.hashCode();
    }

    @Override
    public boolean equals(Fluid o1, Fluid o2)
    {
        return o1 == o2;
    }
}
