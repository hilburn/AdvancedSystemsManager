package advancedsystemsmanager.tileentities.manager;

import advancedsystemsmanager.api.ISystemType;
import advancedsystemsmanager.api.gui.IManagerButton;
import advancedsystemsmanager.api.network.IPacketReader;
import advancedsystemsmanager.api.tileentities.*;
import advancedsystemsmanager.compatibility.rf.RFCompat;
import advancedsystemsmanager.flow.Connection;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.elements.Variable;
import advancedsystemsmanager.flow.execution.Executor;
import advancedsystemsmanager.flow.execution.TriggerHelper;
import advancedsystemsmanager.flow.execution.TriggerHelperBUD;
import advancedsystemsmanager.flow.execution.TriggerHelperRedstone;
import advancedsystemsmanager.flow.menus.MenuInterval;
import advancedsystemsmanager.flow.menus.MenuTriggered;
import advancedsystemsmanager.flow.menus.MenuVariable;
import advancedsystemsmanager.containers.ContainerManager;
import advancedsystemsmanager.client.gui.GuiManager;
import advancedsystemsmanager.client.gui.IInterfaceRenderer;
import advancedsystemsmanager.client.gui.ManagerButtonList;
import advancedsystemsmanager.network.ASMPacket;
import advancedsystemsmanager.network.PacketHandler;
import advancedsystemsmanager.reference.Mods;
import advancedsystemsmanager.registry.*;
import advancedsystemsmanager.tileentities.TileEntityBUD;
import advancedsystemsmanager.tileentities.TileEntityCluster;
import advancedsystemsmanager.util.SystemCoord;
import cofh.api.energy.IEnergyReceiver;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.*;

import static advancedsystemsmanager.api.execution.ICommand.CommandType;

@Optional.Interface(iface = "cofh.api.energy.IEnergyReceiver", modid = Mods.COFH_ENERGY)
public class TileEntityManager extends TileEntity implements ITileInterfaceProvider, ITriggerNode, ISystemTypeFilter, IEnergyReceiver
{
    public static final TriggerHelperRedstone redstoneTrigger = new TriggerHelperRedstone(3, 4);
    public static final TriggerHelperRedstone redstoneCondition = new TriggerHelperRedstone(1, 2);
    public static final TriggerHelperBUD budTrigger = new TriggerHelperBUD();

    public static final int BUTTON_SRC_X = 0;
    public static final int BUTTON_SRC_Y = 234;
    public static final int MAX_CABLE_LENGTH = 128;
    public static final int MAX_COMPONENT_AMOUNT = 511;
    public static final int MAX_CONNECTED_INVENTORIES = 1023;
    public static final String NBT_COMPONENTS = "Components";
    private static final String NBT_MAX_ID = "maxID";
    private static final String NBT_TIMER = "Timer";
    private static final String NBT_VARIABLES = "Variables";
    private static final String NBT_SIDES = "Sides";
    public static boolean energyCostActive;
    private List<FlowComponent> triggers;
    private List<FlowComponent> quickTriggers;
    public ManagerButtonList buttons;
    public boolean serverPacket;
    public boolean variableUpdate;
    public TIntObjectHashMap<FlowComponent> components;
    public TIntObjectHashMap<Variable> variables;
    public TLongObjectHashMap<SystemCoord> network;
    public FlowComponent selectedGroup;
    @SideOnly(Side.CLIENT)
    public IInterfaceRenderer specialRenderer;
    private Connection currentlyConnecting;
    private List<FlowComponent> zLevelRenderingList;
    private int maxID;
    private int triggerOffset;
    private boolean firstInventoryUpdate = true;
    private boolean firstCommandExecution = true;
    private int timer = 0;
    private int[] oldPowered = new int[ForgeDirection.VALID_DIRECTIONS.length];
    private int[] isPowered = new int[ForgeDirection.VALID_DIRECTIONS.length];
    private boolean usingUnlimitedInventories;

