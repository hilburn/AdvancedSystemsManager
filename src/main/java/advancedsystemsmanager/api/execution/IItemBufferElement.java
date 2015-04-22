package advancedsystemsmanager.api.execution;

public interface IItemBufferElement
{
    void prepareSubElements();

    IItemBufferSubElement getSubElement();

    void removeSubElement();

    int retrieveItemCount(int moveCount);

    void decreaseStackSize(int moveCount);

    void releaseSubElements();
}
