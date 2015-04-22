package advancedsystemsmanager.flow.execution;


import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.registry.ConnectionOption;
import advancedsystemsmanager.flow.menus.MenuContainer;
import advancedsystemsmanager.flow.menus.MenuRedstoneStrength;
import advancedsystemsmanager.tileentities.TileEntityInput;
import advancedsystemsmanager.util.ConnectionBlockType;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.EnumSet;
import java.util.List;

public class TriggerHelperRedstone extends TriggerHelper
{
    public int strengthId;

    public TriggerHelperRedstone(int sidesId, int strengthId)
    {
        super(true, 0, sidesId, ConnectionBlockType.RECEIVER);

        this.strengthId = strengthId;
    }

    @Override
    public boolean isBlockPowered(FlowComponent component, int power)
    {
        MenuRedstoneStrength menuStrength = (MenuRedstoneStrength)component.getMenus().get(strengthId);
        boolean inRange = menuStrength.getLow() <= power && power <= menuStrength.getHigh();

        return inRange != menuStrength.isInverted();
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

    public void onRedstoneTrigger(FlowComponent item, TileEntityInput inputTrigger)
    {
        List<SlotInventoryHolder> receivers = CommandExecutor.getContainers(item.getManager(), item.getMenus().get(containerId), blockType);

        if (receivers != null)
        {
            MenuContainer componentMenuContainer = (MenuContainer)item.getMenus().get(containerId);
            int[] newPower = new int[ForgeDirection.VALID_DIRECTIONS.length];
            int[] oldPower = new int[ForgeDirection.VALID_DIRECTIONS.length];
            if (canUseMergedDetection && componentMenuContainer.getOption() == 0)
            {
                for (SlotInventoryHolder receiver : receivers)
                {
                    TileEntityInput input = receiver.getReceiver();

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
                TileEntityInput trigger = (componentMenuContainer.getOption() == 0 || (componentMenuContainer.getOption() == 1 && canUseMergedDetection)) ? inputTrigger : null;
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
