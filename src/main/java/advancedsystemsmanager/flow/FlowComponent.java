package advancedsystemsmanager.flow;


import advancedsystemsmanager.api.execution.ICommand;
import advancedsystemsmanager.api.gui.IGuiElement;
import advancedsystemsmanager.api.network.IPacketProvider;
import advancedsystemsmanager.api.network.IPacketSync;
import advancedsystemsmanager.flow.elements.TextBoxLogic;
import advancedsystemsmanager.flow.menus.Menu;
import advancedsystemsmanager.flow.menus.MenuResult;
import advancedsystemsmanager.gui.GuiManager;
import advancedsystemsmanager.helpers.CollisionHelper;
import advancedsystemsmanager.network.ASMPacket;
import advancedsystemsmanager.network.PacketHandler;
import advancedsystemsmanager.registry.CommandRegistry;
import advancedsystemsmanager.registry.ConnectionOption;
import advancedsystemsmanager.registry.ConnectionSet;
import advancedsystemsmanager.settings.Settings;
import advancedsystemsmanager.tileentities.manager.TileEntityManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.Constants;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FlowComponent implements Comparable<FlowComponent>, IGuiElement<GuiManager>, IPacketSync, IPacketProvider
{
    public static final int COMPONENT_SRC_X = 0;
    public static final int COMPONENT_SRC_Y = 0;
    public static final int COMPONENT_SIZE_W = 64;
    public static final int COMPONENT_SIZE_H = 20;
    public static final int COMPONENT_SIZE_LARGE_W = 124;
    public static final int COMPONENT_SIZE_LARGE_H = 152;
    public static final int COMPONENT_SRC_LARGE_X = 64;
    public static final int DRAGGABLE_SIZE = 6;

    public static final int ARROW_X = -10;
    public static final int ARROW_Y = 5;
    public static final int ARROW_SIZE_W = 9;
    public static final int ARROW_SIZE_H = 10;
    public static final int ARROW_SRC_X = 0;
    public static final int ARROW_SRC_Y = 20;

    public static final int MENU_ITEM_SIZE_W = 120;
    public static final int MENU_ITEM_SIZE_H = 13;
    public static final int MENU_ITEM_SRC_X = 0;
    public static final int MENU_ITEM_SRC_Y = 152;
    public static final int MENU_X = 2;
    public static final int MENU_Y = 20;
    public static final int MENU_SIZE_H = 130;
    public static final int MENU_ITEM_CAPACITY = 5;

    public static final int MENU_ARROW_X = 109;
    public static final int MENU_ARROW_Y = 2;
    public static final int MENU_ARROW_SIZE_W = 9;
    public static final int MENU_ARROW_SIZE_H = 9;
    public static final int MENU_ARROW_SRC_X = 0;
    public static final int MENU_ARROW_SRC_Y = 40;

    public static final int MENU_ITEM_TEXT_X = 5;
    public static final int MENU_ITEM_TEXT_Y = 3;

    public static final int CONNECTION_SIZE_W = 7;
    public static final int CONNECTION_SIZE_H = 6;
    public static final int CONNECTION_SRC_X = 0;
    public static final int CONNECTION_SRC_Y = 58;
    public static final int CONNECTION_SRC_Y_SIDE = 245;

    public static final int ERROR_X = 2;
    public static final int ERROR_Y = 8;
    public static final int ERROR_SIZE_W = 2;
    public static final int ERROR_SIZE_H = 10;
    public static final int ERROR_SRC_X = 42;
    public static final int ERROR_SRC_Y = 212;

    public static final int NODE_SRC_X = 120;
    public static final int NODE_SRC_Y = 152;
    public static final int NODE_SIZE = 4;
    public static final int MAX_NODES = 15;

    public static final int CURSOR_X = -1;
    public static final int CURSOR_Y = -4;
    public static final int CURSOR_Z = 5;
    public static final int TEXT_X = 7;
    public static final int TEXT_Y = 10;
    public static final int TEXT_HEIGHT = 10;
    public static final int EDIT_SRC_X = 32;
    public static final int EDIT_SRC_Y = 189;
    public static final int EDIT_X = 103;
    public static final int EDIT_Y = 6;
    public static final int EDIT_X_SMALL = 105;
    public static final int EDIT_Y_TOP = 2;
    public static final int EDIT_Y_BOT = 11;
    public static final int EDIT_SIZE = 9;
    public static final int EDIT_SIZE_SMALL = 7;

    public static final int TEXT_SPACE = 135;
    public static final int TEXT_SPACE_SHORT = 65;
    public static final int TEXT_MAX_LENGTH = 31;
    public static final String NBT_POS_X = "X";
    public static final String NBT_POS_Y = "Y";
    public static final String NBT_TYPE = "T";
    public static final String NBT_CONNECTION = "C";
    public static final String NBT_INTERVAL = "I";
    public static final String NBT_MENUS = "M";
    public static final String NBT_NAME = "Na";
    public static final String NBT_PARENT = "P";
    private static final String NBT_ID = "ID";
    public int x;
    public int y;
    public int mouseDragX;
    public int mouseDragY;
    public int mouseStartX;
    public int mouseStartY;
    public int resetTimer;
    public boolean isDragging;
    public boolean isLarge;
    public List<Menu> menus;
    public int openMenuId;
    public ConnectionSet connectionSet;
    public ICommand type;
    public TileEntityManager manager;
    public int id;
    public Connection[] connections;
    public int currentInterval;
    public boolean isEditing;
    public TextBoxLogic textBox;
    public String name;
    public FlowComponent parent;
    public List<FlowComponent> childrenInputNodes;
    public List<FlowComponent> childrenOutputNodes;
    public boolean isInventoryListDirty = true;
    public boolean isLoading;
    public String cachedName;
    public String cachedShortName;
    public int parentLoadId = -1;
    public int overrideX = -1;
    public int overrideY = -1;
    private List<IPacketSync> networkSyncList = new ArrayList<IPacketSync>();
    List<String> errors = new ArrayList<String>();

    public FlowComponent(TileEntityManager manager, ICommand type)
    {
        this(manager, 50, 50, type);
    }

    public FlowComponent(TileEntityManager manager, int x, int y, ICommand type)
    {
        this(manager, x, y, manager.getNextFreeID(), type);
    }

    public FlowComponent(TileEntityManager manager, int x, int y, int id, ICommand type)
    {
        this.x = x;
        this.y = y;
        this.connectionSet = type.getSets()[0];
        this.type = type;
        this.manager = manager;
        this.id = id;

        menus = new ArrayList<Menu>();
        type.getMenus(this, menus);
        menus.add(new MenuResult(this));

        openMenuId = -1;
        connections = new Connection[connectionSet.getConnections().length];
        textBox = new TextBoxLogic(this, TEXT_MAX_LENGTH, TEXT_SPACE)
        {
            @Override
            public boolean readData(ASMPacket packet)
            {
                super.readData(packet);
                setComponentName(text);
                return false;
            }
        };

        childrenInputNodes = new ArrayList<FlowComponent>();
        childrenOutputNodes = new ArrayList<FlowComponent>();
    }

    public static FlowComponent readFromNBT(TileEntityManager manager, NBTTagCompound nbtTagCompound, boolean pickup)
    {
        FlowComponent component = null;
        try
        {
            int x = nbtTagCompound.getShort(NBT_POS_X);
            int y = nbtTagCompound.getShort(NBT_POS_Y);
            int typeId = nbtTagCompound.getByte(NBT_TYPE);
            int id = nbtTagCompound.getInteger(NBT_ID);
            component = new FlowComponent(manager, x, y, id, CommandRegistry.getCommand(typeId));
            component.isLoading = true;

            if (nbtTagCompound.hasKey(NBT_NAME))
            {
                component.name = nbtTagCompound.getString(NBT_NAME);
            } else
            {
                component.name = null;
            }

            if (nbtTagCompound.hasKey(NBT_PARENT))
            {
                component.parentLoadId = nbtTagCompound.getInteger(NBT_PARENT);
            }

            NBTTagList connections = nbtTagCompound.getTagList(NBT_CONNECTION, 10);
            for (int i = 0; i < connections.tagCount(); i++)
            {
                Connection.readFromNBT(component.connections, connections.getCompoundTagAt(i), id);
            }

            if (nbtTagCompound.hasKey(NBT_INTERVAL, Constants.NBT.TAG_SHORT))
            {
                component.currentInterval = nbtTagCompound.getShort(NBT_INTERVAL);
            }

            NBTTagList menuTagList = nbtTagCompound.getTagList(NBT_MENUS, 10);
            int menuId = 0;
            for (int i = 0; i < menuTagList.tagCount(); i++)
            {
                NBTTagCompound menuTag = menuTagList.getCompoundTagAt(i);

                component.menus.get(menuId).readFromNBT(menuTag, pickup);
                menuId++;
            }

            return component;
        } finally
        {
            if (component != null)
            {
                component.isLoading = false;
            }
        }
    }

    public int getCurrentInterval()
    {
        return currentInterval;
    }

    public void setCurrentInterval(int currentInterval)
    {
        this.currentInterval = currentInterval;
    }

    public int getX()
    {
        return x;
    }

    public void setX(int x)
    {
        this.x = x;
    }

    public int getY()
    {
        return y;
    }

    public void setY(int y)
    {
        this.y = y;
    }

    public void update(float partial)
    {
        //no need for this to be precise, can ignore the partial time
        if (resetTimer > 0)
        {
            if (resetTimer == 1)
            {
                x = mouseStartX;
                y = mouseStartY;
            }

            resetTimer--;
        }

        for (Menu menu : menus)
        {
            menu.update(partial);
        }
    }

    @SideOnly(Side.CLIENT)
    public void draw(GuiManager gui, int mX, int mY, int zLevel)
    {
        GL11.glPushMatrix();
        GL11.glTranslatef(0, 0, zLevel);

        gui.drawTexture(x, y, isLarge ? COMPONENT_SRC_LARGE_X : COMPONENT_SRC_X, COMPONENT_SRC_Y, getComponentWidth(), getComponentHeight());

        int internalX = mX - x;
        int internalY = mY - y;

        int srcArrowX = isLarge ? 1 : 0;
        int srcArrowY = inArrowBounds(internalX, internalY) ? 1 : 0;
        gui.drawTexture(x + getComponentWidth() + ARROW_X, y + ARROW_Y, ARROW_SRC_X + ARROW_SIZE_W * srcArrowX, ARROW_SRC_Y + ARROW_SIZE_H * srcArrowY, ARROW_SIZE_W, ARROW_SIZE_H);


        if (isLarge)
        {
            for (int i = 0; i < menus.size(); i++)
            {
                Menu menu = menus.get(i);

                if (!menu.isVisible())
                {
                    if (openMenuId == i)
                    {
                        openMenuId = -1;
                    }

                    continue;
                }

                int itemX = getMenuAreaX();
                int itemY = y + getMenuItemY(i);
                gui.drawTexture(itemX, itemY, MENU_ITEM_SRC_X, MENU_ITEM_SRC_Y, MENU_ITEM_SIZE_W, MENU_ITEM_SIZE_H);

                int srcItemArrowX = inMenuArrowBounds(i, internalX, internalY) ? 1 : 0;
                int srcItemArrowY = i == openMenuId ? 1 : 0;
                gui.drawTexture(itemX + MENU_ARROW_X, itemY + MENU_ARROW_Y, MENU_ARROW_SRC_X + MENU_ARROW_SIZE_W * srcItemArrowX, MENU_ARROW_SRC_Y + MENU_ARROW_SIZE_H * srcItemArrowY, MENU_ARROW_SIZE_W, MENU_ARROW_SIZE_H);


                gui.drawString(menu.getName(), x + MENU_X + MENU_ITEM_TEXT_X, y + getMenuItemY(i) + MENU_ITEM_TEXT_Y, 0x404040);

                if (i == openMenuId)
                {
                    GL11.glPushMatrix();
                    GL11.glTranslatef(itemX, getMenuAreaY(i), 0);
                    menu.draw(gui, mX - itemX, mY - getMenuAreaY(i));
                    GL11.glPopMatrix();
                }
            }
        }


        boolean hasConnection = false;
        int outputCount = 0;
        int inputCount = 0;
        int sideCount = 0;
        for (int i = 0; i < connectionSet.getConnections().length; i++)
        {
            ConnectionOption connection = connectionSet.getConnections()[i];

            int[] location = getConnectionLocation(connection, inputCount, outputCount, sideCount);
            if (location == null)
            {
                continue;
            }

            if (connection.isInput())
            {
                inputCount++;
            } else if (connection.getType() == ConnectionOption.ConnectionType.OUTPUT)
            {
                outputCount++;
            } else
            {
                sideCount++;
            }

            int connectionWidth = location[3];
            int connectionHeight = location[4];

            int srcConnectionX = (CollisionHelper.inBounds(location[0], location[1], connectionWidth, connectionHeight, mX, mY)) ? 1 : 0;

            Connection current = manager.getCurrentlyConnecting();
            if (current != null && current.getInputId() == id && current.getInputConnection() == i)
            {
                gui.drawLine(location[0] + connectionWidth / 2, location[1] + connectionHeight / 2, overrideX != -1 ? overrideX : mX, overrideY != -1 ? overrideY : mY);
            }

            Connection connectedConnection = connections[i];
            if (connectedConnection != null)
            {
                hasConnection = true;
                if (connectedConnection.getInputId() == id)
                {
                    FlowComponent other = manager.getFlowItem(connectedConnection.getOutputId());
                    if (other == null)
                    {
                        continue;
                    }
                    int[] otherLocation = other.getConnectionLocationFromId(connectedConnection.getOutputConnection());
                    if (otherLocation == null)
                    {
                        continue;
                    }
                    int startX = location[0] + connectionWidth / 2;
                    int startY = location[1] + connectionHeight / 2;
                    int endX = otherLocation[0] + connectionWidth / 2;
                    int endY = otherLocation[1] + connectionHeight / 2;

                    GL11.glPushMatrix();
                    GL11.glTranslatef(0, 0, -zLevel);
                    List<Point> nodes = connectedConnection.getNodes();
                    for (int j = 0; j <= nodes.size(); j++)
                    {
                        int x1, y1, x2, y2;
                        if (j == 0)
                        {
                            x1 = startX;
                            y1 = startY;
                        } else
                        {
                            x1 = nodes.get(j - 1).getX();
                            y1 = nodes.get(j - 1).getY();
                        }

                        if (j == nodes.size())
                        {
                            x2 = endX;
                            y2 = endY;
                        } else
                        {
                            x2 = nodes.get(j).getX();
                            y2 = nodes.get(j).getY();
                        }

                        gui.drawLine(x1, y1, x2, y2);
                    }

                    for (Point node : nodes)
                    {
                        int x = node.getX() - NODE_SIZE / 2;
                        int y = node.getY() - NODE_SIZE / 2;
                        int srcXNode = connectedConnection.getSelectedNode() == null && CollisionHelper.inBounds(x, y, NODE_SIZE, NODE_SIZE, mX, mY) ? 1 : 0;
                        gui.drawTexture(x, y, NODE_SRC_X + srcXNode * NODE_SIZE, NODE_SRC_Y, NODE_SIZE, NODE_SIZE);
                    }

                    GL11.glPopMatrix();
                }
            }

            gui.drawTexture(location[0], location[1], CONNECTION_SRC_X + srcConnectionX * connectionWidth, location[2], connectionWidth, connectionHeight);

        }

        errors.clear();
        if (hasConnection || getConnectionSet().getConnections().length == 0)
        {
            for (Menu menu : menus)
            {
                if (menu.isVisible())
                {
                    menu.addErrors(errors);
                }
            }
        }

        if (!errors.isEmpty())
        {
            int srcErrorY = CollisionHelper.inBounds(x + ERROR_X, y + ERROR_Y, ERROR_SIZE_W, ERROR_SIZE_H, mX, mY) ? 1 : 0;
            gui.drawTexture(x + ERROR_X, y + ERROR_Y, ERROR_SRC_X, ERROR_SRC_Y + srcErrorY * ERROR_SIZE_H, ERROR_SIZE_W, ERROR_SIZE_H);
        }

        if (!isEditing || isLarge)
        {
            String name = getName();
            if (!isLarge)
            {
                name = getShortName(gui, name);
            }
            gui.drawString(name, x + TEXT_X, y + TEXT_Y, 0.7F, isEditing ? 0x707020 : 0x404040);
        }

        if (isEditing)
        {
            gui.drawString(getShortName(gui, getName()), x + TEXT_X, y + TEXT_Y, 0.7F, 0x207020);
        }

        if (name != null && Settings.isCommandTypes() && !GuiScreen.isCtrlKeyDown())
        {
            gui.drawCenteredString(getType().getName(), x + DRAGGABLE_SIZE, y + 3, 0.7F, getComponentWidth() - DRAGGABLE_SIZE - ARROW_SIZE_W, 0x707070);
        }

        if (isLarge)
        {
            if (isEditing)
            {
                gui.drawCursor(x + TEXT_X + (int)((textBox.getCursorPosition(gui) + CURSOR_X) * 0.7F), y + TEXT_Y + (int)(CURSOR_Y * 0.7F), CURSOR_Z, 0.7F, 0xFFFFFFFF);
                for (int i = 0; i < 2; i++)
                {
                    int buttonX = x + EDIT_X_SMALL;
                    int buttonY = y + (i == 0 ? EDIT_Y_TOP : EDIT_Y_BOT);

                    int srcXButton = CollisionHelper.inBounds(buttonX, buttonY, EDIT_SIZE_SMALL, EDIT_SIZE_SMALL, mX, mY) ? 1 : 0;
                    int srcYButton = i;

                    gui.drawTexture(buttonX, buttonY, EDIT_SRC_X + srcXButton * EDIT_SIZE_SMALL, EDIT_SRC_Y + EDIT_SIZE + EDIT_SIZE_SMALL * srcYButton, EDIT_SIZE_SMALL, EDIT_SIZE_SMALL);
                }
            } else
            {
                int buttonX = x + EDIT_X;
                int buttonY = y + EDIT_Y;
                int srcXButton = CollisionHelper.inBounds(buttonX, buttonY, EDIT_SIZE, EDIT_SIZE, mX, mY) ? 1 : 0;

                gui.drawTexture(buttonX, buttonY, EDIT_SRC_X + srcXButton * EDIT_SIZE, EDIT_SRC_Y, EDIT_SIZE, EDIT_SIZE);
            }
        }

        GL11.glPopMatrix();
    }

    @SideOnly(Side.CLIENT)
    public void drawMouseOver(GuiManager gui, int mX, int mY)
    {
        int outputCount = 0;
        int inputCount = 0;
        int sideCount = 0;
        for (ConnectionOption connection : connectionSet.getConnections())
        {
            int[] location = getConnectionLocation(connection, inputCount, outputCount, sideCount);
            if (location == null)
            {
                continue;
            }

            if (connection.isInput())
            {
                inputCount++;
            } else if (connection.getType() == ConnectionOption.ConnectionType.OUTPUT)
            {
                outputCount++;
            } else
            {
                sideCount++;
            }

            if (CollisionHelper.inBounds(location[0], location[1], CONNECTION_SIZE_W, CONNECTION_SIZE_H, mX, mY))
            {
                gui.drawMouseOver(connection.getName(this, (connection.isInput() ? inputCount : outputCount) - 1), mX, mY);
            }
        }

        if (isLarge)
        {
            for (int i = 0; i < menus.size(); i++)
            {
                Menu menu = menus.get(i);

                if (menu.isVisible() && i == openMenuId)
                {
                    GL11.glPushMatrix();
                    GL11.glTranslatef(getMenuAreaX(), getMenuAreaY(i), 0);
                    menu.drawMouseOver(gui, mX - getMenuAreaX(), mY - getMenuAreaY(i));
                    GL11.glPopMatrix();
                }
            }
        }

        if (!errors.isEmpty())
        {
            if (CollisionHelper.inBounds(x + ERROR_X, y + ERROR_Y, ERROR_SIZE_W, ERROR_SIZE_H, mX, mY))
            {
                String str = "";
                for (int i = 0; i < errors.size(); i++)
                {
                    if (i != 0)
                    {
                        str += "\n\n";
                    }
                    str += StatCollector.translateToLocal(errors.get(i));
                }
                gui.drawMouseOver(str, mX, mY);
            }
        }
    }

    public int[] getConnectionLocation(ConnectionOption connection, int inputCount, int outputCount, int sideCount)
    {
        int id = inputCount + outputCount + sideCount;
        if (!connection.isInput())
        {
            id -= childrenInputNodes.size();
        }

        if (!connection.isValid(this, id))
        {
            return null;
        }

        int targetX;
        int targetY;

        if (connection.getType() == ConnectionOption.ConnectionType.SIDE)
        {
            targetY = y + (int)(getComponentHeight() * ((sideCount + 0.5) / connectionSet.getSideCount()));
            targetY -= CONNECTION_SIZE_H / 2;
            targetX = x + getComponentWidth();
            return new int[]{targetX, targetY, CONNECTION_SRC_Y_SIDE, CONNECTION_SIZE_H, CONNECTION_SIZE_W};
        } else
        {
            int srcConnectionY;
            int currentCount;
            int totalCount;

            if (connection.isInput())
            {
                currentCount = inputCount;
                totalCount = connectionSet.getInputCount(this);
                srcConnectionY = 1;
                targetY = y - CONNECTION_SIZE_H;
            } else
            {
                currentCount = outputCount;
                totalCount = connectionSet.getOutputCount(this);
                srcConnectionY = 0;
                targetY = y + getComponentHeight();
            }

            targetX = x + (int)(getComponentWidth() * ((currentCount + 0.5) / totalCount));
            targetX -= CONNECTION_SIZE_W / 2;

            return new int[]{targetX, targetY, CONNECTION_SRC_Y + srcConnectionY * CONNECTION_SIZE_H, CONNECTION_SIZE_W, CONNECTION_SIZE_H};
        }
    }

    public ConnectionSet getConnectionSet()
    {
        return connectionSet;
    }

    public void setConnectionSet(ConnectionSet connectionSet)
    {
        if (this.connections != null && this.connectionSet != null && !isLoading)
        {
            int oldLength = this.connectionSet.getConnections().length;
            int newLength = connectionSet.getConnections().length;

            for (int i = 0; i < Math.min(oldLength, newLength); i++)
            {
                Connection connection = connections[i];
                if (connection != null && this.connectionSet.getConnections()[i].isInput() != connectionSet.getConnections()[i].isInput())
                {
                    removeConnection(i);
                }
            }

            for (int i = newLength; i < oldLength; i++)
            {
                Connection connection = connections[i];
                if (connection != null)
                {
                    removeConnection(i);
                }
            }

            this.connections = Arrays.copyOf(connections, connectionSet.getConnections().length);
        }
        this.connectionSet = connectionSet;
    }

    public void removeConnection(int id)
    {
        Connection connection = connections[id];
        if (connection != null)
        {
            sendConnectionData(id, false, 0, 0);
            connection.deleteConnection(manager);
        }
    }

    public void addConnection(int id, Connection connection, boolean sync)
    {
        if (sync && getManager().getWorldObj() != null && getManager().getWorldObj().isRemote)
        {
            sendConnectionData(id, true, connection.getOutputId(), connection.getOutputConnection());
        }
        connection.setConnection(manager);
    }

    public TileEntityManager getManager()
    {
        return manager;
    }

    public int getComponentWidth()
    {
        return isLarge ? COMPONENT_SIZE_LARGE_W : COMPONENT_SIZE_W;
    }

    public int getComponentHeight()
    {
        return isLarge ? COMPONENT_SIZE_LARGE_H : COMPONENT_SIZE_H;
    }

    public int getMenuAreaX()
    {
        return x + MENU_X;
    }

    public int getMenuAreaY(int i)
    {
        return y + getMenuItemY(i) + MENU_ITEM_SIZE_H;
    }

    public int getMenuItemY(int id)
    {
        int ret = MENU_Y;
        for (int i = 0; i < id; i++)
        {
            if (menus.get(i).isVisible())
            {
                ret += MENU_ITEM_SIZE_H - 1;
                if (openMenuId == i)
                {
                    ret += getMenuOpenSize() - 1;
                }
            }
        }


        return ret;
    }

    public static int getMenuOpenSize()
    {
        return MENU_SIZE_H - MENU_ITEM_CAPACITY * (MENU_ITEM_SIZE_H - 1);
    }

    @SideOnly(Side.CLIENT)
    public boolean onKeyStroke(GuiManager gui, char c, int k)
    {
        if (isLarge && isEditing)
        {
            textBox.onKeyStroke(gui, c, k);
            return true;
        } else
            return isLarge && openMenuId != -1 && menus.get(openMenuId).onKeyStroke(gui, c, k);
    }

    public boolean onClick(int mX, int mY, int button)
    {
        if (CollisionHelper.inBounds(x, y, getComponentWidth(), getComponentHeight(), mX, mY))
        {
            int internalX = mX - x;
            int internalY = mY - y;

            if (internalX <= DRAGGABLE_SIZE && internalY <= DRAGGABLE_SIZE)
            {
                mouseStartX = mouseDragX = mX;
                mouseStartY = mouseDragY = mY;
                isDragging = true;
            } else if (isLarge && !isEditing && CollisionHelper.inBounds(EDIT_X, EDIT_Y, EDIT_SIZE, EDIT_SIZE, internalX, internalY))
            {
                isEditing = true;
                textBox.setTextAndCursor(getName());
            } else if (isLarge && isEditing && CollisionHelper.inBounds(EDIT_X_SMALL, EDIT_Y_TOP, EDIT_SIZE_SMALL, EDIT_SIZE_SMALL, internalX, internalY))
            {
                isEditing = false;
                name = textBox.getText();
                if (name.equals(""))
                {
                    name = null;
                }
                textBox.setText(null);
            } else if (isLarge && isEditing && CollisionHelper.inBounds(EDIT_X_SMALL, EDIT_Y_BOT, EDIT_SIZE_SMALL, EDIT_SIZE_SMALL, internalX, internalY))
            {
                isEditing = false;
                textBox.setText(null);
            } else if (isLarge && isEditing && CollisionHelper.inBounds(TEXT_X, TEXT_Y, TEXT_SPACE, TEXT_HEIGHT, internalX, internalY))
            {
                if (button == 1)
                {
                    textBox.setTextAndCursor("");
                }
            } else if (inArrowBounds(internalX, internalY) || (Settings.isLargeOpenHitBox() && internalY < COMPONENT_SIZE_H))
            {
                if (!isLarge && type.getCommandType() == ICommand.CommandType.GROUP && Settings.isQuickGroupOpen() && !GuiScreen.isShiftKeyDown())
                {
                    manager.setSelectedGroup(this);
                } else
                {
                    isLarge = !isLarge;
                }
            } else if (!Settings.isLargeOpenHitBox() && internalY < COMPONENT_SIZE_H)
            {
                mouseStartX = mouseDragX = mX;
                mouseStartY = mouseDragY = mY;
                isDragging = true;
            } else if (isLarge)
            {

                for (int i = 0; i < menus.size(); i++)
                {
                    Menu menu = menus.get(i);

                    if (menu.isVisible())
                    {
                        if (inMenuArrowBounds(i, internalX, internalY) || (Settings.isLargeOpenHitBoxMenu() && internalY >= getMenuItemY(i) && internalY <= getMenuItemY(i) + MENU_ITEM_SIZE_H))
                        {
                            if (openMenuId == i)
                            {
                                openMenuId = -1;
                            } else
                            {
                                openMenuId = i;
                            }

                            return true;
                        }

                        if (i == openMenuId)
                        {
                            menu.onClick(mX - getMenuAreaX(), mY - getMenuAreaY(i), button);
                        }
                    }
                }
            }
            return true;
        } else
        {
            int outputCount = 0;
            int inputCount = 0;
            int sideCount = 0;
            for (int i = 0; i < connectionSet.getConnections().length; i++)
            {
                ConnectionOption connection = connectionSet.getConnections()[i];

                int[] location = getConnectionLocation(connection, inputCount, outputCount, sideCount);
                if (location == null)
                {
                    continue;
                }
                if (connection.isInput())
                {
                    inputCount++;
                } else if (connection.getType() == ConnectionOption.ConnectionType.OUTPUT)
                {
                    outputCount++;
                } else
                {
                    sideCount++;
                }
                Connection selected = connections[i];
                if ((selected == null || selected.getSelected() == -1) && CollisionHelper.inBounds(location[0], location[1], CONNECTION_SIZE_W, CONNECTION_SIZE_H, mX, mY))
                {
                    Connection current = manager.getCurrentlyConnecting();
                    if (button == 1 && current == null)
                    {
                        if (selected != null)
                        {
                            if (selected.getNodes().size() < MAX_NODES)
                            {
                                int id = connectionSet.connections[i].isInput() ? selected.getNodes().size() : 0;
                                selected.addAndSelectNode(mX, mY, id);
                                sendConnectionNodeData(i, id, 1, mX, mY);
                            }
                        }
                    } else
                    {
                        if (current == null)
                        {
                            if (connections[i] != null)
                            {
                                removeConnection(i);
                            }
                            manager.setCurrentlyConnecting(new Connection(id, i));
                        } else if (current.getInputId() == this.id && current.getInputConnection() == i)
                        {
                            manager.setCurrentlyConnecting(null);
                        } else if (current.getInputId() != id)
                        {
                            FlowComponent connectTo = manager.getFlowItem(current.getInputId());
                            ConnectionOption connectToOption = connectTo.connectionSet.getConnections()[current.getInputConnection()];
                            if (connectToOption.isInput() != connection.isInput())
                            {
                                if (checkForLoops(current))
                                {
                                    return true;
                                }

                                if (connections[i] != null)
                                {
                                    removeConnection(i);
                                }

                                if (connection.isInput())
                                {
                                    current.setOutputId(id);
                                    current.setOutputConnection(i);
                                    connectTo.addConnection(current.inputConnection, current, true);
                                } else
                                {
                                    current.setInputId(id);
                                    current.setInputConnection(i);
                                    addConnection(i, current, true);
                                }
                                manager.setCurrentlyConnecting(null);
                            }
                        }
                    }

                    return true;
                } else if (selected != null)
                {
                    List<Point> nodes = selected.getNodes();
                    for (int j = 0; j < nodes.size(); j++)
                    {
                        Point node = nodes.get(j);
                        int x = node.getX() - NODE_SIZE / 2;
                        int y = node.getY() - NODE_SIZE / 2;
                        if (CollisionHelper.inBounds(x, y, NODE_SIZE, NODE_SIZE, mX, mY))
                        {
                            if (button == 0)
                            {
                                selected.setSelected(j);
                            } else if (button == 1)
                            {
                                if (GuiScreen.isShiftKeyDown())
                                {
                                    selected.getNodes().remove(j);
                                    sendConnectionNodeData(i, j, 0, 0, 0);
                                } else if (selected.getNodes().size() < MAX_NODES && selected.getSelectedNode() == null)
                                {
                                    selected.addAndSelectNode(mX, mY, j + 1);
                                    sendConnectionNodeData(i, j + 1, 1, mX, mY);
                                }
                            }
                            return true;
                        }
                    }
                }
            }

            return false;
        }
    }

    public boolean isVisible()
    {
        return isVisible(getManager().getSelectedGroup());
    }

    public boolean isVisible(FlowComponent selectedComponent)
    {
        return (selectedComponent == null && parent == null) || (parent != null && parent.equals(selectedComponent));
    }

    public String getShortName(GuiManager gui, String name)
    {
        if (!name.equals(cachedName))
        {
            cachedShortName = "";
            for (char c : StatCollector.translateToLocal(name).toCharArray())
            {
                if (gui.getStringWidth(cachedShortName + c) > TEXT_SPACE_SHORT)
                {
                    break;
                }
                cachedShortName += c;
            }
        }
        cachedName = name;
        return cachedShortName;
    }

    @SideOnly(Side.CLIENT)
    public String getName()
    {
        return textBox.getText() == null ? name == null || GuiScreen.isCtrlKeyDown() ? StatCollector.translateToLocal(getType().getName()) : name : textBox.getText();
    }

    public boolean checkForLoops(Connection connection)
    {
        return checkForLoops(new ArrayList<Integer>(Arrays.asList(id)), connection.getInputId());
    }

    public boolean checkForLoops(List<Integer> usedComponents, int id)
    {
        if (usedComponents.contains(id))
        {
            return true;
        }
        usedComponents.add(id);
        FlowComponent currentComponent = manager.getFlowItem(id);

        for (int i = 0; i < currentComponent.connections.length; i++)
        {
            if (!currentComponent.connectionSet.isInput(i))
            {
                Connection c = currentComponent.getConnection(i);

                if (c != null)
                {
                    if (checkForLoops(usedComponents, c.getOutputId()))
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void removeAllConnections()
    {
        for (int i = 0; i < connectionSet.getConnections().length; i++)
        {
            Connection connection = connections[i];
            if (connection != null)
            {
                removeConnection(i);
            }
        }
    }

    public void deleteConnections()
    {
        for (Connection connection : connections)
        {
            if (connection != null) connection.deleteConnection(manager);
        }
    }

    public void onDrag(int mX, int mY)
    {
        followMouse(mX, mY);

        for (int i = 0; i < menus.size(); i++)
        {
            Menu menu = menus.get(i);

            menu.onDrag(mX - getMenuAreaX(), mY - getMenuAreaY(i), i == openMenuId);
        }


        for (int i = 0; i < connectionSet.getConnections().length; i++)
        {
            Connection connection = connections[i];
            if (connection != null)
            {
                connection.update(mX, mY);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public void followMouse(int mX, int mY)
    {
        if (isDragging)
        {
            x += mX - mouseDragX;
            y += mY - mouseDragY;


            if (GuiScreen.isShiftKeyDown())
            {
                adjustToGrid(10);
                mouseDragX = x;
                mouseDragY = y;
            } else
            {
                mouseDragX = mX;
                mouseDragY = mY;
            }
        }
    }

    public void adjustToGrid(int grid)
    {
        x = Math.round(x / grid) * grid;
        y = Math.round(y / grid) * grid;
    }

    public void onRelease(int mX, int mY, int button)
    {
        followMouse(mX, mY);
        if (isDragging)
        {
            sendLocationData();
        }

        for (int i = 0; i < menus.size(); i++)
        {
            Menu menu = menus.get(i);
            menu.onRelease(mX - getMenuAreaX(), mY - getMenuAreaY(i), isLarge && i == openMenuId);
        }


        for (int i = 0; i < connectionSet.getConnections().length; i++)
        {
            Connection connection = connections[i];
            if (connection != null)
            {
                Point node;
                if ((node = connection.getSelectedNode()) != null)
                {
                    connection.update(mX, mY);
                    sendConnectionNodeData(i, connection.getSelected(), 2, node.getX(), node.getY());
                    connection.setSelected(-1);
                    return;
                }
            }
        }
    }

    public void postRelease()
    {
        isDragging = false;
    }

    public void adjustEverythingToGrid(int grid)
    {
        adjustToGrid(grid);
        for (Connection connection : connections)
        {
            if (connection != null)
            {
                connection.adjustAllToGrid(grid);
            }
        }
    }

    public int[] getConnectionLocationFromId(int id)
    {
        int outputCount = 0;
        int inputCount = 0;
        int sideCount = 0;
        for (int i = 0; i < connectionSet.getConnections().length; i++)
        {
            ConnectionOption connection = connectionSet.getConnections()[i];

            int[] location = getConnectionLocation(connection, inputCount, outputCount, sideCount);
            if (location == null)
            {
                continue;
            }
            if (id == i)
            {
                return location;
            }
            if (connection.isInput())
            {
                inputCount++;
            } else if (connection.getType() == ConnectionOption.ConnectionType.OUTPUT)
            {
                outputCount++;
            } else
            {
                sideCount++;
            }
        }
        return null;
    }

    public boolean inArrowBounds(int internalX, int internalY)
    {
        return CollisionHelper.inBounds(getComponentWidth() + ARROW_X, ARROW_Y, ARROW_SIZE_W, ARROW_SIZE_H, internalX, internalY);
    }

    public boolean inMenuArrowBounds(int i, int internalX, int internalY)
    {
        return CollisionHelper.inBounds(MENU_X + MENU_ARROW_X, getMenuItemY(i) + MENU_ARROW_Y, MENU_ARROW_SIZE_W, MENU_ARROW_SIZE_H, internalX, internalY);
    }

    public ICommand getType()
    {
        return type;
    }

    public List<Menu> getMenus()
    {
        return menus;
    }

    public FlowComponent copy()
    {
        FlowComponent copy = new FlowComponent(manager, x, y, id, type);
        copy.name = name;

        for (int i = 0; i < menus.size(); i++)
        {
            Menu menu = menus.get(i);

            copy.menus.get(i).copyFrom(menu);
        }


        for (int i = 0; i < connectionSet.getConnections().length; i++)
        {
            Connection connection = connections[i];
            if (connection != null)
            {
                copy.connections[i] = connection.copy();
            }
        }

        return copy;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }


    public Connection getConnection(int i)
    {
        return connections[i];
    }

    public boolean isBeingMoved()
    {
        return isDragging;
    }

    public void removeParent(int idToRemove)
    {
        if (parent != null && parent.getId() == idToRemove)
        {
            setParent(null);
        }
    }

    public void linkAfterLoad()
    {
        if (parentLoadId != -1)
        {
            setParent(getManager().getFlowItem(parentLoadId));
        } else
        {
            setParent(null);
        }
        for (Connection connection : connections)
        {
            if (connection != null)
            {
                connection.setConnection(manager);
            }
        }
    }

    public void writeToNBT(NBTTagCompound nbtTagCompound, boolean pickup)
    {
        nbtTagCompound.setShort(NBT_POS_X, (short)x);
        nbtTagCompound.setShort(NBT_POS_Y, (short)y);
        nbtTagCompound.setByte(NBT_TYPE, (byte)type.getId());
        nbtTagCompound.setInteger(NBT_ID, id);

        if (name != null)
        {
            nbtTagCompound.setString(NBT_NAME, name);
        }
        if (parent != null)
        {
            nbtTagCompound.setInteger(NBT_PARENT, parent.getId());
        }

        nbtTagCompound.setTag(NBT_CONNECTION, getConnectionNBT());

        if (this.type.getCommandType() == ICommand.CommandType.TRIGGER)
        {
            nbtTagCompound.setShort(NBT_INTERVAL, (short)currentInterval);
        }

        NBTTagList menuTagList = new NBTTagList();
        for (Menu menu : menus)
        {
            NBTTagCompound menuTag = new NBTTagCompound();
            menu.writeToNBT(menuTag, pickup);
            menuTagList.appendTag(menuTag);

        }
        nbtTagCompound.setTag(NBT_MENUS, menuTagList);
    }

    public NBTTagList getConnectionNBT()
    {
        NBTTagList connections = new NBTTagList();
        for (int i = 0; i < connectionSet.getConnections().length; i++)
        {
            Connection connection = this.connections[i];

            if (connection != null && connection.getInputId() == id)
            {
                NBTTagCompound connectionTag = new NBTTagCompound();
                connection.writeToNBT(connectionTag);
                connections.appendTag(connectionTag);
            }
        }
        return connections;
    }

    public boolean isOpen()
    {
        return isLarge;
    }

    public void setOpen(boolean b)
    {
        isLarge = b;
        openMenuId = -1;
    }

    public void close()
    {
        isLarge = false;
    }

    public void setConnection(int i, Connection connection)
    {
        connections[i] = connection;
    }

    public void clearConnections()
    {
        connections = new Connection[connections.length];
    }

    public String getComponentName()
    {
        return name;
    }

    public void setComponentName(String name)
    {
        this.name = name;
    }

    public FlowComponent getParent()
    {
        return parent;
    }

    public void setParent(FlowComponent parent)
    {
        if (this.parent != null)
        {
            if (getConnectionSet() == ConnectionSet.INPUT_NODE || getConnectionSet() == ConnectionSet.OUTPUT_NODE)
            {
                this.parent.childrenInputNodes.remove(this);
                this.parent.childrenOutputNodes.remove(this);
                Collections.sort(this.parent.childrenInputNodes);
                Collections.sort(this.parent.childrenOutputNodes);
            }
        }
        this.parent = parent;
        if (this.parent != null)
        {
            if (getConnectionSet() == ConnectionSet.INPUT_NODE && !this.parent.childrenInputNodes.contains(this))
            {
                this.parent.childrenInputNodes.add(this);
                Collections.sort(this.parent.childrenInputNodes);
            } else if (getConnectionSet() == ConnectionSet.OUTPUT_NODE && !this.parent.childrenOutputNodes.contains(this))
            {
                this.parent.childrenOutputNodes.add(this);
                Collections.sort(this.parent.childrenOutputNodes);
            }
        }
    }

    @Override
    public int hashCode()
    {
        return id;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FlowComponent component = (FlowComponent)o;

        return id == component.id;

    }

    public List<FlowComponent> getChildrenOutputNodes()
    {
        return childrenOutputNodes;
    }

    public List<FlowComponent> getChildrenInputNodes()
    {
        return childrenInputNodes;
    }

    @Override
    public int compareTo(@Nonnull FlowComponent o)
    {
        return id == o.id ? 0 : id < o.id ? -1 : 1;
    }

    public void resetPosition()
    {
        resetTimer = 20;
    }

    public boolean isInventoryListDirty()
    {
        return isInventoryListDirty;
    }

    public void setInventoryListDirty(boolean inventoryListDirty)
    {
        isInventoryListDirty = inventoryListDirty;
    }

    public void doScroll(int scroll)
    {
        if (isLarge)
        {
            if (openMenuId != -1)
            {
                menus.get(openMenuId).doScroll(scroll);
            }
        }
    }

    public void onGuiClosed()
    {
        for (Menu menu : menus)
        {
            menu.onGuiClosed();
        }
    }

//    public void setNameEdited(boolean b)
//    {
//        isEditing = b;
//        if (b)
//        {
//            textBox.setTextAndCursor("");
//        } else
//        {
//            textBox.setText(null);
//        }
//    }
//
//    public void refreshEditing(String name)
//    {
//        textBox.setTextAndCursor(name);
//    }
//
//    public boolean isNameBeingEdited()
//    {
//        return isEditing;
//    }
//
//    public int getOverrideY()
//    {
//        return overrideY;
//    }
//
//    public void setOverrideY(int overrideY)
//    {
//        this.overrideY = overrideY;
//    }
//
//    public int getOverrideX()
//    {
//        return overrideX;
//    }
//
//    public void setOverrideX(int overrideX)
//    {
//        this.overrideX = overrideX;
//    }

    public Connection[] getConnections()
    {
        return connections;
    }

//    public int getOpenMenuId()
//    {
//        return openMenuId;
//    }
//
//    public void setOpenMenuId(int openMenuId)
//    {
//        this.openMenuId = openMenuId;
//    }

    @Override
    @SideOnly(Side.CLIENT)
    public ASMPacket getSyncPacket()
    {
        return PacketHandler.getComponentPacket(this);
    }

    @Override
    public void registerSyncable(IPacketSync networkSync)
    {
        networkSync.setId(networkSyncList.size());
        networkSyncList.add(networkSync);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void sendPacketToServer(ASMPacket packet)
    {
        PacketHandler.sendDataToServer(packet);
    }

    @Override
    public boolean readData(ASMPacket packet)
    {
        int id = packet.readUnsignedByte();
        if (id < networkSyncList.size())
        {
            return networkSyncList.get(id).readData(packet);
        } else
        {
            switch (id - networkSyncList.size())
            {
                case 0:
                    x = packet.readShort();
                    y = packet.readShort();
                    break;
                case 1:
                    int connectionId = packet.readByte();
                    if (packet.readBoolean())
                    {
                        Connection connection = new Connection(this.id, connectionId, packet.readVarIntFromBuffer(), packet.readByte());
                        connection.setConnection(manager);
                    } else
                    {
                        connections[connectionId].deleteConnection(manager);
                    }
                    break;
                case 2:
                    connectionId = packet.readByte();
                    Connection connection = connections[connectionId];
                    int nodeId = Math.min(packet.readByte(), connection.nodes.size());
                    switch (packet.readByte())
                    {
                        case 0:
                            connection.nodes.remove(nodeId);
                            break;
                        case 1:
                            connection.nodes.add(nodeId, new Point());
                        case 2:
                            Point point = connection.nodes.get(nodeId);
                            point.setX(packet.readShort());
                            point.setY(packet.readShort());
                            break;
                    }
                    break;
                case 3:
                    parentLoadId = packet.readVarIntFromBuffer();
                    linkAfterLoad();
                    break;
            }
            return false;
        }
    }

    private void sendLocationData()
    {
        ASMPacket dw = PacketHandler.getComponentPacket(this);
        dw.writeByte(networkSyncList.size());
        dw.writeShort(x);
        dw.writeShort(y);
        PacketHandler.sendDataToServer(dw);
    }

    private void sendConnectionData(int connectionId, boolean add, int targetComponent, int targetConnection)
    {
        ASMPacket packet = getSyncPacket();
        packet.writeByte(networkSyncList.size() + 1);
        packet.writeByte(connectionId);
        packet.writeBoolean(add);
        if (add)
        {
            packet.writeVarIntToBuffer(targetComponent);
            packet.writeByte(targetConnection);
        }
        packet.sendServerPacket();
    }

    public void sendConnectionNodeData(int connectionId, int nodeId, int action, int x, int y)
    {
        ASMPacket packet = getSyncPacket();
        packet.writeByte(networkSyncList.size() + 2);
        packet.writeByte(connectionId);
        packet.writeByte(nodeId);
        packet.writeByte(action);
        if (action != 0)
        {
            packet.writeShort(x);
            packet.writeShort(y);
        }
        packet.sendServerPacket();
    }
}
