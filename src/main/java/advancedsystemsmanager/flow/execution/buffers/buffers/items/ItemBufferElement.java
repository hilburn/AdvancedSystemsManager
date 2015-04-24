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
    
    public ItemBufferElement(IBufferSubElement<?, ItemStack, ?> subElement)
    {
        addSubElement(subElement);
    }
    
    @Override
    public void addSubElement(IBufferSubElement<?, ItemStack, ?> subElement)
    {
        subElements.add(subElement);
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
    public void removeSubElement()
    {
        iterator.remove();
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
        while (moveCount > 0 && iterator.hasNext())
        {
            int removed = iterator.next().reduceContainerAmount(amountToRemove);
            if (removed < amountToRemove) iterator.remove();
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
    public IBuffer<ItemStack, ? extends IBufferElement<?, ItemStack>, IBufferSubElement<?, ItemStack, ?>> getNewBuffer()
    {
        return new Buffer<ItemStack>(new BufferElementMap<ItemStack>("items"));
    }
    
    
}
