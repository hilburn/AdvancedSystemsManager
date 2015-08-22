package advancedsystemsmanager.registry;

import advancedsystemsmanager.api.gui.IManagerButton;
import advancedsystemsmanager.api.gui.IManagerButtonProvider;
import advancedsystemsmanager.client.gui.ManagerButtonList;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.network.ASMPacket;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.tileentities.manager.DefaultButtonProvider;
import advancedsystemsmanager.tileentities.manager.ManagerButton;
import advancedsystemsmanager.tileentities.manager.Settings;
import advancedsystemsmanager.tileentities.manager.TileEntityManager;
import net.minecraft.client.gui.GuiScreen;

import java.util.ArrayList;
import java.util.List;

public class ManagerButtonRegistry
{
    private static List<IManagerButtonProvider> buttonProviders = new ArrayList<IManagerButtonProvider>();

    static
    {
        registerButtonProvider(new DefaultButtonProvider());
    }

    public static ManagerButtonList getButtons(TileEntityManager manager)
    {
        ManagerButtonList buttons = new ManagerButtonList();
        getButtons(manager, buttons);
        return buttons;
    }

    public static void getButtons(TileEntityManager manager, List<IManagerButton> buttons)
    {
        for (IManagerButtonProvider provider : buttonProviders) buttons.addAll(provider.getButtons(manager));
        buttons.add(new ManagerButton(manager, Names.PREFERENCES, 36, 36)
        {
            @Override
            public boolean readData(ASMPacket packet)
            {
                return false;
            }

            @Override
            public boolean validClick()
            {
                return true;
            }

            @Override
            public boolean writeData(ASMPacket packet)
            {
                Settings.openMenu(manager);
                return false;
            }
        });

        buttons.add(new ManagerButton(manager, Names.EXIT_GROUP, 36, 90)
        {
            @Override
            public String getMouseOver()
            {
                for (FlowComponent item : manager.getFlowItems())
                {
                    if (item.isBeingMoved())
                    {
                        return Names.EXIT_GROUP_DROP;
                    }
                }
                return super.getMouseOver();
            }

            @Override
            public boolean activateOnRelease()
            {
                for (FlowComponent item : manager.getFlowItems())
                {
                    if (item.isBeingMoved())
                    {
                        return true;
                    }
                }
                return false;
            }            @Override
            public boolean readData(ASMPacket packet)
            {
                int id = packet.readInt();
                FlowComponent component = manager.getFlowItem(id);
                boolean moveCluster = packet.readBoolean();
                if (component.getParent() != null)
                {
                    FlowComponent.moveComponents(component, component.getParent().getParent(), moveCluster);
                }
                return true;
            }

            @Override
            public boolean isVisible()
            {
                return manager.selectedGroup != null;
            }            @Override
            public boolean validClick()
            {
                return true;
            }



            @Override
            public boolean writeData(ASMPacket packet)
            {
                for (FlowComponent item : manager.getFlowItems())
                {
                    if (item.isBeingMoved())
                    {
                        //For the server only
                        manager.serverPacket = true;
                        packet.writeInt(item.getId());
                        packet.writeBoolean(GuiScreen.isShiftKeyDown());
                        item.resetPosition();
                        return true;
                    }
                }

                //Client only
                manager.selectedGroup = manager.selectedGroup.getParent();
                return false;
            }


        });
    }

    public static void registerButtonProvider(IManagerButtonProvider provider)
    {
        buttonProviders.add(provider);
    }
}
