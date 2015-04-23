package advancedsystemsmanager.tileentities.manager;

import advancedsystemsmanager.api.ISystemListener;
import advancedsystemsmanager.api.ITileEntityInterface;
import advancedsystemsmanager.api.gui.IManagerButton;
import advancedsystemsmanager.api.gui.ManagerButtonList;
import advancedsystemsmanager.registry.CommandRegistry;
import advancedsystemsmanager.flow.Connection;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.Point;
import advancedsystemsmanager.flow.execution.CommandExecutor;
import advancedsystemsmanager.flow.execution.TriggerHelper;
import advancedsystemsmanager.flow.execution.TriggerHelperBUD;
import advancedsystemsmanager.flow.execution.TriggerHelperRedstone;
import advancedsystemsmanager.flow.menus.*;
import advancedsystemsmanager.gui.ContainerManager;
import advancedsystemsmanager.gui.GuiManager;
import advancedsystemsmanager.gui.IInterfaceRenderer;
import advancedsystemsmanager.flow.elements.*;
import advancedsystemsmanager.network.*;
import advancedsystemsmanager.registry.*;
import advancedsystemsmanager.settings.Settings;
import advancedsystemsmanager.tileentities.TileEntityBUD;
import advancedsystemsmanager.tileentities.TileEntityCluster;
import advancedsystemsmanager.tileentities.TileEntityClusterElement;
import advancedsystemsmanager.tileentities.TileEntityInput;
import advancedsystemsmanager.util.ConnectionBlock;
import advancedsystemsmanager.util.ConnectionBlockType;
import advancedsystemsmanager.util.WorldCoordinate;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

import java.util.*;

public class TileEntityManager extends TileEntity implements ITileEntityInterface
{
    public static final TriggerHelperRedstone redstoneTrigger = new TriggerHelperRedstone(3, 4);
    public static final TriggerHelperRedstone redstoneCondition = new TriggerHelperRedstone(1, 2);
    public static final TriggerHelperBUD budTrigger = new TriggerHelperBUD();

    public static final int BUTTON_SIZE_W = 14;
    public static final int BUTTON_SIZE_H = 14;
    public static final int BUTTON_SRC_X = 242;
    public static final int BUTTON_SRC_Y = 0;
    public static final int BUTTON_INNER_SRC_X = 230;
    public static final int BUTTON_INNER_SRC_Y = 0;
    public List<FlowComponent> items;
    private Connection currentlyConnecting;
    public ManagerButtonList buttons;
    public boolean justSentServerComponentRemovalPacket;
    private List<FlowComponent> zLevelRenderingList;
    private Variable[] variables;
    public FlowComponent selectedComponent;
    @SideOnly(Side.CLIENT)
    public IInterfaceRenderer specialRenderer;

    public TileEntityManager()
    {
        items = new ArrayList<FlowComponent>();
        zLevelRenderingList = new ArrayList<FlowComponent>();
        buttons = ManagerButtonRegistry.getButtons(this);
        removedIds = new ArrayList<Integer>();
        variables = new Variable[VariableColor.values().length];
        for (int i = 0; i < variables.length; i++)
        {
            variables[i] = new Variable(i);
        }
    }

    private List<Integer> removedIds;

    public void removeFlowComponent(int idToRemove, List<FlowComponent> items)
    {
        for (int i = items.size() - 1; i >= 0; i--)
        {
            FlowComponent component = items.get(i);
            if (i == idToRemove)
            {
                component.setParent(null); //unlink it
                items.remove(i);
            } else
            {
                component.updateConnectionIdsAtRemoval(idToRemove);
            }
        }

        if (selectedComponent != null && selectedComponent.getId() == idToRemove)
        {
            selectedComponent = null;
        }

        //do this afterwards so the new ids won't mess anything up
        for (int i = idToRemove; i < items.size(); i++)
        {
            items.get(i).decreaseId();
        }
    }

    public void removeFlowComponent(int idToRemove)
    {
        removeFlowComponent(idToRemove, items);
        if (!worldObj.isRemote)
        {
            removedIds.add(idToRemove);
        } else
        {
            for (int i = 0; i < zLevelRenderingList.size(); i++)
            {
                if (zLevelRenderingList.get(i).getId() == idToRemove)
                {
                    zLevelRenderingList.remove(i);
                    break;
                }
            }
        }
        updateVariables();
    }


    public List<FlowComponent> getFlowItems()
    {
        return items;
    }

    public List<FlowComponent> getZLevelRenderingList()
    {
        return zLevelRenderingList;
    }

    List<ConnectionBlock> inventories = new ArrayList<ConnectionBlock>();

