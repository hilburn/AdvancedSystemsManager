package advancedsystemsmanager.client.gui;

import advancedsystemsmanager.animation.AnimationController;
import advancedsystemsmanager.api.gui.IGuiElement;
import advancedsystemsmanager.api.network.IPacketSync;
import advancedsystemsmanager.containers.ContainerManager;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.client.gui.pong.GuiPong;
import advancedsystemsmanager.helpers.CollisionHelper;
import advancedsystemsmanager.network.MessageHandler;
import advancedsystemsmanager.network.message.SecretMessage;
import advancedsystemsmanager.reference.Mods;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.registry.ThemeHandler;
import advancedsystemsmanager.tileentities.manager.TileEntityManager;
import codechicken.nei.VisiblityData;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;


@SideOnly(Side.CLIENT)
public class GuiManager extends GuiBase
{
    private static final ResourceLocation background = registerTexture("background");
    private static final ResourceLocation COMPONENTS = registerTexture("components");
    public static int GUI_HEIGHT = 256;
    public static int GUI_WIDTH = 512;
    public static int Z_LEVEL_COMPONENT_OPEN_DIFFERENCE = 70;
    public static int Z_LEVEL_COMPONENT_CLOSED_DIFFERENCE = 1;
    public static int Z_LEVEL_COMPONENT_START = 500;
    public static int Z_LEVEL_OPEN_MAXIMUM = 5;
    public IPacketSync selectedComponent;
    public IGuiElement<GuiManager> hoverComponent;
    private long lastTicks;
    private AnimationController controller;
    private boolean useButtons = true;
    private boolean useInfo = true;
    private boolean useMouseOver = true;
    private boolean closeSpecialRenderer;
    private TileEntityManager manager;
    private List<SecretCode> codes = new ArrayList<SecretCode>();

    {
//        codes.add(new SecretCode("animate")
//        {
//            @Override
//            protected void trigger()
//            {
//                controller = new AnimationController(manager, 2);
//            }
//        });
//        codes.add(new SecretCode("animslow")
//        {
//            @Override
//            protected void trigger()
//            {
//                controller = new AnimationController(manager, 1);
//            }
//        });
//        codes.add(new SecretCode("animfast")
//        {
//            @Override
//            protected void trigger()
//            {
//                controller = new AnimationController(manager, 5);
//            }
//        });
//        codes.add(new SecretCode("animrapid")
//        {
//            @Override
//            protected void trigger()
//            {
//                controller = new AnimationController(manager, 20);
//            }
//        });
//        codes.add(new SecretCode("animinstant")
//        {
//            @Override
//            protected void trigger()
//            {
//                controller = new AnimationController(manager, 100);
//            }
//        });
        codes.add(new SecretCode("buttons")
        {
            @Override
            protected void trigger()
            {
                useButtons = !useButtons;
            }
        });
        codes.add(new SecretCode("info")
        {
            @Override
            protected void trigger()
            {
                useInfo = !useInfo;
            }
        });
        codes.add(new SecretCode("mouse")
        {
            @Override
            protected void trigger()
            {
                useMouseOver = !useMouseOver;
            }
        });
        codes.add(new SecretCode(new char[]{200, 200, 208, 208, 203, 205, 203, 205, 'b', 'a'})
        {
            @Override
            protected void trigger()
            {
                MessageHandler.INSTANCE.sendToServer(new SecretMessage());
            }
        });
        codes.add(new SecretCode("pong")
        {
            @Override
            protected void trigger()
            {
                manager.specialRenderer = new GuiPong();
            }
        });
    }

    public GuiManager(TileEntityManager manager, InventoryPlayer player)
    {
        super(new ContainerManager(manager, player));
        xSize = GUI_WIDTH;
        ySize = GUI_HEIGHT;
        this.zLevel = 0;

        this.manager = manager;
        Keyboard.enableRepeatEvents(true);
    }

    @Override
    public ResourceLocation getComponentResource()
    {
        return COMPONENTS;
    }

    @Override
    @Optional.Method(modid = Mods.NEI)
    public VisiblityData modifyVisiblity(GuiContainer guiContainer, VisiblityData visiblityData)
    {
        visiblityData.showNEI = false;
        return visiblityData;
    }

