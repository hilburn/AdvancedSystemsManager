package advancedsystemsmanager.flow.menus;

import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.elements.TextBoxNumber;
import advancedsystemsmanager.flow.setting.FuzzyMode;
import advancedsystemsmanager.flow.setting.ItemSetting;
import advancedsystemsmanager.flow.setting.Setting;
import advancedsystemsmanager.client.gui.GuiManager;
import advancedsystemsmanager.helpers.CollisionHelper;
import advancedsystemsmanager.network.ASMPacket;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.registry.ThemeHandler;
import advancedsystemsmanager.threading.SearchItems;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MenuItem extends MenuStuff<ItemStack>
{
    public static final int DMG_VAL_TEXT_X = 15;
    public static final int DMG_VAL_TEXT_Y = 55;

    public static final int ARROW_SRC_X = 72;
    public static final int ARROW_SRC_Y = 18;
    public static final int ARROW_WIDTH = 9;
    public static final int ARROW_HEIGHT = 12;
    public static final int ARROW_X_LEFT = 5;
    public static final int ARROW_X_RIGHT = 109;
    public static final int ARROW_Y = 37;
    public static final int ARROW_TEXT_Y = 40;


    public TextBoxNumber damageValueTextBox;
    public TextBoxNumber amountTextBox;

    public MenuItem(FlowComponent parent)
    {
        this(parent, true);
    }

    public MenuItem(FlowComponent parent, boolean whitelist)
    {
        super(parent);

        if (settings.get(0).isAmountSpecific())
        {
            numberTextBoxes.addTextBox(amountTextBox = new TextBoxNumber(getParent(), 80, 24, 3, true)
            {
                @Override
                public boolean isVisible()
                {
                    return selectedSetting.isLimitedByAmount();
                }

                @Override
                public boolean readData(ASMPacket packet)
                {
                    Setting<ItemStack> setting = settings.get(packet.readByte());
                    super.readData(packet);
                    setting.setAmount(number);
                    return false;
                }

                @Override
                public boolean writeData(ASMPacket packet)
                {
                    packet.writeByte(selectedSetting.getId());
                    selectedSetting.setAmount(number);
                    return super.writeData(packet);
                }
            });
        }

        numberTextBoxes.addTextBox(damageValueTextBox = new TextBoxNumber(getParent(), 70, 52, 5, true)
        {
            @Override
            public boolean isVisible()
            {
                return getSelectedSetting().canChangeMetaData() && getSelectedSetting().getFuzzyMode().requiresMetaData();
            }

            @Override
            public boolean readData(ASMPacket packet)
            {
                Setting<ItemStack> setting = settings.get(packet.readByte());
                super.readData(packet);
                setting.getContent().setItemDamage(number);
                return false;
            }

            @Override
            public boolean writeData(ASMPacket packet)
            {
                packet.writeByte(selectedSetting.getId());
                selectedSetting.getContent().setItemDamage(number);
                return super.writeData(packet);
            }
        });

        setFirstRadioButtonSelected(whitelist);
    }

    public ItemSetting getSelectedSetting()
    {
        return (ItemSetting)selectedSetting;
    }

    @SideOnly(Side.CLIENT)
    public static List<String> getToolTip(ItemStack itemStack)
    {
        try
        {
            return itemStack.getTooltip(Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().gameSettings.advancedItemTooltips);
        } catch (Exception ex)
        {
            if (itemStack.getItemDamage() == 0)
            {
                return new ArrayList<String>();
            } else
            {
                ItemStack newItem = itemStack.copy();
                newItem.setItemDamage(0);
                return getToolTip(newItem);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public static String getDisplayName(ItemStack itemStack)
    {
        try
        {
            return itemStack.getDisplayName();
        } catch (Exception ex)
        {
            if (itemStack.getItemDamage() == 0)
            {
                return "";
            } else
            {
                ItemStack newItem = itemStack.copy();
                newItem.setItemDamage(0);
                return getDisplayName(newItem);
            }
        }
    }

    @Override
    protected boolean readSpecificData(ASMPacket packet, int action, Setting<ItemStack> setting)
    {
        switch (action)
        {
            case 3:
                setting.setFuzzyType(packet.readByte());
                break;
        }
        return false;
    }

    @Override
    public Setting<ItemStack> getSetting(int id)
    {
        return new ItemSetting(id);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void drawResultObject(GuiManager gui, ItemStack obj, int x, int y)
    {
        gui.drawItemStack(obj, x, y);
//        gui.drawItemAmount(obj, x, y);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void drawSettingObject(GuiManager gui, Setting<ItemStack> setting, int x, int y)
    {
        drawResultObject(gui, setting.getContent(), x, y);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public List<String> getResultObjectMouseOver(ItemStack o)
    {
        return getToolTip(o);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public List<String> getSettingObjectMouseOver(Setting<ItemStack> setting)
    {
        return getResultObjectMouseOver(setting.getContent());
    }

    @Override
    public void updateTextBoxes()
    {
        if (amountTextBox != null)
        {
            amountTextBox.setNumber(selectedSetting.getAmount());
        }
        damageValueTextBox.setNumber(getSelectedSetting().getItem().getItemDamage());
    }

    @SideOnly(Side.CLIENT)
    @Override
    public List<ItemStack> updateSearch(String search, boolean showAll)
    {
        Thread thread = new Thread(new SearchItems(search, scrollControllerSearch, showAll));
        thread.start();
        return scrollControllerSearch.getResult();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void drawInfoMenuContent(GuiManager gui, int mX, int mY)
    {
        if (damageValueTextBox.isVisible())
        {
            gui.drawString(Names.DAMAGE_VALUE, DMG_VAL_TEXT_X, DMG_VAL_TEXT_Y, 0.7F, 0x404040);
        }

        for (int i = 0; i < 2; i++)
        {
            int x = i == 0 ? ARROW_X_LEFT : ARROW_X_RIGHT;
            int y = ARROW_Y;


            gui.drawColouredTexture(x, y, ARROW_SRC_X + i * ARROW_WIDTH, ARROW_SRC_Y, ARROW_WIDTH, ARROW_HEIGHT, 0.7f, ThemeHandler.theme.menus.radioButtons.getColour(true, CollisionHelper.inBounds(x, y, ARROW_WIDTH, ARROW_HEIGHT, mX, mY)));
        }
        gui.drawCenteredString(getSelectedSetting().getFuzzyMode().toString(), ARROW_X_LEFT, ARROW_TEXT_Y, 0.7F, ARROW_X_RIGHT - ARROW_X_LEFT + ARROW_WIDTH, 0x404040);
    }

    @Override
    public void onClick(int mX, int mY, int button)
    {
        super.onClick(mX, mY, button);

        if (isEditing())
        {
            for (int i = -1; i <= 1; i += 2)
            {
                int x = i == 1 ? ARROW_X_RIGHT : ARROW_X_LEFT;
                int y = ARROW_Y;


                if (CollisionHelper.inBounds(x, y, ARROW_WIDTH, ARROW_HEIGHT, mX, mY))
                {
                    int id = getSelectedSetting().getFuzzyMode().ordinal();
                    id += i;
                    if (id < 0)
                    {
                        id = FuzzyMode.values().length - 1;
                    } else if (id == FuzzyMode.values().length)
                    {
                        id = 0;
                    }
                    getSelectedSetting().setFuzzyType(id);
                    ASMPacket packet = getSyncPacket();
                    packet.writeByte(3);
                    packet.writeByte(id);
                    packet.sendServerPacket();
                    break;
                }
            }

            /*if (CollisionHelper.inBounds(EDIT_ITEM_X, EDIT_ITEM_Y, ITEM_SIZE, ITEM_SIZE, mX, mY) && getSelectedSetting().getItem().hasTagCompound()) {
                getParent().getManager().specialRenderer = new NBTRenderer(getSelectedSetting().getItem().getTagCompound());
            }*/
        }
    }

    @Override
    public String getName()
    {
        return Names.ITEM_MENU;
    }
}
