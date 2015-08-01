package advancedsystemsmanager.compatibility.thaumcraft.commands;

import advancedsystemsmanager.api.execution.IBuffer;
import advancedsystemsmanager.api.execution.IBufferElement;
import advancedsystemsmanager.compatibility.thaumcraft.TCCompat;
import advancedsystemsmanager.compatibility.thaumcraft.menus.MenuAspect;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.execution.buffers.Buffer;
import advancedsystemsmanager.compatibility.thaumcraft.buffer.AspectBufferElement;
import advancedsystemsmanager.flow.execution.commands.CommandInput;
import advancedsystemsmanager.flow.menus.*;
import advancedsystemsmanager.flow.setting.Setting;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.registry.SystemTypeRegistry;
import advancedsystemsmanager.util.SystemCoord;
import net.minecraft.tileentity.TileEntity;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IAspectContainer;

import java.util.ArrayList;
import java.util.List;

public class CommandAspectInput extends CommandInput<Aspect>
{
    public CommandAspectInput()
    {
        super(ASPECT_INPUT, Names.ASPECT_INPUT, IBuffer.ASPECT);
    }

    @Override
    public void getMenus(FlowComponent component, List<Menu> menus)
    {
        menus.add(new MenuContainer(component, TCCompat.ASPECT_CONTAINER));
        menus.add(new MenuAspect(component));
    }

    @Override
    protected IBuffer getNewBuffer()
    {
        return new Buffer<Aspect>();
    }

    @Override
    protected List<IBufferElement<Aspect>> getBufferSubElements(int id, List<SystemCoord> blocks, List<Menu> menus)
    {
        MenuAspect settings = (MenuAspect)menus.get(1);
        List<Setting<Aspect>> validSettings = getValidSettings(settings.getSettings());
        List<IBufferElement<Aspect>> subElements = new ArrayList<IBufferElement<Aspect>>();
        for (SystemCoord block : blocks)
        {
            TileEntity entity = block.getTileEntity();
            if (entity instanceof IAspectContainer)
            {
                IAspectContainer aspectContainer = (IAspectContainer)entity;
                for (Aspect aspect : aspectContainer.getAspects().getAspects())
                {
                    if (aspect == null) continue;
                    Setting<Aspect> setting = isValid(validSettings, aspect);
                    boolean whitelist = settings.isFirstRadioButtonSelected();
                    if ((setting == null) != whitelist)
                        subElements.add(new AspectBufferElement(id, aspectContainer, aspect, aspectContainer.getAspects().getAmount(aspect), setting, whitelist));
                }
            }
        }
        return subElements;
    }
}
