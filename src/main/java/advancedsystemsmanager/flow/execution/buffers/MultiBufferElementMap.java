package advancedsystemsmanager.flow.execution.buffers;

import advancedsystemsmanager.api.execution.IBufferElement;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.SetMultimap;

import java.util.*;

public class MultiBufferElementMap implements SetMultimap<String, IBufferElement>
{
    public Map<String, BufferElementSet> values = new HashMap<String, BufferElementSet>();
    
    @Override
    public Set<IBufferElement> get(String key)
    {
        if (!values.containsKey(key)) values.put(key, new BufferElementSet(key));
        return values.get(key);
    }
    
    @Override
    public Set<String> keySet()
    {
        return values.keySet();
    }
    
    @Override
    public Multiset<String> keys()
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
    public boolean put(String key, IBufferElement value)
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
    public boolean putAll(String key, Iterable<? extends IBufferElement> values)
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
    public boolean putAll(Multimap<? extends String, ? extends IBufferElement> multimap)
    {
        return false;
    }
    
    @Override
    public Set<IBufferElement> replaceValues(String key, Iterable<? extends IBufferElement> values)
    {
        return null;
    }
    
    @Override
    public Set<Map.Entry<String, IBufferElement>> entries()
    {
        return null;
    }
    
    @Override
    public Map<String, Collection<IBufferElement>> asMap()
    {
        return null;
    }
}