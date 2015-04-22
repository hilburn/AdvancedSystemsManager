package advancedfactorymanager.interfaces;

import advancedfactorymanager.items.ItemLabeler;
import advancedfactorymanager.network.MessageHandler;
import advancedfactorymanager.network.message.LabelSyncMessage;
import codechicken.nei.VisiblityData;
import codechicken.nei.api.INEIGuiHandler;
import codechicken.nei.api.TaggedInventoryArea;
import cpw.mods.fml.common.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

@Optional.Interface(iface = "codechicken.nei.api.INEIGuiHandler", modid = "NotEnoughItems")
public class GuiLabeler extends GuiScreen implements IVerticalScrollContainer, INEIGuiHandler
{
    private static Comparator<GuiTextEntry> ALPHABETICAL_ORDER = new Comparator<GuiTextEntry>()
    {
        @Override
        public int compare(GuiTextEntry o1, GuiTextEntry o2)
        {
            if (o1.isEditing) return 1;
            int res = String.CASE_INSENSITIVE_ORDER.compare(o1.getText(), o2.getText());
            return res == 0 ? o1.getText().compareTo(o2.getText()) : res;
        }
    };
    public static final ResourceLocation TEXTURE = new ResourceLocation("afm", "textures/gui/GuiLabeler.png");
    private static final int GUI_WIDTH = 140;
    private static final int GUI_HEIGHT = 200;
    private static final int SCROLL_Y = 25;
    public static final int SCROLL_X = 9;
    private static final int SCROLL_Y_MAX = 169;
    private static final int SCROLL_X_MAX = 103;
    private static final int ENTRY_HEIGHT = 16;

    private List<GuiTextEntry> strings = new ArrayList<GuiTextEntry>();
    private List<GuiTextEntry> displayStrings;
    private GuiTextEntry selected = null;
    private ItemStack stack;
    private GuiTextField searchBar;
    private GuiVerticalScrollBar scrollBar;
    private EntityPlayer player;
    public int mouseX = 0;
    public int mouseY = 0;
    private int xSize, ySize, guiLeft, guiTop;

    public GuiLabeler(ItemStack stack, EntityPlayer player)
    {
        this.stack = stack;
        for (String string : ItemLabeler.getSavedStrings(stack))
        {
            strings.add(getGuiTextEntry(string));
        }
        this.xSize = GUI_WIDTH;
        this.ySize = GUI_HEIGHT;
        scrollBar = new GuiVerticalScrollBar(this, SCROLL_X_MAX + 4, SCROLL_Y, SCROLL_Y_MAX - SCROLL_Y);
        searchBar = new GuiTextField(110, 12, 9, 10);
        searchBar.setText(ItemLabeler.getLabel(stack));
        searchBar.fixCursorPos();
        displayStrings = getSearchedStrings();
        this.player = player;
    }

    public static GuiTextEntry getGuiTextEntry(String string)
    {
        return new GuiTextEntry(string, ENTRY_HEIGHT, SCROLL_X_MAX - SCROLL_X);
    }

    @Override
    public void initGui()
    {
        super.initGui();
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;
    }

