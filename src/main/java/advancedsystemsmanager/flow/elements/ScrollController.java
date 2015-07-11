package advancedsystemsmanager.flow.elements;


import advancedsystemsmanager.api.network.IPacketProvider;
import advancedsystemsmanager.gui.GuiManager;
import advancedsystemsmanager.helpers.CollisionHelper;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.reference.Null;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

public abstract class ScrollController<T>
{
    public static final int ITEM_SIZE = 16;
    public static final int ITEM_SIZE_WITH_MARGIN = 20;
    public static final int ARROW_SIZE_W = 10;
    public static final int ARROW_SIZE_H = 6;
    public static final int ARROW_SRC_X = 64;
    public static final int ARROW_SRC_Y = 165;
    public static final int ARROW_X = 105;
    public static final int ARROW_Y_UP = 32;
    public static final int ARROW_Y_DOWN = 42;
    public static final int TEXT_BOX_SIZE_W = 64;
    public static final int TEXT_BOX_SIZE_H = 12;
    public static final int TEXT_BOX_SRC_X = 0;
    public static final int TEXT_BOX_SRC_Y = 165;
    public static final int TEXT_BOX_X = 5;
    public static final int TEXT_BOX_Y = 5;
    public static final int TEXT_BOX_TEXT_X = 3;
    public static final int TEXT_BOX_TEXT_Y = 3;
    public static final int CURSOR_X = 2;
    public static final int CURSOR_Y = 0;
    public static final int CURSOR_Z = 5;
    public static final int AMOUNT_TEXT_X = 75;
    public static final int AMOUNT_TEXT_Y = 9;
    public static final int SCROLL_SPEED = 100;
    public int offset;
    public boolean canScroll;
    public int dir;
    public boolean clicked;
    public boolean selected;
    public TextBoxLogic textBox;
    public List<T> result;
    public boolean hasSearchBox;
    public int itemsPerRow = 5;
    public int visibleRows = 2;
    public int startX = 5;
    public int scrollingUpperLimit = TEXT_BOX_Y + TEXT_BOX_SIZE_H;
    public boolean disabledScroll;
    public long lastUpdate;
    public float left;
    protected int x, y;

    protected ScrollController()
    {
        this.hasSearchBox = true;
    }

    public ScrollController(IPacketProvider packetProvider, boolean hasSearchBox)
    {
        this(packetProvider, hasSearchBox ? "" : null);
    }

    public ScrollController(IPacketProvider packetProvider, String defaultText)
    {
        this.hasSearchBox = defaultText != null;
        if (hasSearchBox)
        {
            textBox = new TextBoxLogic(Null.NULL_PACKET, Integer.MAX_VALUE, TEXT_BOX_SIZE_W - TEXT_BOX_TEXT_X * 2)
            {
                @Override
                public void onUpdate()
                {
                    if (getText().length() > 0)
                    {
                        updateSearch();
                    } else
                    {
                        result.clear();
                        updateScrolling();
                    }
                }
            };

            textBox.setTextAndCursor(defaultText);
        }

        updateSearch();
    }

    public void setX(int val)
    {
        startX = val;
    }

    public void onClick(int mX, int mY, int button)
    {
        if (CollisionHelper.inBounds(x + TEXT_BOX_X, y + TEXT_BOX_Y, TEXT_BOX_SIZE_W, TEXT_BOX_SIZE_H, mX, mY))
        {
            if (button == 0 || !selected)
            {
                selected = !selected;
            } else if (hasSearchBox)
            {
                textBox.setTextAndCursor("");
            }
        }

        List<Point> points = getItemCoordinates();
        for (Point point : points)
        {
            if (CollisionHelper.inBounds(point.x, point.y, ITEM_SIZE, ITEM_SIZE, mX, mY))
            {
                onClick(result.get(point.id), mX, mY, button);
                break;
            }
        }

        if (inArrowBounds(true, mX, mY))
        {
            clicked = true;
            dir = 1;
        } else if (inArrowBounds(false, mX, mY))
        {
            clicked = true;
            dir = -1;
        }
    }

    public List<Point> getItemCoordinates()
    {
        List<Point> points = new ArrayList<Point>();

        int start = getFirstRow();
        for (int row = start; row <= start + getVisibleRows(); row++)
        {
            for (int col = 0; col < itemsPerRow; col++)
            {
                int id = row * itemsPerRow + col;
                if (id >= 0 && id < result.size())
                {
                    int x = getScrollingStartX() + ITEM_SIZE_WITH_MARGIN * col;
                    int y = row * ITEM_SIZE_WITH_MARGIN - offset;
                    if (y >= 0 && y + ITEM_SIZE < getVisibleRows() * ITEM_SIZE_WITH_MARGIN + 5)
                    {
                        points.add(new Point(id, this.x + x, this.y + y + getScrollingStartY()));
                    }
                }
            }
        }

        return points;
    }

    @SideOnly(Side.CLIENT)
    public abstract void onClick(T t, int mX, int mY, int button);

    public boolean inArrowBounds(boolean down, int mX, int mY)
    {
        return CollisionHelper.inBounds(x + ARROW_X, y + (down ? ARROW_Y_DOWN : ARROW_Y_UP), ARROW_SIZE_W, ARROW_SIZE_H, mX, mY);
    }

    public int getFirstRow()
    {
        return (scrollingUpperLimit + offset - getScrollingStartY()) / ITEM_SIZE_WITH_MARGIN;
    }

    public int getVisibleRows()
    {
        return visibleRows;
    }

    public int getScrollingStartX()
    {
        return startX;
    }

    public int getScrollingStartY()
    {
        return scrollingUpperLimit + 3;
    }

