package advancedsystemsmanager.tileentities.manager;

import advancedsystemsmanager.api.gui.IManagerButton;
import advancedsystemsmanager.reference.Textures;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

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
    public String getMouseOver()
    {
        return StatCollector.translateToLocal(hoverText);
    }

    @Override
    public boolean activateOnRelease()
    {
        return false;
    }

    @Override
    public boolean isVisible()
    {
        return true;
    }

    @Override
    public ResourceLocation getTexture()
    {
        return Textures.BUTTONS;
    }
}
