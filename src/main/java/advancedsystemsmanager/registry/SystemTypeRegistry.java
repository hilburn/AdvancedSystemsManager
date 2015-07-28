package advancedsystemsmanager.registry;

import advancedsystemsmanager.api.ISystemType;
import advancedsystemsmanager.api.tileentities.*;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.elements.RadioButton;
import advancedsystemsmanager.flow.elements.RadioButtonList;
import advancedsystemsmanager.flow.menus.MenuContainer;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.tileentities.TileEntityBUD;
import advancedsystemsmanager.tileentities.TileEntityCamouflage;
import advancedsystemsmanager.tileentities.TileEntitySignUpdater;
import advancedsystemsmanager.tileentities.manager.TileEntityManager;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.IFluidHandler;
import thaumcraft.api.aspects.IAspectContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SystemTypeRegistry
{
    private static final List<ISystemType> types = new ArrayList<ISystemType>();
    public static final ISystemType INVENTORY = register(new SystemType<IInventory>(Names.TYPE_INVENTORY, false)
    {
        @Override
        public boolean isInstance(TileEntityManager manager, TileEntity tileEntity)
        {
            return tileEntity instanceof IInventory || tileEntity instanceof IInternalInventory;
        }

        @Override
        public IInventory getType(TileEntity tileEntity)
        {
            return tileEntity instanceof IInventory ? (IInventory)tileEntity : null;
        }
    });
    public static final ISystemType TANK = register(new SystemType<IFluidHandler>(Names.TYPE_TANK, false)
    {
        @Override
        public boolean isInstance(TileEntityManager manager, TileEntity tileEntity)
        {
            return tileEntity instanceof IFluidHandler || tileEntity instanceof IInternalTank;
        }

        @Override
        public IFluidHandler getType(TileEntity tileEntity)
        {
            return tileEntity instanceof IFluidHandler ? (IFluidHandler)tileEntity : ((IInternalTank)tileEntity).getTank();
        }
    });
    public static final ISystemType EMITTER = register(new SystemType<IRedstoneEmitter>(Names.TYPE_EMITTER, false)
    {
        @Override
        public boolean isInstance(TileEntityManager manager, TileEntity tileEntity)
        {
            return tileEntity instanceof IRedstoneEmitter;
        }

        @Override
        public void initRadioButtons(RadioButtonList radioButtonsMulti)
        {
        }
    });
    public static final ISystemType RECEIVER = register(new SystemType<IRedstoneReceiver>(Names.TYPE_RECEIVER, false)
    {
        @Override
        public boolean isInstance(TileEntityManager manager, TileEntity tileEntity)
        {
            return tileEntity instanceof IRedstoneReceiver || manager == tileEntity;
        }

        @Override
        public void initRadioButtons(RadioButtonList radioButtonsMulti)
        {
            radioButtonsMulti.add(new RadioButton(0, Names.RUN_SHARED_ONCE));
            radioButtonsMulti.add(new RadioButton(1, Names.REQUIRE_ALL_TARGETS));
            radioButtonsMulti.add(new RadioButton(2, Names.REQUIRE_ONE_TARGET));
        }

        @Override
        public boolean isVisible(FlowComponent parent)
        {
            return parent.getConnectionSet() == ConnectionSet.REDSTONE;
        }
    });
    public static final ISystemType NODE = register(new SystemType<IRedstoneNode>(Names.TYPE_NODE, true)
    {
        @Override
        public boolean isInstance(TileEntityManager manager, TileEntity tileEntity)
        {
            return tileEntity instanceof IRedstoneNode;
        }

        @Override
        public void initRadioButtons(RadioButtonList radioButtonsMulti)
        {
            radioButtonsMulti.add(new RadioButton(0, Names.RUN_SHARED_ONCE));
            radioButtonsMulti.add(new RadioButton(1, Names.REQUIRE_ALL_TARGETS));
            radioButtonsMulti.add(new RadioButton(2, Names.REQUIRE_ONE_TARGET));
        }

        @Override
        public int getDefaultRadioButton()
        {
            return 2;
        }

        @Override
        public boolean containsGroup(Set<ISystemType> types)
        {
            return types.contains(EMITTER) || types.contains(RECEIVER);
        }
    });
    public static final ISystemType BUD = register(new SystemType<TileEntityBUD>(Names.TYPE_BUD, false)
    {
        @Override
        public boolean isInstance(TileEntityManager manager, TileEntity tileEntity)
        {
            return tileEntity instanceof TileEntityBUD;
        }

        @Override
        public boolean isVisible(FlowComponent parent)
        {
            return parent.getConnectionSet() == ConnectionSet.BUD;
        }

        @Override
        public void initRadioButtons(RadioButtonList radioButtonsMulti)
        {
            radioButtonsMulti.add(new RadioButton(0, Names.REQUIRE_ALL_TARGETS));
            radioButtonsMulti.add(new RadioButton(1, Names.REQUIRE_ONE_TARGET));
        }
    });
    public static final ISystemType CAMOUFLAGE = register(new SystemType<TileEntityCamouflage>(Names.TYPE_CAMOUFLAGE, false)
    {
        @Override
        public boolean isInstance(TileEntityManager manager, TileEntity tileEntity)
        {
            return tileEntity instanceof TileEntityCamouflage;
        }

        @Override
        public void initRadioButtons(RadioButtonList radioButtonsMulti)
        {
        }
    });
    public static final ISystemType SIGN = register(new SystemType<TileEntitySignUpdater>(Names.TYPE_SIGN, false)
    {
        @Override
        public boolean isInstance(TileEntityManager manager, TileEntity tileEntity)
        {
            return tileEntity instanceof TileEntitySignUpdater;
        }

        @Override
        public void initRadioButtons(RadioButtonList radioButtonsMulti)
        {
            super.initRadioButtons(radioButtonsMulti);
        }
    });
    public static final ISystemType ASPECT_CONTAINER = register(new SystemType<IAspectContainer>(Names.TYPE_ASPECT, false)
    {
        @Override
        public boolean isInstance(TileEntityManager manager, TileEntity tileEntity)
        {
            return tileEntity instanceof IAspectContainer;
        }
    });
    public static final ISystemType VARIABLE = new SystemType(Names.TYPE_VARIABLE, false)
    {

        @Override
        public boolean isInstance(TileEntityManager manager, TileEntity tileEntity)
        {
            return false;
        }

        @Override
        public void initRadioButtons(RadioButtonList radioButtonsMulti)
        {
        }

        @Override
        public void addErrors(List errors, MenuContainer container)
        {
        }
    };

    public static ISystemType register(ISystemType type)
    {
        types.add(type);
        return type;
    }

    public static List<ISystemType> getTypes()
    {
        return types;
    }

    public static abstract class SystemType<Type> implements ISystemType<Type>
    {
        private String name;

        private boolean group;

        private SystemType(String name, boolean group)
        {
            this.name = name;
            this.group = group;
        }

        @Override
        public String toString()
        {
            return name;
        }

        public boolean isGroup()
        {
            return group;
        }


        @Override
        public Type getType(TileEntity tileEntity)
        {
            return (Type)tileEntity;
        }

        @Override
        public boolean containsGroup(Set<ISystemType> types)
        {
            return false;
        }

        @Override
        public String getName()
        {
            return name;
        }

        @Override
        public void addErrors(List<String> errors, MenuContainer container)
        {
            if (container.selectedInventories.isEmpty() && container.isVisible())
            {
                errors.add(name + "Error");
            }
        }

        @Override
        public boolean isVisible(FlowComponent parent)
        {
            return true;
        }

        @Override
        public void initRadioButtons(RadioButtonList radioButtonsMulti)
        {
            radioButtonsMulti.add(new RadioButton(0, Names.RUN_SHARED_ONCE));
            radioButtonsMulti.add(new RadioButton(1, Names.RUN_ONE_PER_TARGET));
        }

        @Override
        public int getDefaultRadioButton()
        {
            return 0;
        }
    }
}
