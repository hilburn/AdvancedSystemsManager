package advancedsystemsmanager.api.execution;

import advancedsystemsmanager.flow.Connection;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.menus.Menu;
import advancedsystemsmanager.registry.ConnectionSet;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public interface ICommand
{
    int getId();

    public ConnectionSet[] getSets();

    List<Menu> getMenus(FlowComponent component);

    public String getName();

    public String getLongName();

    public CommandType getCommandType();

    int getX();

    int getY();

    @SideOnly(Side.CLIENT)
    ResourceLocation getTexture();

    void execute(FlowComponent command, int connectionId, IBufferProvider bufferProvider);

    List<Connection> getActiveChildren(FlowComponent command);

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
