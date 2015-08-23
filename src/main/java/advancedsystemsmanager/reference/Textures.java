package advancedsystemsmanager.reference;

import advancedsystemsmanager.client.gui.GuiBase;
import advancedsystemsmanager.helpers.FileHelper;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Textures
{
    public static final String TEXTURES = "textures/";
    public static final String GUI = TEXTURES + "gui/";

    public static final ResourceLocation BUTTONS = GuiBase.registerTexture("components");

    public static final ResourceLocation CURSORS = GuiBase.registerTexture("cursors");
}
