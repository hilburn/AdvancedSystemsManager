package advancedsystemsmanager.compatibility.rf.commands;

import advancedsystemsmanager.api.execution.IBuffer;
import advancedsystemsmanager.api.execution.IBufferElement;
import advancedsystemsmanager.compatibility.rf.RFCompat;
import advancedsystemsmanager.compatibility.rf.buffer.RFBufferElement;
import advancedsystemsmanager.compatibility.rf.menus.MenuRF;
import advancedsystemsmanager.compatibility.rf.menus.MenuRFAmount;
import advancedsystemsmanager.compatibility.rf.menus.MenuRFTarget;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.execution.buffers.Buffer;
import advancedsystemsmanager.flow.execution.commands.CommandInput;
import advancedsystemsmanager.flow.menus.Menu;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.util.SystemCoord;
import cofh.api.energy.IEnergyProvider;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.List;

public class CommandRFInput extends CommandInput<Integer>
{
    public CommandRFInput()
    {
        super(RF_INPUT, Names.RF_INPUT, Buffer.RF);
    }

    @Override
    public void getMenus(FlowComponent component, List<Menu> menus)
    {
        menus.add(new MenuRF(component, RFCompat.RF_PROVIDER));
        menus.add(new MenuRFTarget(component));
        menus.add(new MenuRFAmount(component));
    }

    @Override
    protected IBuffer getNewBuffer()
    {
        return new Buffer<Integer>();
    }

    @Override
    protected List<IBufferElement<Integer>> getBufferSubElements(int id, List<SystemCoord> blocks, List<Menu> menus)
    {
        List<IBufferElement<Integer>> rfBufferElements = new ArrayList<IBufferElement<Integer>>();
        MenuRFTarget targets = (MenuRFTarget) menus.get(1);
        int maxAmount = ((MenuRFAmount) menus.get(2)).getAmount();
        maxAmount = maxAmount == 0 ? Integer.MAX_VALUE : maxAmount;
        for (SystemCoord block : blocks)
        {
            IEnergyProvider provider = (IEnergyProvider)block.getTileEntity();
            for (int side = 0; side < 6; side++)
            {
                if (targets.isActive(side))
                {
                    int extractAmount = provider.extractEnergy(ForgeDirection.VALID_DIRECTIONS[side], maxAmount, true);
                    if (extractAmount > 0)
                    {
                        rfBufferElements.add(new RFBufferElement(id, provider, ForgeDirection.VALID_DIRECTIONS[side], extractAmount));
                        break;
                    }
                }
            }
        }
        return rfBufferElements;
    }
}
