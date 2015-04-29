package advancedsystemsmanager.registry;

import advancedsystemsmanager.api.IRedstoneEmitter;
import advancedsystemsmanager.api.IRedstoneNode;
import advancedsystemsmanager.api.IRedstoneReceiver;
import advancedsystemsmanager.api.ISystemType;
import advancedsystemsmanager.api.execution.IInternalInventory;
import advancedsystemsmanager.api.execution.IInternalTank;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.tileentities.TileEntityBUD;
import advancedsystemsmanager.tileentities.TileEntityCamouflage;
import advancedsystemsmanager.tileentities.TileEntitySignUpdater;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.IFluidHandler;

import java.util.ArrayList;
import java.util.List;

public class SystemTypeRegistry
{
    private static final List<ISystemType> types = new ArrayList<ISystemType>();

    public static void register(ISystemType type)
    {
        types.add(type);
    }

    public static List<ISystemType> getTypes()
    {
        return types;
    }

    public static final SystemType INVENTORY = new SystemType<IInventory>(Names.TYPE_INVENTORY, false)
    {
        @Override
        public boolean isInstance(TileEntity tileEntity)
        {
            return tileEntity instanceof IInventory || tileEntity instanceof IInternalInventory;
        }

        @Override
        public IInventory getType(TileEntity tileEntity)
        {
            return tileEntity instanceof IInventory ? (IInventory) tileEntity : null;
        }
    };
    public static final SystemType TANK = new SystemType<IFluidHandler>(Names.TYPE_TANK, false)
    {
        @Override
        public boolean isInstance(TileEntity tileEntity)
        {
            return tileEntity instanceof IFluidHandler || tileEntity instanceof IInternalTank;
        }

        @Override
        public IFluidHandler getType(TileEntity tileEntity)
        {
            return tileEntity instanceof IFluidHandler ? (IFluidHandler) tileEntity : ((IInternalTank)tileEntity).getTank();
        }
    };
    public static final SystemType EMITTER = new SystemType<IRedstoneEmitter>(Names.TYPE_EMITTER, false)
    {
        @Override
        public boolean isInstance(TileEntity tileEntity)
        {
            return tileEntity instanceof IRedstoneEmitter;
        }
    };
    public static final SystemType RECEIVER = new SystemType<IRedstoneReceiver>(Names.TYPE_RECEIVER, false)
    {
        @Override
        public boolean isInstance(TileEntity tileEntity)
        {
            return tileEntity instanceof IRedstoneReceiver;
        }
    };
    public static final SystemType NODE = new SystemType<IRedstoneNode>(Names.TYPE_NODE, true)
    {
        @Override
        public boolean isInstance(TileEntity tileEntity)
        {
            return tileEntity instanceof IRedstoneNode;
        }
    };
    public static final SystemType BUD = new SystemType<TileEntityBUD>(Names.TYPE_BUD, false)
    {
        @Override
        public boolean isInstance(TileEntity tileEntity)
        {
            return tileEntity instanceof TileEntityBUD;
        }
    };
    public static final SystemType CAMOUFLAGE = new SystemType<TileEntityCamouflage>(Names.TYPE_CAMOUFLAGE, false)
    {
        @Override
        public boolean isInstance(TileEntity tileEntity)
        {
            return tileEntity instanceof TileEntityCamouflage;
        }
    };
    public static final SystemType SIGN = new SystemType<TileEntitySignUpdater>(Names.TYPE_SIGN, false)
    {
        @Override
        public boolean isInstance(TileEntity tileEntity)
        {
            return tileEntity instanceof TileEntitySignUpdater;
        }
    };

    public static abstract class SystemType<Type> implements ISystemType<Type>
    {
        private String name;
        private boolean group;

        private SystemType(String name, boolean group)
        {
            this.name = name;
            this.group = group;
        }

        public boolean isGroup()
        {
            return group;
        }

        @Override
        public Type getType(TileEntity tileEntity)
        {
            return (Type) tileEntity;
        }

        @Override
        public String getName()
        {
            return name;
        }

        @Override
        public String toString()
        {
            return name;
        }
    }
}
