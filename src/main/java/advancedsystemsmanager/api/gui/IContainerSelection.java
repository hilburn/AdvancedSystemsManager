package advancedsystemsmanager.api.gui;

import advancedsystemsmanager.client.gui.GuiManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;

public interface IContainerSelection
{
    long getId();

    @SideOnly(Side.CLIENT)
    void draw(GuiManager gui, int x, int y);

    @SideOnly(Side.CLIENT)
    String getDescription(GuiManager gui);

    @SideOnly(Side.CLIENT)
    String getName(GuiManager gui);

    boolean isVariable(); //fast access
}