    public TileEntityManager()
    {
        zLevelRenderingList = new ArrayList<FlowComponent>();
        buttons = ManagerButtonRegistry.getButtons(this);
        components = new TIntObjectHashMap<FlowComponent>();
        triggers = new ArrayList<FlowComponent>();
        quickTriggers = new ArrayList<FlowComponent>();
        network = new TLongObjectHashMap<SystemCoord>();
        variables = Variable.getDefaultVariables();
        this.triggerOffset = (((173 + xCoord) << 8 + yCoord) << 8 + zCoord) % 20;
    }

    public void removeFlowComponent(int idToRemove, boolean connected)
    {
        List<FlowComponent> removed;
        if (connected)
        {
            removed = new ArrayList<FlowComponent>();
            List<FlowComponent> toRemove = new ArrayList<FlowComponent>();
            FlowComponent.findCluster(toRemove, getFlowItem(idToRemove), null);
            for (FlowComponent flowComponent : toRemove)
            {
                removed.addAll(removeFlowComponents(flowComponent.getId(), true));
            }
        } else
        {
            removed = removeFlowComponents(idToRemove, false);
        }
        if (!worldObj.isRemote)
        {
            triggers.removeAll(removed);
        } else
        {
            zLevelRenderingList.removeAll(removed);
        }
        updateVariables();
    }

    public FlowComponent getFlowItem(int i)
    {
        return components.get(i);
    }

    public List<FlowComponent> removeFlowComponents(int idToRemove, boolean connected)
    {
        List<FlowComponent> result = new ArrayList<FlowComponent>();
        Queue<Integer> remove = new PriorityQueue<Integer>();
        Multimap<FlowComponent, FlowComponent> parents = getParentHierarchy();
        remove.add(idToRemove);
        if (!connected) getFlowItem(idToRemove).deleteConnections();
        while (!remove.isEmpty())
        {
            FlowComponent removed = removeComponent(remove.poll());
            result.add(removed);
            for (FlowComponent child : parents.get(removed))
            {
                remove.add(child.getId());
            }
        }
        return result;
    }

    public void updateVariables()
    {
        for (Variable variable : variables.valueCollection())
        {
            variable.setDeclaration(null);
        }

        for (FlowComponent item : getFlowItems())
        {
            if (item.getType() == CommandRegistry.VARIABLE && item.getConnectionSet() == ConnectionSet.EMPTY)
            {
                int selectedVariable = ((MenuVariable)item.getMenus().get(0)).getSelectedVariable();
                Variable variable = variables.get(selectedVariable);
                if (variable != null && !variable.isValid())
                {
                    variable.setDeclaration(item);
                }
            }
        }
    }

    public Multimap<FlowComponent, FlowComponent> getParentHierarchy()
    {
        Multimap<FlowComponent, FlowComponent> result = HashMultimap.create();
        for (FlowComponent component : getFlowItems())
        {
            if (component.getParent() != null) result.put(component.getParent(), component);
        }
        return result;
    }

    private FlowComponent removeComponent(int idToRemove)
    {
        FlowComponent removed = components.get(idToRemove);
        components.remove(idToRemove);
        if (selectedGroup == removed)
        {
            selectedGroup = null;
        }
        return removed;
    }

    public Collection<FlowComponent> getFlowItems()
    {
        return components.valueCollection();
    }

    public TLongObjectHashMap<SystemCoord> getNetwork()
    {
        return network;
    }

    public List<SystemCoord> getConnectedInventories()
    {
        List<SystemCoord> result = new ArrayList<SystemCoord>(network.valueCollection());
        Collections.sort(result);
        return result;
    }

    public Connection getCurrentlyConnecting()
    {
        return currentlyConnecting;
    }

    public void setCurrentlyConnecting(Connection currentlyConnecting)
    {
        this.currentlyConnecting = currentlyConnecting;
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
        for (SystemCoord inventory : network.valueCollection())
        {
            if (inventory.getTileEntity().isInvalid())
            {
                updateInventories();
                break;
            }
        }
        new Executor(this).executeTriggerCommand(component, validTriggerOutputs);
    }

