package advancedsystemsmanager.compatibility.thaumcraft.commands;

import advancedsystemsmanager.api.execution.IBuffer;
import advancedsystemsmanager.api.execution.IBufferElement;
import advancedsystemsmanager.api.execution.Key;
import advancedsystemsmanager.compatibility.thaumcraft.TCCompat;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.execution.commands.CommandOutput;
import advancedsystemsmanager.flow.menus.Menu;
import advancedsystemsmanager.compatibility.thaumcraft.menus.MenuAspect;
import advancedsystemsmanager.flow.menus.MenuContainer;
import advancedsystemsmanager.flow.setting.Setting;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.util.SystemCoord;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IAspectContainer;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CommandAspectOutput extends CommandOutput<Aspect>
{
    public CommandAspectOutput()
    {
        super(ASPECT_OUTPUT, Names.ASPECT_OUTPUT, IBuffer.ASPECT);
    }

    @Override
    protected void outputFromBuffer(FlowComponent component, IBuffer<Aspect> buffer)
    {
        MenuAspect menuAspect = (MenuAspect)component.menus.get(1);
        List<Setting<Aspect>> validSettings = getValidSettings(menuAspect.getSettings());
        for (SystemCoord block : getContainers(component.manager, (MenuContainer)component.menus.get(0)))
        {
            IAspectContainer aspectContainer = (IAspectContainer)block.getTileEntity();
            Iterator<Map.Entry<Key<Aspect>, IBufferElement<Aspect>>> itr = buffer.getOrderedIterator();
            while (itr.hasNext())
            {
                IBufferElement<Aspect> aspectBufferElement = itr.next().getValue();
                Setting<Aspect> setting = isValid(validSettings, aspectBufferElement.getContent());
                boolean whitelist = menuAspect.useWhiteList();
                if ((setting == null) == whitelist) continue;
                if (aspectContainer.doesContainerAccept(aspectBufferElement.getContent()))
                {
                    int amount =  aspectBufferElement.getSizeLeft();
                    int leftOver = aspectContainer.addToContainer(aspectBufferElement.getContent(), amount);
                    aspectBufferElement.reduceBufferAmount(amount - leftOver);
                    if (aspectBufferElement.getSizeLeft() == 0)
                        aspectBufferElement.remove();
                }
            }
        }
    }

    @Override
    public void getMenus(FlowComponent component, List<Menu> menus)
    {
        menus.add(new MenuContainer(component, TCCompat.ASPECT_CONTAINER));
        menus.add(new MenuAspect(component, false));
    }
}
