package advancedsystemsmanager.flow;

import advancedsystemsmanager.api.execution.IComponentType;
import advancedsystemsmanager.api.gui.IManagerButton;
import advancedsystemsmanager.flow.menus.Menu;
import advancedsystemsmanager.helpers.Localization;
import advancedsystemsmanager.reference.Textures;
import advancedsystemsmanager.registry.ConnectionSet;
import net.minecraft.util.ResourceLocation;

public class ComponentType implements IComponentType
{
    protected static final int BUTTON_SHEET_SIZE = 20;
    public Class<? extends Menu>[] classes;
    public int id;
    public ConnectionSet[] sets;
    public Localization name;
    public Localization longName;

    public ComponentType(int id, Localization name, Localization longName, ConnectionSet[] sets, Class<? extends Menu>... classes)
    {
        this.classes = classes;
        this.sets = sets;
        this.name = name;
        this.longName = longName;
        this.id = id;
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
