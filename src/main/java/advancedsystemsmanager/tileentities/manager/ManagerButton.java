package advancedsystemsmanager.tileentities.manager;

import advancedsystemsmanager.api.gui.IManagerButton;
import advancedsystemsmanager.helpers.Localization;
import advancedsystemsmanager.reference.Textures;
import net.minecraft.util.ResourceLocation;

public abstract class ManagerButton implements IManagerButton
{
    private static final int BUTTON_SHEET_SIZE = 4;
    private static int INDEX = 0;
    protected TileEntityManager manager;
    protected Localization hoverText;
    protected int x, y;

    public ManagerButton(TileEntityManager manager, Localization hover)
    {
        this(manager, hover, INDEX++);
    }

    public ManagerButton(TileEntityManager manager, Localization hover, int index)
    {
        this(manager, hover, (index / BUTTON_SHEET_SIZE) * BUTTON_ICON_SIZE, (index % BUTTON_SHEET_SIZE) * BUTTON_ICON_SIZE);
    }

    public ManagerButton(TileEntityManager manager, Localization hover, int x, int y)
    {
        this.manager = manager;
        this.hoverText = hover;
        this.x = x;
        this.y = y;
    }

    @Override
    public int getX()
    {
        return x;
    }

    @Override
    public int getY()
    {
        return y;
    }

    @Override
    public boolean activateOnRelease()
    {
        return false;
    }

    @Override
    public String getMouseOver()
    {
        return hoverText.toString();
    }

    @Override
    public ResourceLocation getTexture()
    {
        return Textures.BUTTONS;
    }

    @Override
    public boolean isVisible()
    {
        return true;
    }
}
