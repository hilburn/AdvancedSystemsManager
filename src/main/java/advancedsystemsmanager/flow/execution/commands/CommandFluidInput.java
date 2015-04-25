package advancedsystemsmanager.flow.execution.commands;

import advancedsystemsmanager.api.execution.*;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.execution.buffers.buffers.Buffer;
import advancedsystemsmanager.flow.execution.buffers.buffers.FluidBufferElement;
import advancedsystemsmanager.flow.menus.*;
import advancedsystemsmanager.flow.setting.ItemSetting;
import advancedsystemsmanager.flow.setting.LiquidSetting;
import advancedsystemsmanager.flow.setting.Setting;
import advancedsystemsmanager.helpers.Localization;
import advancedsystemsmanager.util.ConnectionBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandFluidInput extends CommandInput<Fluid>
{
    public CommandFluidInput()
    {
        super(FLUID_INPUT, Localization.LIQUID_INPUT_SHORT.toString(), Localization.LIQUID_INPUT_LONG.toString(), IBuffer.FLUID);
    }

    @Override
    public List<Menu> getMenus(FlowComponent component)
    {
        List<Menu> menus = component.getMenus();
        menus.add(new MenuTank(component));
        menus.add(new MenuTargetTank(component));
        menus.add(new MenuLiquid(component));
        return menus;
    }

    @Override
    protected IBuffer getNewBuffer()
    {
        return new Buffer<Fluid>();
    }

    @Override
    protected List<IBufferElement<Fluid>> getBufferSubElements(int id, List<ConnectionBlock> blocks, List<Menu> menus)
    {
        MenuTargetTank target = (MenuTargetTank)menus.get(1);
        MenuLiquid settings = (MenuLiquid)menus.get(2);
        List<IBufferElement<Fluid>> subElements = new ArrayList<IBufferElement<Fluid>>();
        for (ConnectionBlock block : blocks)
        {
            TileEntity entity = block.getTileEntity();
            if (entity instanceof IInternalTank)
            {
                subElements.addAll(((IInternalTank)entity).getSubElements(id, settings));
            }else
            {
                IFluidHandler tank = (IFluidHandler)entity;
                Map<ForgeDirection,FluidTankInfo> tankInfoMap = new HashMap<ForgeDirection, FluidTankInfo>();
                for (int i = 0; i < 6; i++)
                {
                    if (target.activatedDirections[i])
                    {
                        FluidTankInfo[] tankInfos = tank.getTankInfo(ForgeDirection.getOrientation(i));
                        if (tankInfos == null || tankInfos[0] == null || tankInfos[0].fluid == null || tankInfos[0].fluid.amount == 0) continue;
                        boolean safeToAdd = true;
                        for (FluidTankInfo tankInfo : tankInfoMap.values())
                        {
                            if (tankInfo.capacity == tankInfos[0].capacity && tankInfo.fluid.isFluidStackIdentical(tankInfos[0].fluid))
                            {
                                safeToAdd = false;
                                break;
                            }
                        }
                        if (safeToAdd) tankInfoMap.put(ForgeDirection.getOrientation(i), tankInfos[0]);
                    }
                }
                for (Map.Entry<ForgeDirection, FluidTankInfo> entry : tankInfoMap.entrySet())
                {
                    if (isFluidValid(settings.settings, entry.getValue().fluid, settings.isFirstRadioButtonSelected()))
                        subElements.add(new FluidBufferElement(id, tank, entry.getKey(), entry.getValue().fluid.amount, entry.getValue().fluid.getFluid()));
                }
            }
        }
        return subElements;
    }

    private static boolean isFluidValid(List<Setting> settings, FluidStack fluid, boolean whitelist)
    {
        for (Setting setting : settings)
            if (setting.isValid() && ((LiquidSetting)setting).fluid == fluid.getFluid()) return whitelist;
        return !whitelist;
    }
}
