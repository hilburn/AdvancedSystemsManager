package advancedsystemsmanager.api.gui;

import advancedsystemsmanager.api.network.INetworkWriter;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.ResourceLocation;

public interface IManagerButton extends INetworkWriter
{
    public static final int BUTTON_SIZE = 14;
    public static final int BUTTON_ICON_SIZE = 12;

    void onClickReader(ByteBuf buf);

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
