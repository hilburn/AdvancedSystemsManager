package advancedsystemsmanager.api.execution;

public interface IBufferElement<Type extends IBufferSubElement<Type, Value, ?>, Value>
{
    void addSubElement(Type type);

    void prepareSubElements();

    Type getSubElement();

    Value getKey();

    void removeSubElement();

    int retrieveCount(int moveCount);

    void decreaseSize(int moveCount, boolean fair);

    void releaseSubElements();
}
