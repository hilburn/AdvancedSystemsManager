package advancedsystemsmanager.util;

import advancedsystemsmanager.api.ISystemType;
import advancedsystemsmanager.api.gui.IContainerSelection;
import advancedsystemsmanager.api.tileentities.IClusterTile;
import advancedsystemsmanager.flow.elements.Variable;
import advancedsystemsmanager.flow.menus.MenuContainer;
import advancedsystemsmanager.gui.GuiManager;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.registry.ClusterRegistry;
import advancedsystemsmanager.registry.SystemTypeRegistry;
import advancedsystemsmanager.tileentities.manager.TileEntityManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Collection;
import java.util.Set;

public class SystemCoord implements Comparable<SystemCoord>, IContainerSelection<GuiManager>
{
    public int x, y, z, depth, dim;
    public long key;
    public Set<ISystemType> types;
    public TileEntity tileEntity;

    public SystemCoord(int x, int y, int z)
    {
        this(x, y, z, 0, 0);
    }

    public SystemCoord(SystemCoord coord, ForgeDirection dir)
    {
        this(coord.x + dir.offsetX, coord.y + dir.offsetY, coord.z + dir.offsetZ, coord.dim, coord.depth + 1);
    }

    public SystemCoord(int x, int y, int z, int dim)
    {
        this(x, y, z, dim, 0);
    }

    public SystemCoord(int x, int y, int z, int dim, int depth)
    {
        this(x, y, z, dim, depth, null);
    }

    public SystemCoord(int x, int y, int z, int dim, int depth, TileEntity tileEntity)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.dim = dim;
        this.depth = depth;
        this.tileEntity = tileEntity;
        setKey();
    }

    private void setKey()
    {
        this.key = ((long)x & 0xFFFFFF) << 40 | ((long)z & 0xFFFFFF) << 16 | (y & 0xFF) << 8 | dim & 0xFF;
        if (tileEntity instanceof IClusterTile)
            key |= (ClusterRegistry.get((IClusterTile)tileEntity).getId() & 0xF) << 4;
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
        return type == null || types.contains(type) || (type == SystemTypeRegistry.NODE && (types.contains(SystemTypeRegistry.RECEIVER) || types.contains(SystemTypeRegistry.EMITTER)));
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
    public int hashCode()
    {
        int result = 173 + x;
        result = 31 * result + z;
        result = 31 * result + y;
        return result;
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
        gui.drawBlock(tileEntity, x, y);
    }

    @Override
    public String getDescription(GuiManager gui)
    {
        String str = StevesHooks.fixToolTip(gui.getBlockName(tileEntity), tileEntity);

        str += getVariableTag(gui);

        str += "\n" + StatCollector.translateToLocalFormatted(Names.LOCATION, tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
        int distance = getDistance(gui.getManager());
        str += "\n" + StatCollector.translateToLocalFormatted(distance > 1 ? Names.BLOCKS_AWAY : Names.BLOCK_AWAY, distance);
        str += "\n" + StatCollector.translateToLocalFormatted(depth > 1 ? Names.CABLES_AWAY : Names.CABLE_AWAY, depth);

        return str;
    }

    public int getDistance(TileEntityManager manager)
    {
        return (int)Math.round(Math.sqrt(manager.getDistanceFrom(tileEntity.xCoord + 0.5, tileEntity.yCoord + 0.5, tileEntity.zCoord + 0.5)));
    }

    @Override
    public String getName(GuiManager gui)
    {
        return gui.getBlockName(tileEntity);
    }

    @Override
    public boolean isVariable()
    {
        return false;
    }

    @SideOnly(Side.CLIENT)
    private String getVariableTag(GuiManager gui)
    {
        int count = 0;
        String result = "";

        if (GuiScreen.isShiftKeyDown())
        {
            for (Variable variable : gui.getManager().getVariables())
            {
                if (isPartOfVariable(variable))
                {
                    result += "\n" + variable.getDescription(gui);
                    count++;
                }
            }
        }

        return count == 0 ? "" : result;
    }

    @SideOnly(Side.CLIENT)
    public boolean isPartOfVariable(Variable variable)
    {
        return variable.isValid() && ((MenuContainer)variable.getDeclaration().getMenus().get(2)).getSelectedInventories().contains(key);
    }

    public SystemCoord copy()
    {
        return new SystemCoord(x, y, z, dim, depth, tileEntity);
    }

    public void setTileEntity(TileEntity tileEntity)
    {
        this.tileEntity = tileEntity;
        setKey();
    }
}
