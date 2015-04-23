package advancedsystemsmanager.flow;

import advancedsystemsmanager.api.execution.ICommand;
import advancedsystemsmanager.api.gui.IManagerButton;
import advancedsystemsmanager.flow.menus.Menu;
import advancedsystemsmanager.helpers.Localization;
import advancedsystemsmanager.reference.Textures;
import advancedsystemsmanager.registry.ConnectionSet;
import net.minecraft.util.ResourceLocation;

public class Command implements ICommand
{
    protected static final int BUTTON_SHEET_SIZE = 20;
    public Class<? extends Menu>[] classes;
    public int id;
    public ConnectionSet[] sets;
    public Localization name;
    public Localization longName;
    private CommandType commandType;

    public Command(int id, CommandType commandType, Localization name, Localization longName, ConnectionSet[] sets, Class<? extends Menu>... classes)
    {
        this.classes = classes;
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
    public Class<? extends Menu>[] getClasses()
    {
        return classes;
    }

    @Override
    public ConnectionSet[] getSets()
    {
        return sets;
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
}
