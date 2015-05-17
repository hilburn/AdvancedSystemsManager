package advancedsystemsmanager.gui;

import net.minecraft.util.ResourceLocation;

public class GuiVoid extends GuiBase
{
    public static final ResourceLocation texture = registerTexture("void");

    public GuiVoid(ContainerBase container)
    {
        super(container);
    }

    @Override
    public ResourceLocation getComponentResource()
    {
        return texture;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTick, int mX, int mY)
    {

    }
}
