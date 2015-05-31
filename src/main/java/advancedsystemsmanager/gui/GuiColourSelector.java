package advancedsystemsmanager.gui;

import advancedsystemsmanager.api.gui.IDraggable;
import advancedsystemsmanager.api.gui.IGuiElement;
import advancedsystemsmanager.helpers.CollisionHelper;
import advancedsystemsmanager.util.ColourUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class GuiColourSelector implements IGuiElement<GuiBase>, IDraggable
{
    private static final ResourceLocation TEXTURE = GuiBase.registerTexture("FlowComponents");

    private static final int GRAD_WIDTH = 128;
    private static final int GRAD_HEIGHT = 128;
    private static final int HUE_X = 135;

    private static final int HUE_WIDTH = 10;
    private static final int HEIGHT = 128;
    private static final int HUE_SRC_X = 188;
    private static final int HUE_SRC_Y = 0;
    private static final int DRAG_SRC_X = 0;
    private static final int SV_DRAG_SRC_X = 20;
    private static final int DRAG_SRC_Y = 251;
    private static final int DRAG_WIDTH = 20;
    private static final int DRAG_X = HUE_X - (DRAG_WIDTH- HUE_WIDTH)/2;
    private static final int DRAG_HEIGHT = 5;
    private static final int SV_DRAG_WIDTH = 5;
    private static final int SV_DRAG_HEIGHT = 5;

    private static final int[] WHITE = new int[]{255, 255, 255};
    private static final int[] BLACK = new int[]{0, 0, 0};
    private static final int[][] HUE_SCALE = new int[][]{{255, 0, 0}, {255, 255, 0} ,{0, 255, 0}, {0, 255, 255}, {0, 0, 255}, {255, 0, 255}, {255, 0, 0}};


    private int x, y, saturation, value, hue;
    private boolean isDragging, moveHue, clicked, scrollHue;
    private int[] colour = new int[3];
    public int[] hueValue, oldColour;

    public GuiColourSelector(int x, int y)
    {
        this(x, y, Minecraft.getMinecraft().theWorld.rand.nextInt(0xFFFFFF));
    }

    public GuiColourSelector(int x, int y, int colour)
    {
        this.x = x;
        this.y = y;
        setColour(colour);
        float[] hsv = new float[3];
        ColourUtils.RGBtoHSV(colour, hsv);
        setByHue(hsv);
    }
    
    private void setByHue(float[] hsv)
    {
        setHue((int)(HEIGHT * hsv[0]));
        saturation = (int)(GRAD_WIDTH * hsv[1]);
        value = (int)(GRAD_WIDTH * hsv[2]);
    }

    private void setHue(int val)
    {
        hue = Math.max(Math.min(val, HEIGHT), 0);
        hueValue = ColourUtils.HSBtoRGB(1F - (float)hue / HEIGHT, 1F, 1F);
    }

    public void setColour(int colour)
    {
        this.colour[0] = (byte)(colour >> 16);
        this.colour[1] = (byte)(colour >> 8);
        this.colour[2] = (byte)(colour);
    }

    @Override
    public void drag(int mouseX, int mouseY)
    {
        if (isDragging)
        {
            clicked = false;
            setPosition(mouseX, mouseY);
        }
    }

    @Override
    public void release(int mouseX, int mouseY)
    {
        isDragging = false;
        moveHue = false;
        clicked = false;
    }

    private void setPosition(int x, int y)
    {
        if (moveHue)
        {
            setHue(y - this.y);
        } else
        {
            saturation = Math.max(Math.min(x - this.x, HEIGHT), 0);
            value = Math.max(Math.min(y - this.y, HEIGHT), 0);
        }
        setColour();
    }

    private void setColour()
    {
        colour = ColourUtils.HSBtoRGB(1F - (float)hue / HEIGHT, (float)saturation / HEIGHT, 1F - (float)value / HEIGHT);
    }

    @Override
    public void draw(GuiBase guiBase, int mouseX, int mouseY, int zLevel)
    {
        GuiBase.bindTexture(TEXTURE);
//        guiBase.drawTexture(x + HUE_X, y, HUE_SRC_X, HUE_SRC_Y, HUE_WIDTH, HEIGHT);
        guiBase.drawRainbowRectangle(x + HUE_X, y, HUE_WIDTH, HEIGHT, HUE_SCALE);
        guiBase.drawTexture(x + DRAG_X, y + hue - 2, DRAG_SRC_X, DRAG_SRC_Y, DRAG_WIDTH, DRAG_HEIGHT);
        guiBase.drawGradientRectangle(x, y, x + HEIGHT, x + HEIGHT, WHITE, hueValue, BLACK, BLACK);
        guiBase.drawTexture(x + saturation - 2, y + value - 2, SV_DRAG_SRC_X, DRAG_SRC_Y, SV_DRAG_WIDTH, SV_DRAG_HEIGHT);
        if (isDragging && !clicked)
        {
            guiBase.drawRectangle(x + HUE_X + 20, y, x + HUE_X + 35, y + 15, oldColour);
            guiBase.drawRectangle(x + HUE_X + 35, y, x + HUE_X + 50, y + 15, colour);
        }else
        {
            guiBase.drawRectangle(x + HUE_X + 20, y, x + HUE_X + 50, y + 15, colour);
        }
    }

    public void onScroll(int scroll)
    {
        if (scrollHue)
        {
            setHue(hue - scroll / 20);
            setColour();
        }
    }

    @Override
    public void drawMouseOver(GuiBase guiBase, int mouseX, int mouseY)
    {
        scrollHue = CollisionHelper.inBounds(x + HUE_X, y, HUE_WIDTH, HEIGHT, mouseX, mouseY) || CollisionHelper.inBounds(x, y, GRAD_WIDTH, GRAD_HEIGHT, mouseX, mouseY);
    }

    @Override
    public boolean onKeyStroke(GuiBase guiBase, char character, int key)
    {
        return false;
    }

    @Override
    public boolean onClick(int mouseX, int mouseY, int button)
    {
        if (CollisionHelper.inBounds(x + HUE_X, y, HUE_WIDTH, HEIGHT, mouseX, mouseY) || CollisionHelper.inBounds(x + DRAG_X, y + hue - 3, DRAG_WIDTH, DRAG_HEIGHT, mouseX, mouseY))
        {
            clicked = true;
            oldColour = colour;
            moveHue = true;
            setPosition(mouseX, mouseY);
            isDragging = true;
            return true;
        } else if (CollisionHelper.inBounds(x, y, GRAD_WIDTH, GRAD_HEIGHT, mouseX, mouseY))
        {
            clicked = true;
            oldColour = colour;
            moveHue = false;
            setPosition(mouseX, mouseY);
            isDragging = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean isVisible()
    {
        return true;
    }
}
