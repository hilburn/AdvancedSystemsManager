package advancedsystemsmanager.api.gui;

import advancedsystemsmanager.api.network.IPacketReader;
import advancedsystemsmanager.api.network.IPacketWriter;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.ResourceLocation;

public interface IManagerButton extends IPacketWriter, IPacketReader
{
    public static final int BUTTON_SIZE = 14;
    public static final int BUTTON_ICON_SIZE = 12;

    boolean validClick();

    @SideOnly(Side.CLIENT)
    int getX();

    @SideOnly(Side.CLIENT)
    int getY();

    @SideOnly(Side.CLIENT)
    String getMouseOver();

    boolean activateOnRelease();

    boolean isVisible();

    @SideOnly(Side.CLIENT)
    ResourceLocation getTexture();
}
