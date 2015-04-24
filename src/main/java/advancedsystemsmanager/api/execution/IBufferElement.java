package advancedsystemsmanager.api.execution;

public interface IBufferElement<SubElement extends IBufferSubElement<?, Key, ?>, Key>
{
    void addSubElement(IBufferSubElement<?, Key, ?> subElement);

    void prepareSubElements();

    SubElement getSubElement();

    Key getKey();

    void removeSubElement();

    int retrieveCount(int moveCount);

    void decreaseSize(int moveCount, boolean fair);

    void releaseSubElements();

    IBuffer<Key, ? extends IBufferElement<?, Key>, SubElement> getNewBuffer();
}
