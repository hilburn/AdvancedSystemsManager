package advancedfactorymanager.api;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import advancedfactorymanager.interfaces.GuiManager;

public interface IContainerSelection {
    int getId();
    @SideOnly(Side.CLIENT)
    void draw(GuiManager gui, int x, int y);
    @SideOnly(Side.CLIENT)
    String getDescription(GuiManager gui);
    @SideOnly(Side.CLIENT)
    String getName(GuiManager gui);
    boolean isVariable(); //fast access
}
