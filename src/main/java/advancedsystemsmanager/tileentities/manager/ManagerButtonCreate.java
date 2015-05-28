package advancedsystemsmanager.tileentities.manager;

import advancedsystemsmanager.api.execution.ICommand;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.menus.Menu;
import advancedsystemsmanager.flow.menus.MenuCraftingPriority;
import advancedsystemsmanager.flow.menus.MenuStuff;
import advancedsystemsmanager.flow.menus.MenuTarget;
import advancedsystemsmanager.network.ASMPacket;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.settings.Settings;
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
    public boolean readData(ASMPacket packet)
    {
        if (Settings.isLimitless(manager) || manager.getFlowItems().size() < TileEntityManager.MAX_COMPONENT_AMOUNT)
        {
            if (!manager.getWorldObj().isRemote)
            {
                packet.setInt(packet.readerIndex(), manager.getNextFreeID());
            }
            FlowComponent component = new FlowComponent(manager, 50, 50, packet.readInt(), type);

            boolean hasParent = packet.readBoolean();
            if (hasParent)
            {
                component.setParent(manager.getFlowItem(packet.readVarIntFromBuffer()));
            }

            boolean autoSide = packet.readBoolean();
            boolean autoBlackList = packet.readBoolean();
            boolean moveFirst = packet.readBoolean();
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
            return true;
        }
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
        packet.writeInt(0);
        if (manager.selectedGroup != null)
        {
            packet.writeBoolean(true);
            packet.writeVarIntToBuffer(manager.selectedGroup.getId());
        } else
        {
            packet.writeBoolean(false);
        }

        packet.writeBoolean(Settings.isAutoSide());
        packet.writeBoolean(Settings.isAutoBlacklist());
        packet.writeBoolean(Settings.isPriorityMoveFirst());
        return true;
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
