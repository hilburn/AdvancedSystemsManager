package advancedsystemsmanager.flow.execution.buffers.maps;


import advancedsystemsmanager.api.execution.IBufferElement;
import advancedsystemsmanager.flow.execution.buffers.maps.FluidHashingStrategy;
import advancedsystemsmanager.flow.execution.buffers.maps.ItemHashingStrategy;
import gnu.trove.map.hash.TCustomHashMap;
import gnu.trove.strategy.HashingStrategy;

import java.util.HashMap;
import java.util.Map;

public class BufferElementMap<Key> extends TCustomHashMap<Key, IBufferElement<?, Key>>
{
    private static final Map<String, HashingStrategy> hashes = new HashMap<String, HashingStrategy>();

    public static void registerHash(String buffer, HashingStrategy strategy)
    {
        if (!hashes.containsKey(buffer))
            hashes.put(buffer, strategy);
    }

    public BufferElementMap(String buffer)
    {
        super(hashes.get(buffer));
    }

    static
    {
        registerHash("item", new ItemStackHashingStrategy());
        registerHash("highPriority", new ItemStackHashingStrategy());
        registerHash("lowPriority", new ItemStackHashingStrategy());
        registerHash("fluid", new FluidHashingStrategy());
    }
}
