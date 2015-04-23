package advancedsystemsmanager.api.execution;

import advancedsystemsmanager.flow.menus.Menu;
import advancedsystemsmanager.registry.ConnectionSet;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.ResourceLocation;

public interface ICommand
{
    int getId();

    public ConnectionSet[] getSets();

    Class<? extends Menu>[] getClasses();

    public String getName();

    public String getLongName();

    public CommandType getCommandType();

    int getX();

    int getY();

    @SideOnly(Side.CLIENT)
    ResourceLocation getTexture();

    public static enum CommandType
    {
        TRIGGER,
        INPUT,
        OUTPUT,
        COMMAND_CONTROL,
        CRAFTING,
        MISC
    }
}
