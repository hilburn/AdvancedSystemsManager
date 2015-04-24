package advancedsystemsmanager.flow.execution;

import advancedsystemsmanager.api.*;
import advancedsystemsmanager.api.execution.IHiddenInventory;
import advancedsystemsmanager.api.execution.IHiddenTank;
import advancedsystemsmanager.api.execution.IItemBufferElement;
import advancedsystemsmanager.api.execution.IItemBufferSubElement;
import advancedsystemsmanager.flow.Connection;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.elements.*;
import advancedsystemsmanager.flow.menus.*;
import advancedsystemsmanager.flow.menus.MenuListOrder.LoopOrder;
import advancedsystemsmanager.flow.menus.MenuVariable.VariableMode;
import advancedsystemsmanager.flow.setting.ItemSetting;
import advancedsystemsmanager.flow.setting.LiquidSetting;
import advancedsystemsmanager.flow.setting.Setting;
import advancedsystemsmanager.helpers.StevesEnum;
import advancedsystemsmanager.reference.Null;
import advancedsystemsmanager.registry.ConnectionOption;
import advancedsystemsmanager.tileentities.TileEntityCreative;
import advancedsystemsmanager.tileentities.manager.TileEntityManager;
import advancedsystemsmanager.tileentities.TileEntityRFNode;
import advancedsystemsmanager.util.ConnectionBlock;
import advancedsystemsmanager.util.ConnectionBlockType;
import cofh.api.energy.IEnergyConnection;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import powercrystals.minefactoryreloaded.api.IDeepStorageUnit;

import java.util.*;

public class CommandExecutor
{
    public TileEntityManager manager;
    public List<RFBufferElement> rfBuffer;
    public List<ItemBufferElement> itemBuffer;
    public List<LiquidBufferElement> liquidBuffer;
    public List<CraftingBufferFluidElement> craftingBufferHigh;
    public List<CraftingBufferFluidElement> craftingBufferLow;
    public List<Integer> usedCommands;
    public static final int MAX_FLUID_TRANSFER = 10000000;

    public CommandExecutor(TileEntityManager manager)
    {
        this.manager = manager;
        this.itemBuffer = new ArrayList<ItemBufferElement>();
        this.craftingBufferHigh = new ArrayList<CraftingBufferFluidElement>();
        this.craftingBufferLow = new ArrayList<CraftingBufferFluidElement>();
        this.rfBuffer = new ArrayList<RFBufferElement>();
        this.liquidBuffer = new ArrayList<LiquidBufferElement>();
        this.usedCommands = new ArrayList<Integer>();
    }

    public CommandExecutor(TileEntityManager manager, List<ItemBufferElement> itemBufferSplit, List<CraftingBufferFluidElement> craftingBufferHighSplit, List<CraftingBufferFluidElement> craftingBufferLowSplit, List<LiquidBufferElement> liquidBufferSplit, List<RFBufferElement> rfBuffer, List<Integer> usedCommandCopy)
    {
        this.manager = manager;
        this.itemBuffer = itemBufferSplit;
        this.craftingBufferHigh = craftingBufferHighSplit;
        this.craftingBufferLow = craftingBufferLowSplit;
        this.usedCommands = usedCommandCopy;
        this.rfBuffer = rfBuffer;
        this.liquidBuffer = liquidBufferSplit;
    }

    public void executeTriggerCommand(FlowComponent command, EnumSet<ConnectionOption> validTriggerOutputs)
    {
        for (Variable variable : this.manager.getVariables())
        {
            if (variable.isValid() && (!variable.hasBeenExecuted() || ((MenuVariable)variable.getDeclaration().getMenus().get(0)).getVariableMode() == VariableMode.LOCAL))
            {
                this.executeCommand(variable.getDeclaration(), 0);
                variable.setExecuted(true);
            }
        }
        this.executeChildCommands(command, validTriggerOutputs);
    }

    public void executeChildCommands(FlowComponent command, EnumSet<ConnectionOption> validTriggerOutputs)
    {
        for (int i = 0; i < command.getConnectionSet().getConnections().length; ++i)
        {
            Connection connection = command.getConnection(i);
            ConnectionOption option = command.getConnectionSet().getConnections()[i];
            if (connection != null && !option.isInput() && validTriggerOutputs.contains(option))
            {
                this.executeCommand(this.manager.getFlowItems().get(connection.getComponentId()), connection.getConnectionId());
            }
        }
    }