    @Override
    public Container getContainer(EntityPlayer player)
    {
        return new ContainerManager(this, player.inventory);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public GuiScreen getGui(EntityPlayer player)
    {
        return new GuiManager(this, player.inventory);
    }

    @Override
    public boolean readData(ASMPacket packet, EntityPlayer player)
    {
        boolean result = false;
        switch (packet.readByte())
        {
            case PacketHandler.SYNC_ALL:
                updateInventories();
                int flowControlCount = packet.readVarIntFromBuffer();
                components.clear();
                getZLevelRenderingList().clear();
                for (int i = 0; i < flowControlCount; i++)
                {
                    NBTTagCompound tagCompound = packet.readNBTTagCompoundFromBuffer();
                    FlowComponent component = FlowComponent.readFromNBT(this, tagCompound, false);
                    if (component != null)
                        addNewComponent(component);
                }
                variables.clear();
                int variableCount = packet.readVarIntFromBuffer();
                for (int i = 0; i < variableCount; i++)
                {
                    int colour = packet.readUnsignedMedium();
                    String name = packet.readStringFromBuffer();
                    addVariable(new Variable(colour, name));
                }
                for (FlowComponent item : getFlowItems())
                {
                    item.linkAfterLoad();
                }

                if (Settings.isAutoCloseGroup())
                {
                    selectedGroup = null;
                } else
                {
                    while (selectedGroup != null && !findNewSelectedComponent(selectedGroup.getId()))
                    {
                        selectedGroup = selectedGroup.getParent();
                    }
                }
                variableUpdate = true;
                break;
            case PacketHandler.SETTING_MESSAGE:
                if (!worldObj.isRemote)
                {
                    boolean val = packet.readBoolean();
                    if ((val || !isUsingUnlimitedStuff()) && player.capabilities.isCreativeMode)
                    {
                        Settings.setLimitless(this, val);
                    }
                    /*System.out.println("ACTION");
                    for (FlowComponent item : items) {
                        item.adjustEverythingToGridRaw();
                    }
                    for (FlowComponent item : items) {
                        item.adjustEverythingToGridFine();
                    } */
                }
                break;
            case PacketHandler.SYNC_COMPONENT:
                IPacketReader nr = getFlowItem(packet.readVarIntFromBuffer());

                result = nr != null && nr.readData(packet);
                break;
            case 3:
                updateInventories();
                break;
            case PacketHandler.NEW_VARIABLE:
                addNewVariable(new Variable(packet.readMedium()));
                variableUpdate = true;
                break;
            case PacketHandler.BUTTON_CLICK:
                int buttonId = packet.readByte();
                if (buttonId >= 0 && buttonId < buttons.size())
                {
                    IManagerButton button = buttons.get(buttonId);
                    result = button.readData(packet);
                }
                break;
            case PacketHandler.SPECIAL_DATA:
                if (worldObj.isRemote && specialRenderer instanceof IPacketReader)
                {
                    ((IPacketReader)specialRenderer).readData(packet);
                }
                break;
        }
        if (!this.worldObj.isRemote) this.markDirty();
        return result;
    }

    public void updateInventories()
    {
        usingUnlimitedInventories = false;
        SystemCoord[] oldCoordinates = network.values(new SystemCoord[network.size()]);

        List<SystemCoord> visited = new ArrayList<SystemCoord>();
        network.clear();
        Queue<SystemCoord> queue = new PriorityQueue<SystemCoord>();
        SystemCoord start = new SystemCoord(xCoord, yCoord, zCoord, worldObj, 0, this);
        queue.add(start);
        visited.add(start);
        addInventory(start);

        while (!queue.isEmpty())
        {
            SystemCoord element = queue.poll();

            for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS)
            {
                SystemCoord target = new SystemCoord(element, direction);

                if (!visited.contains(target) && (Settings.isLimitless(this) || network.size() < MAX_CONNECTED_INVENTORIES))
                {
                    visited.add(target);

                    if ((Settings.isLimitless(this) || element.getDepth() < MAX_CABLE_LENGTH) && BlockRegistry.cable.isCable(target.getBlock(), target.getMetadata()))
                    {
                        queue.add(target);
                    }

                    TileEntity te = target.getWorldTE();
                    if (te == null) continue;
                    if (te instanceof TileEntityCluster)
                    {
                        for (IClusterTile tileEntityClusterElement : ((TileEntityCluster)te).getTiles())
                        {
                            ((TileEntityCluster)te).setWorld(tileEntityClusterElement);
                            SystemCoord coord = target.copy();
                            coord.setTileEntity((TileEntity)tileEntityClusterElement);
                            addInventory(coord);
                        }
                    } else
                    {
                        target.setTileEntity(te);
                        addInventory(target);
                    }
                }
            }

        }

        if (!firstInventoryUpdate)
        {
            for (SystemCoord oldCoordinate : oldCoordinates)
            {
                if (oldCoordinate.getTileEntity() instanceof ISystemListener)
                {
                    if (!network.containsKey(oldCoordinate.getKey()))
                    {
                        ((ISystemListener)oldCoordinate.getTileEntity()).removed(this);
                    }
                }
            }
        }
        firstInventoryUpdate = false;
    }

