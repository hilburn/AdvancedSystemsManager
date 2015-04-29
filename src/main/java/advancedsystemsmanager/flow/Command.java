package advancedsystemsmanager.flow;

import advancedsystemsmanager.api.execution.IBufferProvider;
import advancedsystemsmanager.api.execution.ICommand;
import advancedsystemsmanager.api.gui.IManagerButton;
import advancedsystemsmanager.flow.menus.Menu;
import advancedsystemsmanager.reference.Textures;
import advancedsystemsmanager.registry.ConnectionSet;
import net.minecraft.util.ResourceLocation;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.List;

public class Command implements ICommand
{
    protected static final int BUTTON_SHEET_SIZE = 20;
    public Constructor<? extends Menu>[] constructors;
    public int id;
    public ConnectionSet[] sets;
    public String name;
    public String longName;
    private CommandType commandType;

    public Command(int id, CommandType commandType, String name, String longName, ConnectionSet[] sets, Class<? extends Menu>... classes)
    {
        constructors = (Constructor<? extends Menu>[])Array.newInstance(Constructor.class, classes.length);
        int i = 0;
        for (Class<? extends Menu> menu : classes)
        {
            try
            {
                constructors[i++] = menu.getConstructor(FlowComponent.class);
            } catch (NoSuchMethodException ignored)
            {
            }
        }
        this.sets = sets;
        this.name = name;
        this.longName = longName;
        this.id = id;
        this.commandType = commandType;
    }

    @Override
    public int getId()
    {
        return id;
    }

    @Override
    public ConnectionSet[] getSets()
    {
        return sets;
    }

    @Override
    public void getMenus(FlowComponent component, List<Menu> menus)
    {
        for (Constructor<? extends Menu> constructor : constructors)
        {
            try
            {
                menus.add(constructor.newInstance(component));
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getName()
    {
        return name.toString();
    }

    @Override
    public String getLongName()
    {
        return longName.toString();
    }

    @Override
    public CommandType getCommandType()
    {
        return commandType;
    }

    @Override
    public int getX()
    {
        return 230 - (getId() / BUTTON_SHEET_SIZE) * IManagerButton.BUTTON_ICON_SIZE;
    }

    @Override
    public int getY()
    {
        return (getId() % BUTTON_SHEET_SIZE) * IManagerButton.BUTTON_ICON_SIZE;
    }

    @Override
    public ResourceLocation getTexture()
    {
        return Textures.BUTTONS;
    }

    @Override
    public void execute(FlowComponent command, int connectionId, IBufferProvider bufferProvider)
    {

    }

    @Override
    public List<Connection> getActiveChildren(FlowComponent command)
    {
        return null;
    }
}