    @Override
    public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_)
    {
        this.drawDefaultBackground();
        this.drawGuiContainerBackgroundLayer(p_73863_3_, p_73863_1_, p_73863_2_);
    }

    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_)
    {
        GL11.glPushMatrix();
        int x = this.guiLeft;
        int y = this.guiTop;
        GL11.glTranslatef(x, y, 0);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        bindTexture(TEXTURE);

        drawTexturedModalRect(0, 0, 0, 0, GUI_WIDTH, GUI_HEIGHT);
        scrollBar.draw();
        searchBar.draw();

        drawRect(SCROLL_X - 1, SCROLL_Y - 1, SCROLL_X_MAX + 1, SCROLL_Y_MAX + 1, 0xff676767);
        drawRect(SCROLL_X, SCROLL_Y, SCROLL_X_MAX, SCROLL_Y_MAX, 0xff9d9d9d);

        drawDisplayStrings();
        GL11.glPopMatrix();
    }

    private int getListViewSize()
    {
        return (SCROLL_Y_MAX - SCROLL_Y) / ENTRY_HEIGHT;
    }

    private void drawDisplayStrings()
    {
        int i = 0;
        int startIndex = Math.round((displayStrings.size() - getListViewSize()) * scrollBar.getScrollValue());
        for (GuiTextEntry entry : displayStrings)
        {
            entry.setY(SCROLL_Y + (i - startIndex) * ENTRY_HEIGHT);
            entry.isVisible = !(entry.y < SCROLL_Y || entry.y + entry.height > SCROLL_Y_MAX);
            entry.draw();
            i++;
        }
    }

    @Override
    protected void keyTyped(char character, int keyCode)
    {
        boolean reset = false;
        if (keyCode == 1)
        {
            this.mc.thePlayer.closeScreen();
        } else if (keyCode == 28)
        {
            if (!isEditing() && isNewEntry(searchBar.getText()))
            {
                strings.add(getGuiTextEntry(searchBar.getText()));
                Collections.sort(strings, ALPHABETICAL_ORDER);
            }
            reset = true;
        }
        searchBar.keyTyped(character, keyCode);
        if (isEditing()) this.selected.setText(searchBar.getText());
        if (reset)
        {
            searchBar.setText("");
            searchBar.fixCursorPos();
            if (this.selected != null)
            {
                if (this.selected.getText().isEmpty())
                {
                    strings.remove(selected);
                } else
                {
                    this.selected.isEditing = false;
                    this.selected.isSelected = false;
                }
                this.selected = null;
            }
        }
        displayStrings = getSearchedStrings();

        scrollBar.setYPos(0);
    }

    private boolean isEditing()
    {
        return this.selected != null && this.selected.isEditing;
    }

    private boolean isNewEntry(String string)
    {
        if (string.isEmpty()) return false;
        for (GuiTextEntry entry : displayStrings)
        {
            if (string.equals(entry.getText())) return false;
        }
        return true;
    }

    public static void bindTexture(ResourceLocation resource)
    {
        Minecraft.getMinecraft().getTextureManager().bindTexture(resource);
    }

    public List<GuiTextEntry> getSearchedStrings()
    {
        List<GuiTextEntry> result = new ArrayList<GuiTextEntry>();
        Pattern pattern = Pattern.compile(Pattern.quote(searchBar.getText()), Pattern.CASE_INSENSITIVE);
        for (GuiTextEntry entry : strings)
        {
            if (pattern.matcher(entry.getText()).find()) result.add(entry);
        }
        return result;
    }

    @Override
    public void onGuiClosed()
    {
        List<String> save = new ArrayList<String>();
        for (GuiTextEntry entry : strings) if (!save.contains(entry.getText())) save.add(entry.getText());
        searchBar.close();
        ItemLabeler.saveStrings(stack, save);
        ItemLabeler.setLabel(stack, searchBar.getText());
        MessageHandler.INSTANCE.sendToServer(new LabelSyncMessage(stack, player));
    }

    @Override
    public void handleMouseInput()
    {
        super.handleMouseInput();
        int i = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int j = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
        mouseX = i - (width - xSize) / 2;
        mouseY = j - (height - ySize) / 2;
        if (isScrollBarActive())
        {
            scrollBar.handleMouseInput();
        }
        if (Mouse.getEventButtonState() && !(mouseX < SCROLL_X || mouseX > SCROLL_X_MAX || mouseY < SCROLL_Y || mouseY > SCROLL_Y_MAX))
        {
            for (GuiTextEntry entry : displayStrings)
            {
                entry.handleMouseInput(mouseX, mouseY);
                if (entry.isSelected)
                {
                    selected = entry;
                    searchBar.setText(entry.getText());
                    searchBar.fixCursorPos();
                }
            }
            displayStrings = getSearchedStrings();
        }
    }

    @Override
    public boolean isScrollBarActive()
    {
        return displayStrings.size() > getListViewSize();
    }

    @Override
    public int getScreenWidth()
    {
        return width;
    }

    @Override
    public int getScreenHeight()
    {
        return height;
    }

    @Override
    public int getGuiWidth()
    {
        return xSize;
    }

    @Override
    public int getGuiHeight()
    {
        return ySize;
    }

    @Override
    public int getScrollAmount()
    {
        return 5;
    }

    @Override
    @Optional.Method(modid = "NotEnoughItems")
    public VisiblityData modifyVisiblity(GuiContainer guiContainer, VisiblityData visiblityData)
    {
        return visiblityData;
    }

    @Override
    @Optional.Method(modid = "NotEnoughItems")
    public Iterable<Integer> getItemSpawnSlots(GuiContainer gui, ItemStack item)
    {
        return null;
    }

    @Override
    @Optional.Method(modid = "NotEnoughItems")
    public boolean hideItemPanelSlot(GuiContainer gui, int x, int y, int w, int h)
    {
        return !(x + w < this.guiLeft || x > this.guiLeft + this.width || y + h < this.guiTop || y > this.guiTop + this.height);
    }

    @Override
    @Optional.Method(modid = "NotEnoughItems")
    public List<TaggedInventoryArea> getInventoryAreas(GuiContainer gui)
    {
        return null;
    }

    @Override
    @Optional.Method(modid = "NotEnoughItems")
    public boolean handleDragNDrop(GuiContainer gui, int mouseX, int mouseY, ItemStack draggedStack, int button)
    {
        return false;
    }
}
