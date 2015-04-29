package advancedsystemsmanager.flow.execution.commands;

import advancedsystemsmanager.api.execution.IBuffer;
import advancedsystemsmanager.api.execution.IBufferElement;
import advancedsystemsmanager.api.execution.IInternalTank;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.execution.buffers.Buffer;
import advancedsystemsmanager.flow.execution.buffers.elements.FluidBufferElement;
import advancedsystemsmanager.flow.menus.Menu;
import advancedsystemsmanager.flow.menus.MenuLiquid;
import advancedsystemsmanager.flow.menus.MenuTank;
import advancedsystemsmanager.flow.menus.MenuTargetTank;
import advancedsystemsmanager.flow.setting.Setting;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.util.SystemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
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
        super(FLUID_INPUT, Names.LIQUID_INPUT, IBuffer.FLUID);
    }

    @Override
    public void getMenus(FlowComponent component, List<Menu> menus)
    {
        menus.add(new MenuTank(component));
        menus.add(new MenuTargetTank(component));
        menus.add(new MenuLiquid(component));
    }

    @Override
    protected IBuffer getNewBuffer()
    {
        return new Buffer<Fluid>();
    }

    @Override
    protected List<IBufferElement<Fluid>> getBufferSubElements(int id, List<SystemBlock> blocks, List<Menu> menus)
    {
        MenuTargetTank target = (MenuTargetTank)menus.get(1);
        MenuLiquid settings = (MenuLiquid)menus.get(2);
        List<Setting<Fluid>> validSettings = getValidSettings(settings.getSettings());
        List<IBufferElement<Fluid>> subElements = new ArrayList<IBufferElement<Fluid>>();
        for (SystemBlock block : blocks)
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
                    Setting<Fluid> setting = isValid(validSettings, entry.getValue().fluid.getFluid());
                    boolean whitelist = settings.isFirstRadioButtonSelected();
                    if (setting == null && whitelist) continue;
                    subElements.add(new FluidBufferElement(id, tank, entry.getKey(), entry.getValue().fluid.amount, entry.getValue().fluid.getFluid(), setting, whitelist));
                }
            }
        }
        return subElements;
    }
}
