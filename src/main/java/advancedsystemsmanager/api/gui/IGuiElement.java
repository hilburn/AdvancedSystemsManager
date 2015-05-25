package advancedsystemsmanager.api.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;

public interface IGuiElement<Gui extends GuiScreen>
{
    @SideOnly(Side.CLIENT)
    void draw(Gui gui, int mouseX, int mouseY, int zLevel);

    @SideOnly(Side.CLIENT)
    void drawMouseOver(Gui gui, int mouseX, int mouseY);

    @SideOnly(Side.CLIENT)
    boolean onKeyStroke(Gui gui, char character, int key);

    @SideOnly(Side.CLIENT)
    boolean onClick(int mouseX, int mouseY, int button);

    @SideOnly(Side.CLIENT)
    boolean isVisible();
}