    public void executeCommand(FlowComponent command, int connectionId)
    {
        if (!this.usedCommands.contains(Integer.valueOf(command.getId())))
        {
            try
            {
                this.usedCommands.add(command.getId());
                switch (command.getType().getId())
                {
                    case 0:
                        MenuTriggered trigger = (MenuTriggered)command.getMenus().get(6);
                        trigger.setCountdown();
                        return;
                    case 1:
                        List<SlotInventoryHolder> inputInventory = this.getInventories(command.getMenus().get(0));
                        if (inputInventory != null)
                        {
                            this.getValidSlots(command.getMenus().get(1), inputInventory);
                            this.getItems(command.getMenus().get(2), inputInventory);
                        }
                        break;
                    case 2:
                        List<SlotInventoryHolder> outputInventory = this.getInventories(command.getMenus().get(0));
                        if (outputInventory != null)
                        {
                            this.getValidSlots(command.getMenus().get(1), outputInventory);
                            this.insertItems(command.getMenus().get(2), outputInventory);
                        }
                        break;
                    case 3:
                        List<SlotInventoryHolder> conditionInventory = this.getInventories(command.getMenus().get(0));
                        if (conditionInventory != null)
                        {
                            this.getValidSlots(command.getMenus().get(1), conditionInventory);
                            if (this.searchForStuff(command.getMenus().get(2), conditionInventory, false))
                            {
                                this.executeChildCommands(command, EnumSet.of(ConnectionOption.CONDITION_TRUE));
                            } else
                            {
                                this.executeChildCommands(command, EnumSet.of(ConnectionOption.CONDITION_FALSE));
                            }

                            return;
                        }

                        return;
                    case 4:
                        if (MenuSplit.isSplitConnection(command) && this.splitFlow(command.getMenus().get(0)))
                        {
                            return;
                        }
                        break;
                    case 5:
                        List<SlotInventoryHolder> inputTank = this.getTanks(command.getMenus().get(0));
                        if (inputTank != null)
                        {
                            this.getValidTanks(command.getMenus().get(1), inputTank);
                            this.getLiquids(command.getMenus().get(2), inputTank);
                        }
                        break;
                    case 6:
                        List<SlotInventoryHolder> outputTank = this.getTanks(command.getMenus().get(0));
                        if (outputTank != null)
                        {
                            this.getValidTanks(command.getMenus().get(1), outputTank);
                            this.insertLiquids(command.getMenus().get(2), outputTank);
                        }
                        break;
                    case 7:
                        List<SlotInventoryHolder> conditionTank = this.getTanks(command.getMenus().get(0));
                        if (conditionTank != null)
                        {
                            this.getValidTanks(command.getMenus().get(1), conditionTank);
                            if (this.searchForStuff(command.getMenus().get(2), conditionTank, true))
                            {
                                this.executeChildCommands(command, EnumSet.of(ConnectionOption.CONDITION_TRUE));
                            } else
                            {
                                this.executeChildCommands(command, EnumSet.of(ConnectionOption.CONDITION_FALSE));
                            }

                            return;
                        }

                        return;
                    case 8:
                        List<SlotInventoryHolder> emitters = this.getEmitters(command.getMenus().get(0));
                        if (emitters != null)
                        {
                            for (SlotInventoryHolder emitter : emitters)
                            {
                                emitter.getEmitter().updateState((MenuRedstoneSidesEmitter)command.getMenus().get(1), (MenuRedstoneOutput)command.getMenus().get(2), (MenuPulse)command.getMenus().get(3));
                            }
                        }
                        break;
                    case 9:
                        List<SlotInventoryHolder> nodes = this.getNodes(command.getMenus().get(0));
                        if (nodes != null)
                        {
                            if (this.evaluateRedstoneCondition(nodes, command))
                            {
                                this.executeChildCommands(command, EnumSet.of(ConnectionOption.CONDITION_TRUE));
                            } else
                            {
                                this.executeChildCommands(command, EnumSet.of(ConnectionOption.CONDITION_FALSE));
                            }

                            return;
                        }

                        return;
                    case 10:
                        List<SlotInventoryHolder> tiles = this.getTiles(command.getMenus().get(2));
                        if (tiles != null)
                        {
                            this.updateVariable(tiles, (MenuVariable)command.getMenus().get(0), (MenuListOrder)command.getMenus().get(3));
                        }
                        break;
                    case 11:
                        this.updateForLoop(command, (MenuVariableLoop)command.getMenus().get(0), (MenuContainerTypes)command.getMenus().get(1), (MenuListOrder)command.getMenus().get(2));
                        this.executeChildCommands(command, EnumSet.of(ConnectionOption.STANDARD_OUTPUT));
                        return;
                    case 12:
                        CraftingBufferFluidElement element = new CraftingBufferFluidElement(this, (MenuCrafting)command.getMenus().get(0), (MenuContainerScrap)command.getMenus().get(2));
                        if (((MenuCraftingPriority)command.getMenus().get(1)).shouldPrioritizeCrafting())
                        {
                            this.craftingBufferHigh.add(element);
                        } else
                        {
                            this.craftingBufferLow.add(element);
                        }
                        break;
                    case 13:
                        if (connectionId < command.getChildrenInputNodes().size())
                        {
                            this.executeChildCommands(command.getChildrenInputNodes().get(connectionId), EnumSet.allOf(ConnectionOption.class));
                        }
                        return;
                    case 14:
                        FlowComponent parent = command.getParent();
                        if (parent != null)
                        {
                            for (int var28 = 0; var28 < parent.getChildrenOutputNodes().size(); ++var28)
                            {
                                if (command.equals(parent.getChildrenOutputNodes().get(var28)))
                                {
                                    Connection var30 = parent.getConnection(parent.getConnectionSet().getInputCount() + var28);
                                    if (var30 != null)
                                    {
                                        this.executeCommand(this.manager.getFlowItems().get(var30.getComponentId()), var30.getConnectionId());
                                    }

                                    return;
                                }
                            }
                        }
                        return;
                    case 15:
                        List<SlotInventoryHolder> camouflage = this.getCamouflage(command.getMenus().get(0));
                        if (camouflage != null)
                        {
                            MenuCamouflageShape var29 = (MenuCamouflageShape)command.getMenus().get(1);
                            MenuCamouflageInside var31 = (MenuCamouflageInside)command.getMenus().get(2);
                            MenuCamouflageSides var32 = (MenuCamouflageSides)command.getMenus().get(3);
                            MenuCamouflageItems items = (MenuCamouflageItems)command.getMenus().get(4);
                            if (items.isFirstRadioButtonSelected() || items.getSettings().get(0).isValid())
                            {
                                ItemStack itemStack = items.isFirstRadioButtonSelected() ? null : ((ItemSetting)items.getSettings().get(0)).getItem();

                                for (SlotInventoryHolder slotInventoryHolder : camouflage)
                                {
                                    slotInventoryHolder.getCamouflage().setBounds(var29);

                                    for (int i = 0; i < ForgeDirection.VALID_DIRECTIONS.length; ++i)
                                    {
                                        if (var32.isSideRequired(i))
                                        {
                                            slotInventoryHolder.getCamouflage().setItem(itemStack, i, var31.getCurrentType());
                                        }
                                    }
                                }
                            }
                        }
                        break;
                    case 16:
                        List<SlotInventoryHolder> sign = this.getSign(command.getMenus().get(0));
                        if (sign != null)
                        {
                            for (SlotInventoryHolder slotInventoryHolder : sign)
                            {
                                slotInventoryHolder.getSign().updateSign((MenuSignText)command.getMenus().get(1));
                            }
                        }
                        break;
                    case 17:
                        List<SlotInventoryHolder> inputStorage = this.getRFInput(command.getMenus().get(0));
                        if (inputStorage != null)
                        {
                            this.getInputRF(command.getMenus().get(1), inputStorage);
                        }
                        break;
                    case 18:
                        List<SlotInventoryHolder> outputStorage = this.getRFOutput(command.getMenus().get(0));
                        if (outputStorage != null)
                        {
                            this.insertRF(command.getMenus().get(1), outputStorage);
                        }
                        break;
                    case 19:
                        List<SlotInventoryHolder> conditionStorage = this.getRFStorage(command.getMenus().get(0));
                        if (conditionStorage != null)
                        {
                            this.getValidRFStorage(command.getMenus().get(1), conditionStorage);
                            if (this.searchForPower((MenuRFCondition)command.getMenus().get(2), conditionStorage))
                            {
                                this.executeChildCommands(command, EnumSet.of(ConnectionOption.CONDITION_TRUE));
                            } else
                            {
                                this.executeChildCommands(command, EnumSet.of(ConnectionOption.CONDITION_FALSE));
                            }
                            return;
                        }
                }

                this.executeChildCommands(command, EnumSet.allOf(ConnectionOption.class));
            } finally
            {
                this.usedCommands.remove(Integer.valueOf(command.getId()));
            }
        }
    }

    public List<SlotInventoryHolder> getEmitters(Menu menu)
    {
        return getContainers(this.manager, menu, ConnectionBlockType.EMITTER);
    }

    public List<SlotInventoryHolder> getInventories(Menu menu)
    {
        return getContainers(this.manager, menu, ConnectionBlockType.INVENTORY);
    }

    public List<SlotInventoryHolder> getTanks(Menu menu)
    {
        return getContainers(this.manager, menu, ConnectionBlockType.TANK);
    }

