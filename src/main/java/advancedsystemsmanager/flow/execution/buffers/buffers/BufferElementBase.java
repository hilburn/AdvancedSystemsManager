package advancedsystemsmanager.flow.execution.buffers.buffers;

import advancedsystemsmanager.api.execution.IBufferElement;
import advancedsystemsmanager.api.execution.Key;

public abstract class BufferElementBase<Type> implements IBufferElement<Type>
{
    protected int id;
    protected int amount;
    protected Type content;

    public BufferElementBase(int id)
    {
        this.id = id;
    }

    @Override
    public int getCommandID()
    {
        return this.id;
    }

    @Override
    public Key<Type> getKey()
    {
        return new Key<Type>(content);
    }
}
