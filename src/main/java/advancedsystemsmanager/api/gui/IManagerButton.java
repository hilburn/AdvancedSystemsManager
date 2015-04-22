package advancedsystemsmanager.api.gui;

import advancedsystemsmanager.network.DataReader;
import advancedsystemsmanager.network.DataWriter;
import net.minecraft.util.ResourceLocation;

public interface IManagerButton
{
    public static final int BUTTON_SIZE = 14;
    public static final int BUTTON_ICON_SIZE = 12;

    void onClick(DataReader dr);

    boolean onClick(DataWriter dw);

    int getX();

    int getY();

    String getMouseOver();

    boolean activateOnRelease();

    boolean isVisible();

    ResourceLocation getTexture();
}
