package advancedsystemsmanager.api.execution;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public interface IBuffer<Type>
{
    public static final String ITEM = "item";
    public static final String CRAFT_HIGH = "craftHigh";
    public static final String CRAFT_LOW = "craftLow";
    public static final String FLUID = "fluid";
    public static final String RF = "rf";
    public static final String GAS = "gas";
    public static final String ESSENTIA = "essentia";

    boolean contains(Type type);

    //IBufferElement<Type> get(Type type);

    List<IBufferElement<Type>> get(Type type);

    int getAccessibleCount(Type type);

    void remove(Type type, int amount, boolean fair);

//    void remove(IBufferSubElement<Type> subElement);

    boolean add(IBufferElement<Type> subElement);

    Iterator<Map.Entry<Key<Type>, IBufferElement<Type>>> getOrderedIterator();

    Iterator<Key<Type>> getKeyIterator();

    boolean shouldSplit();

    IBuffer split(int amount, int i, boolean fair);

//    void addToOrderedList(IBufferSubElement<Type> subElement);
//
//    Iterator<IBufferSubElement<Type>> getOrderedIterator();
}
