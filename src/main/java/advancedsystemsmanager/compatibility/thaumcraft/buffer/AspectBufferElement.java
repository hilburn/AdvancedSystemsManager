package advancedsystemsmanager.compatibility.thaumcraft.buffer;

import advancedsystemsmanager.api.execution.IBufferElement;
import advancedsystemsmanager.flow.execution.buffers.elements.BufferElementBase;
import advancedsystemsmanager.flow.setting.Setting;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IAspectContainer;

public class AspectBufferElement extends BufferElementBase<Aspect>
{
    protected IAspectContainer container;

    public AspectBufferElement(int id, IAspectContainer container, Aspect aspect, int amount, Setting<Aspect> setting, boolean whitelist)
    {
        this(id, container, aspect, amount);
        this.setting = setting;
        this.whitelist = whitelist;
    }

    public AspectBufferElement(int id, IAspectContainer container, Aspect aspect, int amount)
    {
        super(id);
        this.amount = amount;
        this.content = aspect;
        this.container = container;
    }

    @Override
    public void remove()
    {
    }

    @Override
    public void onUpdate()
    {
    }

    @Override
    public int getSizeLeft()
    {
        return getMaxWithSetting(Math.min(this.container.getAspects().getAmount(this.content), this.amount));
    }

    @Override
    public int reduceBufferAmount(int amount)
    {
        this.amount -= amount;
        this.container.takeFromContainer(this.content, amount);
        return amount;
    }

    @Override
    public IBufferElement<Aspect> getSplitElement(int elementAmount, int id, boolean fair)
    {
        AspectBufferElement element = new AspectBufferElement(this.id, this.container, this.content, this.amount, this.setting, this.whitelist);
        int oldAmount = getSizeLeft();
        int amount = oldAmount / elementAmount;
        if (!fair)
        {
            int amountLeft = oldAmount % elementAmount;
            if (id < amountLeft)
                amount++;
        }
        element.amount = amount;
        return element;
    }
}
