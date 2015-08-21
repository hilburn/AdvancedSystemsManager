package advancedsystemsmanager.api.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public interface IDraggable
{
    @SideOnly(Side.CLIENT)
    void drag(int mouseX, int mouseY);

    @SideOnly(Side.CLIENT)
    void release(int mouseX, int mouseY);
}
