package advancedsystemsmanager.compatibility.thaumcraft.menus;

import advancedsystemsmanager.client.gui.GuiBase;
import advancedsystemsmanager.compatibility.thaumcraft.TCCompat;
import advancedsystemsmanager.compatibility.thaumcraft.setting.AspectSetting;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.menus.MenuStuff;
import advancedsystemsmanager.flow.setting.Setting;
import advancedsystemsmanager.network.ASMPacket;
import advancedsystemsmanager.reference.Names;
import thaumcraft.api.aspects.Aspect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class MenuAspect extends MenuStuff<Aspect>
{
    public MenuAspect(FlowComponent parent)
    {
        this(parent, true);
    }

    public MenuAspect(FlowComponent parent, boolean whitelist)
    {
        super(parent);
        setFirstRadioButtonSelected(whitelist);
    }

    @Override
    public Setting<Aspect> getSetting(int id)
    {
        return new AspectSetting(id);
    }

    @Override
    public List<Aspect> updateSearch(String search, boolean showAll)
    {
        List<Aspect> aspects = new ArrayList<Aspect>();
        aspects.addAll(Aspect.getPrimalAspects());
        aspects.addAll(Aspect.getCompoundAspects());
        if (!showAll)
        {
            Iterator<Aspect> itr = aspects.iterator();
            Pattern pattern = Pattern.compile(search, Pattern.CASE_INSENSITIVE);
            while (itr.hasNext())
            {
                Aspect aspect = itr.next();
                if (!pattern.matcher(aspect.getName()).find())
                    itr.remove();
            }
        }
        return aspects;
    }

    @Override
    public void drawResultObject(GuiBase gui, Aspect obj, int x, int y)
    {
        TCCompat.drawAspect(gui, obj, x, y);
    }

    @Override
    public List<String> getResultObjectMouseOver(Aspect obj)
    {
        ArrayList<String> ret = new ArrayList<String>();
        ret.add(obj.getName());
        return ret;
    }

    @Override
    public void updateTextBoxes()
    {

    }

    @Override
    public void drawSettingObject(GuiBase gui, Setting<Aspect> setting, int x, int y)
    {
        drawResultObject(gui, setting.getContent(), x, y);
    }

    @Override
    public List<String> getSettingObjectMouseOver(Setting<Aspect> setting)
    {
        return getResultObjectMouseOver(setting.getContent());
    }

    @Override
    protected boolean readSpecificData(ASMPacket packet, int action, Setting<Aspect> setting)
    {
        return false;
    }

    @Override
    public void drawInfoMenuContent(GuiBase gui, int mX, int mY)
    {

    }

    @Override
    public String getName()
    {
        return Names.ASPECTS_MENU;
    }
}