    public List<FlowComponent> getZLevelRenderingList()
    {
        return zLevelRenderingList;
    }

    public boolean addNewComponent(FlowComponent component)
    {
        boolean result = components.put(component.getId(), component) != null;
        if (component.getType().getCommandType() == CommandType.TRIGGER) triggers.add(component);
        if (worldObj != null && worldObj.isRemote) zLevelRenderingList.add(0, component);
        return result;
    }

    private void addVariable(Variable variable)
    {
        variables.put(variable.colour, variable);
    }

    private boolean findNewSelectedComponent(int id)
    {
        if (components.containsKey(id))
        {
            selectedGroup = components.get(id);
            return true;
        }
        return false;
    }

    public boolean isUsingUnlimitedStuff()
    {
        return components.size() > MAX_COMPONENT_AMOUNT || usingUnlimitedInventories;
    }

    public void addNewVariable(Variable variable)
    {
        variables.put(variable.colour, variable);
    }

    private void addInventory(SystemCoord target)
    {
        boolean isValidConnection = false;

        target.resetTypes();
        for (ISystemType connectionBlockType : SystemTypeRegistry.getTypes())
        {
            if (connectionBlockType.isInstance(this, target.getTileEntity()))
            {
                if (target.getTileEntity() instanceof ISystemTypeFilter && !((ISystemTypeFilter)target.getTileEntity()).isOfType(connectionBlockType))
                    continue;
                isValidConnection = true;
                target.addType(connectionBlockType);
            }
        }

        if (isValidConnection)
        {
            if (target.getDepth() >= MAX_CABLE_LENGTH || network.size() >= MAX_CONNECTED_INVENTORIES)
            {
                usingUnlimitedInventories = true;
            }
            network.put(target.getKey(), target);
            if (target.getTileEntity() instanceof ISystemListener)
            {
                ((ISystemListener)target.getTileEntity()).added(this);
            }
        }
    }

    public void removeVariableDeclaration(int colour, FlowComponent component)
    {
        Variable variable = variables.get(colour);
        if (variable != null && variable.getDeclaration() == component)
            variable.setDeclaration(null);
    }

    public void updateDeclaration(FlowComponent component, int colour)
    {
        if (variables.containsKey(colour))
        {
            Variable variable = variables.get(colour);
            if (!variable.isValid())
                variable.setDeclaration(component);
        }
    }

    public int getNextFreeID()
    {
        while (components.containsKey(++maxID) || maxID < 0) if (maxID < 0) maxID = 0;
        return maxID;
    }

