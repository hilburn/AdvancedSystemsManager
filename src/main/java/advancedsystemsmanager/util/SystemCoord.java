package advancedsystemsmanager.util;

import advancedsystemsmanager.api.ISystemType;
import advancedsystemsmanager.api.gui.IContainerSelection;
import advancedsystemsmanager.api.tileentities.IClusterElement;
import advancedsystemsmanager.api.tileentities.IClusterTile;
import advancedsystemsmanager.flow.elements.Variable;
import advancedsystemsmanager.flow.menus.MenuContainer;
import advancedsystemsmanager.client.gui.GuiManager;
import advancedsystemsmanager.naming.NameRegistry;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.reference.Null;
import advancedsystemsmanager.registry.ClusterRegistry;
import advancedsystemsmanager.tileentities.manager.TileEntityManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import powercrystals.minefactoryreloaded.api.IDeepStorageUnit;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class SystemCoord implements Comparable<SystemCoord>, IContainerSelection
{
    private int x, y, z, depth, dim;
    private long key;
    private Set<ISystemType> types;
//    private TileEntity tileEntity;
    private IClusterElement elementType;
    private World world;

    public SystemCoord(SystemCoord coord, ForgeDirection dir)
    {
        this(coord.x + dir.offsetX, coord.y + dir.offsetY, coord.z + dir.offsetZ, coord.world, coord.depth + 1);
    }

    public SystemCoord(int x, int y, int z, World world, int depth)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.dim = world.provider.dimensionId;
        this.depth = depth;
        this.world = world;
        setKey();
    }

    private void setKey()
    {
        this.key = ((long)dim & 0xFF) << 48 | ((long)x & 0xFFFFF) << 28 | (z & 0xFFFFF) << 8 | (y & 0xFF);
        if (elementType != null)
        {
            key |= ((long)elementType.getId() & 0x7F) << 56;
        }
//        if (tileEntity instanceof IClusterTile)
//            key |= ((long)ClusterRegistry.getID((IClusterTile)tileEntity) & 0x7F) << 56; //This means that the SystemCoord key will always be positive.
    }

    public SystemCoord(int x, int y, int z, World world)
    {
        this(x, y, z, world, 0);
    }

    public void addType(ISystemType type)
    {
        types.add(type);
    }

    public boolean isOfAnyType(Collection<ISystemType> types)
    {
        for (ISystemType type : types)
        {
            if (isOfType(type))
            {
                return true;
            }
        }

        return false;
    }

    public boolean isOfType(ISystemType type)
    {
        return isOfType(this.types, type);
    }

    public static boolean isOfType(Set<ISystemType> types, ISystemType type)
    {
        return type == null || types.contains(type) || (type.isGroup() && type.containsGroup(types));
    }

    public boolean isValid()
    {
        return world.blockExists(x, y, z);
    }

    @Override
    public int hashCode()
    {
        int result = 173 + x;
        result = 31 * result + z;
        result = 31 * result + y;
        return result;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof SystemCoord)) return false;

        SystemCoord that = (SystemCoord)o;

        return x == that.x && y == that.y && z == that.z;

    }

    @Override
    public int compareTo(SystemCoord o)
    {
        return depth == o.depth ? x == o.x && y == o.y && z == o.z ? Integer.compare(dim, o.dim) : Long.compare(key, o.key) : depth < o.depth ? -1 : 1;
    }

    @Override
    public long getId()
    {
        return key;
    }

    @Override
    public void draw(GuiManager gui, int x, int y)
    {
        gui.drawBlock(getTileEntity(), x, y);
    }

    @Override
    public String getDescription(GuiManager gui)
    {
        TileEntity tileEntity = getTileEntity();
        String str = fixToolTip(gui.getBlockName(tileEntity), tileEntity);

        str += getVariableTag(gui);

        str += "\n" + StatCollector.translateToLocalFormatted(Names.LOCATION, tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
        int distance = getDistance(gui.getManager());
        str += "\n" + StatCollector.translateToLocalFormatted(distance > 1 ? Names.BLOCKS_AWAY : Names.BLOCK_AWAY, distance);
        str += "\n" + StatCollector.translateToLocalFormatted(depth > 1 ? Names.CABLES_AWAY : Names.CABLE_AWAY, depth);

        return str;
    }

    public static String fixToolTip(String string, TileEntity tileEntity)
    {
        if (tileEntity != null && tileEntity.hasWorldObj())
        {
            String label = getLabel(tileEntity);
            if (label != null) string = "§3" + label;
            string += getContentString(tileEntity);
        }
        return string;
    }

    public static String getLabel(TileEntity tileEntity)
    {
        return NameRegistry.getSavedName(tileEntity.getWorldObj().provider.dimensionId, tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
    }

    @Override
    public String getName(GuiManager gui)
    {
        return gui.getBlockName(getTileEntity());
    }

    @Override
    public boolean isVariable()
    {
        return false;
    }

    @SideOnly(Side.CLIENT)
    private String getVariableTag(GuiManager gui)
    {
        boolean isInVariable = false;
        String result = "";

        if (GuiScreen.isShiftKeyDown())
        {
            for (Variable variable : gui.getManager().getVariables())
            {
                if (isPartOfVariable(variable))
                {
                    result += "\n" + variable.getDescription(gui);
                    isInVariable = true;
                }
            }
        }

        return isInVariable ? "" : result;
    }

    public int getDistance(TileEntityManager manager)
    {
        return (int)Math.round(Math.sqrt(manager.getDistanceFrom(x + 0.5d, y + 0.5d, z + 0.5d)));
    }

    @SideOnly(Side.CLIENT)
    public boolean isPartOfVariable(Variable variable)
    {
        return variable.isValid() && ((MenuContainer)variable.getDeclaration().getMenus().get(2)).getSelectedInventories().contains(key);
    }

    public SystemCoord copy()
    {
        return new SystemCoord(x, y, z, world, depth);
    }

    public boolean containerAdvancedSearch(String search)
    {
        TileEntity tileEntity = getTileEntity();
        String toSearch = getLabel(tileEntity);
        Pattern pattern = Pattern.compile(Pattern.quote(search), Pattern.CASE_INSENSITIVE);
        return (toSearch != null && pattern.matcher(toSearch).find()) || pattern.matcher(getContentString(tileEntity)).find();
    }

    public static String getContentString(TileEntity tileEntity)
    {
        String result = "";
        if (tileEntity instanceof IDeepStorageUnit)
        {
            ItemStack stack = ((IDeepStorageUnit)tileEntity).getStoredItemType();
            String contains = "\n";
            if (stack == null || stack.isItemEqual(Null.NULL_STACK))
                contains += StatCollector.translateToLocal(Names.BARREL_EMTPTY);
            else
                contains += StatCollector.translateToLocalFormatted(Names.BARREL_CONTAINS, stack.getDisplayName());
            result += contains;
        } else if (tileEntity instanceof IFluidHandler)
        {
            String tankInfo = "";
            int i = 1;
            FluidTankInfo[] fluidTankInfo = ((IFluidHandler)tileEntity).getTankInfo(ForgeDirection.UNKNOWN);
            if (fluidTankInfo != null)
            {
                for (FluidTankInfo info : fluidTankInfo)
                {
                    if (info.fluid == null || info.fluid.getFluid() == null) continue;
                    tankInfo += info.fluid.getLocalizedName() + (i++ < fluidTankInfo.length ? ", " : "");
                }
            }
            if (tankInfo.isEmpty()) result += "\n" + StatCollector.translateToLocal(Names.BARREL_EMTPTY);
            else result += "\n" + StatCollector.translateToLocalFormatted(Names.BARREL_CONTAINS, tankInfo);
        }
        return result;
    }

    public void resetTypes()
    {
        this.types = new HashSet<ISystemType>();
    }

    public Block getBlock()
    {
        return world.getBlock(x, y, z);
    }

    public int getMetadata()
    {
        return world.getBlockMetadata(x, y, z);
    }

    public TileEntity getWorldTE()
    {
        return world.getTileEntity(x, y, z);
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public int getZ()
    {
        return z;
    }

    public int getDepth()
    {
        return depth;
    }

    public TileEntity getTileEntity()
    {
        return elementType != null? elementType.getTileEntity(world, x, y, z) : world.getTileEntity(x, y, z);
    }

    public int getComparatorOutput(int side)
    {
        return world.blockExists(x, y, z)? world.getBlock(x, y, z).getComparatorInputOverride(world, x, y, z, side) : 0;
    }

    public void setClusterType(IClusterElement elementType)
    {
        this.elementType = elementType;
        setKey();
    }

    public Set<ISystemType> getTypes()
    {
        return types;
    }

    public World getWorld()
    {
        return world;
    }
}