    public void setVisibleRows(int n)
    {
        visibleRows = n;
    }

    public void onRelease(int mX, int mY)
    {
        clicked = false;
    }

    @SideOnly(Side.CLIENT)
    public boolean onKeyStroke(GuiManager gui, char c, int k)
    {
        if (selected && hasSearchBox)
        {
            textBox.onKeyStroke(gui, c, k);

            return true;
        } else
        {
            return false;
        }
    }

    @SideOnly(Side.CLIENT)
    public void draw(GuiManager gui, int mX, int mY)
    {
        int srcBoxY = selected ? 1 : 0;

        if (hasSearchBox)
        {
            gui.drawTexture(x + TEXT_BOX_X, y + TEXT_BOX_Y, TEXT_BOX_SRC_X, TEXT_BOX_SRC_Y + srcBoxY * TEXT_BOX_SIZE_H, TEXT_BOX_SIZE_W, TEXT_BOX_SIZE_H);
            gui.drawString(textBox.getText(), x + TEXT_BOX_X + TEXT_BOX_TEXT_X, y + TEXT_BOX_Y + TEXT_BOX_TEXT_Y, 0xFFFFFF);

            if (selected)
            {
                gui.drawCursor(x + TEXT_BOX_X + textBox.getCursorPosition(gui) + CURSOR_X, y + TEXT_BOX_Y + CURSOR_Y, CURSOR_Z, 0xFFFFFFFF);
            }

            if (textBox.getText().length() > 0 || result.size() > 0)
            {
                gui.drawStringFormatted(Names.ITEMS_FOUND, x + AMOUNT_TEXT_X, y + AMOUNT_TEXT_Y, 0.7F, 0x404040, result.size());
            }
        }

        if (result.size() > 0)
        {
            drawArrow(gui, true, mX, mY);
            drawArrow(gui, false, mX, mY);

            List<Point> points = getItemCoordinates();
            for (Point point : points)
            {
                draw(gui, result.get(point.id), point.x, point.y, CollisionHelper.inBounds(point.x, point.y, ITEM_SIZE, ITEM_SIZE, mX, mY));
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public void drawArrow(GuiManager gui, boolean down, int mX, int mY)
    {
        if (canScroll)
        {
            int srcArrowX = clicked && down == (dir == 1) ? 2 : inArrowBounds(down, mX, mY) ? 1 : 0;
            int srcArrowY = down ? 1 : 0;

            gui.drawTexture(x + ARROW_X, y + (down ? ARROW_Y_DOWN : ARROW_Y_UP), ARROW_SRC_X + srcArrowX * ARROW_SIZE_W, ARROW_SRC_Y + srcArrowY * ARROW_SIZE_H, ARROW_SIZE_W, ARROW_SIZE_H);
        }
    }

    @SideOnly(Side.CLIENT)
    public abstract void draw(GuiManager gui, T t, int x, int y, boolean hover);

    public void update(float partial)
    {
        if (clicked && canScroll)
        {
            partial += left;
            int change = (int)(partial * SCROLL_SPEED);
            left = partial - (change / (float)SCROLL_SPEED);


            moveOffset(change * dir);
        }
    }

    public void moveOffset(int change)
    {
        offset += change;
        int min = 0;
        int max = ((int)(Math.ceil(((float)result.size() / itemsPerRow)) - getVisibleRows())) * ITEM_SIZE_WITH_MARGIN - (ITEM_SIZE_WITH_MARGIN - ITEM_SIZE);
        if (offset < min)
        {
            offset = min;
        } else if (offset > max)
        {
            offset = max;
        }
    }

    @SideOnly(Side.CLIENT)
    public void drawMouseOver(GuiManager gui, int mX, int mY)
    {
        List<Point> points = getItemCoordinates();
        for (Point point : points)
        {
            if (CollisionHelper.inBounds(point.x, point.y, ITEM_SIZE, ITEM_SIZE, mX, mY))
            {
                drawMouseOver(gui, result.get(point.id), mX, mY);
                break;
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public abstract void drawMouseOver(GuiManager gui, T t, int mX, int mY);

    public void setItemsPerRow(int n)
    {
        itemsPerRow = n;
    }

    public void setItemUpperLimit(int n)
    {
        scrollingUpperLimit = n;
    }

    public List<T> getResult()
    {
        return result;
    }

    public String getText()
    {
        return textBox.getText();
    }

    public void setText(String s)
    {
        textBox.setText(s);
        updateSearch();
    }

    public void updateSearch()
    {
        if (hasSearchBox)
        {
            result = updateSearch(textBox.getText().toLowerCase(), textBox.getText().toLowerCase().equals(".all"));
        } else
        {
            result = updateSearch("", false);
        }
        updateScrolling();
    }

    public abstract List<T> updateSearch(String search, boolean all);

    public void updateScrolling()
    {
        canScroll = result.size() > itemsPerRow * getVisibleRows();
        if (!canScroll)
        {
            offset = 0;
        }
    }

    public void setTextAndCursor(String s)
    {
        textBox.setTextAndCursor(s);
    }

    public void doScroll(int scroll)
    {
        if (canScroll())
        {
            moveOffset(scroll / -20);
        }
    }

    public boolean canScroll()
    {
        return !disabledScroll && result.size() > itemsPerRow;
    }

    public void setDisabledScroll(boolean disabledScroll)
    {
        this.disabledScroll = disabledScroll;
    }

    public long getLastUpdate()
    {
        return lastUpdate;
    }

    public class Point
    {
        protected int id, x, y;

        public Point(int id, int x, int y)
        {
            this.id = id;
            this.x = x;
            this.y = y;
        }
    }
}
