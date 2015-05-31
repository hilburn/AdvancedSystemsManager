package advancedsystemsmanager.gui;

import advancedsystemsmanager.api.gui.IDraggable;
import advancedsystemsmanager.api.gui.IGuiElement;
import advancedsystemsmanager.flow.elements.TextBoxNumber;
import advancedsystemsmanager.flow.elements.TextBoxNumberList;
import advancedsystemsmanager.helpers.CollisionHelper;
import advancedsystemsmanager.reference.Null;
import advancedsystemsmanager.util.ColourUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class GuiColourSelector implements IGuiElement<GuiBase>, IDraggable
{
    private static final ResourceLocation TEXTURE = GuiBase.registerTexture("FlowComponents");

    protected static final int GRAD_WIDTH = 128;
    protected static final int GRAD_HEIGHT = 128;
    protected static final int HUE_X = 135;

    protected static final int HUE_WIDTH = 10;
    protected static final int HEIGHT = 128;
    protected static final int SCALING = 100;
    protected static final int DRAG_SRC_X = 0;
    protected static final int SV_DRAG_SRC_X = 20;
    protected static final int DRAG_SRC_Y = 251;
    protected static final int DRAG_WIDTH = 20;
    protected static final int DRAG_X = HUE_X - (DRAG_WIDTH- HUE_WIDTH)/2;
    protected static final int DRAG_HEIGHT = 5;
    protected static final int SV_DRAG_WIDTH = 5;
    protected static final int SV_DRAG_HEIGHT = 5;
    protected static final int OUTPUT_X = HUE_X + 20;
    protected static final int OUTPUT_OLD_X = HUE_X + 35;
    protected static final int OUTPUT_X_END = HUE_X + 50;

    private static final int[] WHITE = new int[]{255, 255, 255};
    private static final int[] BLACK = new int[]{0, 0, 0};
    private static final int[][] HUE_SCALE = new int[][]{{255, 0, 0}, {255, 255, 0} ,{0, 255, 0}, {0, 255, 255}, {0, 0, 255}, {255, 0, 255}, {255, 0, 0}};

    protected int x;
    protected int y;
    private int saturation;
    private int value;
    private int hue;
    private boolean isDragging, moveHue, clicked, scrollHue, hasUpdated;
    private int[] colour = new int[3];
    private int[] hueValue, oldColour;
    private TextBoxNumberList textBoxes;

    public GuiColourSelector(int x, int y)
    {
        this(x, y, Minecraft.getMinecraft().theWorld.rand.nextInt(0xFFFFFF));
    }

    public GuiColourSelector(int x, int y, int rgb)
    {
        this.x = x;
        this.y = y;
        textBoxes = new TextBoxNumberList();
        textBoxes.addTextBox(new TextBoxNumber(Null.NULL_PACKET, x + OUTPUT_X, y + 45, 3, false)
        {
            @Override
            public void onUpdate()
            {
                setColour(number, colour[1], colour[2]);
            }

            @Override
            public int getMaxNumber()
            {
                return 255;
            }
        });
        textBoxes.addTextBox(new TextBoxNumber(Null.NULL_PACKET, x + OUTPUT_X, y + 60, 3, false)
        {
            @Override
            public void onUpdate()
            {
                setColour(colour[0], number, colour[2]);
            }

            @Override
            public int getMaxNumber()
            {
                return 255;
            }
        });
        textBoxes.addTextBox(new TextBoxNumber(Null.NULL_PACKET, x + OUTPUT_X, y + 75, 3, false)
        {
            @Override
            public void onUpdate()
            {
                setColour(colour[0], colour[1], number);
            }

            @Override
            public int getMaxNumber()
            {
                return 255;
            }
        });
        setRGB(rgb);
    }

    public boolean hasUpdated()
    {
        return hasUpdated;
    }

    public void setUpdated(boolean value)
    {
        hasUpdated = value;
    }

    public int getRGB()
    {
        return colour[0] << 16 | colour[1] << 8 | colour[2];
    }

    public void setRGB(int rgb)
    {
        setColour(rgb);
        float[] hsv = new float[3];
        ColourUtils.HextoHSV(rgb, hsv);
        setByHue(hsv);
        setColour();
    }

    public void setColour(int r, int g, int b)
    {
        float[] hsv = new float[3];
        ColourUtils.RGBtoHSV(r, g, b, hsv);
        setByHue(hsv);
        setColour();
    }

    private void setByHue(float[] hsv)
    {
        setHue((int)(HEIGHT * hsv[0]));
        saturation = (int)(GRAD_WIDTH * SCALING * hsv[1]);
        value = (int)(GRAD_WIDTH * SCALING * (1f - hsv[2]));
    }

    private void setHue(int val)
    {
        hue = Math.max(Math.min(val, HEIGHT), 0) * SCALING;
        hueValue = ColourUtils.HSBtoRGB((float)hue / (HEIGHT * SCALING), 1F, 1F);
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
            saturation = Math.max(Math.min(x - this.x, HEIGHT), 0) * SCALING;
            value = Math.max(Math.min(y - this.y, HEIGHT), 0) * SCALING;
        }
        setColour();
    }

    private void setColour()
    {
        colour = ColourUtils.HSBtoRGB((float)hue / (HEIGHT * SCALING), (float)saturation / (HEIGHT * SCALING), 1F - (float)value / (HEIGHT * SCALING));
        textBoxes.getTextBox(0).setNumber(colour[0]);
        textBoxes.getTextBox(1).setNumber(colour[1]);
        textBoxes.getTextBox(2).setNumber(colour[2]);
        hasUpdated = true;
    }

    @Override
    public void draw(GuiBase guiBase, int mouseX, int mouseY, int zLevel)
    {
        GuiBase.bindTexture(TEXTURE);
        guiBase.drawRainbowRectangle(x + HUE_X, y, HUE_WIDTH, HEIGHT, HUE_SCALE);
        guiBase.drawTexture(x + DRAG_X, y + hue / SCALING - 2, DRAG_SRC_X, DRAG_SRC_Y, DRAG_WIDTH, DRAG_HEIGHT);
        guiBase.drawGradientRectangle(x, y, x + HEIGHT, y + HEIGHT, WHITE, hueValue, BLACK, BLACK);
        guiBase.drawTexture(x + saturation / SCALING - 2, y + value / SCALING - 2, SV_DRAG_SRC_X, DRAG_SRC_Y, SV_DRAG_WIDTH, SV_DRAG_HEIGHT);
        textBoxes.draw(guiBase, mouseX, mouseY);
        drawColourOutput(guiBase);
    }

    protected void drawColourOutput(GuiBase guiBase)
    {
        if (isDragging && !clicked)
        {
            guiBase.drawRectangle(x + OUTPUT_X, y, x + OUTPUT_OLD_X, y + 15, oldColour);
            guiBase.drawRectangle(x + OUTPUT_OLD_X, y, x + OUTPUT_X_END, y + 15, colour);
        }else
        {
            guiBase.drawRectangle(x + OUTPUT_X, y, x + OUTPUT_X_END, y + 15, colour);
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
        return textBoxes.onKeyStroke(guiBase, character, key);
    }

    @Override
    public boolean onClick(int mouseX, int mouseY, int button)
    {
        if (CollisionHelper.inBounds(x + HUE_X, y, HUE_WIDTH, HEIGHT, mouseX, mouseY) || CollisionHelper.inBounds(x + DRAG_X, y + hue - 3, DRAG_WIDTH, DRAG_HEIGHT, mouseX, mouseY))
        {
            return startDragging(mouseX, mouseY, true);
        } else if (CollisionHelper.inBounds(x, y, GRAD_WIDTH, GRAD_HEIGHT, mouseX, mouseY))
        {
            return startDragging(mouseX, mouseY, false);
        }
        textBoxes.onClick(mouseX, mouseY, button);
        return false;
    }

    private boolean startDragging(int mouseX, int mouseY, boolean hue)
    {
        clicked = true;
        oldColour = colour;
        moveHue = hue;
        setPosition(mouseX, mouseY);
        isDragging = true;
        return true;
    }

    @Override
    public boolean isVisible()
    {
        return true;
    }

    public int[] getColour()
    {
        return colour;
    }
}
