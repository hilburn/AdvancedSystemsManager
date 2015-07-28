package advancedsystemsmanager.flow.setting;

import advancedsystemsmanager.network.ASMPacket;
import advancedsystemsmanager.reference.Names;
import net.minecraft.nbt.NBTTagCompound;
import thaumcraft.api.aspects.Aspect;

import java.util.ArrayList;
import java.util.List;

public class AspectSetting extends Setting<Aspect>
{
    public static final String NBT_ASPECT_TAG = "AspectTag";
    public Aspect aspect;

    public AspectSetting(int id)
    {
        super(id);
    }

    @Override
    public void clear()
    {
        super.clear();

        aspect = null;
        setDefaultAmount();
    }

    @Override
    public List<String> getMouseOver()
    {
        List<String> ret = new ArrayList<String>();

        if (aspect == null)
        {
            ret.add(Names.NO_ASPECT_SELECTED);
        } else
        {
            ret.add(aspect.getName());
        }

        ret.add("");
        ret.add(Names.CHANGE_ASPECT);
        if (aspect != null)
        {
            ret.add(Names.EDIT_SETTING);
        }

        return ret;
    }

    @Override
    public int getDefaultAmount()
    {
        return 1;
    }

    @Override
    public Aspect getContent()
    {
        return aspect;
    }

    @Override
    public boolean isValid()
    {
        return aspect != null;
    }

    @Override
    public void copyFrom(Setting setting)
    {
        aspect = ((AspectSetting)setting).getContent();
    }

    @Override
    public void setContent(Aspect obj)
    {
        aspect = obj;
    }

    @Override
    public boolean isContentEqual(Aspect check)
    {
        return check == aspect;
    }

    @Override
    public void save(NBTTagCompound settingTag)
    {
        super.save(settingTag);
        settingTag.setString(NBT_ASPECT_TAG, aspect.getTag());
    }

    @Override
    public void load(NBTTagCompound settingTag)
    {
        super.load(settingTag);
        aspect = Aspect.getAspect(settingTag.getString(NBT_ASPECT_TAG));
    }

    @Override
    public void writeContentData(ASMPacket packet)
    {
        super.writeContentData(packet);
        packet.writeStringToBuffer(aspect.getTag());
    }

    @Override
    public void readContentData(ASMPacket packet)
    {
        super.readContentData(packet);
        aspect = Aspect.getAspect(packet.readStringFromBuffer());
    }
}