    public void triggerBUD(TileEntityBUD tileEntityBUD)
    {
        for (FlowComponent item : triggers)
        {
            if (item.getConnectionSet() == ConnectionSet.BUD)
            {
                budTrigger.triggerBUD(item, tileEntityBUD);
            }
        }
    }

    public FlowComponent getSelectedGroup()
    {
        return selectedGroup;
    }

    public void setSelectedGroup(FlowComponent selectedGroup)
    {
        this.selectedGroup = selectedGroup;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound)
    {
        super.readFromNBT(nbtTagCompound);
        readContentFromNBT(nbtTagCompound, false);
        byte[] sides = nbtTagCompound.getByteArray(NBT_SIDES);
        int[] powered = new int[ForgeDirection.VALID_DIRECTIONS.length];
        for (int i = 0; i < sides.length; i++)
        {
            powered[i] = sides[i];

        }
        oldPowered = isPowered = powered;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound)
    {
        super.writeToNBT(nbtTagCompound);
        writeContentToNBT(nbtTagCompound, false);
        byte[] sides = new byte[isPowered.length];
        for (int i = 0; i < sides.length; i++)
        {
            sides[i] = (byte)isPowered[i];
        }
        nbtTagCompound.setByteArray(NBT_SIDES, sides);
    }

