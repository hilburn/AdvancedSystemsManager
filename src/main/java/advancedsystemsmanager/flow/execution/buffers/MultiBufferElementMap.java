package advancedsystemsmanager.flow.execution.buffers;

import advancedsystemsmanager.api.execution.IBufferElement;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.SetMultimap;

import java.util.*;

public class MultiBufferElementMap implements SetMultimap<Class<? extends IBufferElement>, IBufferElement>
{
    public Map<Class<? extends IBufferElement>, BufferElementSet> values = new HashMap<Class<? extends IBufferElement>, BufferElementSet>();
    
    @Override
    public Set<IBufferElement> get(Class<? extends IBufferElement> key)
    {
        if (!values.containsKey(key)) values.put(key, new BufferElementSet(key));
        return values.get(key);
    }
    
    @Override
    public Set<Class<? extends IBufferElement>> keySet()
    {
        return values.keySet();
    }
    
    @Override
    public Multiset<Class<? extends IBufferElement>> keys()
    {
        return null;
    }
    
    @Override
    public Collection<IBufferElement> values()
    {
        return null;
    }
    
    @Override
    public Set<IBufferElement> removeAll(Object key)
    {
        return values.remove(key);
    }
    
    @Override
    public void clear()
    {
        values.clear();
    }
    
    @Override
    public int size()
    {
        int size = 0;
        for (Set<IBufferElement> set : values.values()) size+=set.size();
        return size;
    }
    
    @Override
    public boolean isEmpty()
    {
        return values.isEmpty();
    }
    
    @Override
    public boolean containsKey(Object key)
    {
        return values.containsKey(key);
    }
    
    @Override
    public boolean containsValue(Object value)
    {
        return false;
    }
    
    @Override
    public boolean containsEntry(Object key, Object value)
    {
        return values.containsKey(key) && values.get(key).contains(value);
    }
    
    @Override
    public boolean put(Class<? extends IBufferElement> key, IBufferElement value)
    {
        get(key).add(value);
        return true;
    }
    
    @Override
    public boolean remove(Object key, Object value)
    {
        return values.containsKey(key) && values.get(key).remove(value);
    }
    
    @Override
    public boolean putAll(Class<? extends IBufferElement> key, Iterable<? extends IBufferElement> values)
    {
        Iterator<? extends IBufferElement> itr = values.iterator();
        if (itr.hasNext())
        {
            Set<IBufferElement> set = this.get(key);
            while(itr.hasNext())
                set.add(itr.next());
            return true;
        }
        return false;
    }
    
    @Override
    public boolean putAll(Multimap<? extends Class<? extends IBufferElement>, ? extends IBufferElement> multimap)
    {
        return false;
    }
    
    @Override
    public Set<IBufferElement> replaceValues(Class<? extends IBufferElement> key, Iterable<? extends IBufferElement> values)
    {
        return null;
    }
    
    @Override
    public Set<Map.Entry<Class<? extends IBufferElement>, IBufferElement>> entries()
    {
        return null;
    }
    
    @Override
    public Map<Class<? extends IBufferElement>, Collection<IBufferElement>> asMap()
    {
        return null;
    }
}