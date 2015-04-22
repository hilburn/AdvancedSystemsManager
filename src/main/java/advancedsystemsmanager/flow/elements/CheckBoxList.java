package advancedsystemsmanager.flow.elements;


import advancedsystemsmanager.helpers.CollisionHelper;
import advancedsystemsmanager.gui.GuiManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

public class CheckBoxList
{
    public static final int CHECK_BOX_SIZE = 8;
    public static final int CHECK_BOX_SRC_X = 42;
    public static final int CHECK_BOX_SRC_Y = 106;
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
    public void draw(GuiManager gui, int mX, int mY)
    {
        for (CheckBox checkBox : checkBoxes)
        {
            if (checkBox.isVisible())
            {
                int srcCheckBoxX = checkBox.getValue() ? 1 : 0;
                int srcCheckBoxY = CollisionHelper.inBounds(checkBox.getX(), checkBox.getY(), CHECK_BOX_SIZE, CHECK_BOX_SIZE, mX, mY) ? 1 : 0;

                gui.drawTexture(checkBox.getX(), checkBox.getY(), CHECK_BOX_SRC_X + srcCheckBoxX * CHECK_BOX_SIZE, CHECK_BOX_SRC_Y + srcCheckBoxY * CHECK_BOX_SIZE, CHECK_BOX_SIZE, CHECK_BOX_SIZE);
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
