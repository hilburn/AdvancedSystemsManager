package advancedsystemsmanager.api.gui;

import advancedsystemsmanager.client.gui.GuiBase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public interface IContainerSelection
{
    long getId();

    @SideOnly(Side.CLIENT)
    void draw(GuiBase gui, int x, int y);

    @SideOnly(Side.CLIENT)
    String getDescription(GuiBase gui);

    @SideOnly(Side.CLIENT)
    String getName(GuiBase gui);

    boolean isVariable(); //fast access
}
