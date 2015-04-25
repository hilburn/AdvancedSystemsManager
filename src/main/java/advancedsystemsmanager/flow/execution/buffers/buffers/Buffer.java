package advancedsystemsmanager.flow.execution.buffers.buffers;

import advancedsystemsmanager.api.execution.IBuffer;
import advancedsystemsmanager.api.execution.IBufferSubElement;
import advancedsystemsmanager.api.execution.Key;
import com.google.common.collect.LinkedListMultimap;

import java.util.Iterator;
import java.util.List;

public class Buffer<Type> implements IBuffer<Type>
{
    private LinkedListMultimap<Key<Type>, IBufferSubElement<Type>> multiMap = LinkedListMultimap.create();


    @Override
    public boolean contains(Type type)
    {
        return multiMap.containsKey(getKey(type));
    }

    @Override
    public List<IBufferSubElement<Type>> get(Type type)
    {
        return multiMap.get(getKey(type));
    }

    @Override
    public int getAccessibleCount(Type type)
    {
        int amount = 0;
        for (IBufferSubElement<Type> subElement : multiMap.get(getKey(type))) amount += subElement.getSizeLeft();
        return amount;
    }

    @Override
    public void remove(Type type, int amount, boolean fair)
    {
        Key<Type> key = getKey(type);
        if (multiMap.containsKey(key))
            remove(multiMap.get(key), amount, fair);
    }

    public void remove(List<IBufferSubElement<Type>> subElements, int amount, boolean fair)
    {
        int amountToRemove = fair? Math.min(amount/subElements.size(), 1) : amount;
        Iterator<IBufferSubElement<Type>> iterator = subElements.iterator();
        while (amount > 0 && iterator.hasNext())
        {
            int removed = iterator.next().reduceBufferAmount(amountToRemove);
            if (removed < amountToRemove)
            {
                iterator.remove();
            }
            amount -= removed;
        }
        if (amount > 0 && subElements.size() > 0) remove(subElements, amount, fair);
    }

    public Key<Type> getKey(Type key)
    {
        return new Key<Type>(key);
    }

    @Override
    public boolean add(IBufferSubElement<Type> subElement)
    {
        return multiMap.put(subElement.getKey(), subElement);
    }
}
