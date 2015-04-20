package advancedfactorymanager.tileentities;

import advancedfactorymanager.api.IHiddenInventory;
import advancedfactorymanager.api.IHiddenTank;
import advancedfactorymanager.blocks.ClusterMethodRegistration;
import advancedfactorymanager.components.*;
import advancedfactorymanager.helpers.AEHelper;
import advancedfactorymanager.registry.BlockRegistry;
import appeng.api.AEApi;
import appeng.api.networking.*;
import appeng.api.networking.security.IActionHost;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.util.AECableType;
import appeng.api.util.AEColor;
import appeng.api.util.DimensionalCoord;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Optional;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import java.util.*;

@Optional.Interface(iface = "advancedfactorymanager.api.IHiddenTank", modid = "extracells")
public class TileEntityAENode extends TileEntityClusterElement implements IGridHost, IActionHost, IHiddenInventory, IHiddenTank
{
    private class GridBlock implements IGridBlock
    {
        @Override
        public double getIdlePowerUsage()
        {
            return 10;
        }

        @Override
        public EnumSet<GridFlags> getFlags()
        {
            return EnumSet.of(GridFlags.REQUIRE_CHANNEL);
        }

        @Override
        public boolean isWorldAccessible()
        {
            return true;
        }

        @Override
        public DimensionalCoord getLocation()
        {
            return new DimensionalCoord(TileEntityAENode.this);
        }

        @Override
        public AEColor getGridColor()
        {
            return AEColor.Transparent;
        }

        @Override
        public void onGridNotification(GridNotification gridNotification)
        {

        }

        @Override
        public void setNetworkStatus(IGrid iGrid, int i)
        {

        }

        @Override
        public EnumSet<ForgeDirection> getConnectableSides()
        {
            return EnumSet.allOf(ForgeDirection.class);
        }

        @Override
        public IGridHost getMachine()
        {
            return TileEntityAENode.this;
        }

        @Override
        public void gridChanged()
        {

        }

        @Override
        public ItemStack getMachineRepresentation()
        {
            return new ItemStack(BlockRegistry.cableAENode);
        }
    }

    private class AEFakeTank implements IFluidHandler
    {
        @Override
        public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
        {
            IAEFluidStack toAdd = AEHelper.insert(getNode(), resource, TileEntityAENode.this, doFill);
            return toAdd == null ? resource.amount : resource.amount - (int)toAdd.getStackSize();
        }

        @Override
        public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain)
        {
            IAEFluidStack drain = AEHelper.extract(getNode(), resource, TileEntityAENode.this, doDrain);
            return drain == null ? null : drain.getFluidStack();
        }

        @Override
        public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
        {
            Iterator<IAEFluidStack> itr = AEHelper.getItrFluids(getNode());
            if (itr != null && itr.hasNext())
            {
                FluidStack stack = itr.next().getFluidStack();
                stack.amount = Math.min(maxDrain, stack.amount);
                return drain(from, stack, doDrain);
            }
            return null;
        }

        @Override
        public boolean canFill(ForgeDirection from, Fluid fluid)
        {
            return AEHelper.insert(getNode(), new FluidStack(fluid, 1), TileEntityAENode.this, true) != null;
        }

        @Override
        public boolean canDrain(ForgeDirection from, Fluid fluid)
        {
            return AEHelper.find(getNode(), new FluidStack(fluid, 1)) != null;
        }

