package advancedsystemsmanager.api.execution;

public interface IBufferSubElement<Type>
{
    void remove();

    void onUpdate();

    int getSizeLeft();

    int reduceBufferAmount(int amount);

    Key<Type> getKey();

    IBufferSubElement<Type> getSplitElement(int elementAmount, int id, boolean fair);

//    IBufferElement<Type> getNewBufferElement();

    int getCommandID();
}