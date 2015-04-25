package advancedsystemsmanager.flow.execution.buffers.buffers.items;

import advancedsystemsmanager.api.execution.IBuffer;
import advancedsystemsmanager.api.execution.IBufferElement;
import advancedsystemsmanager.api.execution.IBufferSubElement;
import advancedsystemsmanager.flow.execution.buffers.buffers.Buffer;
import advancedsystemsmanager.flow.execution.buffers.maps.BufferElementMap;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ItemBufferElement implements IBufferElement<IBufferSubElement<?, ItemStack, ?>, ItemStack>
{
    List<IBufferSubElement<?, ItemStack, ?>> subElements = new ArrayList<IBufferSubElement<?, ItemStack, ?>>();
    Iterator<IBufferSubElement<?, ItemStack, ?>> iterator;
    IBuffer<ItemStack, ? extends IBufferElement<?, ItemStack>, IBufferSubElement<?, ItemStack, ?>> buffer;

    public ItemBufferElement()
    {
    }
    
    @Override
    public void addSubElement(IBufferSubElement<?, ItemStack, ?> subElement)
    {
        subElements.add(subElement);
        getBuffer().addToOrderedList(subElement);
    }

    @Override
    public void prepareSubElements()
    {
        iterator = subElements.iterator();
    }

    @Override
    public IBufferSubElement<?, ItemStack, ?> getSubElement()
    {
        return iterator.next();
    }

    @Override
    public ItemStack getKey()
    {
        return subElements.get(0).getKey();
    }

    @Override
    public void removeSubElement(IBufferSubElement subElement)
    {
        subElements.remove(subElement);
        getBuffer().remove(subElement);
    }

    @Override
    public int retrieveCount(int moveCount)
    {
        int amount = 0;
        for (IBufferSubElement subElement : subElements) amount += subElement.getSizeLeft();
        return amount;
    }

    @Override
    public void decreaseSize(int moveCount, boolean fair)
    {
        int amountToRemove = fair? Math.min(moveCount/subElements.size(), 1) : moveCount;
        prepareSubElements();
        IBufferSubElement subElement;
        while (moveCount > 0 && iterator.hasNext())
        {
            int removed = (subElement = iterator.next()).reduceContainerAmount(amountToRemove);
            if (removed < amountToRemove)
            {
                iterator.remove();
                buffer.remove(subElement);
            }
            moveCount -= removed;

        }
        if (moveCount > 0 && subElements.size() > 0) decreaseSize(moveCount, fair);
    }

    @Override
    public void releaseSubElements()
    {
        iterator = null;
    }

    @Override
    public IBufferElement<IBufferSubElement<?, ItemStack, ?>, ItemStack> setBuffer(IBuffer<ItemStack, ? extends IBufferElement<?, ItemStack>, IBufferSubElement<?, ItemStack, ?>> buffer)
    {
        this.buffer = buffer;
        return this;
    }

    @Override
    public IBuffer<ItemStack, ? extends IBufferElement<?, ItemStack>, IBufferSubElement<?, ItemStack, ?>> getBuffer()
    {
        return buffer == null? new Buffer<ItemStack>(new BufferElementMap<ItemStack>("items")) : buffer;
    }
    
    
}
