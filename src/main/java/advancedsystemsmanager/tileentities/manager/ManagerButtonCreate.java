package advancedsystemsmanager.tileentities.manager;

import advancedsystemsmanager.api.execution.ICommand;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.menus.Menu;
import advancedsystemsmanager.flow.menus.MenuCraftingPriority;
import advancedsystemsmanager.flow.menus.MenuStuff;
import advancedsystemsmanager.flow.menus.MenuTarget;
import advancedsystemsmanager.network.DataReader;
import advancedsystemsmanager.network.DataWriter;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.settings.Settings;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public class ManagerButtonCreate extends ManagerButton
{
    private ICommand type;

    public ManagerButtonCreate(TileEntityManager manager, ICommand type)
    {
        super(manager, type.getLongName(), type.getX(), type.getY());
        this.type = type;
    }

    @Override
    public void readData(ByteBuf buf)
    {
        if (Settings.isLimitless(manager) || manager.getFlowItems().size() < TileEntityManager.MAX_COMPONENT_AMOUNT)
        {
            FlowComponent component = new FlowComponent(manager, 50, 50, type);

            boolean hasParent = buf.readBoolean();
            if (hasParent)
            {
                component.setParent(manager.getFlowItem(buf.readInt()));
            }

            boolean autoSide = buf.readBoolean();
            boolean autoBlackList = buf.readBoolean();
            boolean moveFirst = buf.readBoolean();
            boolean isInput = type.getCommandType() == ICommand.CommandType.INPUT;
            boolean isOutput = type.getCommandType() == ICommand.CommandType.OUTPUT;
            if (autoSide)
            {
                for (Menu menu : component.getMenus())
                {
                    if (menu instanceof MenuTarget)
                    {
                        ((MenuTarget)menu).setActive(isOutput ? 1 : 0);
                    }
                }
            }
            if (autoBlackList && isInput)
            {
                for (Menu menu : component.getMenus())
                {
                    if (menu instanceof MenuStuff)
                    {
                        ((MenuStuff)menu).setBlackList();
                    }
                }
            }
            if (type.getCommandType() == ICommand.CommandType.CRAFTING)
            {
                for (Menu menu : component.getMenus())
                {
                    if (menu instanceof MenuCraftingPriority)
                    {
                        ((MenuCraftingPriority)menu).setPrioritizeCrafting(!moveFirst);
                    }
                }
            }

            manager.addNewComponent(component);
        }
    }

    @Override
    public boolean validClick()
    {
        return true;
    }


    @Override
    public void writeNetworkComponent(ByteBuf buf)
    {
        if (manager.selectedGroup != null)
        {
            buf.writeBoolean(true);
            buf.writeInt(manager.selectedGroup.getId());
        } else
        {
            buf.writeBoolean(false);
        }

        buf.writeBoolean(Settings.isAutoSide());
        buf.writeBoolean(Settings.isAutoBlacklist());
        buf.writeBoolean(Settings.isPriorityMoveFirst());
    }

    @Override
    public String getMouseOver()
    {
        if (!Settings.isLimitless(manager) && manager.getFlowItems().size() == TileEntityManager.MAX_COMPONENT_AMOUNT)
        {
            return Names.MAXIMUM_COMPONENT_ERROR;
        } else
        {
            //TODO: hmmmm
            return StatCollector.translateToLocal(Names.CREATE_COMMAND) + " " + super.getMouseOver();
        }
    }

    @Override
    public ResourceLocation getTexture()
    {
        return type.getTexture();
    }
}
