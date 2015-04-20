package advancedfactorymanager.components;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RFBufferElement
{

    private FlowComponent component;
    private SlotInventoryHolder inventoryHolder;
    private List<EnergyFacingHolder> holders;
    private Iterator<EnergyFacingHolder> iterator;

    public RFBufferElement(FlowComponent owner, SlotInventoryHolder inventoryHolder, EnergyFacingHolder target)
    {
        this(owner, inventoryHolder);
        this.addTarget(target);
    }

    private RFBufferElement(FlowComponent owner, SlotInventoryHolder inventoryHolder)
    {
        this.component = owner;
        this.inventoryHolder = inventoryHolder;
        this.holders = new ArrayList();
    }

    private void addTarget(EnergyFacingHolder energyHolder)
    {
        this.holders.add(energyHolder);
    }

    public List<EnergyFacingHolder> getSubElements()
    {
        return holders;
    }

    public EnergyFacingHolder getSubElement()
    {
        return this.iterator.hasNext() ? this.iterator.next() : null;
    }

    public void removeSubElement()
    {
        this.iterator.remove();
    }

    public void releaseSubElements()
    {
        this.iterator = null;
    }

    public int getMaxExtract()
    {
        int result = 0;
        for (EnergyFacingHolder holder : holders)
        {
            result += holder.extract(Integer.MAX_VALUE, true);
        }
        return result;
    }

    public int removeRF(int amount)
    {
        int removeEach = amount / holders.size();
        int result = amount;
        for (Iterator<EnergyFacingHolder> itr = holders.iterator(); itr.hasNext(); )
        {
            EnergyFacingHolder holder = itr.next();
            int removed = holder.extract(removeEach, false);
            if (removed < removeEach) itr.remove();
            result -= removed;
        }
        if (result > 0 && holders.size() > 0) return removeRF(result);
        return amount - result;
    }
}
