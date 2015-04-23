package advancedsystemsmanager.flow.execution.buffers;


import advancedsystemsmanager.api.execution.IBufferElement;
import gnu.trove.set.hash.TCustomHashSet;
import gnu.trove.strategy.HashingStrategy;

import java.util.HashMap;
import java.util.Map;

public class BufferElementSet extends TCustomHashSet<IBufferElement>
{
    private static final Map<Class<? extends IBufferElement>, HashingStrategy> hashes = new HashMap<Class<? extends IBufferElement>, HashingStrategy>();

    public static void registerHash(Class<? extends IBufferElement> clazz, HashingStrategy<? extends IBufferElement> strategy)
    {
        if (!hashes.containsKey(clazz))
            hashes.put(clazz, strategy);
    }

    public BufferElementSet(IBufferElement bufferElement)
    {
        super(hashes.get(bufferElement.getClass()));
        this.add(bufferElement);
    }

    public BufferElementSet(Class<? extends IBufferElement> bufferClass)
    {
        super(hashes.get(bufferClass));
    }

    static
    {

    }
}
