package advancedsystemsmanager.tileentities.manager;

import advancedsystemsmanager.api.execution.IComponentType;
import advancedsystemsmanager.api.gui.IManagerButton;
import advancedsystemsmanager.api.gui.IManagerButtonProvider;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.menus.MenuGroup;
import advancedsystemsmanager.helpers.CopyHelper;
import advancedsystemsmanager.helpers.Localization;
import advancedsystemsmanager.helpers.StevesEnum;
import advancedsystemsmanager.network.DataReader;
import advancedsystemsmanager.network.DataWriter;
import advancedsystemsmanager.registry.ComponentRegistry;
import advancedsystemsmanager.settings.Settings;
import net.minecraft.client.gui.GuiScreen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class DefaultButtonProvider implements IManagerButtonProvider
{
    @Override
    public List<IManagerButton> getButtons(TileEntityManager manager)
    {
        List<IManagerButton> buttons = new ArrayList<IManagerButton>();
        for (IComponentType type : ComponentRegistry.getComponents())
        {
            buttons.add(new ManagerButtonCreate(manager, type));
        }
        buttons.add(new ManagerButton(manager, Localization.DELETE_COMMAND.toString(), 230 - IManagerButton.BUTTON_ICON_SIZE, 0)
        {
            @Override
            public void onClick(DataReader dr)
            {
                int idToRemove = dr.readComponentId();
                manager.removeFlowComponent(idToRemove);
            }

            @Override
            public boolean onClick(DataWriter dw)
            {
                manager.justSentServerComponentRemovalPacket = true;
                for (FlowComponent item : manager.getFlowItems())
                {
                    if (item.isBeingMoved())
                    {
                        dw.writeComponentId(manager, item.getId());
                        return true;
                    }
                }
                return false;
            }

            @Override
            public boolean activateOnRelease()
            {
                return true;
            }
        });
        return buttons;
    }
}
