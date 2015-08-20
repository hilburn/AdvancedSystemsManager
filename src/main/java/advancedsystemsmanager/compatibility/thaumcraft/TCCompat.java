package advancedsystemsmanager.compatibility.thaumcraft;

import advancedsystemsmanager.api.ISystemType;
import advancedsystemsmanager.client.gui.GuiBase;
import advancedsystemsmanager.client.gui.GuiManager;
import advancedsystemsmanager.compatibility.CompatBase;
import advancedsystemsmanager.compatibility.ModCompat;
import advancedsystemsmanager.compatibility.thaumcraft.commands.CommandAspectInput;
import advancedsystemsmanager.compatibility.thaumcraft.commands.CommandAspectOutput;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.registry.CommandRegistry;
import advancedsystemsmanager.registry.SystemTypeRegistry;
import advancedsystemsmanager.tileentities.manager.TileEntityManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IAspectContainer;

import java.awt.*;

public class TCCompat extends CompatBase
{
    public static ISystemType ASPECT_CONTAINER;

    @Override
    protected void init()
    {
        ASPECT_CONTAINER = SystemTypeRegistry.register(new SystemTypeRegistry.SystemType<IAspectContainer>(Names.TYPE_ASPECT, false)
        {
            @Override
            public boolean isInstance(TileEntityManager manager, TileEntity tileEntity)
            {
                return tileEntity instanceof IAspectContainer;
            }
        });
        CommandRegistry.registerCommand(new CommandAspectInput());
        CommandRegistry.registerCommand(new CommandAspectOutput());
    }

    @Override
    protected void postInit()
    {
        ModCompat.registerLabel(IAspectContainer.class);
    }

    public static void drawAspect(GuiManager gui, Aspect aspect, int x, int y)
    {
        GL11.glPushMatrix();
        GL11.glAlphaFunc(GL11.GL_GREATER, 1 / 255F);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glPushMatrix();

        GuiBase.bindTexture(aspect.getImage());
        Color color = new Color(aspect.getColor());
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA(color.getRed(), color.getGreen(), color.getBlue(), 230);
        tessellator.addVertexWithUV(x + 1, y + 15, gui.getZLevel(), 0, 1);
        tessellator.addVertexWithUV(x + 15, y + 15, gui.getZLevel(), 1, 1);
        tessellator.addVertexWithUV(x + 15, y + 1, gui.getZLevel(), 1, 0);
        tessellator.addVertexWithUV(x + 1, y + 1, gui.getZLevel(), 0, 0);
        tessellator.draw();
        GuiBase.bindTexture(gui.getComponentResource());
        GL11.glPopMatrix();

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
        GL11.glPopMatrix();
    }
}