    @Override
    public void updateEntity()
    {
        serverPacket = false;
        if (!worldObj.isRemote)
        {
            quickTickTriggers();
            if (timer++ % 20 == triggerOffset)
            {
                for (FlowComponent item : triggers)
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
        }
    }

    public void quickTickTriggers()
    {
        for (Iterator<FlowComponent> itr = quickTriggers.iterator(); itr.hasNext(); )
        {
            MenuTriggered toTrigger = (MenuTriggered)itr.next().getMenus().get(6);
            if (toTrigger.isVisible())
            {
                toTrigger.tick();
                if (toTrigger.remove()) itr.remove();
            } else
            {
                itr.remove();
            }
        }
    }

    public void addQuickTrigger(FlowComponent component)
    {
        quickTriggers.add(component);
    }

    public void writeContentToNBT(NBTTagCompound nbtTagCompound, boolean pickup)
    {
        nbtTagCompound.setByte(NBT_TIMER, (byte)(timer % 20));
        nbtTagCompound.setInteger(NBT_MAX_ID, maxID);
        NBTTagList components = new NBTTagList();
        for (FlowComponent item : getFlowItems())
        {
            NBTTagCompound component = new NBTTagCompound();
            item.writeToNBT(component, pickup);
            components.appendTag(component);
        }
        nbtTagCompound.setTag(NBT_COMPONENTS, components);
        NBTTagList variablesTag = new NBTTagList();
        for (Variable variable : variables.valueCollection())
        {
            NBTTagCompound variableTag = new NBTTagCompound();
            variable.writeToNBT(variableTag);
            variablesTag.appendTag(variableTag);
        }
        nbtTagCompound.setTag(NBT_VARIABLES, variablesTag);
    }

    public void readContentFromNBT(NBTTagCompound nbtTagCompound, boolean pickup)
    {
        timer = nbtTagCompound.getByte(NBT_TIMER);
        maxID = nbtTagCompound.getInteger(NBT_MAX_ID);
        NBTTagList components = nbtTagCompound.getTagList(NBT_COMPONENTS, 10);
        for (int i = 0; i < components.tagCount(); i++)
        {
            NBTTagCompound component = components.getCompoundTagAt(i);
            FlowComponent flowComponent = FlowComponent.readFromNBT(this, component, pickup);
            if (flowComponent != null)
                addNewComponent(flowComponent);
        }

        for (FlowComponent item : getFlowItems())
        {
            item.linkAfterLoad();
        }

        NBTTagList variablesTag = nbtTagCompound.getTagList(NBT_VARIABLES, 10);
        for (int i = 0; i < variablesTag.tagCount(); i++)
        {
            NBTTagCompound variableTag = variablesTag.getCompoundTagAt(i);
            addVariable(new Variable(variableTag));
        }
    }

    public int getNextColour(int colour, int direction)
    {
        List<Variable> variables = new ArrayList<Variable>(getVariables());
        Collections.sort(variables);
        for (int i = 0; i < variables.size(); i++)
        {
            if (variables.get(i).colour == colour)
            {
                int returnVar = (i + direction) % variables.size();
                if (returnVar < 0) returnVar += variables.size();
                return variables.get(returnVar).colour;
            }
        }
        return variables.get(0).colour;
    }

    public Collection<Variable> getVariables()
    {
        return variables.valueCollection();
    }

    public String getUniqueComponentName(FlowComponent component)
    {
        String name = getComponentName(component);
        int modifier = 0;
        for (int i : components.keys())
        {
            FlowComponent other = components.get(i);
            if (getComponentName(other).equals(name)) modifier++;
        }
        if (modifier > 0) name += " [" + modifier + "]";
        return name;
    }

    public String getComponentName(FlowComponent component)
    {
        String name = component.getComponentName();
        if (name == null) name = component.getType().getName();
        return name;
    }

    public boolean hasInventory(long key)
    {
        return network.containsKey(key) && network.get(key).isValid();
    }

    public SystemCoord getInventory(long selected)
    {
        return network.get(selected);
    }

    @Override
    public boolean writeData(ASMPacket packet)
    {
        packet.writeByte(PacketHandler.SYNC_ALL);
        packet.writeVarIntToBuffer(components.size());
        for (FlowComponent flowComponent : components.valueCollection())
        {
            NBTTagCompound tagCompound = new NBTTagCompound();
            flowComponent.writeToNBT(tagCompound, false);
            packet.writeNBTTagCompoundToBuffer(tagCompound);
        }
        packet.writeVarIntToBuffer(variables.size());
        for (Variable variable : variables.valueCollection())
        {
            packet.writeMedium(variable.colour);
            packet.writeStringToBuffer(variable.getNameFromColor());
        }
        return true;
    }

    public Variable getVariable(int selectedVariable)
    {
        return variables.get(selectedVariable);
    }

    public void triggerRedstone()
    {
        isPowered = new int[isPowered.length];
        for (int i = 0; i < isPowered.length; i++)
        {
            ForgeDirection direction = ForgeDirection.VALID_DIRECTIONS[i];
            isPowered[i] = worldObj.getIndirectPowerLevelTo(direction.offsetX + this.xCoord, direction.offsetY + this.yCoord, direction.offsetZ + this.zCoord, i);
        }
        triggerRedstone(this);

        oldPowered = isPowered;
    }

    public void triggerRedstone(ITriggerNode inputTrigger)
    {
        for (FlowComponent item : triggers)
        {
            if (item.getConnectionSet() == ConnectionSet.REDSTONE)
            {
                redstoneTrigger.onRedstoneTrigger(item, inputTrigger);
            }
        }
    }

    @Override
    public int[] getData()
    {
        return isPowered;
    }

    @Override
    public int[] getOldData()
    {
        return oldPowered;
    }

    @Override
    @Optional.Method(modid = Mods.COFH_ENERGY)
    public int receiveEnergy(ForgeDirection forgeDirection, int amount, boolean simulate)
    {
        return 0;
    }

    @Override
    @Optional.Method(modid = Mods.COFH_ENERGY)
    public int getEnergyStored(ForgeDirection forgeDirection)
    {
        return 0;
    }

    @Override
    @Optional.Method(modid = Mods.COFH_ENERGY)
    public int getMaxEnergyStored(ForgeDirection forgeDirection)
    {
        return 0;
    }

    @Override
    @Optional.Method(modid = Mods.COFH_ENERGY)
    public boolean canConnectEnergy(ForgeDirection forgeDirection)
    {
        return requiresPower();
    }

    public boolean requiresPower()
    {
        return energyCostActive && !Settings.isLimitless(this);
    }

    @Override
    public boolean isOfType(ISystemType type)
    {
        return type != RFCompat.RF_RECEIVER || requiresPower();
    }
}
