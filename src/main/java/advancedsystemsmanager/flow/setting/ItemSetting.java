package advancedsystemsmanager.flow.setting;

import advancedsystemsmanager.flow.menus.MenuItem;
import advancedsystemsmanager.network.ASMPacket;
import advancedsystemsmanager.reference.Names;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

public class ItemSetting extends Setting<ItemStack>
{
    public static final String NBT_SETTING_FUZZY = "FuzzyMode";
    public FuzzyMode fuzzyMode;
    public ItemStack content;

    public ItemSetting(int id)
    {
        super(id);
    }

    @Override
    public void clear()
    {
        super.clear();

        fuzzyMode = FuzzyMode.PRECISE;
        content = null;
    }

    @Override
    public List<String> getMouseOver()
    {
        if (content != null && GuiScreen.isShiftKeyDown())
        {
            return MenuItem.getToolTip(content);
        }

        List<String> ret = new ArrayList<String>();

        if (content == null)
        {
            ret.add(Names.NO_ITEM_SELECTED);
        } else
        {
            ret.add(MenuItem.getDisplayName(content));
        }

        ret.add("");
        ret.add(Names.CHANGE_ITEM);
        if (content != null)
        {
            ret.add(Names.EDIT_SETTING);
            ret.add(Names.FULL_DESCRIPTION);
        }

        return ret;
    }

    @Override
    public int getDefaultAmount()
    {
        return 1;
    }

    @Override
    public ItemStack getContent()
    {
        return content;
    }

    @Override
    public boolean isValid()
    {
        return content != null;
    }

    @Override
    public void setFuzzyType(int id)
    {
        fuzzyMode = FuzzyMode.values()[id];
    }

    @Override
    public void copyFrom(Setting setting)
    {
        content = ((ItemSetting)setting).getItem().copy();
        fuzzyMode = ((ItemSetting)setting).fuzzyMode;
    }

    public ItemStack getItem()
    {
        return content;
    }

    @Override
    public void load(NBTTagCompound settingTag)
    {
        super.load(settingTag);
        content = ItemStack.loadItemStackFromNBT(settingTag);
        fuzzyMode = FuzzyMode.values()[settingTag.getByte(NBT_SETTING_FUZZY)];
    }

    @Override
    public void save(NBTTagCompound settingTag)
    {
        super.save(settingTag);
        if (content != null) content.writeToNBT(settingTag);
        settingTag.setByte(NBT_SETTING_FUZZY, (byte)fuzzyMode.ordinal());
    }

    @Override
    public void setContent(ItemStack obj)
    {
        content = obj.copy();
    }

    @Override
    public boolean isContentEqual(ItemStack check)
    {
        return isEqualForCommandExecutor(check);
    }

    public boolean isEqualForCommandExecutor(ItemStack other)
    {
        if (!isValid() || other == null)
        {
            return false;
        } else
        {
            switch (fuzzyMode)
            {
                case ORE_DICTIONARY:
                    int[] ids = OreDictionary.getOreIDs(this.getItem());
                    if (ids.length > 0)
                    {
                        int[] otherIds = OreDictionary.getOreIDs(other);
                        for (int id : ids)
                        {
                            for (int oId : otherIds)
                            {
                                if (id == oId) return true;
                            }
                        }
                    }
                    //note that this falls through into the precise one, this is on purpose
                case PRECISE:
                    return this.getItem().getItem() == other.getItem() && this.getItem().getItemDamage() == other.getItemDamage() && ItemStack.areItemStackTagsEqual(getItem(), other);
                case NBT_FUZZY:
                    return this.getItem().getItem() == other.getItem() && this.getItem().getItemDamage() == other.getItemDamage();
                case FUZZY:
                    return this.getItem().getItem() == other.getItem();
                case MOD_GROUPING:
                    return ModItemHelper.areItemsFromSameMod(this.getItem().getItem(), other.getItem());
                case ALL:
                    return true;
                default:
                    return false;
            }
        }
    }

    @Override
    public void writeContentData(ASMPacket packet)
    {
        super.writeContentData(packet);
        packet.writeItemStackToBuffer(content);
    }

    @Override
    public void readContentData(ASMPacket packet)
    {
        super.readContentData(packet);
        content = packet.readItemStackFromBuffer();
    }

    public FuzzyMode getFuzzyMode()
    {
        return fuzzyMode;
    }

    public boolean canChangeMetaData()
    {
        return true;
    }
}
