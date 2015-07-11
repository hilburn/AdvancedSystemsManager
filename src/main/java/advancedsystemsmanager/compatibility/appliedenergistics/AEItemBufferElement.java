package advancedsystemsmanager.compatibility.appliedenergistics;

import advancedsystemsmanager.api.execution.IBufferElement;
import advancedsystemsmanager.api.execution.Key;
import advancedsystemsmanager.flow.execution.buffers.elements.BufferElementBase;
import advancedsystemsmanager.flow.setting.Setting;
import advancedsystemsmanager.tileentities.TileEntityAENode;
import appeng.api.storage.data.IAEItemStack;
import net.minecraft.item.ItemStack;

public class AEItemBufferElement extends BufferElementBase<ItemStack>
{
    private TileEntityAENode node;
    private IAEItemStack stack;

    public AEItemBufferElement(int id, TileEntityAENode node, IAEItemStack stack, Setting<ItemStack> setting, boolean whitelist)
    {
        this(id, node, stack);
        this.setting = setting;
        this.whitelist = whitelist;
    }

    private AEItemBufferElement(int id, TileEntityAENode node, IAEItemStack stack)
    {
        super(id);
        this.node = node;
        this.stack = stack;
        this.amount = (int)Math.min(Integer.MAX_VALUE, stack.getStackSize());
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
        return (int)Math.min(stack.getStackSize(), amount);
    }

    @Override
    public int reduceBufferAmount(int amount)
    {
        return (int)node.helper.extract(stack.copy().setStackSize(amount)).getStackSize();
    }

    @Override
    public IBufferElement<ItemStack> getSplitElement(int elementAmount, int id, boolean fair)
    {
        AEItemBufferElement element = new AEItemBufferElement(this.id, this.node, this.stack, this.setting, this.whitelist);
        int oldAmount = this.getSizeLeft();
        int amount = oldAmount / elementAmount;
        if (!fair)
        {
            int amountLeft = oldAmount % elementAmount;
            if (id < amountLeft)
            {
                ++amount;
            }
        }
        element.amount = amount;
        return element;
    }

    @Override
    public Key<ItemStack> getKey()
    {
        return new Key.ItemKey(stack.getItemStack());
    }

    @Override
    public ItemStack getContent()
    {
        return stack.getItemStack();
    }
}
