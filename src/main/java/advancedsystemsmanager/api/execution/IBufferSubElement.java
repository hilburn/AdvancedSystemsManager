package advancedsystemsmanager.api.execution;

public interface IBufferSubElement<Type extends IBufferSubElement<Type, Value, Container>, Value, Container>
{
    void remove();

    void onUpdate();

    int getSizeLeft();

    void reduceBufferAmount(int amount);

    void reduceContainerAmount(int amount);

    Value getValue();

    Container getContainer();

    Type getSplitElement(int elementAmount, int id, boolean fair);
}