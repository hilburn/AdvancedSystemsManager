package advancedsystemsmanager.flow.elements;

import advancedsystemsmanager.client.gui.GuiBase;
import advancedsystemsmanager.helpers.CollisionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WideNumberBoxList
{
    public List<WideNumberBox> textBoxes = new ArrayList();
    public WideNumberBox selectedTextBox;

    @SideOnly(Side.CLIENT)
    public void draw(GuiBase gui, int mX, int mY)
    {
        Iterator i$ = this.textBoxes.iterator();

        while (i$.hasNext())
        {
            WideNumberBox textBox = (WideNumberBox)i$.next();
            if (textBox.isVisible())
            {
                int srcTextBoxY = textBox.equals(this.selectedTextBox) ? 177 : 165;
                gui.drawTexture(textBox.getX(), textBox.getY(), 0, srcTextBoxY, textBox.getWidth(), 12);
                String str = String.valueOf(textBox.getNumber());
                gui.drawString(str, textBox.getX(), textBox.getY() + textBox.getTextY(), textBox.getTextSize(), 16777215);
            }
        }
    }

    public void onClick(int mX, int mY, int button)
    {
        Iterator i$ = this.textBoxes.iterator();

        while (i$.hasNext())
        {
            WideNumberBox textBox = (WideNumberBox)i$.next();
            if (textBox.isVisible() && CollisionHelper.inBounds(textBox.getX(), textBox.getY(), textBox.getWidth(), 12, mX, mY))
            {
                if (textBox.equals(this.selectedTextBox))
                {
                    if (button == 0)
                    {
                        this.selectedTextBox = null;
                    } else
                    {
                        textBox.setNumber(0);
                        this.selectedTextBox.onUpdate();
                    }
                } else
                {
                    this.selectedTextBox = textBox;
                }
                break;
            }
        }

    }

    @SideOnly(Side.CLIENT)
    public boolean onKeyStroke(GuiBase gui, char c, int k)
    {
        if (this.selectedTextBox != null && this.selectedTextBox.isVisible())
        {
            int i;
            if (Character.isDigit(c))
            {
                i = Integer.parseInt(String.valueOf(c));
                if ((double)Math.abs(this.selectedTextBox.getNumber()) < Math.pow(10.0D, (double)(this.selectedTextBox.getLength() - 1)))
                {
                    this.selectedTextBox.setNumber(Math.min(this.selectedTextBox.getNumber() * 10 + i, this.selectedTextBox.getMaxNumber()));
                    this.selectedTextBox.onUpdate();
                }

                return true;
            }

            if (c == 45 && this.selectedTextBox.allowNegative())
            {
                this.selectedTextBox.setNumber(this.selectedTextBox.getNumber() * -1);
                this.selectedTextBox.onUpdate();
            } else
            {
                if (k == 14)
                {
                    this.selectedTextBox.setNumber(this.selectedTextBox.getNumber() / 10);
                    this.selectedTextBox.onUpdate();
                    return true;
                }

                if (k == 15)
                {
                    for (i = 0; i < this.textBoxes.size(); ++i)
                    {
                        WideNumberBox textBox = this.textBoxes.get(i);
                        if (textBox.equals(this.selectedTextBox))
                        {
                            int nextId = (i + 1) % this.textBoxes.size();
                            this.selectedTextBox = this.textBoxes.get(nextId);
                            break;
                        }
                    }

                    return true;
                }
            }
        }

        return false;
    }

    public void addTextBox(WideNumberBox textBox)
    {
        this.textBoxes.add(textBox);
    }

    public WideNumberBox getTextBox(int id)
    {
        return this.textBoxes.get(id);
    }
}
