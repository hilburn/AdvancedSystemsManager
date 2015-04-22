package advancedsystemsmanager.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Mouse;

public class GuiTextEntry extends Gui
{
    public String string;
    public int height, width;
    private FontRenderer fontRenderer;
    public int x, y;
    boolean isSelected;
    public boolean isEditing;
    public boolean isVisible;

    public GuiTextEntry(String string, int height, int width)
    {
        this.string = string;
        this.height = height;
        this.width = width;
        this.x = GuiLabeler.SCROLL_X;
        this.fontRenderer = Minecraft.getMinecraft().fontRenderer;
    }

    private boolean isVisible()
    {
        return isVisible;
    }

    public void draw()
    {
        if (isVisible())
        {
            drawBackground();
            drawText();
        }
    }

    public void setY(int y)
    {
        this.y = y;
    }

    public void handleMouseInput(int mouseX, int mouseY)
    {
        if (this.isVisible())
        {
            if (Mouse.getEventButtonState())
            {
                onMouseClick(mouseX, mouseY);
            }
        }
    }

    private void onMouseClick(int mouseX, int mouseY)
    {
        boolean oldSelected = this.isSelected;
        this.isSelected = pointIntersects(mouseX, mouseY);
        this.isEditing = oldSelected && isSelected;
    }

    public boolean pointIntersects(int x, int y)
    {
        return !(x < this.x || x > this.x + width || y < this.y || y > this.y + height);
    }

    private void drawBackground()
    {
        drawRect(this.x, this.y, this.x + width, this.y + height, 0xFF787878);
        drawRect(this.x + 1, this.y + 1, this.x + width - 1, this.y + height - 1, isSelected ? 0xffb5b4b4 : 0xff9c9c9c);
    }

    public String getText()
    {
        return string;
    }

    public void drawText()
    {
        fontRenderer.drawString(getText(), this.x + 2, this.y + height / 2 - 4, isEditing ? 0x207020 : 0x000000);
    }

    public void setText(String text)
    {
        this.string = text;
    }
}