    @Override
    protected void drawGuiBackground(float partialTick, int mX, int mY)
    {
        mX = scaleX(mX);
        mY = scaleY(mY);

        drawBackground();

        mX -= guiLeft;
        mY -= guiTop;

        bindTexture(COMPONENTS);

        if (closeSpecialRenderer)
        {
            manager.specialRenderer = null;
            closeSpecialRenderer = false;
        } else if (hasSpecialRenderer())
        {
            getSpecialRenderer().draw(this, mX, mY);
            getSpecialRenderer().drawMouseOver(this, mX, mY);
            return;
        }

        if (useButtons)
        {
            manager.buttons.draw(this, mX, mY, 0);
        }


        //update components completely independent on their visibility
        long ms = Minecraft.getSystemTime();
        float elapsedSeconds = (ms - this.lastTicks) / 1000F;
//        if (controller != null)
//        {
//            controller.update(elapsedSeconds);
//        }
        for (FlowComponent component : manager.getFlowItems())
        {
            component.update(elapsedSeconds);
        }
        this.lastTicks = ms;

        int zLevel = Z_LEVEL_COMPONENT_START;
        int openCount = 0;
        for (FlowComponent itemBase : manager.getZLevelRenderingList())
        {
            if (itemBase.isVisible())
            {
                if (itemBase.isOpen() && openCount == Z_LEVEL_OPEN_MAXIMUM)
                {
                    itemBase.close();
                }

                if (itemBase.isOpen())
                {
                    zLevel -= Z_LEVEL_COMPONENT_OPEN_DIFFERENCE;
                    openCount++;
                } else
                {
                    zLevel -= Z_LEVEL_COMPONENT_CLOSED_DIFFERENCE;
                }
                itemBase.draw(this, mX, mY, zLevel);

                if (itemBase.isBeingMoved() || CollisionHelper.inBounds(itemBase.getX(), itemBase.getY(), itemBase.getComponentWidth(), itemBase.getComponentHeight(), mX, mY))
                {
                    CollisionHelper.disable();
                }
            }
        }
        CollisionHelper.enable();

        if (useInfo)
        {
            drawString(getInfo(), 5, ySize - 13, 1F, 0x606060);
        }

        if (useMouseOver)
        {

            if (useButtons)
            {
                manager.buttons.drawMouseOver(this, mX, mY);
            }

            for (FlowComponent itemBase : manager.getZLevelRenderingList())
            {
                if (itemBase.isVisible())
                {
                    itemBase.drawMouseOver(this, mX, mY);
                    if (itemBase.isBeingMoved() || CollisionHelper.inBounds(itemBase.getX(), itemBase.getY(), itemBase.getComponentWidth(), itemBase.getComponentHeight(), mX, mY))
                    {
                        CollisionHelper.disable();
                    }
                }
            }
        }
        CollisionHelper.enable();
    }

    private void drawBackground()
    {
        bindTexture(background);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawing(GL11.GL_QUAD_STRIP);
        int[] colour = ThemeHandler.theme.managerBackground.getColour();
        tessellator.setColorRGBA(colour[0], colour[1], colour[2], colour[3]);
        tessellator.addVertexWithUV(0, 0, zLevel, 0.0f, 0.0f);
        tessellator.addVertexWithUV(0, 256, zLevel, 0.0f, 1.0f);
        tessellator.addVertexWithUV(256, 0, zLevel, 1.0f, 0.0f);
        tessellator.addVertexWithUV(256, 256, zLevel, 1.0f, 1.0f);
        tessellator.addVertexWithUV(512, 0, zLevel, 0.0f, 0f);
        tessellator.addVertexWithUV(512, 256, zLevel, 0.0f, 1.0f);
        tessellator.draw();
    }

    private boolean hasSpecialRenderer()
    {
        return getSpecialRenderer() != null;
    }

    private IInterfaceRenderer getSpecialRenderer()
    {
        return manager.specialRenderer;
    }

    private String getInfo()
    {
        String ret = StatCollector.translateToLocalFormatted(Names.COMMANDS, manager.getFlowItems().size());

        String path = "";
        FlowComponent component = manager.getSelectedGroup();

        if (component != null)
        {
            ret += "|";
        }
        while (component != null)
        {
            String nextPath = "> " + component.getName() + " " + path;
            if (getStringWidth(ret + nextPath) > xSize - 15)
            {
                path = "... " + path;
                break;
            }
            path = nextPath;
            component = component.getParent();
        }
        ret += path;

        return ret;
    }

