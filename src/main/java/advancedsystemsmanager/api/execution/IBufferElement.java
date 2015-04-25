package advancedsystemsmanager.api.execution;

public interface IBufferElement<SubElement extends IBufferSubElement<?, Key, ?>, Key>
{
    void addSubElement(IBufferSubElement<?, Key, ?> subElement);

    void prepareSubElements();

    SubElement getSubElement();

    Key getKey();

    void removeSubElement(IBufferSubElement subElement);

    int retrieveCount(int moveCount);

    void decreaseSize(int moveCount, boolean fair);

    void releaseSubElements();

    IBufferElement<SubElement, Key> setBuffer(IBuffer<Key,? extends IBufferElement<?, Key>,SubElement> buffer);

    IBuffer<Key, ? extends IBufferElement<?, Key>, SubElement> getBuffer();
}
