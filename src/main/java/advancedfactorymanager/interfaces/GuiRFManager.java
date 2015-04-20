package advancedfactorymanager.interfaces;

import advancedfactorymanager.animation.AnimationController;
import advancedfactorymanager.components.FlowComponent;
import advancedfactorymanager.helpers.CollisionHelper;
import advancedfactorymanager.helpers.Localization;
import advancedfactorymanager.network.DataWriter;
import advancedfactorymanager.network.PacketHandler;
import advancedfactorymanager.tileentities.TileEntityManager;
import advancedfactorymanager.tileentities.TileEntityManager.Button;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiRFManager extends GuiManager
{
    private static final ResourceLocation BACKGROUND_1 = registerTexture("Background1");
    private static final ResourceLocation BACKGROUND_2 = registerTexture("Background2");
    private static final ResourceLocation COMPONENTS = new ResourceLocation("afm", "textures/gui/FlowComponents.png");
    public static int Z_LEVEL_COMPONENT_OPEN_DIFFERENCE = 100;
    public static int Z_LEVEL_COMPONENT_CLOSED_DIFFERENCE = 1;
    public static int Z_LEVEL_COMPONENT_START = 750;
    public static int Z_LEVEL_OPEN_MAXIMUM = 5;
    private long lastTicks;
    private AnimationController controller;
    private boolean doubleShiftFlag;
    private boolean useGreenScreen;
    private boolean useBlueScreen;
    private boolean usePinkScreen;
    private boolean useButtons = true;
    private boolean useInfo = true;
    private boolean useMouseOver = true;
    private List<GuiRFManager.SecretCode> codes = new ArrayList<SecretCode>();
    private TileEntityManager manager;

    public GuiRFManager(TileEntityManager manager, InventoryPlayer player)
    {
        super(manager, player);
        this.codes.add(new GuiRFManager.SecretCode("animate")
        {
            protected void trigger()
            {
                GuiRFManager.this.controller = new AnimationController(GuiRFManager.this.manager, 2);
            }
        });
        this.codes.add(new GuiRFManager.SecretCode("animslow")
        {
            protected void trigger()
            {
                GuiRFManager.this.controller = new AnimationController(GuiRFManager.this.manager, 1);
            }
        });
        this.codes.add(new GuiRFManager.SecretCode("animfast")
        {
            protected void trigger()
            {
                GuiRFManager.this.controller = new AnimationController(GuiRFManager.this.manager, 5);
            }
        });
        this.codes.add(new GuiRFManager.SecretCode("animrapid")
        {
            protected void trigger()
            {
                GuiRFManager.this.controller = new AnimationController(GuiRFManager.this.manager, 20);
            }
        });
        this.codes.add(new GuiRFManager.SecretCode("animinstant")
        {
            protected void trigger()
            {
                GuiRFManager.this.controller = new AnimationController(GuiRFManager.this.manager, 100);
            }
        });
        this.codes.add(new GuiRFManager.SecretCode("green")
        {
            protected void trigger()
            {
                GuiRFManager.this.useGreenScreen = !GuiRFManager.this.useGreenScreen;
                GuiRFManager.this.useBlueScreen = false;
                GuiRFManager.this.usePinkScreen = false;
            }
        });
        this.codes.add(new GuiRFManager.SecretCode("blue")
        {
            protected void trigger()
            {
                GuiRFManager.this.useBlueScreen = !GuiRFManager.this.useBlueScreen;
                GuiRFManager.this.useGreenScreen = false;
                GuiRFManager.this.usePinkScreen = false;
            }
        });
        this.codes.add(new GuiRFManager.SecretCode("pink")
        {
            protected void trigger()
            {
                GuiRFManager.this.usePinkScreen = !GuiRFManager.this.usePinkScreen;
                GuiRFManager.this.useGreenScreen = false;
                GuiRFManager.this.useBlueScreen = false;
            }
        });
        this.codes.add(new GuiRFManager.SecretCode("buttons")
        {
            protected void trigger()
            {
                GuiRFManager.this.useButtons = !GuiRFManager.this.useButtons;
            }
        });
        this.codes.add(new GuiRFManager.SecretCode("info")
        {
            protected void trigger()
            {
                GuiRFManager.this.useInfo = !GuiRFManager.this.useInfo;
            }
        });
        this.codes.add(new GuiRFManager.SecretCode("mouse")
        {
            protected void trigger()
            {
                GuiRFManager.this.useMouseOver = !GuiRFManager.this.useMouseOver;
            }
        });
        this.xSize = 512;
        this.ySize = 256;
        this.manager = manager;
        Keyboard.enableRepeatEvents(true);
    }

    public ResourceLocation getComponentResource()
    {
        return COMPONENTS;
    }

    public void drawWorldBackground(int val)
    {
        if (this.usePinkScreen)
        {
            drawRect(0, 0, this.width, this.height, -1310580);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        } else if (this.useBlueScreen)
        {
            drawRect(0, 0, this.width, this.height, -16774511);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        } else if (this.useGreenScreen)
        {
            drawRect(0, 0, this.width, this.height, -16711936);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        } else
        {
            super.drawWorldBackground(val);
        }

    }

    protected void drawGuiContainerBackgroundLayer(float f, int x, int y)
    {
        if (!this.useGreenScreen && !this.useBlueScreen && !this.usePinkScreen)
        {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            bindTexture(BACKGROUND_1);
            this.drawTexture(0, 0, 0, 0, 256, 256);
            bindTexture(BACKGROUND_2);
            this.drawTexture(256, 0, 0, 0, 256, 256);
        }

        x -= this.guiLeft;
        y -= this.guiTop;
        bindTexture(COMPONENTS);
        if (this.hasSpecialRenderer())
        {
            this.getSpecialRenderer().draw(this, x, y);
            this.getSpecialRenderer().drawMouseOver(this, x, y);
        } else
        {
            if (this.useButtons)
            {
                for (int ticks = 0; ticks < this.manager.buttons.size(); ++ticks)
                {
                    Button button = this.manager.buttons.get(ticks);
                    if (button.isVisible())
                    {
                        int elapsedSeconds = CollisionHelper.inBounds(button.getX(), button.getY(), 14, 14, x, y) ? 1 : 0;
                        int column = ticks / 20;
                        this.drawTexture(button.getX(), button.getY(), 242, elapsedSeconds * 14, 14, 14);
                        this.drawTexture(button.getX() + 1, button.getY() + 1, 230 - column * 12, (ticks % 20) * 12, 12, 12);
                    }
                }
            }

            long var11 = Minecraft.getSystemTime();
            float var12 = (float)(var11 - this.lastTicks) / 1000.0F;
            if (this.controller != null)
            {
                this.controller.update(var12);
            }

            for (FlowComponent openCount : this.manager.getFlowItems())
            {
                openCount.update(var12);
            }

            this.lastTicks = var11;
            int var13 = Z_LEVEL_COMPONENT_START;
            int var14 = 0;

            FlowComponent itemBase;
            for (int i$ = 0; i$ < this.manager.getZLevelRenderingList().size(); ++i$)
            {
                itemBase = this.manager.getZLevelRenderingList().get(i$);
                if (itemBase.isVisible())
                {
                    if (itemBase.isOpen() && var14 == Z_LEVEL_OPEN_MAXIMUM)
                    {
                        itemBase.close();
                    }

                    if (itemBase.isOpen())
                    {
                        var13 -= Z_LEVEL_COMPONENT_OPEN_DIFFERENCE;
                        ++var14;
                    } else
                    {
                        var13 -= Z_LEVEL_COMPONENT_CLOSED_DIFFERENCE;
                    }

                    itemBase.draw(this, x, y, var13);
                    if (itemBase.isBeingMoved() || CollisionHelper.inBounds(itemBase.getX(), itemBase.getY(), itemBase.getComponentWidth(), itemBase.getComponentHeight(), x, y))
                    {
                        CollisionHelper.disableInBoundsCheck = true;
                    }
                }
            }

            CollisionHelper.disableInBoundsCheck = false;
            if (this.useInfo)
            {
                this.drawString(this.getInfo(), 5, this.ySize - 13, 1.0F, 6316128);
            }

            if (this.useMouseOver)
            {
                Iterator var15;
                if (this.useButtons)
                {
                    var15 = this.manager.buttons.iterator();

                    while (var15.hasNext())
                    {
                        Button var16 = (Button)var15.next();
                        if (var16.isVisible() && CollisionHelper.inBounds(var16.getX(), var16.getY(), 14, 14, x, y))
                        {
                            this.drawMouseOver(var16.getMouseOver(), x, y);
                        }
                    }
                }

                var15 = this.manager.getZLevelRenderingList().iterator();

                while (var15.hasNext())
                {
                    itemBase = (FlowComponent)var15.next();
                    if (itemBase.isVisible())
                    {
                        itemBase.drawMouseOver(this, x, y);
                        if (itemBase.isBeingMoved() || CollisionHelper.inBounds(itemBase.getX(), itemBase.getY(), itemBase.getComponentWidth(), itemBase.getComponentHeight(), x, y))
                        {
                            CollisionHelper.disableInBoundsCheck = true;
                        }
                    }
                }
            }

            CollisionHelper.disableInBoundsCheck = false;
            if (!Keyboard.isKeyDown(54) && this.doubleShiftFlag)
            {
                this.doubleShiftFlag = false;
            }

        }
    }

    public void handleMouseInput()
    {
        super.handleMouseInput();
        int scroll = Mouse.getEventDWheel();
        if (scroll != 0)
        {
            if (this.hasSpecialRenderer())
            {
                this.getSpecialRenderer().onScroll(scroll);
                return;
            }

            for (FlowComponent component : this.manager.getZLevelRenderingList())
            {
                if (component.isVisible())
                {
                    component.doScroll(scroll);
                    return;
                }
            }
        }

    }

    private String getInfo()
    {
        String ret = Localization.COMMANDS.toString() + ": " + this.manager.getFlowItems().size() + "  ";
        String path = "";
        FlowComponent component = this.manager.getSelectedComponent();
        if (component != null)
        {
            ret = ret + "|";
        }

        while (component != null)
        {
            String nextPath = "> " + component.getName() + " " + path;
            if (this.getStringWidth(ret + nextPath) > this.xSize - 15)
            {
                path = "... " + path;
                break;
            }

            path = nextPath;
            component = component.getParent();
        }

        ret = ret + path;
        return ret;
    }

    protected void keyTyped(char c, int k)
    {
        if (this.hasSpecialRenderer())
        {
            this.getSpecialRenderer().onKeyTyped(this, c, k);
        } else
        {
            if (k == 54 && !this.doubleShiftFlag)
            {
                DataWriter recognized = PacketHandler.getWriterForServerActionPacket();
                PacketHandler.sendDataToServer(recognized);
                this.doubleShiftFlag = true;
            }

            for (FlowComponent i$ : this.manager.getZLevelRenderingList())
            {
                if (i$.isVisible() && i$.onKeyStroke(this, c, k) && k != 1)
                {
                    return;
                }
            }

            boolean recognized2 = false;

            for (GuiRFManager.SecretCode code : this.codes)
            {
                if (code.keyTyped(c))
                {
                    recognized2 = true;
                }
            }

            if (recognized2)
            {
                return;
            }
        }

        super.keyTyped(c, k);
    }

    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);

        for (FlowComponent flowComponent : this.manager.getFlowItems())
        {
            flowComponent.onGuiClosed();
        }

        super.onGuiClosed();
    }

    @Override
    public void drawItemStack(ItemStack itemstack, int x, int y)
    {
        GL11.glPushMatrix();
        RenderHelper.enableGUIStandardItemLighting();
        GL11.glDisable(2896);
        GL11.glEnable(32826);
        GL11.glEnable(2903);
        GL11.glEnable(2896);
        itemRender.zLevel = 1.0F;

        try
        {
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            itemRender.renderItemAndEffectIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), itemstack, x + this.guiLeft, y + this.guiTop);
            itemRender.renderItemOverlayIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), itemstack, x + this.guiLeft, y + this.guiTop, "");
        } catch (Exception var9)
        {
            if (itemstack != null && itemstack.getItem() != null && itemstack.getItemDamage() != 0)
            {
                ItemStack newStack = itemstack.copy();
                newStack.setItemDamage(0);
                this.drawItemStack(newStack, x, y);
            }
        } finally
        {
            itemRender.zLevel = 0.0F;
            bindTexture(this.getComponentResource());
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glDisable(2896);
            GL11.glBlendFunc(770, 771);
            GL11.glEnable(3008);
            GL11.glPopMatrix();
        }
    }

    public TileEntityManager getManager()
    {
        return this.manager;
    }

    private boolean hasSpecialRenderer()
    {
        return this.getSpecialRenderer() != null;
    }

    private IInterfaceRenderer getSpecialRenderer()
    {
        return this.manager.specialRenderer;
    }

    private abstract class SecretCode
    {
        private final String code;
        private int triggerNumber;

        private SecretCode(String code)
        {
            this.code = code;
        }

        public boolean keyTyped(char c)
        {
            if (Character.isAlphabetic(c))
            {
                if (this.code.charAt(this.triggerNumber) == c)
                {
                    if (this.triggerNumber + 1 > this.code.length() - 1)
                    {
                        this.triggerNumber = 0;
                        this.trigger();
                    } else
                    {
                        ++this.triggerNumber;
                    }

                    return true;
                }

                if (this.triggerNumber != 0)
                {
                    this.triggerNumber = 0;
                    this.keyTyped(c);
                }
            }

            return false;
        }

        protected abstract void trigger();
    }
}
