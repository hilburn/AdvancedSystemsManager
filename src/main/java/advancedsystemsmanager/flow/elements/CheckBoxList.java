package advancedsystemsmanager.flow.elements;

import advancedsystemsmanager.client.gui.GuiBase;
import advancedsystemsmanager.helpers.CollisionHelper;
import advancedsystemsmanager.registry.ThemeHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

public class CheckBoxList
{
    public static final int CHECK_BOX_SIZE = 8;
    public static final int CHECK_BOX_SRC_X = 106;
    public static final int CHECK_BOX_SELECTED_SRC_X = 114;
    public static final int CHECK_BOX_SRC_Y = 20;
    public static final int CHECK_BOX_TEXT_X = 12;
    public static final int CHECK_BOX_TEXT_Y = 2;

    public List<CheckBox> checkBoxes;

    public CheckBoxList()
    {
        checkBoxes = new ArrayList<CheckBox>();
    }

    public void addCheckBox(CheckBox checkBox)
    {
        checkBoxes.add(checkBox);
    }

    @SideOnly(Side.CLIENT)
    public void draw(GuiBase gui, int mX, int mY)
    {
        for (CheckBox checkBox : checkBoxes)
        {
            if (checkBox.isVisible())
            {
                boolean selected = checkBox.getValue();
                boolean mouseover = CollisionHelper.inBounds(checkBox.getX(), checkBox.getY(), CHECK_BOX_SIZE, CHECK_BOX_SIZE, mX, mY);

                gui.drawColouredTexture(checkBox.getX(), checkBox.getY(), CHECK_BOX_SRC_X, CHECK_BOX_SRC_Y, CHECK_BOX_SIZE, CHECK_BOX_SIZE, ThemeHandler.theme.menus.checkboxes.getColour(selected, mouseover));
                gui.drawColouredTexture(checkBox.getX(), checkBox.getY(), CHECK_BOX_SELECTED_SRC_X, CHECK_BOX_SRC_Y, CHECK_BOX_SIZE, CHECK_BOX_SIZE, ThemeHandler.theme.menus.checkboxes.getColour(selected, mouseover));
                if (checkBox.getName() != null)
                {
                    gui.drawSplitString(checkBox.getName(), checkBox.getX() + CHECK_BOX_TEXT_X, checkBox.getY() + CHECK_BOX_TEXT_Y, checkBox.getTextWidth(), 0.7F, 0x404040);
                }
            }
        }
    }

    public void onClick(int mX, int mY)
    {
        for (CheckBox checkBox : checkBoxes)
        {
            if (checkBox.isVisible() && CollisionHelper.inBounds(checkBox.getX(), checkBox.getY(), CHECK_BOX_SIZE, CHECK_BOX_SIZE, mX, mY))
            {
                checkBox.setValue(!checkBox.getValue());
                checkBox.onUpdate();
                break;
            }
        }
    }
}
