package advancedsystemsmanager.flow.execution.commands;

import advancedsystemsmanager.api.tileentities.IInternalTank;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.execution.buffers.elements.FluidBufferElement;
import advancedsystemsmanager.flow.menus.*;
import advancedsystemsmanager.flow.setting.Setting;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.util.SystemCoord;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CommandFluidCondition extends CommandCondition<Fluid, MenuTargetTank>
{
    public CommandFluidCondition()
    {
        super(FLUID_CONDITION, Names.LIQUID_CONDITION);
    }

    @Override
    public void searchForStuff(SystemCoord block, List<Setting<Fluid>> settings, MenuTargetTank target, Set<Setting<Fluid>> found)
    {
        TileEntity entity = block.getTileEntity();
        if (entity instanceof IInternalTank)
        {

        } else
        {
            IFluidHandler tank = (IFluidHandler)entity;
            Map<ForgeDirection, FluidTankInfo> tankInfoMap = new HashMap<ForgeDirection, FluidTankInfo>();
            for (int i = 0; i < 6; i++)
            {
                if (target.activatedDirections[i])
                {
                    FluidTankInfo[] tankInfos = tank.getTankInfo(ForgeDirection.getOrientation(i));
                    if (tankInfos == null || tankInfos[0] == null || tankInfos[0].fluid == null || tankInfos[0].fluid.amount == 0)
                        continue;
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
                Setting<Fluid> setting = isValid(settings, entry.getValue().fluid.getFluid());
                if (setting != null)
                {
                    if (found != null)
                    {
                        found.add(setting);
                    }
                    setting.reduceAmount(entry.getValue().fluid.amount);
                }
            }
        }
    }

    @Override
    public void getMenus(FlowComponent component, List<Menu> menus)
    {
        menus.add(new MenuTankCondition(component));
        menus.add(new MenuTargetTank(component));
        menus.add(new MenuLiquidCondition(component));
    }
}
