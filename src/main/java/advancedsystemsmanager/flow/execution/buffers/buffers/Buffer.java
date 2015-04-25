package advancedsystemsmanager.flow.execution.buffers.buffers;

import advancedsystemsmanager.api.execution.IBuffer;
import advancedsystemsmanager.api.execution.IBufferElement;
import advancedsystemsmanager.api.execution.IBufferSubElement;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Buffer<Key> implements IBuffer<Key, IBufferElement<?, Key>, IBufferSubElement<?, Key, ?>>
{
    private Map<Key, IBufferElement<?, Key>> elements;
    private List<IBufferSubElement> subElements;

    public Buffer(Map<Key, IBufferElement<?, Key>> map)
    {
        this.elements = map;
        this.subElements = new ArrayList<IBufferSubElement>();
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
    public boolean add(IBufferSubElement subElement)
    {
        if (!elements.containsKey(subElement.getKey()))
        {
            IBufferElement element = subElement.getNewBufferElement();
            element.setBuffer(this);
            element.addSubElement(subElement);
            elements.put((Key)subElement.getKey(), element);
        }
        else
        {
            elements.get(subElement.getKey()).addSubElement(subElement);
        }
        return true;
    }

    @Override
    public void addToOrderedList(IBufferSubElement subElement)
    {
        subElements.add(subElement);
    }

    @Override
    public Iterator<IBufferSubElement> getOrderedIterator()
    {
        return new OrderedIterator();
    }

    public void remove(IBufferSubElement subElement)
    {
        subElements.remove(subElement);
    }

    public class OrderedIterator implements Iterator<IBufferSubElement>
    {
        IBufferSubElement value;
        Iterator<IBufferSubElement> itr = subElements.iterator();
        @Override
        public boolean hasNext()
        {
            return itr.hasNext();
        }

        @Override
        public IBufferSubElement next()
        {
            value = itr.next();
            return value;
        }

        @Override
        public void remove()
        {
            itr.remove();
            elements.get(value.getKey()).removeSubElement(value);
        }
    }
}