    public List<SlotInventoryHolder> getRFInput(Menu menu)
    {
        return getContainers(this.manager, menu, StevesEnum.RF_PROVIDER);
    }

    public List<SlotInventoryHolder> getRFOutput(Menu menu)
    {
        return getContainers(this.manager, menu, StevesEnum.RF_RECEIVER);
    }

    public List<SlotInventoryHolder> getRFStorage(Menu menu)
    {
        return getContainers(this.manager, menu, StevesEnum.RF_CONNECTION);
    }

    public List<SlotInventoryHolder> getNodes(Menu menu)
    {
        return getContainers(this.manager, menu, ConnectionBlockType.NODE);
    }

    public List<SlotInventoryHolder> getCamouflage(Menu menu)
    {
        return getContainers(this.manager, menu, ConnectionBlockType.CAMOUFLAGE);
    }

    public List<SlotInventoryHolder> getSign(Menu menu)
    {
        return getContainers(this.manager, menu, ConnectionBlockType.SIGN);
    }

    public List<SlotInventoryHolder> getTiles(Menu menu)
    {
        return getContainers(this.manager, menu, null);
    }

    public static List<SlotInventoryHolder> getContainers(TileEntityManager manager, Menu menu, ConnectionBlockType type)
    {
        if (!(menu instanceof MenuContainer)) return null;
        MenuContainer menuContainer = (MenuContainer)menu;
        if (menuContainer.getSelectedInventories().size() == 0)
        {
            return null;
        } else
        {
            ArrayList<SlotInventoryHolder> ret = new ArrayList<SlotInventoryHolder>();
            List<ConnectionBlock> inventories = manager.getConnectedInventories();
            Variable[] variables = manager.getVariables();

            int i;
            label50:
            for (i = 0; i < variables.length; ++i)
            {
                Variable selected = variables[i];
                if (selected.isValid())
                {

                    for (int val : menuContainer.getSelectedInventories())
                    {
                        if (val == i)
                        {
                            List<Integer> selection = selected.getContainers();
                            Iterator<Integer> i$1 = selection.iterator();

                            while (true)
                            {
                                if (!i$1.hasNext())
                                {
                                    continue label50;
                                }

                                int selected1 = i$1.next();
                                addContainer(inventories, ret, selected1, menuContainer, type, ((MenuContainerTypes)selected.getDeclaration().getMenus().get(1)).getValidTypes());
                            }
                        }
                    }
                }
            }

            for (i = 0; i < menuContainer.getSelectedInventories().size(); ++i)
            {
                int var14 = menuContainer.getSelectedInventories().get(i) - VariableColor.values().length;
                addContainer(inventories, ret, var14, menuContainer, type, EnumSet.allOf(ConnectionBlockType.class));
            }

            if (ret.isEmpty())
            {
                return null;
            } else
            {
                return ret;
            }
        }
    }

    public static void addContainer(List<ConnectionBlock> inventories, List<SlotInventoryHolder> ret, int selected, MenuContainer menuContainer, ConnectionBlockType requestType, EnumSet<ConnectionBlockType> variableType)
    {
        if (selected >= 0 && selected < inventories.size())
        {
            ConnectionBlock connection = inventories.get(selected);
            if (connection.isOfType(requestType) && connection.isOfAnyType(variableType) && !connection.getTileEntity().isInvalid() && !containsTe(ret, connection.getTileEntity()))
            {
                ret.add(new SlotInventoryHolder(selected, connection.getTileEntity(), menuContainer.getOption()));
            }
        }
    }

    public static boolean containsTe(List<SlotInventoryHolder> lst, TileEntity te)
    {
        Iterator<SlotInventoryHolder> i$ = lst.iterator();

        SlotInventoryHolder slotInventoryHolder;
        do
        {
            if (!i$.hasNext())
            {
                return false;
            }

            slotInventoryHolder = i$.next();
        }
        while (slotInventoryHolder.getTile().xCoord != te.xCoord || slotInventoryHolder.getTile().yCoord != te.yCoord || slotInventoryHolder.getTile().zCoord != te.zCoord || !slotInventoryHolder.getTile().getClass().equals(te.getClass()));

        return true;
    }

