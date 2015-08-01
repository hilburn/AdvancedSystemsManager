package advancedsystemsmanager.client.gui;

import advancedsystemsmanager.containers.ContainerBase;
import net.minecraft.util.ResourceLocation;

public class GuiVoid extends GuiBase
{
    public static final ResourceLocation TEXTURE = registerTexture("void");

    public GuiVoid(ContainerBase container)
    {
        super(container);
    }

    @Override
    public ResourceLocation getComponentResource()
    {
        return TEXTURE;
    }

    @Override
    protected void drawGuiBackground(float partialTick, int mX, int mY)
    {
        bindTexture(TEXTURE);
        drawTexture(0, 0, 0, 0, xSize, ySize);
    }
}
