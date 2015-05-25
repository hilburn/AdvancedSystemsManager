package advancedsystemsmanager.tileentities;

import advancedsystemsmanager.api.execution.IBufferElement;
import advancedsystemsmanager.api.tileentities.IInternalInventory;
import advancedsystemsmanager.api.tileentities.IInternalTank;
import advancedsystemsmanager.compatibility.appliedenergistics.AEFluidBufferElement;
import advancedsystemsmanager.compatibility.appliedenergistics.AEHelper;
import advancedsystemsmanager.compatibility.appliedenergistics.AEItemBufferElement;
import advancedsystemsmanager.flow.execution.*;
import advancedsystemsmanager.flow.menus.MenuItem;
import advancedsystemsmanager.flow.menus.MenuLiquid;
import advancedsystemsmanager.flow.menus.MenuStuff;
import advancedsystemsmanager.flow.setting.ItemSetting;
import advancedsystemsmanager.flow.setting.Setting;
import advancedsystemsmanager.registry.BlockRegistry;
import advancedsystemsmanager.registry.CommandRegistry;
import advancedsystemsmanager.util.ClusterMethodRegistration;
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

@Optional.Interface(iface = "advancedsystemsmanager.api.tileentities.IHiddenTank", modid = "extracells")
public class TileEntityAENode extends TileEntityClusterElement implements IGridHost, IActionHost, IInternalInventory, IInternalTank
{
    public AEHelper helper;
    private GridBlock gridBlock;
    private IGridNode gridNode;
    private IFluidHandler tank;
    private boolean isReady;
    public TileEntityAENode()
    {
        this.gridBlock = new GridBlock();
        this.tank = new AEFakeTank();
        this.helper = new AEHelper(this);
    }

    @Override
    public void updateEntity()
    {
        super.updateEntity();
        this.isReady = true;
        getNode();
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
    public boolean canUpdate()
    {
        return !this.isReady;
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
    public IGridNode getActionableNode()
    {
        return getNode();
    }

    @Override
    public EnumSet<ClusterMethodRegistration> getRegistrations()
    {
        return EnumSet.noneOf(ClusterMethodRegistration.class);
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
    public int getAmountToInsert(ItemStack stack)
    {
        ItemStack insertable = helper.getInsertable(stack);
        return insertable == null ? 0 : insertable.stackSize;
    }

    @Override
    public void insertItemStack(ItemStack stack)
    {
        helper.insert(stack, false);
    }

    @Override
    public List<IBufferElement<ItemStack>> getSubElements(int id, MenuItem menuItem)
    {
        List<IBufferElement<ItemStack>> elements = new ArrayList<IBufferElement<ItemStack>>();
        Iterator<IAEItemStack> itr = helper.getItrItems();
        if (itr == null) return elements;
        List<Setting<ItemStack>> settings = CommandRegistry.INPUT.getValidSettings(menuItem.getSettings());
        while (itr.hasNext())
        {
            IAEItemStack stack = itr.next();
            if (stack != null)
            {
                Setting<ItemStack> setting = CommandRegistry.INPUT.isValid(settings, stack.getItemStack());
                addAEItemToBuffer(id, menuItem, setting, stack, elements);
            }
        }
        return elements;
    }

    @Override
    public void isItemValid(Collection<Setting> settings, Map<Integer, ConditionSettingChecker> conditionSettingCheckerMap)
    {
        for (Setting setting : settings)
        {
            ItemStack stack = helper.find(((ItemSetting)setting).getItem());
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

    private void addAEItemToBuffer(int id, MenuStuff menuItem, Setting<ItemStack> setting, IAEItemStack stack, List<IBufferElement<ItemStack>> itemBuffer)
    {
        if (menuItem.useWhiteList() == (setting != null) || setting != null && setting.isLimitedByAmount())
        {
            itemBuffer.add(new AEItemBufferElement(id, this, stack, setting, menuItem.useWhiteList()));
        }
    }

    @Override
    public IFluidHandler getTank()
    {
        return tank;
    }

    @Override
    public List<IBufferElement<Fluid>> getSubElements(int id, MenuLiquid menuLiquid)
    {
        List<IBufferElement<Fluid>> elements = new ArrayList<IBufferElement<Fluid>>();
        Iterator<IAEFluidStack> itr = helper.getItrFluids();
        if (itr == null) return elements;
        List<Setting<Fluid>> validSettings = CommandRegistry.LIQUID_INPUT.getValidSettings(menuLiquid.getSettings());
        while (itr.hasNext())
        {
            IAEFluidStack stack = itr.next();
            if (stack != null)
            {
                Setting<Fluid> setting = CommandRegistry.LIQUID_INPUT.isValid(validSettings, stack.getFluidStack().getFluid());
                addAEFluidToBuffer(id, menuLiquid, setting, stack, elements);
            }
        }
        return elements;
    }

    private void addAEFluidToBuffer(int id, MenuStuff menuLiquid, Setting<Fluid> setting, IAEFluidStack stack, List<IBufferElement<Fluid>> liquidBuffer)
    {
        if (menuLiquid.useWhiteList() == (setting != null) || setting != null && setting.isLimitedByAmount())
        {
            liquidBuffer.add(new AEFluidBufferElement(id, this, (int)stack.getStackSize(), stack.getFluid(), setting, menuLiquid.useWhiteList()));
        }
    }

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
            IAEFluidStack toAdd = helper.insert(resource, doFill);
            return toAdd == null ? resource.amount : resource.amount - (int)toAdd.getStackSize();
        }

        @Override
        public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain)
        {
            IAEFluidStack drain = helper.extract(resource, doDrain);
            return drain == null ? null : drain.getFluidStack();
        }

        @Override
        public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
        {
            Iterator<IAEFluidStack> itr = helper.getItrFluids();
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
            return helper.insert(new FluidStack(fluid, 1), true) != null;
        }

        @Override
        public boolean canDrain(ForgeDirection from, Fluid fluid)
        {
            return helper.find(new FluidStack(fluid, 1)) != null;
        }

        @Override
        public FluidTankInfo[] getTankInfo(ForgeDirection from)
        {
            List<FluidTankInfo> tankInfo = new ArrayList<FluidTankInfo>();
            Iterator<IAEFluidStack> itr = helper.getItrFluids();
            if (itr == null) return new FluidTankInfo[0];
            while (itr.hasNext())
            {
                FluidStack stack = itr.next().getFluidStack();
                tankInfo.add(new FluidTankInfo(stack, stack.amount));
            }
            return tankInfo.toArray(new FluidTankInfo[tankInfo.size()]);
        }
    }
}
