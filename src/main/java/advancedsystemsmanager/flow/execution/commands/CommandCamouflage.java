package advancedsystemsmanager.flow.execution.commands;

import advancedsystemsmanager.api.execution.IBufferProvider;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.menus.*;
import advancedsystemsmanager.flow.setting.ItemSetting;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.registry.ConnectionSet;
import advancedsystemsmanager.registry.SystemTypeRegistry;
import advancedsystemsmanager.tileentities.TileEntityCamouflage;
import advancedsystemsmanager.util.SystemCoord;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

public class CommandCamouflage extends CommandBase
{
    public CommandCamouflage()
    {
        super(15, Names.CAMOUFLAGE, CommandType.MISC, ConnectionSet.STANDARD);
    }

    @Override
    public void getMenus(FlowComponent component, List<Menu> menus)
    {
        menus.add(new MenuContainer(component, SystemTypeRegistry.CAMOUFLAGE));
        menus.add(new MenuCamouflageShape(component));
        menus.add(new MenuCamouflageInside(component));
        menus.add(new MenuCamouflageSides(component));
        menus.add(new MenuCamouflageItems(component));
    }

    @Override
    public void execute(FlowComponent command, int connectionId, IBufferProvider bufferProvider)
    {
        List<SystemCoord> camouflageBlocks = getContainers(command.getManager(), (MenuContainer)command.getMenus().get(0));
        if (camouflageBlocks != null)
        {
            MenuCamouflageShape shape = (MenuCamouflageShape)command.getMenus().get(1);
            MenuCamouflageInside inside = (MenuCamouflageInside)command.getMenus().get(2);
            MenuCamouflageSides sides = (MenuCamouflageSides)command.getMenus().get(3);
            MenuCamouflageItems items = (MenuCamouflageItems)command.getMenus().get(4);
            if (items.isFirstRadioButtonSelected() || items.getSettings().get(0).isValid())
            {
                ItemStack itemStack = items.isFirstRadioButtonSelected() ? null : ((ItemSetting)items.getSettings().get(0)).getItem();

                for (SystemCoord systemCoord : camouflageBlocks)
                {
                    TileEntityCamouflage camouflage = (TileEntityCamouflage)systemCoord.tileEntity;
                    camouflage.setBounds(shape);
                    for (int i = 0; i < ForgeDirection.VALID_DIRECTIONS.length; ++i)
                    {
                        if (sides.isSideRequired(i))
                        {
                            camouflage.setItem(itemStack, i, inside.getCurrentType());
                        }
                    }
                }
            }
        }
    }
}
