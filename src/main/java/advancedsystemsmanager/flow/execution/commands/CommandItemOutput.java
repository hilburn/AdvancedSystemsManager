package advancedsystemsmanager.flow.execution.commands;

import advancedsystemsmanager.api.execution.IBuffer;
import advancedsystemsmanager.api.execution.IBufferProvider;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.menus.Menu;
import advancedsystemsmanager.reference.Names;
import net.minecraft.item.ItemStack;

import java.util.List;

public class CommandItemOutput extends CommandOutput<ItemStack>
{
    public CommandItemOutput()
    {
        super(ITEM_INPUT, Names.ITEM_INPUT, null);
    }

    @Override
    public void execute(FlowComponent command, int connectionId, IBufferProvider bufferProvider)
    {
        if (bufferProvider.containsBuffer(IBuffer.CRAFT_HIGH))
            outputFromBuffer(command, bufferProvider.getBuffer(IBuffer.CRAFT_HIGH));

        if (bufferProvider.containsBuffer(IBuffer.ITEM))
            outputFromBuffer(command, bufferProvider.getBuffer(IBuffer.ITEM));

        if (bufferProvider.containsBuffer(IBuffer.CRAFT_LOW))
            outputFromBuffer(command, bufferProvider.getBuffer(IBuffer.CRAFT_LOW));
    }

    @Override
    protected void outputFromBuffer(FlowComponent component, IBuffer<ItemStack> buffer)
    {

    }

    @Override
    public void getMenus(FlowComponent component, List<Menu> menus)
    {

    }
}
