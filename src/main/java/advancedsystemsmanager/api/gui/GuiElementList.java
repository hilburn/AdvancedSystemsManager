package advancedsystemsmanager.api.gui;

import net.minecraft.client.gui.GuiScreen;

import java.util.*;

public class GuiElementList<Type extends IGuiElement<Gui>, Gui extends GuiScreen> extends ArrayList<Type> implements IGuiElement<Gui>
{

    @Override
    public void draw(Gui gui, int mouseX, int mouseY, int zLevel)
    {
        for (Type type : this)
        {
            type.draw(gui, mouseX, mouseY, zLevel);
        }
    }

    @Override
    public void drawMouseOver(Gui gui, int mouseX, int mouseY)
    {
        for (Type type : this)
        {
            type.drawMouseOver(gui, mouseX, mouseY);
        }
    }

    @Override
    public boolean onKeyStroke(Gui gui, char character, int key)
    {
        for (Type type : this)
        {
            if (type.onKeyStroke(gui, character, key)) return true;
        }
        return false;
    }

    @Override
    public void onClick(int mouseX, int mouseY, int button)
    {
        for (Type type : this)
        {
            type.onClick(mouseX, mouseY, button);
        }
    }

    @Override
    public boolean isVisible()
    {
        return true;
    }
}
