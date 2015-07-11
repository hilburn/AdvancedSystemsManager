package advancedsystemsmanager.flow.execution;


import advancedsystemsmanager.api.tileentities.ITriggerNode;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.execution.commands.CommandBase;
import advancedsystemsmanager.flow.menus.MenuContainer;
import advancedsystemsmanager.flow.menus.MenuRedstoneStrength;
import advancedsystemsmanager.registry.ConnectionOption;
import advancedsystemsmanager.registry.SystemTypeRegistry;
import advancedsystemsmanager.tileentities.TileEntityReceiver;
import advancedsystemsmanager.util.SystemCoord;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.EnumSet;
import java.util.List;

public class TriggerHelperRedstone extends TriggerHelper
{
    public int strengthId;

    public TriggerHelperRedstone(int sidesId, int strengthId)
    {
        super(true, 0, sidesId, SystemTypeRegistry.RECEIVER);

        this.strengthId = strengthId;
    }

    @Override
    public void onTrigger(FlowComponent item, EnumSet<ConnectionOption> valid)
    {
        if (isTriggerPowered(item, true))
        {
            valid.add(ConnectionOption.REDSTONE_HIGH);
        }
        if (isTriggerPowered(item, false))
        {
            valid.add(ConnectionOption.REDSTONE_LOW);
        }
    }

    @Override
    public boolean isBlockPowered(FlowComponent component, int power)
    {
        MenuRedstoneStrength menuStrength = (MenuRedstoneStrength)component.getMenus().get(strengthId);
        boolean inRange = menuStrength.getLow() <= power && power <= menuStrength.getHigh();

        return inRange != menuStrength.isInverted();
    }

    public void onRedstoneTrigger(FlowComponent item, ITriggerNode inputTrigger)
    {
        MenuContainer componentMenuContainer = (MenuContainer)item.getMenus().get(containerId);
        List<SystemCoord> receivers = CommandBase.getContainers(item.getManager(), componentMenuContainer);

        if (receivers != null)
        {

            int[] newPower = new int[ForgeDirection.VALID_DIRECTIONS.length];
            int[] oldPower = new int[ForgeDirection.VALID_DIRECTIONS.length];
            if (canUseMergedDetection && componentMenuContainer.getOption() == 0)
            {
                for (SystemCoord receiver : receivers)
                {
                    ITriggerNode input = (ITriggerNode)receiver.getTileEntity();

                    for (int i = 0; i < newPower.length; i++)
                    {
                        newPower[i] = Math.min(15, newPower[i] + input.getData()[i]);
                        oldPower[i] = Math.min(15, oldPower[i] + input.getOldData()[i]);
                    }
                }
                if (isPulseReceived(item, newPower, oldPower, true))
                {
                    activateTrigger(item, EnumSet.of(ConnectionOption.REDSTONE_PULSE_HIGH));
                }
                if (isPulseReceived(item, newPower, oldPower, false))
                {
                    activateTrigger(item, EnumSet.of(ConnectionOption.REDSTONE_PULSE_LOW));
                }
            } else
            {
                ITriggerNode trigger = (componentMenuContainer.getOption() == 0 || (componentMenuContainer.getOption() == 1 && canUseMergedDetection)) ? inputTrigger : null;
                if (isPulseReceived(item, receivers, trigger, true))
                {
                    activateTrigger(item, EnumSet.of(ConnectionOption.REDSTONE_PULSE_HIGH));
                }

                if (isPulseReceived(item, receivers, trigger, false))
                {
                    activateTrigger(item, EnumSet.of(ConnectionOption.REDSTONE_PULSE_LOW));
                }
            }
        }
    }
}
