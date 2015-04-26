package advancedsystemsmanager.api.execution;

public interface IBufferElement<Type>
{
    void remove();

    void onUpdate();

    int getSizeLeft();

    int reduceBufferAmount(int amount);

    Key<Type> getKey();

    Type getContent();

    IBufferElement<Type> getSplitElement(int elementAmount, int id, boolean fair);

//    IBufferElement<Type> getNewBufferElement();

    int getCommandID();
}