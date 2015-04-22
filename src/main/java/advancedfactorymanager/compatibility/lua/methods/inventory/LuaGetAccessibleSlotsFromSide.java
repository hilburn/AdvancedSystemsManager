package advancedfactorymanager.compatibility.lua.methods.inventory;

import hilburnlib.compatibility.lua.methods.LuaMethod;
import hilburnlib.parsing.lua.ParsingRegistry;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.tileentity.TileEntity;

public class LuaGetAccessibleSlotsFromSide extends LuaMethod
{
    public LuaGetAccessibleSlotsFromSide()
    {
        super("getAccessibleSlotsFromSide", "(int side)", Number.class);
    }

    @Override
    public Object[] action(TileEntity te, Object[] args) throws Exception
    {
        return new Object[]{ParsingRegistry.getInstance().toLua(((ISidedInventory)te).getAccessibleSlotsFromSide((int)args[0]))};
    }

    @Override
    public boolean applies(TileEntity te)
    {
        return te instanceof ISidedInventory;
    }
}
