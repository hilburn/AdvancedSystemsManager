package advancedsystemsmanager.api;

import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.elements.RadioButtonList;
import advancedsystemsmanager.flow.menus.MenuContainer;
import net.minecraft.tileentity.TileEntity;

import java.util.List;

public interface ISystemType<Type>
{
    public boolean isInstance(TileEntity tileEntity);

    public Type getType(TileEntity tileEntity);

    public boolean isGroup();

    public String getName();

    void addErrors(List<String> errors, MenuContainer container);

    boolean isVisible(FlowComponent parent);

    void initRadioButtons(RadioButtonList radioButtonsMulti);

    int getDefaultRadioButton();
}
