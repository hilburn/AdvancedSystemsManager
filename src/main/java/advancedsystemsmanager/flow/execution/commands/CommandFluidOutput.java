package advancedsystemsmanager.flow.execution.commands;

import advancedsystemsmanager.api.execution.*;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.execution.*;
import advancedsystemsmanager.flow.menus.*;
import advancedsystemsmanager.flow.setting.Setting;
import advancedsystemsmanager.helpers.Localization;
import advancedsystemsmanager.util.ConnectionBlock;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CommandFluidOutput extends CommandOutput<Fluid>
{
    public CommandFluidOutput()
    {
        super(6, Localization.LIQUID_OUTPUT_SHORT.toString(), Localization.LIQUID_OUTPUT_LONG.toString(), IBuffer.FLUID);
    }

    @Override
    protected void outputFromBuffer(FlowComponent component, IBuffer<Fluid> buffer)
    {
        MenuLiquid menuLiquid = (MenuLiquid)component.menus.get(2);
        List<Setting<Fluid>> validSettings = getValidSettings(menuLiquid.getSettings());
        List<Integer> validSides = new ArrayList<Integer>();
        for (int i = 0; i< ((MenuTargetTank)component.getMenus().get(1)).activatedDirections.length; i++) if (((MenuTargetTank)component.getMenus().get(1)).activatedDirections[i]) validSides.add(i);
        for (ConnectionBlock block : getContainers(component.manager, (MenuContainer)component.menus.get(0)))
        {
            IFluidHandler tank = block.getTileEntity() instanceof IInternalTank ? ((IInternalTank)block.getTileEntity()).getTank() : (IFluidHandler)block.getTileEntity();
            Iterator<Map.Entry<Key<Fluid>,IBufferElement<Fluid>>> iterator = buffer.getOrderedIterator();
            while (iterator.hasNext())
            {
                IBufferElement<Fluid> fluidBufferElement = iterator.next().getValue();
                FluidStack fluidStack = new FluidStack(fluidBufferElement.getContent(), 0);
                Setting<Fluid> setting = isValid(validSettings, fluidBufferElement.getContent());
                boolean whitelist = menuLiquid.isFirstRadioButtonSelected();
                if (setting == null && whitelist) continue;
                for (int side : validSides)
                {
                    FluidStack temp = fluidStack.copy();
                    temp.amount = fluidBufferElement.getSizeLeft();
                    int amount = tank.fill(ForgeDirection.VALID_DIRECTIONS[side], temp, false);
                    if (amount > 0)
                    {
                        temp.amount = amount;
                        temp.amount = fluidBufferElement.reduceBufferAmount(amount);
                        if (temp.amount > 0)
                        {
                            tank.fill(ForgeDirection.VALID_DIRECTIONS[side], temp, true);
                            if (fluidBufferElement.getSizeLeft() == 0)
                            {
                                fluidBufferElement.remove();
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void getMenus(FlowComponent component, List<Menu> menus)
    {
        menus.add(new MenuTank(component));
        menus.add(new MenuTargetTank(component));
        menus.add(new MenuLiquid(component, false));
    }
}
