package advancedsystemsmanager.api.execution;

public interface IBuffer<Key, Element extends IBufferElement<?, Key>, SubElement>
{
    public static final String ITEM = "item";
    public static final String CRAFT_HIGH = "craftHigh";
    public static final String CAFT_LOW = "craftLow";
    public static final String FLUID = "fluid";
    public static final String RF = "rf";
    public static final String GAS = "gas";
    public static final String ESSENTIA = "essentia";

    boolean contains(Key key);

    Element get(Key key);

    int getAccessibleCount(Key key);

    void remove(Key key, int amount, boolean fair);

    boolean add(IBufferSubElement subElement);
}