    public List<ConnectionBlock> getConnectedInventories()
    {
        return inventories;
    }

    public static final int MAX_CABLE_LENGTH = 128;
    public static final int MAX_COMPONENT_AMOUNT = 511;
    public static final int MAX_CONNECTED_INVENTORIES = 1023;

    private boolean firstInventoryUpdate = true;
    private boolean firstCommandExecution = true;

    public void updateInventories()
    {
        usingUnlimitedInventories = false;
        WorldCoordinate[] oldCoordinates = new WorldCoordinate[inventories.size()];
        for (int i = 0; i < oldCoordinates.length; i++)
        {
            TileEntity inventory = inventories.get(i).getTileEntity();
            oldCoordinates[i] = new WorldCoordinate(inventory.xCoord, inventory.yCoord, inventory.zCoord);
            oldCoordinates[i].setTileEntity(inventory);
        }

        List<WorldCoordinate> visited = new ArrayList<WorldCoordinate>();
        inventories.clear();
        Queue<WorldCoordinate> queue = new PriorityQueue<WorldCoordinate>();
        WorldCoordinate start = new WorldCoordinate(xCoord, yCoord, zCoord, 0);
        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty())
        {
            WorldCoordinate element = queue.poll();

            for (int x = -1; x <= 1; x++)
            {
                for (int y = -1; y <= 1; y++)
                {
                    for (int z = -1; z <= 1; z++)
                    {
                        if (Math.abs(x) + Math.abs(y) + Math.abs(z) == 1)
                        {
                            WorldCoordinate target = new WorldCoordinate(element.getX() + x, element.getY() + y, element.getZ() + z, element.getDepth() + 1);

                            if (!visited.contains(target) && (Settings.isLimitless(this) || inventories.size() < MAX_CONNECTED_INVENTORIES))
                            {
                                visited.add(target);
                                TileEntity te = worldObj.getTileEntity(target.getX(), target.getY(), target.getZ());

                                if (te instanceof TileEntityCluster)
                                {

                                    for (TileEntityClusterElement tileEntityClusterElement : ((TileEntityCluster)te).getElements())
                                    {
                                        ((TileEntityCluster)te).setWorldObject(tileEntityClusterElement);
                                        addInventory(tileEntityClusterElement, target);
                                    }
                                } else
                                {
                                    addInventory(te, target);
                                }


                                if ((Settings.isLimitless(this) || element.getDepth() < MAX_CABLE_LENGTH) && BlockRegistry.blockCable.isCable(worldObj.getBlock(target.getX(), target.getY(), target.getZ()), worldObj.getBlockMetadata(target.getX(), target.getY(), target.getZ())))
                                {
                                    queue.add(target);
                                }
                            }
                        }

                    }
                }
            }

        }

        if (!firstInventoryUpdate)
        {
            for (WorldCoordinate oldCoordinate : oldCoordinates)
            {
                if (oldCoordinate.getTileEntity() instanceof ISystemListener)
                {
                    boolean found = false;
                    for (ConnectionBlock inventory : inventories)
                    {
                        if (oldCoordinate.getX() == inventory.getTileEntity().xCoord && oldCoordinate.getY() == inventory.getTileEntity().yCoord && oldCoordinate.getZ() == inventory.getTileEntity().zCoord)
                        {
                            found = true;
                            break;
                        }
                    }

                    if (!found)
                    {
                        ((ISystemListener)oldCoordinate.getTileEntity()).removed(this);
                    }
                }
            }

            if (!worldObj.isRemote)
            {
                updateInventorySelection(oldCoordinates);
            } else
            {
                for (FlowComponent item : items)
                {
                    item.setInventoryListDirty(true);
                }
            }
        }


