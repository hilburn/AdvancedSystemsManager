package advancedsystemsmanager.tileentities.manager;

import advancedsystemsmanager.api.gui.IManagerButton;
import advancedsystemsmanager.helpers.Localization;
import advancedsystemsmanager.reference.Textures;
import net.minecraft.util.ResourceLocation;

public abstract class ManagerButton implements IManagerButton
{
    protected TileEntityManager manager;
    protected String hoverText;
    protected int x, y;

    public ManagerButton(TileEntityManager manager, String hover, int x, int y)
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
        return hoverText;
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