        @Override
        public FluidTankInfo[] getTankInfo(ForgeDirection from)
        {
            List<FluidTankInfo> tankInfo = new ArrayList<FluidTankInfo>();
            Iterator<IAEFluidStack> itr = AEHelper.getItrFluids(getNode());
            if (itr == null) return new FluidTankInfo[0];
            while (itr.hasNext())
            {
                FluidStack stack = itr.next().getFluidStack();
                tankInfo.add(new FluidTankInfo(stack, stack.amount));
            }
            return tankInfo.toArray(new FluidTankInfo[tankInfo.size()]);
        }
    }

    private GridBlock gridBlock;
    private IGridNode gridNode;
    private IFluidHandler tank;
    private boolean isReady;

    public TileEntityAENode()
    {
        this.gridBlock = new GridBlock();
        this.tank = new AEFakeTank();
    }

    @Override
    public void updateEntity()
    {
        super.updateEntity();
        this.isReady = true;
        getNode();
    }

    @Override
    public boolean canUpdate()
    {
        return !this.isReady;
    }

    public IGridNode getNode()
    {
        if (this.gridNode == null && FMLCommonHandler.instance().getEffectiveSide().isServer() && this.isReady)
        {
            this.gridNode = AEApi.instance().createGridNode(this.gridBlock);
            this.gridNode.updateState();
        }

        return this.gridNode;
    }

    @Override
    public IGridNode getActionableNode()
    {
        return getNode();
    }

    @Override
    protected EnumSet<ClusterMethodRegistration> getRegistrations()
    {
        return null;
    }

    @Override
    public IGridNode getGridNode(ForgeDirection forgeDirection)
    {
        return getNode();
    }

    @Override
    public AECableType getCableConnectionType(ForgeDirection forgeDirection)
    {
        return AECableType.SMART;
    }

    @Override
    public void securityBreak()
    {
        this.worldObj.func_147480_a(this.xCoord, this.yCoord, this.zCoord, true);
    }

    @Override
    public void invalidate()
    {
        super.invalidate();
        if (this.gridNode != null)
        {
            this.gridNode.destroy();
            this.gridNode = null;
        }
    }

    @Override
    public void onChunkUnload()
    {
        super.onChunkUnload();
        if (this.gridNode != null)
        {
            this.gridNode.destroy();
            this.gridNode = null;
        }
    }

    @Override
    public int getInsertable(ItemStack stack)
    {
        ItemStack insertable = AEHelper.getInsertable(getNode(), stack, this);
        return insertable == null ? 0 : insertable.stackSize;
    }

    @Override
    public void insertItemStack(ItemStack stack)
    {
        AEHelper.insert(getNode(), stack, this, false);
    }

    @Override
    public void addItemsToBuffer(ComponentMenuStuff menuItem, SlotInventoryHolder inventory, List<ItemBufferElement> itemBuffer, CommandExecutorRF commandExecutorRF)
    {
        Iterator<IAEItemStack> itr = AEHelper.getItrItems(this.getNode());
        if (itr == null) return;
        while (itr.hasNext())
        {
            IAEItemStack stack = itr.next();
            if (stack != null)
            {
                Setting setting = commandExecutorRF.isItemValid(menuItem.getSettings(), stack.getItemStack());
                addAEItemToBuffer(menuItem, inventory, setting, stack, itemBuffer);
            }
        }
    }

    @Override
    public void isItemValid(Collection<Setting> settings, Map<Integer, ConditionSettingChecker> conditionSettingCheckerMap)
    {
        for (Setting setting : settings)
        {
            ItemStack stack = AEHelper.find(getNode(), ((ItemSetting)setting).getItem());
            if (stack != null)
            {
                ConditionSettingChecker conditionSettingChecker = conditionSettingCheckerMap.get(setting.getId());
                if (conditionSettingChecker == null)
                {
                    conditionSettingCheckerMap.put(setting.getId(), conditionSettingChecker = new ConditionSettingChecker(setting));
                }
                conditionSettingChecker.addCount(stack.stackSize);
            }
        }
    }

    private void addAEItemToBuffer(ComponentMenuStuff menuItem, SlotInventoryHolder inventory, Setting setting, IAEItemStack stack, List<ItemBufferElement> itemBuffer)
    {
        if (menuItem.useWhiteList() == (setting != null) || setting != null && setting.isLimitedByAmount())
        {
            FlowComponent owner = menuItem.getParent();
            SlotStackInventoryHolder target = new AEItemBufferElement(stack, this);
            boolean added = false;

            for (ItemBufferElement itemBufferElement : itemBuffer)
            {
                if (itemBufferElement.addTarget(owner, setting, inventory, target))
                {
                    added = true;
                    break;
                }
            }
            if (!added)
            {
                itemBuffer.add(new ItemBufferElement(owner, setting, inventory, menuItem.useWhiteList(), target));
            }
        }
    }

    @Override
    public void addFluidsToBuffer(ComponentMenuStuff menuItem, SlotInventoryHolder tank, List<LiquidBufferElement> liquidBuffer, CommandExecutorRF commandExecutorRF)
    {
        Iterator<IAEFluidStack> itr = AEHelper.getItrFluids(this.getNode());
        if (itr == null) return;
        while (itr.hasNext())
        {
            IAEFluidStack stack = itr.next();
            if (stack != null)
            {
                Setting setting = commandExecutorRF.isLiquidValid(menuItem, stack.getFluidStack());
                addAEFluidToBuffer(menuItem, tank, setting, stack, liquidBuffer);
            }
        }
    }

    private void addAEFluidToBuffer(ComponentMenuStuff menuItem, SlotInventoryHolder tank, Setting setting, IAEFluidStack stack, List<LiquidBufferElement> liquidBuffer)
    {
        if (menuItem.useWhiteList() == (setting != null) || setting != null && setting.isLimitedByAmount())
        {
            FlowComponent owner = menuItem.getParent();
            StackTankHolder target = new AEFluidBufferElement(stack, (TileEntityAENode)tank.getTile());
            boolean added = false;

            for (LiquidBufferElement liquidBufferElement : liquidBuffer)
            {
                if (liquidBufferElement.addTarget(owner, setting, tank, target))
                {
                    added = true;
                    break;
                }
            }

            if (!added)
            {
                liquidBuffer.add(new LiquidBufferElement(owner, setting, tank, menuItem.useWhiteList(), target));
            }
        }
    }

    @Override
    public IFluidHandler getTank()
    {
        return tank;
    }
}
