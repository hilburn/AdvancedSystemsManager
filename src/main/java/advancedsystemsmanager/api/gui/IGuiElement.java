package advancedsystemsmanager.api.gui;

import advancedsystemsmanager.client.gui.GuiBase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public interface IGuiElement
{
    @SideOnly(Side.CLIENT)
    void draw(GuiBase gui, int mouseX, int mouseY, int zLevel);

    @SideOnly(Side.CLIENT)
    boolean drawMouseOver(GuiBase gui, int mouseX, int mouseY);

    @SideOnly(Side.CLIENT)
    boolean onKeyStroke(GuiBase gui, char character, int key);

    @SideOnly(Side.CLIENT)
    boolean onClick(int mouseX, int mouseY, int button);

    @SideOnly(Side.CLIENT)
    boolean isVisible();
}
