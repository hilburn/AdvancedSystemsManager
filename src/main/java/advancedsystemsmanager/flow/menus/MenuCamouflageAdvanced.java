package advancedsystemsmanager.flow.menus;


import advancedsystemsmanager.client.gui.GuiBase;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.helpers.CollisionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class MenuCamouflageAdvanced extends Menu
{
    public static final int ERROR_X = 115;
    public static final int ERROR_Y = 2;
    public static final int ERROR_SIZE_W = 2;
    public static final int ERROR_SIZE_H = 10;
    public static final int ERROR_SRC_X = 44;
    public static final int ERROR_SRC_Y = 212;

    public MenuCamouflageAdvanced(FlowComponent parent)
    {
        super(parent);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void draw(GuiBase gui, int mX, int mY)
    {
        int srcY = CollisionHelper.inBounds(ERROR_X, ERROR_Y, ERROR_SIZE_W, ERROR_SIZE_H, mX, mY) ? 1 : 0;
        gui.drawTexture(ERROR_X, ERROR_Y, ERROR_SRC_X, ERROR_SRC_Y + srcY * ERROR_SIZE_H, ERROR_SIZE_W, ERROR_SIZE_H);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean drawMouseOver(GuiBase gui, int mX, int mY)
    {
        if (CollisionHelper.inBounds(ERROR_X, ERROR_Y, ERROR_SIZE_W, ERROR_SIZE_H, mX, mY))
        {
            gui.drawMouseOver(getWarningText(), mX, mY, 200);
            return true;
        }
        return false;
    }

    public abstract String getWarningText();
}