    public static void getValidSlots(Menu menu, List<SlotInventoryHolder> inventories)
    {
        MenuTargetInventory menuTarget = (MenuTargetInventory)menu;

        for (SlotInventoryHolder slotInventoryHolder : inventories)
        {
            if (!(slotInventoryHolder.getTile() instanceof IHiddenInventory))
            {
                IInventory inventory = slotInventoryHolder.getInventory();
                Map<Integer, SlotSideTarget> validSlots = slotInventoryHolder.getValidSlots();

                for (int side = 0; side < MenuTarget.directions.length; ++side)
                {
                    if (menuTarget.isActive(side))
                    {
                        int[] inventoryValidSlots;
                        int start;
                        if (inventory instanceof ISidedInventory)
                        {
                            inventoryValidSlots = ((ISidedInventory)inventory).getAccessibleSlotsFromSide(side);
                        } else
                        {
                            inventoryValidSlots = new int[inventory.getSizeInventory()];

                            for (start = 0; start < inventoryValidSlots.length; inventoryValidSlots[start] = start++) ;
                        }

                        int end;
                        if (menuTarget.useAdvancedSetting(side))
                        {
                            start = menuTarget.getStart(side);
                            end = menuTarget.getEnd(side);
                        } else
                        {
                            start = 0;
                            end = inventory.getSizeInventory();
                        }

                        if (start <= end)
                        {
                            for (int inventoryValidSlot : inventoryValidSlots)
                            {
                                if (inventoryValidSlot >= start && inventoryValidSlot <= end)
                                {
                                    SlotSideTarget target = validSlots.get(inventoryValidSlot);
                                    if (target == null)
                                    {
                                        validSlots.put(inventoryValidSlot, new SlotSideTarget(inventoryValidSlot, side));
                                    } else
                                    {
                                        target.addSide(side);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void getValidRFStorage(Menu menu, List<SlotInventoryHolder> cells)
    {
        MenuTargetRF menuTarget = (MenuTargetRF)menu;
        List<SlotInventoryHolder> result = new ArrayList<SlotInventoryHolder>();
        for (SlotInventoryHolder cell1 : cells)
        {
            IEnergyConnection cell = (IEnergyConnection)cell1.getTile();
            if (cell == null || cell1.getTile() instanceof TileEntityRFNode) continue;
            if (cell instanceof IEnergyReceiver || cell instanceof IEnergyProvider)
            {
                for (int side = 0; side < MenuTarget.directions.length; ++side)
                {
                    if (menuTarget.isActive(side))
                    {
                        if (cell.canConnectEnergy(ForgeDirection.getOrientation(side)))
                        {
                            result.add(cell1);
                            break;
                        }
                    }
                }
            }
        }
        cells = result;
    }

    public boolean searchForPower(MenuRFCondition componentMenu, List<SlotInventoryHolder> cells)
    {
        int total = 0;
        for (SlotInventoryHolder cell1 : cells)
        {
            IEnergyConnection cell = (IEnergyConnection)cell1.getTile();
            if (cell instanceof IEnergyReceiver || cell instanceof IEnergyProvider)
            {
                for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
                {
                    int stored;
                    if (cell instanceof IEnergyReceiver) stored = ((IEnergyReceiver)cell).getEnergyStored(dir);
                    else stored = ((IEnergyProvider)cell).getEnergyStored(dir);
                    if (stored > 0)
                    {
                        total += stored;
                        break;
                    }
                }
            }
        }
        return (total < componentMenu.getAmount()) == componentMenu.isLessThan();
    }

    public void getInputRF(Menu menu, List<SlotInventoryHolder> inputStorage)
    {
        MenuTargetRF menuTarget = (MenuTargetRF)menu;
        List<Integer> validSides = getValidSides(menuTarget);
        for (SlotInventoryHolder anInputStorage : inputStorage)
        {
            IEnergyProvider cell = (IEnergyProvider)anInputStorage.getTile();
            if (cell == null) continue;
//            if (cell instanceof TileEntityRFNode)
//                ((TileEntityRFNode)cell).setInputSides(validSides.toArray(new Integer[validSides.size()]));
            for (int side : validSides)
            {
                ForgeDirection dir = ForgeDirection.getOrientation(side);
                int extractEnergy = cell.extractEnergy(dir, Integer.MAX_VALUE, true);
                if (extractEnergy > 0)
                {
                    rfBuffer.add(new RFBufferElement(menu.getParent(), anInputStorage, new EnergyFacingHolder(cell, dir)));
                    break;
                }
            }
        }
    }

    public List<Integer> getValidSides(MenuTargetRF menuTarget)
    {
        List<Integer> validDirections = new ArrayList<Integer>();
        for (int side = 0; side < MenuTarget.directions.length; ++side)
        {
            if (menuTarget.isActive(side))
            {
                validDirections.add(side);
            }
        }
        return validDirections;
    }

    public void insertRF(Menu menu, List<SlotInventoryHolder> outputStorage)
    {
        MenuTargetRF menuTarget = (MenuTargetRF)menu;
        long bufferSize = 0;
        for (RFBufferElement rfElement : rfBuffer)
            bufferSize += rfElement.getMaxExtract();
        List<Integer> validSides = getValidSides(menuTarget);
        List<IEnergyReceiver> validOutputs = new ArrayList<IEnergyReceiver>();
        for (SlotInventoryHolder holder : outputStorage)
        {
            IEnergyReceiver cell = (IEnergyReceiver)holder.getTile();
            if (cell == null) continue;
//            if (cell instanceof TileEntityRFNode)
//                ((TileEntityRFNode)cell).setOutputSides(validSides.toArray(new Integer[validSides.size()]));
            for (int side : validSides)
            {
                int maxReceive = cell.receiveEnergy(ForgeDirection.getOrientation(side), Integer.MAX_VALUE, true);
                if (maxReceive > 0)
                {
                    validOutputs.add(cell);
                    break;
                }
            }
        }
        insertRF(validSides.toArray(new Integer[validSides.size()]), validOutputs, bufferSize);
    }

    public void insertRF(Integer[] directions, List<IEnergyReceiver> validOutputs, long bufferSize)
    {
        for (Iterator<IEnergyReceiver> itr = validOutputs.iterator(); itr.hasNext(); )
        {
            IEnergyReceiver cell = itr.next();
            int maxReceive = 0;
            for (int side : directions)
            {
                maxReceive = cell.receiveEnergy(ForgeDirection.getOrientation(side), Integer.MAX_VALUE, true);
                if (maxReceive > 0)
                {
                    break;
                }
            }
            if (maxReceive == 0) itr.remove();
        }
        int inserted = validOutputs.size();
        for (Iterator<IEnergyReceiver> itr = validOutputs.iterator(); itr.hasNext() && !rfBuffer.isEmpty(); inserted--)
        {
            IEnergyReceiver cell = itr.next();
            int maxReceive = (int)(bufferSize / inserted);
            for (int side : directions)
            {
                int insert = cell.receiveEnergy(ForgeDirection.getOrientation(side), maxReceive, false);
                if (insert > 0)
                {
                    if (insert < maxReceive) itr.remove();
                    removeRF(insert);
                    bufferSize -= insert;
                    break;
                }
            }
        }
        if (bufferSize > 0 && validOutputs.size() > 0 && !rfBuffer.isEmpty())
            insertRF(directions, validOutputs, bufferSize);
    }

    public void removeRF(int amount)
    {
        int remove = amount / rfBuffer.size();
        for (Iterator<RFBufferElement> itr = rfBuffer.iterator(); itr.hasNext(); )
        {
            int removed = itr.next().removeRF(remove);
            if (removed < remove) itr.remove();
            amount -= removed;
        }
        if (amount > 0 && remove > 0 && rfBuffer.size() > 0) removeRF(amount);
    }

    public boolean isSlotValid(IInventory inventory, ItemStack item, SlotSideTarget slot, boolean isInput)
    {
        if (item == null)
        {
            return false;
        } else
        {
            if (inventory instanceof ISidedInventory)
            {
                boolean hasValidSide = false;

                for (int side : slot.getSides())
                {
                    if (isInput && ((ISidedInventory)inventory).canExtractItem(slot.getSlot(), item, side))
                    {
                        hasValidSide = true;
                        break;
                    }

                    if (!isInput && ((ISidedInventory)inventory).canInsertItem(slot.getSlot(), item, side))
                    {
                        hasValidSide = true;
                        break;
                    }
                }

                if (!hasValidSide)
                {
                    return false;
                }
            }
            return isInput || inventory.isItemValidForSlot(slot.getSlot(), item);
        }
    }

    public void getItems(Menu menu, List<SlotInventoryHolder> inventories)
    {
        MenuStuff menuItem = (MenuStuff)menu;
        for (SlotInventoryHolder inventory : inventories)
        {
            if (inventory.getTile() instanceof IHiddenInventory)
            {
                IHiddenInventory node = (IHiddenInventory)inventory.getTile();
                node.addItemsToBuffer(menuItem, inventory, itemBuffer, this);
            } else
            {
                Iterator<SlotSideTarget> itr;
                SlotSideTarget slot;
                Setting setting;
                if (inventory.getInventory() instanceof TileEntityCreative)
                {
                    if (menuItem.useWhiteList())
                    {
                        itr = inventory.getValidSlots().values().iterator();

                        while (itr.hasNext())
                        {
                            slot = itr.next();

                            for (Setting setting1 : menuItem.getSettings())
                            {
                                setting = setting1;
                                ItemStack item = ((ItemSetting)setting).getItem();
                                if (item != null)
                                {
                                    item = item.copy();
                                    item.stackSize = setting.isLimitedByAmount() ? setting.getAmount() : setting.getDefaultAmount();
                                    this.addItemToBuffer(menuItem, inventory, setting, item, slot);
                                }
                            }
                        }
                    }
                } else
                {
                    itr = inventory.getValidSlots().values().iterator();

                    while (itr.hasNext())
                    {
                        slot = itr.next();
                        ItemStack itemStack = inventory.getInventory().getStackInSlot(slot.getSlot());
                        if (this.isSlotValid(inventory.getInventory(), itemStack, slot, true))
                        {
                            setting = this.isItemValid(((MenuStuff)menu).getSettings(), itemStack);
                            this.addItemToBuffer(menuItem, inventory, setting, itemStack, slot);
                        }
                    }
                }
            }
        }
    }

    public void addItemToBuffer(MenuStuff menuItem, SlotInventoryHolder inventory, Setting setting, ItemStack itemStack, SlotSideTarget slot)
    {
        if (menuItem.useWhiteList() == (setting != null) || setting != null && setting.isLimitedByAmount())
        {
            FlowComponent owner = menuItem.getParent();
            SlotStackInventoryHolder target = new SlotStackInventoryHolder(itemStack, inventory.getInventory(), slot.getSlot());
            boolean added = false;

            for (ItemBufferElement itemBufferElement : this.itemBuffer)
            {
                if (itemBufferElement.addTarget(owner, setting, inventory, target))
                {
                    added = true;
                    break;
                }
            }

            if (!added)
            {
                this.itemBuffer.add(new ItemBufferElement(owner, setting, inventory, menuItem.useWhiteList(), target));
            }
        }
    }

    public void getValidTanks(Menu menu, List<SlotInventoryHolder> tanks)
    {
        MenuTargetTank menuTarget = (MenuTargetTank)menu;

        for (SlotInventoryHolder slotHolder : tanks)
        {
            Map<Integer, SlotSideTarget> validTanks = slotHolder.getValidSlots();
            if (slotHolder.getTile() instanceof IHiddenTank)
            {
                SlotSideTarget var13 = validTanks.get(0);
                if (var13 == null)
                {
                    validTanks.put(0, new SlotSideTarget(0, 0));
                } else
                {
                    var13.addSide(0);
                }
            } else
            {
                IFluidHandler tank = slotHolder.getTank();
                for (int side = 0; side < MenuTarget.directions.length; ++side)
                {
                    if (menuTarget.isActive(side))
                    {
                        if (menuTarget.useAdvancedSetting(side))
                        {
                            boolean target = true;

                            for (FluidTankInfo fluidTankInfo : tank.getTankInfo(MenuTarget.directions[side]))
                            {
                                if (fluidTankInfo.fluid != null && fluidTankInfo.fluid.amount > 0)
                                {
                                    target = false;
                                    break;
                                }
                            }

                            if (target != menuTarget.requireEmpty(side))
                            {
                                continue;
                            }
                        }

                        SlotSideTarget var13 = validTanks.get(0);
                        if (var13 == null)
                        {
                            validTanks.put(0, new SlotSideTarget(0, side));
                        } else
                        {
                            var13.addSide(side);
                        }
                    }
                }
            }
        }
    }

    public void getLiquids(Menu menu, List<SlotInventoryHolder> tanks)
    {
        for (SlotInventoryHolder tank : tanks)
        {
            MenuStuff menuItem = (MenuStuff)menu;
            if (tank.getTile() instanceof IHiddenTank)
            {
                IHiddenTank node = (IHiddenTank)tank.getTile();
                node.addFluidsToBuffer(menuItem, tank, liquidBuffer, this);
            } else if (tank.getTank() instanceof TileEntityCreative)
            {
                Iterator<SlotSideTarget> itr;
                if (menuItem.useWhiteList())
                {
                    itr = tank.getValidSlots().values().iterator();

                    while (itr.hasNext())
                    {
                        itr.next();

                        for (Setting setting : menuItem.getSettings())
                        {
                            Fluid fluid = ((LiquidSetting)setting).getFluid();
                            if (fluid != null)
                            {
                                FluidStack stack = new FluidStack(fluid, setting.isLimitedByAmount() ? setting.getAmount() : setting.getDefaultAmount());
                                this.addLiquidToBuffer(menuItem, tank, setting, stack, 0);
                            }
                        }
                    }
                }
            } else
            {
                for (SlotSideTarget slot : tank.getValidSlots().values())
                {
                    ArrayList<FluidTankInfo> tankInfos = new ArrayList<FluidTankInfo>();

                    for (Integer side : slot.getSides())
                    {
                        FluidTankInfo[] currentTankInfos = tank.getTank().getTankInfo(ForgeDirection.VALID_DIRECTIONS[side]);
                        if (currentTankInfos != null)
                        {
                            FluidTankInfo[] arr$ = currentTankInfos;
                            int len$ = currentTankInfos.length;

                            int i$3;
                            FluidTankInfo fluidTankInfo;
                            for (i$3 = 0; i$3 < len$; ++i$3)
                            {
                                fluidTankInfo = arr$[i$3];
                                if (fluidTankInfo != null)
                                {
                                    boolean alreadyUsed = false;

                                    for (FluidTankInfo setting : tankInfos)
                                    {
                                        if (FluidStack.areFluidStackTagsEqual(setting.fluid, fluidTankInfo.fluid) && setting.capacity == fluidTankInfo.capacity)
                                        {
                                            alreadyUsed = true;
                                        }
                                    }

                                    if (!alreadyUsed)
                                    {
                                        FluidStack var23 = fluidTankInfo.fluid;
                                        if (var23 != null)
                                        {
                                            var23 = var23.copy();
                                            Setting var24 = this.isLiquidValid(menu, var23);
                                            this.addLiquidToBuffer(menuItem, tank, var24, var23, side);
                                        }
                                    }
                                }
                            }

                            arr$ = tank.getTank().getTankInfo(ForgeDirection.VALID_DIRECTIONS[side]);
                            len$ = arr$.length;

                            for (i$3 = 0; i$3 < len$; ++i$3)
                            {
                                fluidTankInfo = arr$[i$3];
                                if (fluidTankInfo != null)
                                {
                                    tankInfos.add(fluidTankInfo);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void addLiquidToBuffer(MenuStuff menuItem, SlotInventoryHolder tank, Setting setting, FluidStack fluidStack, int side)
    {
        if (menuItem.useWhiteList() == (setting != null) || setting != null && setting.isLimitedByAmount())
        {
            FlowComponent owner = menuItem.getParent();
            StackTankHolder target = new StackTankHolder(fluidStack, tank.getTank(), ForgeDirection.VALID_DIRECTIONS[side]);
            boolean added = false;

            for (LiquidBufferElement liquidBufferElement : this.liquidBuffer)
            {
                if (liquidBufferElement.addTarget(owner, setting, tank, target))
                {
                    added = true;
                    break;
                }
            }

            if (!added)
            {
                LiquidBufferElement itemBufferElement1 = new LiquidBufferElement(owner, setting, tank, menuItem.useWhiteList(), target);
                this.liquidBuffer.add(itemBufferElement1);
            }
        }
    }

    public Setting isItemValid(List<Setting> settings, ItemStack itemStack)
    {
        Iterator<Setting> itr = settings.iterator();
        Setting setting;
        do
        {
            if (!itr.hasNext())
            {
                return null;
            }

            setting = itr.next();
        } while (!((ItemSetting)setting).isEqualForCommandExecutor(itemStack));

        return setting;
    }

    public Setting isLiquidValid(Menu menu, FluidStack fluidStack)
    {
        MenuStuff menuItem = (MenuStuff)menu;
        if (fluidStack != null)
        {
            int fluidId = fluidStack.fluidID;

            for (Setting setting : menuItem.getSettings())
            {
                if (setting.isValid() && ((LiquidSetting)setting).getLiquidId() == fluidId)
                {
                    return setting;
                }
            }
        }
        return null;
    }

    public void insertItems(Menu menu, List<SlotInventoryHolder> inventories)
    {
        MenuStuff menuItem = (MenuStuff)menu;
        ArrayList<OutputItemCounter> outputCounters = new ArrayList<OutputItemCounter>();

        for (SlotInventoryHolder inventoryHolder : inventories)
        {
            if (!inventoryHolder.isShared())
            {
                outputCounters.clear();
            }

            for (CraftingBufferFluidElement craftingBufferElement : craftingBufferHigh)
            {
                this.insertItemsFromInputBufferElement(menuItem, inventories, outputCounters, inventoryHolder, craftingBufferElement);
            }

            for (ItemBufferElement itemBufferElement : itemBuffer)
            {
                this.insertItemsFromInputBufferElement(menuItem, inventories, outputCounters, inventoryHolder, itemBufferElement);
            }

            for (CraftingBufferFluidElement craftingBufferElement : craftingBufferLow)
            {
                this.insertItemsFromInputBufferElement(menuItem, inventories, outputCounters, inventoryHolder, craftingBufferElement);
            }
        }
    }

    public void insertItemsFromInputBufferElement(MenuStuff menuItem, List<SlotInventoryHolder> inventories, List<OutputItemCounter> outputCounters, SlotInventoryHolder inventoryHolder, IItemBufferElement itemBufferElement)
    {
        itemBufferElement.prepareSubElements();

        IInventory inventory = inventoryHolder.getTile() instanceof IHiddenInventory ? Null.NULL_INVENTORY : inventoryHolder.getInventory();
        IItemBufferSubElement subElement;
        while ((subElement = itemBufferElement.getSubElement()) != null)
        {
            ItemStack itemStack = subElement.getKey();
            Setting setting = this.isItemValid(menuItem.getSettings(), itemStack);
            if (menuItem.useWhiteList() != (setting == null) || setting != null && setting.isLimitedByAmount())
            {
                OutputItemCounter outputItemCounter = null;

                for (OutputItemCounter slot : outputCounters)
                {
                    if (slot.areSettingsSame(setting))
                    {
                        outputItemCounter = slot;
                        break;
                    }
                }

                if (outputItemCounter == null)
                {
                    outputItemCounter = new OutputItemCounter(this.itemBuffer, inventories, inventory, setting, menuItem.useWhiteList());
                    outputCounters.add(outputItemCounter);
                }

                if (inventoryHolder.getTile() instanceof IHiddenInventory)
                {
                    IHiddenInventory hidden = (IHiddenInventory)inventoryHolder.getTile();
                    int moveCount = Math.min(hidden.getInsertable(itemStack), itemStack.stackSize);
                    if (moveCount > 0)
                    {
                        moveCount = Math.min(subElement.getSizeLeft(), moveCount);
                        moveCount = outputItemCounter.retrieveItemCount(moveCount);
                        moveCount = itemBufferElement.retrieveItemCount(moveCount);
                        if (moveCount > 0)
                        {
                            itemBufferElement.decreaseStackSize(moveCount);
                            outputItemCounter.modifyStackSize(moveCount);
                            ItemStack toInsert = itemStack.copy();
                            toInsert.stackSize = moveCount;
                            hidden.insertItemStack(toInsert);
                            subElement.reduceBufferAmount(moveCount);

                            if (subElement.getSizeLeft() == 0)
                            {
                                subElement.remove();
                                itemBufferElement.removeSubElement();
                            }
                            subElement.onUpdate();
                        }
                    }
                } else
                {

                    for (SlotSideTarget slot : inventoryHolder.getValidSlots().values())
                    {
                        if (this.isSlotValid(inventory, itemStack, slot, false))
                        {
                            ItemStack itemInSlot = inventory.getStackInSlot(slot.getSlot());
                            boolean newItem = itemInSlot == null;
                            if (newItem || itemInSlot.isItemEqual(itemStack) && ItemStack.areItemStackTagsEqual(itemStack, itemInSlot) && itemStack.isStackable())
                            {
                                int itemCountInSlot = newItem ? 0 : itemInSlot.stackSize;
                                int moveCount = Math.min(subElement.getSizeLeft(), Math.min(inventory.getInventoryStackLimit(), itemStack.getMaxStackSize()) - itemCountInSlot);
                                moveCount = outputItemCounter.retrieveItemCount(moveCount);
                                moveCount = itemBufferElement.retrieveItemCount(moveCount);
                                if (moveCount > 0)
                                {
                                    if (newItem)
                                    {
                                        itemInSlot = itemStack.copy();
                                        itemInSlot.stackSize = 0;
                                    }

                                    itemBufferElement.decreaseStackSize(moveCount);
                                    outputItemCounter.modifyStackSize(moveCount);
                                    itemInSlot.stackSize += moveCount;
                                    subElement.reduceBufferAmount(moveCount);
                                    if (newItem)
                                    {
                                        inventory.setInventorySlotContents(slot.getSlot(), itemInSlot);
                                    }

                                    boolean done = false;
                                    if (subElement.getSizeLeft() == 0)
                                    {
                                        subElement.remove();
                                        itemBufferElement.removeSubElement();
                                        done = true;
                                    }

                                    inventory.markDirty();
                                    subElement.onUpdate();
                                    if (done)
                                    {
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        itemBufferElement.releaseSubElements();
    }

    public void insertLiquids(Menu menu, List<SlotInventoryHolder> tanks)
    {
        MenuStuff menuItem = (MenuStuff)menu;
        ArrayList<OutputLiquidCounter> outputCounters = new ArrayList<OutputLiquidCounter>();

        for (SlotInventoryHolder tankHolder : tanks)
        {
            if (!tankHolder.isShared())
            {
                outputCounters.clear();
            }

            IFluidHandler tank = tankHolder.getTile() instanceof IHiddenTank ? ((IHiddenTank)tankHolder.getTile()).getTank() : tankHolder.getTank();

            for (LiquidBufferElement liquidBufferElement : this.liquidBuffer)
            {
                Iterator<StackTankHolder> liquidIterator = liquidBufferElement.getHolders().iterator();

                while (liquidIterator.hasNext())
                {
                    StackTankHolder holder = liquidIterator.next();
                    FluidStack fluidStack = holder.getFluidStack();
                    Setting setting = this.isLiquidValid(menu, fluidStack);
                    if (menuItem.useWhiteList() != (setting == null) || setting != null && setting.isLimitedByAmount())
                    {
                        OutputLiquidCounter outputLiquidCounter = null;

                        for (OutputLiquidCounter slot : outputCounters)
                        {
                            if (slot.areSettingsSame(setting))
                            {
                                outputLiquidCounter = slot;
                                break;
                            }
                        }

                        if (outputLiquidCounter == null)
                        {
                            outputLiquidCounter = new OutputLiquidCounter(this.liquidBuffer, tanks, tankHolder, setting, menuItem.useWhiteList());
                            outputCounters.add(outputLiquidCounter);
                        }

                        for (SlotSideTarget slot : tankHolder.getValidSlots().values())
                        {

                            for (int side : slot.getSides())
                            {
                                FluidStack temp = fluidStack.copy();
                                temp.amount = holder.getSizeLeft();
                                int amount = tank.fill(ForgeDirection.VALID_DIRECTIONS[side], temp, false);
                                amount = liquidBufferElement.retrieveItemCount(amount);
                                amount = outputLiquidCounter.retrieveItemCount(amount);
                                if (amount > 0)
                                {
                                    FluidStack resource = fluidStack.copy();
                                    resource.amount = amount;
                                    resource = holder.getTank().drain(holder.getSide(), resource, true);
                                    if (resource != null && resource.amount > 0)
                                    {
                                        tank.fill(ForgeDirection.VALID_DIRECTIONS[side], resource, true);
                                        liquidBufferElement.decreaseStackSize(resource.amount);
                                        outputLiquidCounter.modifyStackSize(resource.amount);
                                        holder.reduceAmount(resource.amount);
                                        if (holder.getSizeLeft() == 0)
                                        {
                                            liquidIterator.remove();
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    public boolean searchForStuff(Menu menu, List<SlotInventoryHolder> inventories, boolean useLiquids)
    {
        int i;
        if (!inventories.get(0).isShared())
        {
            boolean var7 = inventories.get(0).getSharedOption() == 1;

            for (i = 0; i < inventories.size(); ++i)
            {
                HashMap<Integer, ConditionSettingChecker> conditionSettingCheckerMap = new HashMap<Integer, ConditionSettingChecker>();
                this.calculateConditionData(menu, inventories.get(i), conditionSettingCheckerMap, useLiquids);
                if (this.checkConditionResult(menu, conditionSettingCheckerMap))
                {
                    if (!var7)
                    {
                        return true;
                    }
                } else if (var7)
                {
                    return false;
                }
            }

            return var7;
        } else
        {
            HashMap<Integer, ConditionSettingChecker> useAnd = new HashMap<Integer, ConditionSettingChecker>();

            for (i = 0; i < inventories.size(); ++i)
            {
                this.calculateConditionData(menu, inventories.get(i), useAnd, useLiquids);
            }

            return this.checkConditionResult(menu, useAnd);
        }
    }

    public void calculateConditionData(Menu menu, SlotInventoryHolder inventoryHolder, Map<Integer, ConditionSettingChecker> conditionSettingCheckerMap, boolean useLiquid)
    {
        if (useLiquid)
        {
            this.calculateConditionDataLiquid(menu, inventoryHolder, conditionSettingCheckerMap);
        } else
        {
            this.calculateConditionDataItem(menu, inventoryHolder, conditionSettingCheckerMap);
        }

    }

    public void calculateConditionDataItem(Menu menu, SlotInventoryHolder inventoryHolder, Map<Integer, ConditionSettingChecker> conditionSettingCheckerMap)
    {
        if (inventoryHolder.getTile() instanceof IHiddenInventory)
        {
            ((IHiddenInventory)inventoryHolder.getTile()).isItemValid(((MenuStuff)menu).getSettings(), conditionSettingCheckerMap);
        } else
        {
            for (SlotSideTarget slot : inventoryHolder.getValidSlots().values())
            {
                ItemStack itemStack = inventoryHolder.getInventory().getStackInSlot(slot.getSlot());
                if (this.isSlotValid(inventoryHolder.getInventory(), itemStack, slot, true))
                {
                    if (inventoryHolder.getInventory() instanceof IDeepStorageUnit)
                        itemStack = ((IDeepStorageUnit)inventoryHolder.getInventory()).getStoredItemType();
                    Setting setting = this.isItemValid(((MenuStuff)menu).getSettings(), itemStack);
                    if (setting != null)
                    {
                        ConditionSettingChecker conditionSettingChecker = conditionSettingCheckerMap.get(setting.getId());
                        if (conditionSettingChecker == null)
                        {
                            conditionSettingCheckerMap.put(setting.getId(), conditionSettingChecker = new ConditionSettingChecker(setting));
                        }

                        conditionSettingChecker.addCount(itemStack.stackSize);
                    }
                }
            }
        }
    }

    public void calculateConditionDataLiquid(Menu menu, SlotInventoryHolder tank, Map<Integer, ConditionSettingChecker> conditionSettingCheckerMap)
    {

        for (SlotSideTarget slot : tank.getValidSlots().values())
        {
            ArrayList<FluidTankInfo> tankInfos = new ArrayList<FluidTankInfo>();

            for (int side : slot.getSides())
            {
                FluidTankInfo[] currentTankInfos = tank.getTile() instanceof IHiddenTank ? ((IHiddenTank)tank.getTile()).getTank().getTankInfo(null) : tank.getTank().getTankInfo(ForgeDirection.VALID_DIRECTIONS[side]);
                if (currentTankInfos != null)
                {
                    for (FluidTankInfo fluidTankInfo : currentTankInfos)
                    {
                        if (fluidTankInfo != null)
                        {
                            boolean alreadyUsed = false;

                            for (FluidTankInfo setting : tankInfos)
                            {
                                if (FluidStack.areFluidStackTagsEqual(setting.fluid, fluidTankInfo.fluid) && setting.capacity == fluidTankInfo.capacity)
                                {
                                    alreadyUsed = true;
                                }
                            }

                            if (!alreadyUsed)
                            {
                                FluidStack var18 = fluidTankInfo.fluid;
                                Setting setting = this.isLiquidValid(menu, var18);
                                if (setting != null)
                                {
                                    ConditionSettingChecker conditionSettingChecker = conditionSettingCheckerMap.get(setting.getId());
                                    if (conditionSettingChecker == null)
                                    {
                                        conditionSettingCheckerMap.put(setting.getId(), conditionSettingChecker = new ConditionSettingChecker(setting));
                                    }

                                    conditionSettingChecker.addCount(var18.amount);
                                }
                            }
                        }
                    }

                    for (FluidTankInfo fluidTankInfo : currentTankInfos)
                    {
                        if (fluidTankInfo != null)
                        {
                            tankInfos.add(fluidTankInfo);
                        }
                    }
                }
            }
        }
    }

    public boolean checkConditionResult(Menu menu, Map<Integer, ConditionSettingChecker> conditionSettingCheckerMap)
    {
        MenuStuff menuItem = (MenuStuff)menu;
        IConditionStuffMenu menuCondition = (IConditionStuffMenu)menu;

        for (Setting setting : menuItem.getSettings())
        {
            if (setting.isValid())
            {
                ConditionSettingChecker conditionSettingChecker = conditionSettingCheckerMap.get(setting.getId());
                if (conditionSettingChecker != null && conditionSettingChecker.isTrue())
                {
                    if (!menuCondition.requiresAll())
                    {
                        return true;
                    }
                } else if (menuCondition.requiresAll())
                {
                    return false;
                }
            }
        }
        return menuCondition.requiresAll();
    }

    public boolean splitFlow(Menu menu)
    {
        MenuSplit split = (MenuSplit)menu;
        if (!split.useSplit())
        {
            return false;
        } else
        {
            int amount = menu.getParent().getConnectionSet().getOutputCount();
            if (!split.useEmpty())
            {
                ConnectionOption[] usedId = menu.getParent().getConnectionSet().getConnections();

                for (int connections = 0; connections < usedId.length; ++connections)
                {
                    ConnectionOption i = usedId[connections];
                    if (!i.isInput() && menu.getParent().getConnection(connections) == null)
                    {
                        --amount;
                    }
                }
            }

            int var14 = 0;
            ConnectionOption[] var15 = menu.getParent().getConnectionSet().getConnections();

            for (int var16 = 0; var16 < var15.length; ++var16)
            {
                ConnectionOption connectionOption = var15[var16];
                Connection connection = menu.getParent().getConnection(var16);
                if (!connectionOption.isInput() && connection != null)
                {
                    ArrayList<ItemBufferElement> itemBufferSplit = new ArrayList<ItemBufferElement>();
                    ArrayList<LiquidBufferElement> liquidBufferSplit = new ArrayList<LiquidBufferElement>();
                    Iterator usedCommandCopy = this.itemBuffer.iterator();

                    while (usedCommandCopy.hasNext())
                    {
                        ItemBufferElement newExecutor = (ItemBufferElement)usedCommandCopy.next();
                        itemBufferSplit.add(newExecutor.getSplitElement(amount, var14, split.useFair()));
                    }

                    usedCommandCopy = this.liquidBuffer.iterator();

                    while (usedCommandCopy.hasNext())
                    {
                        LiquidBufferElement var18 = (LiquidBufferElement)usedCommandCopy.next();
                        liquidBufferSplit.add(var18.getSplitElement(amount, var14, split.useFair()));
                    }

                    ArrayList<Integer> var17 = new ArrayList<Integer>();

                    for (int usedCommand : this.usedCommands)
                    {
                        var17.add(usedCommand);
                    }

                    CommandExecutor var20 = new CommandExecutor(this.manager, itemBufferSplit, new ArrayList<CraftingBufferFluidElement>(this.craftingBufferHigh), new ArrayList<CraftingBufferFluidElement>(this.craftingBufferLow), liquidBufferSplit, rfBuffer, var17);
                    var20.executeCommand(this.manager.getFlowItems().get(connection.getComponentId()), connection.getConnectionId());
                    ++var14;
                }
            }
            return true;
        }
    }

    public boolean evaluateRedstoneCondition(List<SlotInventoryHolder> nodes, FlowComponent component)
    {
        return TileEntityManager.redstoneCondition.isTriggerPowered(nodes, component, true);
    }

    public void updateVariable(List<SlotInventoryHolder> tiles, MenuVariable menuVariable, MenuListOrder menuOrder)
    {
        VariableMode mode = menuVariable.getVariableMode();
        Variable variable = this.manager.getVariables()[menuVariable.getSelectedVariable()];
        if (variable.isValid())
        {
            boolean remove = mode == VariableMode.REMOVE;
            if (!remove && mode != VariableMode.ADD)
            {
                variable.clearContainers();
            }

            List<Integer> idList = new ArrayList<Integer>();

            for (SlotInventoryHolder validTypes : tiles)
            {
                idList.add(validTypes.getId());
            }

            if (!menuVariable.isDeclaration())
            {
                idList = this.applyOrder(idList, menuOrder);
            }

            List<ConnectionBlock> inventories1 = this.manager.getConnectedInventories();
            EnumSet<ConnectionBlockType> validTypes1 = ((MenuContainerTypes)variable.getDeclaration().getMenus().get(1)).getValidTypes();

            for (int id : idList)
            {
                if (remove)
                {
                    variable.remove(id);
                } else if (id >= 0 && id < inventories1.size() && (inventories1.get(id)).isOfAnyType(validTypes1))
                {
                    variable.add(id);
                }
            }
        }
    }

    public void updateForLoop(FlowComponent command, MenuVariableLoop variableMenu, MenuContainerTypes typesMenu, MenuListOrder orderMenu)
    {
        Variable list = variableMenu.getListVariable();
        Variable element = variableMenu.getElementVariable();
        if (list.isValid() && element.isValid())
        {
            List<Integer> selection = this.applyOrder(list.getContainers(), orderMenu);
            EnumSet<ConnectionBlockType> validTypes = typesMenu.getValidTypes();
            validTypes.addAll(((MenuContainerTypes)element.getDeclaration().getMenus().get(1)).getValidTypes());
            List<ConnectionBlock> inventories = this.manager.getConnectedInventories();

            for (int selected : selection)
            {
                if (selected >= 0 && selected < inventories.size())
                {
                    ConnectionBlock inventory = inventories.get(selected);
                    if (inventory.isOfAnyType(validTypes))
                    {
                        element.clearContainers();
                        element.add(selected);
                        this.executeChildCommands(command, EnumSet.of(ConnectionOption.FOR_EACH));
                    }
                }
            }
        }
    }

    public List<Integer> applyOrder(List<Integer> original, MenuListOrder orderMenu)
    {
        ArrayList<Integer> ret = new ArrayList<Integer>(original);
        if (orderMenu.getOrder() == LoopOrder.RANDOM)
        {
            Collections.shuffle(ret);
        } else if (orderMenu.getOrder() == LoopOrder.NORMAL)
        {
            if (!orderMenu.isReversed())
            {
                Collections.reverse(ret);
            }
        } else
        {
            Collections.sort(ret, orderMenu.getComparator());
        }

        if (!orderMenu.useAll())
        {
            int len = orderMenu.getAmount();

            while (ret.size() > len)
            {
                ret.remove(ret.size() - 1);
            }
        }

        return ret;
    }
}
