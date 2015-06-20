package advancedsystemsmanager.flow.execution;

import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.execution.commands.CommandBase;
import advancedsystemsmanager.flow.menus.MenuContainer;
import advancedsystemsmanager.flow.menus.MenuUpdateBlock;
import advancedsystemsmanager.registry.ConnectionOption;
import advancedsystemsmanager.registry.SystemTypeRegistry;
import advancedsystemsmanager.tileentities.TileEntityBUD;
import advancedsystemsmanager.util.SystemCoord;

import java.util.EnumSet;
import java.util.List;

public class TriggerHelperBUD extends TriggerHelper
{
    public static final int TRIGGER_BUD_BLOCK_ID = 5;

    public TriggerHelperBUD()
    {
        super(false, 1, 3, SystemTypeRegistry.BUD);
    }

    @Override
    public void onTrigger(FlowComponent item, EnumSet<ConnectionOption> valid)
    {
        List<SlotInventoryHolder> buds = CommandExecutor.getContainers(item.getManager(), item.getMenus().get(containerId), blockType);

        if (buds != null)
        {
            for (SlotInventoryHolder bud : buds)
            {
                bud.getBUD().updateData();
            }

            if (isSpecialPulseReceived(item, true))
            {
                valid.add(ConnectionOption.BUD_PULSE_HIGH);
                valid.add(ConnectionOption.BUD_HIGH);
            } else if (isTriggerPowered(item, true))
            {
                valid.add(ConnectionOption.BUD_HIGH);
            }

            if (isSpecialPulseReceived(item, false))
            {
                valid.add(ConnectionOption.BUD_PULSE_LOW);
                valid.add(ConnectionOption.BUD_LOW);
            } else if (isTriggerPowered(item, false))
            {
                valid.add(ConnectionOption.BUD_LOW);
            }


            for (SlotInventoryHolder bud : buds)
            {
                bud.getBUD().makeOld();
            }
        }

    }

    @Override
    public boolean isBlockPowered(FlowComponent component, int power)
    {
        int id = power >>> 4;
        int meta = power & 15;


        MenuUpdateBlock updateMenu = (MenuUpdateBlock)component.getMenus().get(TRIGGER_BUD_BLOCK_ID);

        if (updateMenu.useId())
        {
            boolean idMatch = id == updateMenu.getBlockId();

            if (updateMenu.isIdInverted() == idMatch)
            {
                return false;
            }
        }

        for (MenuUpdateBlock.MetaSetting setting : updateMenu.getMetaSettings())
        {
            if (setting.inUse())
            {
                int count = 0;
                int settingMeta = 0;
                for (int i = 0; i < setting.bits.length; i++)
                {
                    if (setting.bits[i])
                    {
                        settingMeta |= ((meta >> i) & 1) << count;
                        count++;
                    }
                }
                boolean metaMatch = setting.lowerTextBox.getNumber() <= settingMeta && settingMeta <= setting.higherTextBox.getNumber();
                if (setting.inverted == metaMatch)
                {
                    return false;
                }
            }
        }

        return true;
    }

    public void triggerBUD(FlowComponent item, TileEntityBUD tileEntityBUD)
    {
        MenuContainer componentMenuContainer = (MenuContainer)item.getMenus().get(containerId);
        List<SystemCoord> receivers = CommandBase.getContainers(item.getManager(), componentMenuContainer);

        if (receivers != null)
        {
            TileEntityBUD trigger = componentMenuContainer.getOption() == 0 ? tileEntityBUD : null;
            EnumSet<ConnectionOption> valid = EnumSet.noneOf(ConnectionOption.class);
            if (isTriggerPowered(item, true))
            {
                valid.add(ConnectionOption.BUD);

                if (isPulseReceived(item, receivers, trigger, true))
                {
                    valid.add(ConnectionOption.BUD_PULSE_HIGH);
                }
            } else if (isPulseReceived(item, receivers, trigger, false))
            {
                valid.add(ConnectionOption.BUD_PULSE_LOW);
            }

            if (!valid.isEmpty())
            {
                activateTrigger(item, valid);
            }
        }
    }
}
