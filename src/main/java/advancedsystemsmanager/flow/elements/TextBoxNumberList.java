package advancedsystemsmanager.flow.elements;

import advancedsystemsmanager.gui.GuiBase;
import advancedsystemsmanager.helpers.CollisionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

public class TextBoxNumberList
{
    public static final int TEXT_BOX_SIZE_H = 12;
    public static final int TEXT_BOX_SRC_X = 0;
    public static final int TEXT_BOX_SRC_Y = 221;

    public List<TextBoxNumber> textBoxes;
    public TextBoxNumber selectedTextBox;

    public TextBoxNumberList()
    {
        textBoxes = new ArrayList<TextBoxNumber>();
    }

    @SideOnly(Side.CLIENT)
    public void draw(GuiBase gui, int mX, int mY)
    {
        for (TextBoxNumber textBox : textBoxes)
        {
            if (textBox.isVisible())
            {
                int srcTextBoxX = textBox.equals(selectedTextBox) ? 1 : 0;
                int srcTextBoxY = textBox.isWide() ? 1 : 0;

                gui.drawTexture(textBox.getX(), textBox.getY(), TEXT_BOX_SRC_X + srcTextBoxX * textBox.getWidth(), TEXT_BOX_SRC_Y + srcTextBoxY * TEXT_BOX_SIZE_H, textBox.getWidth(), TEXT_BOX_SIZE_H);
                String str = String.valueOf(textBox.getNumber());
                gui.drawCenteredString(str, textBox.getX(), textBox.getY() + textBox.getTextY(), textBox.getTextSize(), textBox.getWidth(), 0xFFFFFF);
            }
        }
    }

    public void onClick(int mX, int mY, int button)
    {
        for (TextBoxNumber textBox : textBoxes)
        {
            if (textBox.isVisible() && CollisionHelper.inBounds(textBox.getX(), textBox.getY(), textBox.getWidth(), TEXT_BOX_SIZE_H, mX, mY))
            {
                if (textBox.equals(selectedTextBox))
                {
                    if (button == 0)
                    {
                        selectedTextBox = null;
                    } else
                    {
                        textBox.setNumber(0);
                        selectedTextBox.onUpdate();
                    }
                } else
                {
                    selectedTextBox = textBox;
                }
                break;
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public boolean onKeyStroke(GuiBase gui, char c, int k)
    {
        if (selectedTextBox != null && selectedTextBox.isVisible())
        {
            if (Character.isDigit(c))
            {
                int number = Integer.parseInt(String.valueOf(c));
                if (Math.abs(selectedTextBox.getNumber()) < Math.pow(10, selectedTextBox.getLength() - 1))
                {
                    selectedTextBox.setNumber((Math.abs(selectedTextBox.getNumber()) * 10 + number) * (selectedTextBox.getNumber() < 0 ? -1 : 1));
                    selectedTextBox.onUpdate();
                }
                return true;
            } else if (c == '-' && selectedTextBox.allowNegative())
            {
                selectedTextBox.setNumber(selectedTextBox.getNumber() * -1);
                selectedTextBox.onUpdate();
            } else if (k == 14)
            {
                selectedTextBox.setNumber(selectedTextBox.getNumber() / 10);
                selectedTextBox.onUpdate();
                return true;
            } else if (k == 15)
            {
                for (int i = 0; i < textBoxes.size(); i++)
                {
                    TextBoxNumber textBox = textBoxes.get(i);

                    if (textBox.equals(selectedTextBox))
                    {
                        int nextId = (i + 1) % textBoxes.size();
                        selectedTextBox = textBoxes.get(nextId);
                        break;
                    }
                }
                return true;
            }
        }

        return false;
    }

    public void addTextBox(TextBoxNumber textBox)
    {
        textBoxes.add(textBox);
    }

    public TextBoxNumber getTextBox(int id)
    {
        return textBoxes.get(id);
    }

}
