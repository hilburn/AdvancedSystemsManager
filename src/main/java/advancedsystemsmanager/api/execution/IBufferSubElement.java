package advancedsystemsmanager.api.execution;

public interface IBufferSubElement<SubElement extends IBufferSubElement<SubElement, Key, Container>, Key, Container>
{
    void remove();

    void onUpdate();

    int getSizeLeft();

    int reduceBufferAmount(int amount);

    int reduceContainerAmount(int amount);

    Key getKey();

    Container getContainer();

    SubElement getSplitElement(int elementAmount, int id, boolean fair);

    IBufferElement<?, Key> getNewBufferElement();
}