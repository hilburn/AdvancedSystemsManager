package advancedsystemsmanager.compatibility.thaumcraft;

import advancedsystemsmanager.client.gui.GuiBase;
import advancedsystemsmanager.client.gui.GuiManager;
import advancedsystemsmanager.compatibility.CompatBase;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.aspects.Aspect;

import java.awt.*;

public class TCCompat extends CompatBase
{


    @Override
    protected void init()
    {

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
