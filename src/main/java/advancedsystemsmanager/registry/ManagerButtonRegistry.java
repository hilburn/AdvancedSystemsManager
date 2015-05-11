package advancedsystemsmanager.registry;

import advancedsystemsmanager.api.gui.IManagerButton;
import advancedsystemsmanager.api.gui.IManagerButtonProvider;
import advancedsystemsmanager.api.gui.ManagerButtonList;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.menus.MenuGroup;
import advancedsystemsmanager.network.DataReader;
import advancedsystemsmanager.network.DataWriter;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.settings.Settings;
import advancedsystemsmanager.tileentities.manager.DefaultButtonProvider;
import advancedsystemsmanager.tileentities.manager.ManagerButton;
import advancedsystemsmanager.tileentities.manager.TileEntityManager;
import io.netty.buffer.ByteBuf;
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
        buttons.add(new ManagerButton(manager, Names.PREFERENCES, 230 - IManagerButton.BUTTON_ICON_SIZE, IManagerButton.BUTTON_ICON_SIZE * 2)
        {
            @Override
            public void onClickReader(ByteBuf buf)
            {

            }

            @Override
            public boolean validClick()
            {
                return false;
            }

            @Override
            public void writeNetworkComponent(ByteBuf buf)
            {
                Settings.openMenu(manager);
            }
        });

        buttons.add(new ManagerButton(manager, Names.EXIT_GROUP, 230 - IManagerButton.BUTTON_ICON_SIZE, IManagerButton.BUTTON_ICON_SIZE * 3)
        {
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
            }

            @Override
            public void onClickReader(ByteBuf buf)
            {
                int id = buf.readInt();
                FlowComponent component = manager.getFlowItem(id);
                boolean moveCluster = buf.readBoolean();
                if (component.getParent() != null)
                {
                    MenuGroup.moveComponents(component, component.getParent().getParent(), moveCluster);
                }
            }

            @Override
            public boolean validClick()
            {
                return true;
            }

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
            public void writeNetworkComponent(ByteBuf buf)
            {
                for (FlowComponent item : manager.getFlowItems())
                {
                    if (item.isBeingMoved())
                    {
                        //For the server only
                        manager.justSentServerComponentRemovalPacket = true;
                        buf.writeInt(item.getId());
                        buf.writeBoolean(GuiScreen.isShiftKeyDown());
                        item.resetPosition();
                        return;
                    }
                }

                //Client only
                manager.selectedGroup = manager.selectedGroup.getParent();
            }

            @Override
            public boolean isVisible()
            {
                return manager.selectedGroup != null;
            }
        });
    }

    public static void registerButtonProvider(IManagerButtonProvider provider)
    {
        buttonProviders.add(provider);
    }
}
