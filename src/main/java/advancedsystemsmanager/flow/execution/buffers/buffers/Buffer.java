package advancedsystemsmanager.flow.execution.buffers.buffers;

import advancedsystemsmanager.api.execution.IBuffer;
import advancedsystemsmanager.api.execution.IBufferElement;
import advancedsystemsmanager.api.execution.IBufferSubElement;

import java.util.Map;

public class Buffer<Key> implements IBuffer<Key, IBufferElement<?, Key>, IBufferSubElement<?, Key, ?>>
{
    private Map<Key, IBufferElement<?, Key>> elements;

    public Buffer(Map<Key, IBufferElement<?, Key>> map)
    {
        this.elements = map;
    }

    @Override
    public boolean contains(Key key)
    {
        return elements.containsKey(key);
    }

    @Override
    public IBufferElement<?, Key> get(Key key)
    {
        return elements.get(key);
    }

    @Override
    public int getAccessibleCount(Key key)
    {
        return elements.containsKey(key) ? elements.get(key).retrieveCount(Integer.MAX_VALUE) : 0;
    }

    @Override
    public void remove(Key key, int amount, boolean fair)
    {
        if (elements.containsKey(key))
            elements.get(key).decreaseSize(amount, fair);
    }

    @Override
    public boolean add(IBufferSubElement<?, Key, ?> subElement)
    {
        if (!elements.containsKey(subElement.getKey()))
        {
            elements.put(subElement.getKey(), subElement.getNewBufferElement());
        }
        elements.get(subElement.getKey()).addSubElement(subElement);
        return true;
    }
}