    @Override
    protected void mouseClicked(int x, int y, int button)
    {
        x = scaleX(x);
        y = scaleY(y);

        super.mouseClicked(x, y, button);

        x -= guiLeft;
        y -= guiTop;

        if (hasSpecialRenderer())
        {
            getSpecialRenderer().onClick(this, x, y, button);
            return;
        }


        for (ListIterator<FlowComponent> itr = manager.getZLevelRenderingList().listIterator(); itr.hasNext(); )
        {
            FlowComponent itemBase = itr.next();
            if (itemBase.isVisible() && itemBase.onClick(x, y, button))
            {
                itr.remove();
                manager.getZLevelRenderingList().add(0, itemBase);
                setSelected(itemBase);
                return;
            }
        }

        if (useButtons)
        {
            manager.buttons.onClick(x, y, button);
        }
    }

    @Override
    protected void mouseClickMove(int x, int y, int button, long ticks)
    {
        x = scaleX(x);
        y = scaleY(y);

        super.mouseClickMove(x, y, button, ticks);

        x -= guiLeft;
        y -= guiTop;

        if (hasSpecialRenderer())
        {
            getSpecialRenderer().onDrag(this, x, y);
            return;
        }

        if (selectedComponent instanceof FlowComponent)
        {
            FlowComponent dragged = (FlowComponent)selectedComponent;
            dragged.onDrag(x, y, button);
        }
    }

    @Override
    protected void mouseMovedOrUp(int x, int y, int button)
    {
        x = scaleX(x);
        y = scaleY(y);

        super.mouseMovedOrUp(x, y, button);

        x -= guiLeft;
        y -= guiTop;

        if (hasSpecialRenderer())
        {
            getSpecialRenderer().onRelease(this, x, y, button);
            return;
        }

        if (useButtons)
        {
            manager.buttons.onClick(x, y, button, true);
        }

        if (selectedComponent instanceof FlowComponent)
        {
            FlowComponent released = (FlowComponent)selectedComponent;
            if (!manager.serverPacket)
            {
                for (FlowComponent itemBase : manager.getZLevelRenderingList())
                {
                    if (itemBase.isVisible())
                    {
                        itemBase.onRelease(x, y, button);
                    }
                }
            }
            released.postRelease();
        }
    }

    @Override
    protected void keyTyped(char c, int k)
    {
        if (hasSpecialRenderer())
        {
            if (c == 1)
            {
                closeSpecialRenderer = true;
                return;
            }
            if (getSpecialRenderer().onKeyTyped(this, c, k))
            {
                return;
            }
        } else
        {

            if (selectedComponent instanceof FlowComponent)
            {
                if (((FlowComponent)selectedComponent).onKeyStroke(this, c, k) && k != 1) return;
            }

            boolean recognized = false;
            for (SecretCode code : codes)
            {
                if (code.keyTyped(c, k))
                {
                    recognized = true;
                }
            }

            if (recognized)
            {
                return;
            }
        }

        super.keyTyped(c, k);
    }

    @Override
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);

        for (FlowComponent flowComponent : manager.getFlowItems())
        {
            flowComponent.onGuiClosed();
        }

        manager.specialRenderer = null;

        super.onGuiClosed();
    }

    public void setSelected(IPacketSync selected)
    {
        this.selectedComponent = selected;
    }

    public void handleMouseInput()
    {
        super.handleMouseInput();


        int scroll = Mouse.getEventDWheel();
        if (scroll != 0)
        {
            if (hasSpecialRenderer())
            {
                getSpecialRenderer().onScroll(scroll);
                return;
            }

            for (FlowComponent component : manager.getZLevelRenderingList())
            {
                if (component.isVisible())
                {
                    component.doScroll(scroll);
                    return;
                }
            }
        }
    }

    public TileEntityManager getManager()
    {
        return manager;
    }

    private abstract class SecretCode
    {
        private final char[] code;
        private int triggerNumber;

        private SecretCode(String code)
        {
            this(code.toCharArray());
        }

        private SecretCode(char[] code)
        {
            this.code = code;
        }

        public boolean keyTyped(char c, int k)
        {
            if (Character.isAlphabetic(c))
            {
                if (code[triggerNumber] == c)
                {
                    triggerNumber++;
                    if (triggerNumber == code.length)
                    {
                        triggerNumber = 0;
                        trigger();
                    }
                    return true;
                } else if (triggerNumber != 0)
                {
                    triggerNumber = 0;
                    keyTyped(c, k);
                }
            } else if (code[triggerNumber] == k)
            {
                triggerNumber++;
                if (triggerNumber == code.length)
                {
                    triggerNumber = 0;
                    trigger();
                }
                return true;
            }

            return false;
        }

        protected abstract void trigger();
    }

}
