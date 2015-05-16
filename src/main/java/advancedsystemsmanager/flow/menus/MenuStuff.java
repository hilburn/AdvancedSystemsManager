package advancedsystemsmanager.flow.menus;


import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.elements.*;
import advancedsystemsmanager.flow.setting.Setting;
import advancedsystemsmanager.gui.ContainerManager;
import advancedsystemsmanager.gui.GuiManager;
import advancedsystemsmanager.helpers.CollisionHelper;
import advancedsystemsmanager.network.DataBitHelper;
import advancedsystemsmanager.network.DataWriter;
import advancedsystemsmanager.network.PacketHandler;
import advancedsystemsmanager.reference.Names;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public abstract class MenuStuff<Type> extends Menu
{
    public static final int RADIO_BUTTON_X_LEFT = 5;
    public static final int RADIO_BUTTON_X_RIGHT = 65;
    public static final int RADIO_BUTTON_Y = 5;
    public static final int ITEM_SIZE = 16;
    public static final int ITEM_SIZE_WITH_MARGIN = 20;
    public static final int ITEM_X = 5;
    public static final int SETTING_SRC_X = 0;
    public static final int SETTING_SRC_Y = 189;
    public static final int EDIT_ITEM_X = 5;
    public static final int EDIT_ITEM_Y = 5;
    public static final int BACK_SRC_X = 46;
    public static final int BACK_SRC_Y = 52;
    public static final int BACK_SIZE_W = 9;
    public static final int BACK_SIZE_H = 9;
    public static final int BACK_X = 108;
    public static final int BACK_Y = 57;
    public static final int DELETE_SRC_X = 0;
    public static final int DELETE_SRC_Y = 130;
    public static final int DELETE_SIZE_W = 32;
    public static final int DELETE_SIZE_H = 11;
    public static final int DELETE_X = 85;
    public static final int DELETE_Y = 3;
    public static final int DELETE_TEXT_Y = 3;
    public static final String NBT_RADIO_SELECTION = "FirstSelected";
    public static final String NBT_SETTINGS = "Settings";
    public static final String NBT_SETTING_ID = "Id";
    public static final String NBT_SETTING_USE_SIZE = "SizeLimit";
    public ScrollController scrollControllerSearch;
    public ScrollController<Setting<Type>> scrollControllerSelected;
    public List<Setting<Type>> settings;
    public List<Setting<Type>> externalSettings;
    public Setting selectedSetting;
    public boolean editSetting;
    public TextBoxNumberList numberTextBoxes;
    public RadioButtonList radioButtons;
    public CheckBoxList checkBoxes;

    public MenuStuff(FlowComponent parent, Class<? extends Setting> settingClass)
    {
        super(parent);


        settings = new ArrayList<Setting<Type>>();
        externalSettings = new ArrayList<Setting<Type>>();
        for (int i = 0; i < getSettingCount(); i++)
        {
            try
            {
                Constructor<? extends Setting> constructor = settingClass.getConstructor(int.class);
                Object obj = constructor.newInstance(i);
                Setting<Type> setting = (Setting<Type>)obj;
                settings.add(setting);
                externalSettings.add(setting);
            } catch (Exception ex)
            {
                System.err.println("Failed to create setting");
            }

        }
        numberTextBoxes = new TextBoxNumberList();


        radioButtons = new RadioButtonList()
        {
            @Override
            public void updateSelectedOption(int selectedOption)
            {
                this.selectedOption = selectedOption;
                needSync = true;
            }
        };

        initRadioButtons();

        checkBoxes = new CheckBoxList();
        if (settings.get(0).isAmountSpecific())
        {
            checkBoxes.addCheckBox(new CheckBox(Names.SPECIFY_AMOUNT, 5, 25)
            {
                @Override
                public void setValue(boolean val)
                {
                    selectedSetting.setLimitedByAmount(val);
                }

                @Override
                public boolean getValue()
                {
                    return selectedSetting.isLimitedByAmount();
                }

                @Override
                public void onUpdate()
                {
                    needSync = true;
                }
            });
        }

        //final MenuStuff self = this;
        scrollControllerSearch = new ScrollController(true)
        {
            @Override
            public List updateSearch(String search, boolean all)
            {
                if (search.equals(""))
                {
                    return new ArrayList();
                }

                return MenuStuff.this.updateSearch(search, all);
            }

            @Override
            public void onClick(Object o, int mX, int mY, int button)
            {
                selectedSetting.setContent(o);
                selectedSetting = null;
                updateScrolling();
                needSync = true;
            }

            @Override
            public void draw(GuiManager gui, Object o, int x, int y, boolean hover)
            {
                drawResultObject(gui, o, x, y);
            }

            @Override
            public void drawMouseOver(GuiManager gui, Object o, int mX, int mY)
            {
                if (o != null)
                {
                    gui.drawMouseOver(getResultObjectMouseOver(o), mX, mY);
                }
            }
        };

        scrollControllerSelected = new ScrollController<Setting<Type>>(false)
        {
            @Override
            public List<Setting<Type>> updateSearch(String search, boolean all)
            {
                return settings;
            }

            @Override
            public void onClick(Setting setting, int mX, int mY, int button)
            {
                selectedSetting = setting;
                editSetting = button == 1 && doAllowEdit();


                if (editSetting && !selectedSetting.isValid())
                {
                    selectedSetting = null;
                    editSetting = false;
                } else
                {
                    if (editSetting)
                    {
                        updateTextBoxes();
                    }
                    updateScrolling();
                }
            }

            @Override
            public void draw(GuiManager gui, Setting setting, int x, int y, boolean hover)
            {
                int srcSettingX = setting.isValid() ? 0 : 1;
                int srcSettingY = hover ? 1 : 0;

                gui.drawTexture(x, y, SETTING_SRC_X + srcSettingX * ITEM_SIZE, SETTING_SRC_Y + srcSettingY * ITEM_SIZE, ITEM_SIZE, ITEM_SIZE);
                if (setting.isValid())
                {
                    drawSettingObject(gui, setting, x, y);
                }
            }

            @Override
            public void drawMouseOver(GuiManager gui, Setting setting, int mX, int mY)
            {
                if (setting.isValid())
                {
                    gui.drawMouseOver(getSettingObjectMouseOver(setting), mX, mY);
                }

            }
        };
    }

    public void initRadioButtons()
    {
        radioButtons.add(new RadioButton(RADIO_BUTTON_X_LEFT, RADIO_BUTTON_Y, Names.WHITE_LIST));
        radioButtons.add(new RadioButton(RADIO_BUTTON_X_RIGHT, RADIO_BUTTON_Y, Names.BLACK_LIST));
    }

    public int getSettingCount()
    {
        return 30;
    }

    public boolean doAllowEdit()
    {
        return true;
    }

    @SideOnly(Side.CLIENT)
    public abstract void drawResultObject(GuiManager gui, Object obj, int x, int y);

    @SideOnly(Side.CLIENT)
    public abstract void drawSettingObject(GuiManager gui, Setting setting, int x, int y);

    @SideOnly(Side.CLIENT)
    public abstract List<String> getResultObjectMouseOver(Object obj);

    @SideOnly(Side.CLIENT)
    public abstract List<String> getSettingObjectMouseOver(Setting setting);

    public abstract void updateTextBoxes();

    public void writeRadioButtonRefreshState(DataWriter dw, boolean value)
    {
        dw.writeBoolean(value);
    }

    public abstract DataBitHelper getAmountBitLength();

    public abstract void writeSpecificHeaderData(DataWriter dw, DataTypeHeader header, Setting setting);

    public void onSettingContentChange()
    {

    }

    @SideOnly(Side.CLIENT)
    public abstract List updateSearch(String search, boolean showAll);

    @SideOnly(Side.CLIENT)
    @Override
    public void draw(GuiManager gui, int mX, int mY)
    {
        if (isEditing())
        {
            checkBoxes.draw(gui, mX, mY);

            drawSettingObject(gui, selectedSetting, EDIT_ITEM_X, EDIT_ITEM_Y);

            numberTextBoxes.draw(gui, mX, mY);

            drawInfoMenuContent(gui, mX, mY);

            int srcDeleteY = inDeleteBounds(mX, mY) ? 1 : 0;
            gui.drawTexture(DELETE_X, DELETE_Y, DELETE_SRC_X, DELETE_SRC_Y + srcDeleteY * DELETE_SIZE_H, DELETE_SIZE_W, DELETE_SIZE_H);
            gui.drawCenteredString(Names.DELETE, DELETE_X, DELETE_Y + DELETE_TEXT_Y, 0.7F, DELETE_SIZE_W, 0xBB4040);
        } else
        {
            if (!isSearching())
            {
                radioButtons.draw(gui, mX, mY);
            }
            if (isListVisible())
            {
                getScrollingList().draw(gui, mX, mY);
            }
        }

        if (selectedSetting != null)
        {
            int srcBackX = inBackBounds(mX, mY) ? 1 : 0;

            gui.drawTexture(BACK_X, BACK_Y, BACK_SRC_X + srcBackX * BACK_SIZE_W, BACK_SRC_Y, BACK_SIZE_W, BACK_SIZE_H);
        }
    }

    public boolean isListVisible()
    {
        return true;
    }

    @SideOnly(Side.CLIENT)
    public abstract void drawInfoMenuContent(GuiManager gui, int mX, int mY);

    public boolean inBackBounds(int mX, int mY)
    {
        return CollisionHelper.inBounds(BACK_X, BACK_Y, BACK_SIZE_W, BACK_SIZE_H, mX, mY);
    }

    public boolean inDeleteBounds(int mX, int mY)
    {
        return CollisionHelper.inBounds(DELETE_X, DELETE_Y, DELETE_SIZE_W, DELETE_SIZE_H, mX, mY);
    }

    public ScrollController getScrollingList()
    {
        return isSearching() ? scrollControllerSearch : scrollControllerSelected;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void drawMouseOver(GuiManager gui, int mX, int mY)
    {
        if (isEditing())
        {
            if (CollisionHelper.inBounds(EDIT_ITEM_X, EDIT_ITEM_Y, ITEM_SIZE, ITEM_SIZE, mX, mY))
            {
                scrollControllerSelected.drawMouseOver(gui, selectedSetting, mX, mY);
            } else if (inDeleteBounds(mX, mY))
            {
                gui.drawMouseOver(Names.DELETE_ITEM_SELECTION, mX, mY);
            }
        } else if (isListVisible())
        {
            getScrollingList().drawMouseOver(gui, mX, mY);
        }


        if (selectedSetting != null && inBackBounds(mX, mY))
        {
            gui.drawMouseOver(isEditing() ? Names.GO_BACK : Names.CANCEL, mX, mY);
        }
    }

    @Override
    public void onClick(int mX, int mY, int button)
    {
        if (isEditing())
        {
            checkBoxes.onClick(mX, mY);

            numberTextBoxes.onClick(mX, mY, button);

            if (inDeleteBounds(mX, mY))
            {
                selectedSetting.delete();
                needSync = true;
                selectedSetting = null;
                getScrollingList().updateScrolling();
            }
        } else
        {
            if (!isSearching())
            {
                radioButtons.onClick(mX, mY, button);
            }
            if (isListVisible())
            {
                getScrollingList().onClick(mX, mY, button);
            }
        }

        if (selectedSetting != null && inBackBounds(mX, mY))
        {
            selectedSetting = null;
            getScrollingList().updateScrolling();
        }
    }

    @Override
    public void onDrag(int mX, int mY, boolean isMenuOpen)
    {

    }

    @Override
    public void onRelease(int mX, int mY, boolean isMenuOpen)
    {
        getScrollingList().onRelease(mX, mY);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean onKeyStroke(GuiManager gui, char c, int k)
    {
        return isSearching() && getScrollingList().onKeyStroke(gui, c, k) || isEditing() && numberTextBoxes.onKeyStroke(gui, c, k);
    }

    @Override
    public void copyFrom(Menu menu)
    {
        MenuStuff<Type> menuItem = (MenuStuff<Type>)menu;

        setFirstRadioButtonSelected(menuItem.isFirstRadioButtonSelected());

        for (int i = 0; i < settings.size(); i++)
        {

            if (!menuItem.settings.get(i).isValid())
            {
                settings.get(i).clear();
            } else
            {
                settings.get(i).copyFrom(menuItem.settings.get(i));
                if (settings.get(i).isAmountSpecific())
                {
                    settings.get(i).setLimitedByAmount(menuItem.settings.get(i).isLimitedByAmount());
                    settings.get(i).setAmount(menuItem.settings.get(i).getAmount());
                }
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound, boolean pickup)
    {
        setFirstRadioButtonSelected(nbtTagCompound.getBoolean(NBT_RADIO_SELECTION));

        NBTTagList settingTagList = nbtTagCompound.getTagList(NBT_SETTINGS, 10);
        for (int i = 0; i < settingTagList.tagCount(); i++)
        {
            NBTTagCompound settingTag = settingTagList.getCompoundTagAt(i);
            Setting setting = settings.get(settingTag.getByte(NBT_SETTING_ID));
            setting.load(settingTag);
            if (setting.isAmountSpecific())
            {
                setting.setLimitedByAmount(settingTag.getBoolean(NBT_SETTING_USE_SIZE));
            }
        }

        onSettingContentChange();
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound, boolean pickup)
    {
        nbtTagCompound.setBoolean(NBT_RADIO_SELECTION, isFirstRadioButtonSelected());

        NBTTagList settingTagList = new NBTTagList();
        for (int i = 0; i < settings.size(); i++)
        {
            Setting setting = settings.get(i);
            if (setting.isValid())
            {
                NBTTagCompound settingTag = new NBTTagCompound();
                settingTag.setByte(NBT_SETTING_ID, (byte)setting.getId());
                setting.save(settingTag);
                if (setting.isAmountSpecific())
                {
                    settingTag.setBoolean(NBT_SETTING_USE_SIZE, setting.isLimitedByAmount());
                }
                settingTagList.appendTag(settingTag);
            }
        }
        nbtTagCompound.setTag(NBT_SETTINGS, settingTagList);
    }

    @Override
    public void addErrors(List<String> errors)
    {
        if (useWhiteList())
        {
            for (Setting setting : settings)
            {
                if (setting.isValid())
                {
                    return;
                }
            }
            errors.add(Names.EMPTY_WHITE_LIST_ERROR);
        }
    }

    @Override
    public void update(float partial)
    {
        if (isSearching())
        {
            scrollControllerSearch.update(partial);
        } else if (!isSearching() && !isEditing())
        {
            scrollControllerSelected.update(partial);
        }
    }

    @Override
    public void doScroll(int scroll)
    {
        if (isSearching())
        {
            scrollControllerSearch.doScroll(scroll);
        } else if (!isSearching() && !isEditing())
        {
            scrollControllerSelected.doScroll(scroll);
        }
    }

    public boolean isEditing()
    {
        return selectedSetting != null && editSetting;
    }

    public boolean isSearching()
    {
        return selectedSetting != null && !editSetting;
    }

    public boolean useWhiteList()
    {
        return isFirstRadioButtonSelected();
    }

    public boolean isFirstRadioButtonSelected()
    {
        return radioButtons.getSelectedOption() == 0;
    }

    public void setFirstRadioButtonSelected(boolean value)
    {
        radioButtons.setSelectedOption(value ? 0 : 1);
    }

    public List<Setting<Type>> getSettings()
    {
        return externalSettings;
    }

    public void setBlackList()
    {
        setFirstRadioButtonSelected(false);
    }

    public enum DataTypeHeader
    {
        CLEAR(0),
        SET_ITEM(1),
        USE_AMOUNT(2),
        USE_FUZZY(3),
        AMOUNT(4),
        META(5);

        public int id;

        DataTypeHeader(int header)
        {
            this.id = header;
        }
    }
}


