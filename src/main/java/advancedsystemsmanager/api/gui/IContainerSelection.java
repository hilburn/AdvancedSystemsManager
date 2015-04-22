package advancedsystemsmanager.api.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;

public interface IContainerSelection<Gui extends GuiScreen>
{
    int getId();

    @SideOnly(Side.CLIENT)
    void draw(Gui gui, int x, int y);

    @SideOnly(Side.CLIENT)
    String getDescription(Gui gui);

    @SideOnly(Side.CLIENT)
    String getName(Gui gui);

    boolean isVariable(); //fast access
}
