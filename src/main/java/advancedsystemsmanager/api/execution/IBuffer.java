package advancedsystemsmanager.api.execution;

import java.util.Iterator;
import java.util.List;

public interface IBuffer<Type>
{
    public static final String ITEM = "item";
    public static final String CRAFT_HIGH = "craftHigh";
    public static final String CAFT_LOW = "craftLow";
    public static final String FLUID = "fluid";
    public static final String RF = "rf";
    public static final String GAS = "gas";
    public static final String ESSENTIA = "essentia";

    boolean contains(Type type);

    //IBufferElement<Type> get(Type type);

    List<IBufferSubElement<Type>> get(Type type);

    int getAccessibleCount(Type type);

    void remove(Type type, int amount, boolean fair);

//    void remove(IBufferSubElement<Type> subElement);

    boolean add(IBufferSubElement<Type> subElement);

//    void addToOrderedList(IBufferSubElement<Type> subElement);
//
//    Iterator<IBufferSubElement<Type>> getOrderedIterator();
}
