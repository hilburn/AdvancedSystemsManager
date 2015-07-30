package advancedsystemsmanager.flow.menus;


import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.elements.CheckBox;
import advancedsystemsmanager.flow.elements.CheckBoxList;
import advancedsystemsmanager.flow.elements.TextBoxNumber;
import advancedsystemsmanager.flow.elements.TextBoxNumberList;
import advancedsystemsmanager.flow.setting.ItemSetting;
import advancedsystemsmanager.client.gui.GuiManager;
import advancedsystemsmanager.network.ASMPacket;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.registry.ConnectionSet;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class MenuUpdateBlock extends MenuItem
{
    public static final int ID_START_X = 1;
    public static final int ID_START_Y = 1;
    public static final int ID_TEXT_BOX = 42;
    public static final int META_START_X = 1;
    public static final int META_START_Y = 21;
    public static final int META_SPACING = 17;
    public static final int META_SETTINGS = 3;
    public static final int META_BITS = 4;
    public static final int META_TEXT_BOX_OFFSET_1 = 37;
    public static final int META_TEXT_BOX_OFFSET_2 = 58;
    public static final int META_INVERTED_OFFSET = 83;
    public static final int META_TEXT_X = 3;
    public static final int META_TEXT_Y = 17;
    public static final int CHECKBOX_OFFSET = 2;
    public static final String NBT_USE_ID = "UseId";
    public static final String NBT_ID = "BlockId";
    public static final String NBT_INVERTED = "Inverted";
    public static final String NBT_SETTINGS = "Meta";
    public static final String NBT_BITS = "Bits";
    public static final String NBT_LOW = "Low";
    public static final String NBT_HIGH = "High";
    public TextBoxNumberList textBoxes;
    public CheckBoxList checkBoxes;
    public boolean useId;
    public boolean idInverted;
    public MetaSetting[] settings;


    public MenuUpdateBlock(FlowComponent parent)
    {
        super(parent);

        settings = new MetaSetting[META_SETTINGS];

        textBoxes = new TextBoxNumberList();

        checkBoxes = new CheckBoxList();

        scrollControllerSelected.setItemsPerRow(1);
        scrollControllerSelected.setVisibleRows(1);
        scrollControllerSelected.setItemUpperLimit(-2);
        scrollControllerSelected.setX(ID_START_X + ID_TEXT_BOX + 10);


        checkBoxes.addCheckBox(new CheckBox(getParent(), Names.USE_ID, ID_START_X, ID_START_Y + CHECKBOX_OFFSET)
        {
            @Override
            public void setValue(boolean val)
            {
                useId = val;
            }

            @Override
            public boolean getValue()
            {
                return useId;
            }
        });

        /*textBoxes.addTextBox(textBoxId = new TextBoxNumber(ID_START_X + ID_TEXT_BOX, ID_START_Y, 4, true) {
            @Override
            public int getMaxNumber() {
                return 4095;
            }

            @Override
            public void onNumberChanged() {
                sendServerData(0, 1);
            }

            @Override
            public boolean isVisible() {
                return useId;
            }
        });*/

        checkBoxes.addCheckBox(new CheckBox(getParent(), Names.INVERT, ID_START_X + META_INVERTED_OFFSET, ID_START_Y + CHECKBOX_OFFSET)
        {
            @Override
            public void setValue(boolean val)
            {
                idInverted = val;
            }

            @Override
            public boolean getValue()
            {
                return idInverted;
            }

            @Override
            public boolean isVisible()
            {
                return useId;
            }
        });

        for (int i = 0; i < META_SETTINGS; i++)
        {
            final int setting = i;
            settings[setting] = new MetaSetting();
            for (int j = 0; j < settings[setting].bits.length; j++)
            {
                final int bit = j;
                checkBoxes.addCheckBox(new CheckBox(getParent(), null, META_START_X + (settings[setting].bits.length - (bit + 1)) * CheckBoxList.CHECK_BOX_SIZE, META_START_Y + CHECKBOX_OFFSET + setting * META_SPACING)
                {
                    @Override
                    public void setValue(boolean val)
                    {
                        settings[setting].bits[bit] = val;
                        if (!val)
                        {
                            settings[setting].lowerTextBox.setNumber(settings[setting].lowerTextBox.getNumber());
                            settings[setting].higherTextBox.setNumber(settings[setting].higherTextBox.getNumber());
                        }
                    }

                    @Override
                    public boolean getValue()
                    {
                        return settings[setting].bits[bit];
                    }
                });

                settings[setting].bits[bit] = setting == 0;
            }

            textBoxes.addTextBox(settings[setting].lowerTextBox = new TextBoxNumber(getParent(), META_START_X + META_TEXT_BOX_OFFSET_1, META_START_Y + setting * META_SPACING, 2, false)
            {
                @Override
                public int getMaxNumber()
                {
                    return settings[setting].getMaxNumber();
                }                @Override
                public boolean isVisible()
                {
                    return settings[setting].inUse();
                }



            });

            textBoxes.addTextBox(settings[setting].higherTextBox = new TextBoxNumber(getParent(), META_START_X + META_TEXT_BOX_OFFSET_2, META_START_Y + setting * META_SPACING, 2, false)
            {
                @Override
                public int getMaxNumber()
                {
                    return settings[setting].getMaxNumber();
                }

                @Override
                public boolean isVisible()
                {
                    return settings[setting].inUse();
                }
            });

            checkBoxes.addCheckBox(new CheckBox(getParent(), Names.INVERT, META_START_X + META_INVERTED_OFFSET, META_START_Y + CHECKBOX_OFFSET + setting * META_SPACING)
            {
                @Override
                public void setValue(boolean val)
                {
                    settings[setting].inverted = val;
                }

                @Override
                public boolean getValue()
                {
                    return settings[setting].inverted;
                }

                @Override
                public boolean isVisible()
                {
                    return settings[setting].inUse();
                }
            });

            settings[setting].higherTextBox.setNumber(15);
        }


    }

    public void writeData(ASMPacket dw, int id, int subId)
    {
        dw.writeBoolean(false); //no setting specific
        dw.writeBoolean(true); //other data
        dw.writeByte(id);
        dw.writeByte(subId);

        if (id == 0)
        {
            if (subId == 0)
            {
                dw.writeBoolean(useId);
            } else if (subId == 2)
            {
                dw.writeBoolean(idInverted);
            }
        } else
        {
            id--;
            MetaSetting setting = settings[id];
            if (subId < 4)
            {
                dw.writeBoolean(setting.bits[subId]);
            } else if (subId == 4)
            {
                dw.writeByte(setting.lowerTextBox.getNumber());
            } else if (subId == 5)
            {
                dw.writeByte(setting.higherTextBox.getNumber());
            } else if (subId == 6)
            {
                dw.writeBoolean(setting.inverted);
            }
        }
    }

    public boolean useId()
    {
        return useId;
    }

    public int getBlockId()
    {
        ItemSetting itemSetting = (ItemSetting)getSettings().get(0);
        return itemSetting.getItem() == null ? 0 : Item.getIdFromItem(itemSetting.getItem().getItem());
    }

    public boolean isIdInverted()
    {
        return idInverted;
    }

    public MetaSetting[] getMetaSettings()
    {
        return settings;
    }

    @Override
    public void onClick(int mX, int mY, int button)
    {
        if (!isEditing() && !isSearching())
        {
            textBoxes.onClick(mX, mY, button);
            checkBoxes.onClick(mX, mY);
            if (useId)
            {
                super.onClick(mX, mY, button);
            }
        } else
        {
            super.onClick(mX, mY, button);
        }
    }

    @Override
    public String getName()
    {
        return Names.UPDATE_BLOCK_MENU;
    }

    @Override
    public boolean isVisible()
    {
        return getParent().getConnectionSet() == ConnectionSet.BUD;
    }

    @Override
    public int getSettingCount()
    {
        return 1;
    }

    @Override
    public void initRadioButtons()
    {
    }

    @Override
    public void writeRadioButtonRefreshState(ASMPacket dw, boolean value)
    {
        dw.writeBoolean(false);
        super.writeRadioButtonRefreshState(dw, value);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void draw(GuiManager gui, int mX, int mY)
    {
        if (!isEditing() && !isSearching())
        {
            textBoxes.draw(gui, mX, mY);
            checkBoxes.draw(gui, mX, mY);
            gui.drawString(Names.META, META_TEXT_X, META_TEXT_Y, 0.7F, 0x404040);
            if (useId)
            {
                super.draw(gui, mX, mY);
            }
        } else
        {
            super.draw(gui, mX, mY);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void drawMouseOver(GuiManager gui, int mX, int mY)
    {
        if (isEditing() || isSearching() || useId)
        {
            super.drawMouseOver(gui, mX, mY);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean onKeyStroke(GuiManager gui, char c, int k)
    {
        if (!isEditing() && !isSearching())
        {
            return textBoxes.onKeyStroke(gui, c, k);
        } else
        {
            return super.onKeyStroke(gui, c, k);
        }
    }

    @Override
    public void copyFrom(Menu menu)
    {
        super.copyFrom(menu);

        MenuUpdateBlock menuUpdate = (MenuUpdateBlock)menu;
        useId = menuUpdate.useId;
        idInverted = menuUpdate.idInverted;

        for (int i = 0; i < settings.length; i++)
        {
            System.arraycopy(menuUpdate.settings[i].bits, 0, settings[i].bits, 0, settings[i].bits.length);
            settings[i].lowerTextBox.setNumber(menuUpdate.settings[i].lowerTextBox.getNumber());
            settings[i].higherTextBox.setNumber(menuUpdate.settings[i].higherTextBox.getNumber());
            settings[i].inverted = menuUpdate.settings[i].inverted;
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound, boolean pickup)
    {
        super.readFromNBT(nbtTagCompound, pickup);

        useId = nbtTagCompound.getBoolean(NBT_USE_ID);

        idInverted = nbtTagCompound.getBoolean(NBT_INVERTED);

        NBTTagList list = nbtTagCompound.getTagList(NBT_SETTINGS, 10);
        for (int i = 0; i < list.tagCount(); i++)
        {
            MetaSetting setting = settings[i];
            NBTTagCompound settingTag = list.getCompoundTagAt(i);

            byte bits = settingTag.getByte(NBT_BITS);
            for (int j = 0; j < setting.bits.length; j++)
            {
                setting.bits[j] = ((bits >> j) & 1) != 0;
            }
            setting.lowerTextBox.setNumber(settingTag.getByte(NBT_LOW));
            setting.higherTextBox.setNumber(settingTag.getByte(NBT_HIGH));
            setting.inverted = settingTag.getBoolean(NBT_INVERTED);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound, boolean pickup)
    {
        super.writeToNBT(nbtTagCompound, pickup);

        nbtTagCompound.setBoolean(NBT_USE_ID, useId);
        nbtTagCompound.setBoolean(NBT_INVERTED, idInverted);

        NBTTagList list = new NBTTagList();
        for (MetaSetting setting : settings)
        {
            NBTTagCompound settingTag = new NBTTagCompound();
            byte bits = 0;
            for (int i = 0; i < setting.bits.length; i++)
            {
                if (setting.bits[i])
                {
                    bits |= 1 << i;
                }
            }
            settingTag.setByte(NBT_BITS, bits);
            settingTag.setByte(NBT_LOW, (byte)setting.lowerTextBox.getNumber());
            settingTag.setByte(NBT_HIGH, (byte)setting.higherTextBox.getNumber());
            settingTag.setBoolean(NBT_INVERTED, setting.inverted);

            list.appendTag(settingTag);
        }
        nbtTagCompound.setTag(NBT_SETTINGS, list);
    }

    public class MetaSetting
    {
        public boolean[] bits = new boolean[META_BITS];
        public TextBoxNumber lowerTextBox;
        public TextBoxNumber higherTextBox;
        public boolean inverted;

        public boolean inUse()
        {
            return selectedBits() > 0;
        }

        public int selectedBits()
        {
            int count = 0;
            for (boolean bit : bits)
            {
                if (bit)
                {
                    count++;
                }
            }

            return count;
        }

        public int getMaxNumber()
        {
            return (int)Math.pow(2, selectedBits()) - 1;
        }
    }
}