        firstInventoryUpdate = false;
    }

    private void addInventory(TileEntity te, WorldCoordinate target)
    {
        ConnectionBlock connection = new ConnectionBlock(te, target.getDepth());
        boolean isValidConnection = false;

        for (ConnectionBlockType connectionBlockType : ConnectionBlockType.values())
        {
            if (connectionBlockType.isInstance(connection.getTileEntity()))
            {
                isValidConnection = true;
                connection.addType(connectionBlockType);
            }
        }

        if (isValidConnection)
        {
            connection.setId(variables.length + inventories.size());

            if (target.getDepth() >= MAX_CABLE_LENGTH || inventories.size() >= MAX_CONNECTED_INVENTORIES)
            {
                usingUnlimitedInventories = true;
            }
            inventories.add(connection);
            if (connection.getTileEntity() instanceof ISystemListener)
            {
                ((ISystemListener)connection.getTileEntity()).added(this);
            }
        }
    }

    private void updateInventorySelection(WorldCoordinate[] oldCoordinates)
    {
        for (FlowComponent item : items)
        {
            for (Menu menu : item.getMenus())
            {
                if (menu instanceof MenuContainer)
                {
                    MenuContainer menuInventory = (MenuContainer)menu;

                    List<Integer> oldSelection = menuInventory.getSelectedInventories();
                    menuInventory.setSelectedInventories(getNewSelection(oldCoordinates, oldSelection, true));
                }
            }

        }

        for (Variable variable : variables)
        {
            variable.setContainers(getNewSelection(oldCoordinates, variable.getContainers(), false));
        }
    }

    private List<Integer> getNewSelection(WorldCoordinate[] oldCoordinates, List<Integer> oldSelection, boolean hasVariables)
    {

        List<Integer> newSelection = new ArrayList<Integer>();

        for (int i = 0; i < oldSelection.size(); i++)
        {
            int selection = oldSelection.get(i);
            if (hasVariables && selection >= 0 && selection < 16)
            {
                newSelection.add(selection);
            } else
            {
                if (hasVariables)
                {
                    selection -= variables.length;
                }

                if (selection >= 0 && selection < oldCoordinates.length)
                {
                    WorldCoordinate coordinate = oldCoordinates[selection];

                    for (int j = 0; j < inventories.size(); j++)
                    {
                        TileEntity inventory = inventories.get(j).getTileEntity();
                        if (coordinate.getX() == inventory.xCoord && coordinate.getY() == inventory.yCoord && coordinate.getZ() == inventory.zCoord && inventory.getClass().equals(coordinate.getTileEntity().getClass()))
                        {
                            int id = j + (hasVariables ? variables.length : 0);
                            if (!newSelection.contains(id))
                            {
                                newSelection.add(id);
                            }

                            break;
                        }
                    }

                }
            }
        }

        return newSelection;
    }


    public Connection getCurrentlyConnecting()
    {
        return currentlyConnecting;
    }

    public void setCurrentlyConnecting(Connection currentlyConnecting)
    {
        this.currentlyConnecting = currentlyConnecting;
    }

    private int timer = 0;

    @Override
    public void updateEntity()
    {
        justSentServerComponentRemovalPacket = false;
        if (!worldObj.isRemote)
        {

            if (timer >= 20)
            {
                timer = 0;

                for (FlowComponent item : items)
                {

                    if (item.getType() == CommandRegistry.TRIGGER)
                    {
                        MenuInterval componentMenuInterval = (MenuInterval)item.getMenus().get(TriggerHelper.TRIGGER_INTERVAL_ID);
                        int interval = componentMenuInterval.getInterval();
                        if (interval == 0)
                        {
                            continue;
                        }
                        item.setCurrentInterval(item.getCurrentInterval() + 1);
                        if (item.getCurrentInterval() >= interval)
                        {
                            item.setCurrentInterval(0);

                            EnumSet<ConnectionOption> valid = EnumSet.of(ConnectionOption.INTERVAL);
                            if (item.getConnectionSet() == ConnectionSet.REDSTONE)
                            {
                                redstoneTrigger.onTrigger(item, valid);
                            } else if (item.getConnectionSet() == ConnectionSet.BUD)
                            {
                                budTrigger.onTrigger(item, valid);
                            }
                            activateTrigger(item, valid);
                        }
                    }
                }


            } else
            {
                timer++;
            }
        }
    }

    public void updateFirst()
    {
        if (firstCommandExecution)
        {
            updateInventories();
            updateVariables();

            firstCommandExecution = false;
        }
    }

    public void activateTrigger(FlowComponent component, EnumSet<ConnectionOption> validTriggerOutputs)
    {
        updateFirst();
        for (ConnectionBlock inventory : inventories)
        {
            if (inventory.getTileEntity().isInvalid())
            {
                updateInventories();
                break;
            }
        }
        new CommandExecutor(this).executeTriggerCommand(component, validTriggerOutputs);
    }


    public void triggerRedstone(TileEntityInput inputTrigger)
    {
        for (FlowComponent item : items)
        {
            if (item.getType() == CommandRegistry.TRIGGER && item.getConnectionSet() == ConnectionSet.REDSTONE)
            {
                redstoneTrigger.onRedstoneTrigger(item, inputTrigger);
            }
        }
    }

    public void triggerChat()
    {
        for (FlowComponent item : items)
        {
            if (item.getType() == CommandRegistry.TRIGGER && item.getConnectionSet() == ConnectionSet.CHAT)
            {
                activateTrigger(item, EnumSet.allOf(ConnectionOption.class));
            }
        }
    }


    public void readGenericData(DataReader dr)
    {
        if (worldObj.isRemote)
        {
            if (dr.readBoolean())
            {
                updateInventories();
            } else
            {
                removeFlowComponent(dr.readComponentId());
            }
        } else
        {
            int buttonId = dr.readData(DataBitHelper.GUI_BUTTON_ID);
            if (buttonId >= 0 && buttonId < buttons.size())
            {
                IManagerButton button = buttons.get(buttonId);
                if (button.isVisible())
                {
                    button.onClick(dr);
                }
            }
        }
    }


    private TileEntityManager self = this;

    public List<Integer> getRemovedIds()
    {
        return removedIds;
    }

    @Override
    public Container getContainer(TileEntity te, InventoryPlayer inv)
    {
        return new ContainerManager((TileEntityManager)te, inv);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public GuiScreen getGui(TileEntity te, InventoryPlayer inv)
    {
        return new GuiManager((TileEntityManager)te, inv);
    }

    @Override
    public void readAllData(DataReader dr, EntityPlayer player)
    {
        updateInventories();
        int flowControlCount = dr.readComponentId();
        getFlowItems().clear();
        getZLevelRenderingList().clear();
        for (int i = 0; i < flowControlCount; i++)
        {
            readAllComponentData(dr);
        }
        for (FlowComponent item : items)
        {
            item.linkParentAfterLoad();
        }

        if (Settings.isAutoCloseGroup())
        {
            selectedComponent = null;
        } else
        {
            while (selectedComponent != null && !findNewSelectedComponent(selectedComponent.getId()))
            {
                selectedComponent = selectedComponent.getParent();
            }
        }
    }

    private boolean findNewSelectedComponent(int id)
    {
        for (FlowComponent item : items)
        {
            if (item.getId() == id)
            {
                selectedComponent = item;
                return true;
            }
        }

        return false;
    }

    private void readAllComponentData(DataReader dr)
    {
        int x = dr.readData(DataBitHelper.FLOW_CONTROL_X);
        int y = dr.readData(DataBitHelper.FLOW_CONTROL_Y);
        int id = dr.readData(DataBitHelper.FLOW_CONTROL_TYPE_ID);

        FlowComponent flowComponent = new FlowComponent(this, x, y, CommandRegistry.getCommand(id));
        flowComponent.setComponentName(dr.readString(DataBitHelper.NAME_LENGTH));

        boolean hasParent = dr.readBoolean();
        if (hasParent)
        {
            flowComponent.setParentLoadId(dr.readComponentId());
        } else
        {
            flowComponent.setParentLoadId(-1);
        }

        for (Menu menu : flowComponent.getMenus())
        {
            menu.readData(dr);
        }

        flowComponent.clearConnections();
        for (int i = 0; i < flowComponent.getConnectionSet().getConnections().length; i++)
        {
            boolean hasConnection = dr.readBoolean();

            if (hasConnection)
            {
                Connection connection = new Connection(dr.readComponentId(), dr.readData(DataBitHelper.CONNECTION_ID));
                flowComponent.setConnection(i, connection);


                int length = dr.readData(DataBitHelper.NODE_ID);
                for (int j = 0; j < length; j++)
                {
                    connection.getNodes().add(new Point(dr.readData(DataBitHelper.FLOW_CONTROL_X), dr.readData(DataBitHelper.FLOW_CONTROL_Y)));
                }
            }
        }


        getFlowItems().add(flowComponent);
        getZLevelRenderingList().add(0, flowComponent);

        updateVariables();
    }

    private boolean usingUnlimitedInventories;

    private boolean isUsingUnlimitedStuff()
    {
        return items.size() > MAX_COMPONENT_AMOUNT || usingUnlimitedInventories;
    }

    @Override
    public void readUpdatedData(DataReader dr, EntityPlayer player)
    {
        if (!worldObj.isRemote && dr.readBoolean())
        {
            boolean val = dr.readBoolean();
            if ((val || !isUsingUnlimitedStuff()) && player.capabilities.isCreativeMode)
            {
                Settings.setLimitless(this, val);
            }
            //TODO use ids for different actions
            /*System.out.println("ACTION");
            for (FlowComponent item : items) {
                item.adjustEverythingToGridRaw();
            }
            for (FlowComponent item : items) {
                item.adjustEverythingToGridFine();
            } */
            return;
        }

        boolean isNew = worldObj.isRemote && dr.readBoolean();
        if (isNew)
        {
            readAllComponentData(dr);
            items.get(items.size() - 1).linkParentAfterLoad();
        } else
        {
            boolean isSpecificComponent = dr.readBoolean();
            if (isSpecificComponent)
            {

                INetworkReader nr = getNetworkReaderForComponentPacket(dr, this);

                if (nr != null)
                {
                    nr.readNetworkComponent(dr);
                }
            } else
            {
                readGenericData(dr);
            }
        }
    }

    @Override
    public void writeAllData(DataWriter dw)
    {
        dw.writeComponentId(this, getFlowItems().size());
        for (FlowComponent flowComponent : getFlowItems())
        {
            PacketHandler.writeAllComponentData(dw, flowComponent);
        }
    }

    private INetworkReader getNetworkReaderForComponentPacket(DataReader dr, TileEntityManager jam)
    {

        int componentId = dr.readComponentId();
        if (componentId >= 0 && componentId < jam.getFlowItems().size())
        {
            FlowComponent component = jam.getFlowItems().get(componentId);

            if (dr.readBoolean())
            {
                int menuId = dr.readData(DataBitHelper.FLOW_CONTROL_MENU_COUNT);
                if (menuId >= 0 && menuId < component.getMenus().size())
                {
                    return component.getMenus().get(menuId);
                }
            } else
            {
                return component;
            }
        }

        return null;
    }

    public Variable[] getVariables()
    {
        return variables;
    }

    public void updateVariables()
    {
        for (Variable variable : variables)
        {
            variable.setDeclaration(null);
        }

        for (FlowComponent item : items)
        {
            if (item.getType() == CommandRegistry.VARIABLE && item.getConnectionSet() == ConnectionSet.EMPTY)
            {
                int selectedVariable = ((MenuVariable)item.getMenus().get(0)).getSelectedVariable();
                variables[selectedVariable].setDeclaration(item);
            }
        }
    }

    public void triggerBUD(TileEntityBUD tileEntityBUD)
    {
        for (FlowComponent item : items)
        {
            if (item.getType() == CommandRegistry.TRIGGER && item.getConnectionSet() == ConnectionSet.BUD)
            {
                budTrigger.triggerBUD(item, tileEntityBUD);
            }
        }
    }

    public FlowComponent getSelectedComponent()
    {
        return selectedComponent;
    }

    public void setSelectedComponent(FlowComponent selectedComponent)
    {
        this.selectedComponent = selectedComponent;
    }

    private static final String NBT_TIMER = "Timer";
    private static final String NBT_COMPONENTS = "Components";
    private static final String NBT_VARIABLES = "Variables";

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound)
    {
        super.readFromNBT(nbtTagCompound);

        readContentFromNBT(nbtTagCompound, false);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound)
    {
        super.writeToNBT(nbtTagCompound);

        writeContentToNBT(nbtTagCompound, false);
    }

    public void readContentFromNBT(NBTTagCompound nbtTagCompound, boolean pickup)
    {
        timer = nbtTagCompound.getByte(NBT_TIMER);

        NBTTagList components = nbtTagCompound.getTagList(NBT_COMPONENTS, 10);
        for (int i = 0; i < components.tagCount(); i++)
        {
            NBTTagCompound component = components.getCompoundTagAt(i);

            items.add(FlowComponent.readFromNBT(this, component, 12, pickup));
        }

        for (FlowComponent item : items)
        {
            item.linkParentAfterLoad();
        }

        NBTTagList variablesTag = nbtTagCompound.getTagList(NBT_VARIABLES, 10);
        for (int i = 0; i < variablesTag.tagCount(); i++)
        {
            NBTTagCompound variableTag = variablesTag.getCompoundTagAt(i);
            variables[i].readFromNBT(variableTag);
        }

    }

    public void writeContentToNBT(NBTTagCompound nbtTagCompound, boolean pickup)
    {
        nbtTagCompound.setByte(NBT_TIMER, (byte)timer);

        NBTTagList components = new NBTTagList();
        for (FlowComponent item : items)
        {
            NBTTagCompound component = new NBTTagCompound();
            item.writeToNBT(component, pickup);
            components.appendTag(component);
        }
        nbtTagCompound.setTag(NBT_COMPONENTS, components);


        NBTTagList variablesTag = new NBTTagList();
        for (Variable variable : variables)
        {
            NBTTagCompound variableTag = new NBTTagCompound();
            variable.writeToNBT(variableTag);
            variablesTag.appendTag(variableTag);
        }
        nbtTagCompound.setTag(NBT_VARIABLES, variablesTag);
    }

}
