package advancedsystemsmanager.api.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public interface IGuiListElement extends IGuiElement
{
    @SideOnly(Side.CLIENT)
    int getWidth();

    @SideOnly(Side.CLIENT)
    int getHeight();

    @SideOnly(Side.CLIENT)
    void setX(int x);

    @SideOnly(Side.CLIENT)
    int getX();

    @SideOnly(Side.CLIENT)
    void setY(int y);

    @SideOnly(Side.CLIENT)
    int getY();
}
