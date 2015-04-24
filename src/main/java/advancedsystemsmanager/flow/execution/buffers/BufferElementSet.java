package advancedsystemsmanager.flow.execution.buffers;


import advancedsystemsmanager.api.execution.IBufferElement;
import gnu.trove.set.hash.TCustomHashSet;
import gnu.trove.strategy.HashingStrategy;

import java.util.HashMap;
import java.util.Map;

public class BufferElementSet extends TCustomHashSet<IBufferElement>
{
    private static final Map<String, HashingStrategy> hashes = new HashMap<String, HashingStrategy>();

    public static void registerHash(String buffer, HashingStrategy<? extends IBufferElement> strategy)
    {
        if (!hashes.containsKey(buffer))
            hashes.put(buffer, strategy);
    }

    public BufferElementSet(String buffer)
    {
        super(hashes.get(buffer));
    }

    static
    {
        registerHash("item", new ItemHashingStrategy());
        registerHash("highPriority", new ItemHashingStrategy());
        registerHash("lowPriority", new ItemHashingStrategy());
        registerHash("fluid", new FluidHashingStrategy());
    }
}
