package advancedsystemsmanager.client.render;

import advancedsystemsmanager.blocks.BlockCamouflageBase;
import advancedsystemsmanager.registry.BlockRegistry;
import advancedsystemsmanager.tileentities.TileEntityCamouflage;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderCamouflage implements ISimpleBlockRenderingHandler
{
    private int id;
    private static double THICKNESS = 0.0015d;
    private static final RenderCamouflageBlocks CAMO_RENDER = new RenderCamouflageBlocks();

    public RenderCamouflage()
    {
        id = RenderingRegistry.getNextAvailableRenderId();
        BlockCamouflageBase.RENDER_ID = id;
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer)
    {
        block.setBlockBoundsForItemRender();
        renderer.setRenderBoundsFromBlock(block);

        GL11.glPushMatrix();
        GL11.glRotatef(90, 0, 1, 0);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

        Tessellator tessellator = Tessellator.instance;

        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, -1.0F, 0.0F);
        renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(0, metadata));

        tessellator.setNormal(0F, 1F, 0F);
        renderer.renderFaceYPos(block, 0, 0, 0, block.getIcon(1, metadata));

        tessellator.setNormal(0.0F, 0.0F, -1.0F);
        renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(2, metadata));

        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(3, metadata));

        tessellator.setNormal(-1.0F, 0.0F, 0.0F);
        renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(4, metadata));

        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(5, metadata));

        tessellator.draw();

        GL11.glPopMatrix();
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
    {
        Tessellator.instance.setColorOpaque_F(1F, 1F, 1F);

        if (block instanceof BlockCamouflageBase)
        {
            TileEntityCamouflage camouflage = BlockRegistry.cableCamouflage.getTileEntity(world, x, y, z);
            if (camouflage != null)
            {
                block.setBlockBoundsBasedOnState(world, x, y, z);
                double maxX = block.getBlockBoundsMaxX();
                double maxY = block.getBlockBoundsMaxY();
                double maxZ = block.getBlockBoundsMaxZ();
                double minX = block.getBlockBoundsMinX();
                double minY = block.getBlockBoundsMinY();
                double minZ = block.getBlockBoundsMinZ();

                IBlockAccess renderWorld = renderer.blockAccess;
//                renderer.renderAllFaces=true;
                for (int i = 0; i< 6; i++)
                {
//                    ForgeDirection dir = ForgeDirection.getOrientation(i);
//                    if (block.shouldSideBeRendered(renderWorld, x, y, z, i))
//                    {
                        if (camouflage.hasSideBlock(i))
                        {
                            setBlockBounds(CAMO_RENDER, minX, minY, minZ, maxX, maxY, maxZ, i);
                            CAMO_RENDER.copyFrom(renderer);
                            CAMO_RENDER.setBlockAccess(new CamouflageBlockAccess(i, camouflage, renderWorld));
                            CAMO_RENDER.renderBlockByRenderType(camouflage.getSideBlock(i), x, y, z);
                        } else
                        {
                            setBlockBounds(renderer, minX, minY, minZ, maxX, maxY, maxZ, i);
                            renderer.renderStandardBlock(block, x, y, z);
                        }
//                    }
                }
//                renderer.renderAllFaces = false;
                renderer.unlockBlockBounds();

//                if (camouflage.getCamouflageType().useDoubleRendering())
//                {
//                    BlockCamouflageBase camoBlock = (BlockCamouflageBase) block;
//
//                    float maxX = (float) block.getBlockBoundsMaxX();
//                    float maxY = (float) block.getBlockBoundsMaxY();
//                    float maxZ = (float) block.getBlockBoundsMaxZ();
//                    float minX = (float) block.getBlockBoundsMinX();
//                    float minY = (float) block.getBlockBoundsMinY();
//                    float minZ = (float) block.getBlockBoundsMinZ();
//
//                    float f = 0.0015F;
//                    float f2 = 0.002F;
//                    block.setBlockBounds(maxX + f2, maxY - f, maxZ + f2, minX - f2, minY + f, minZ - f2);
//                    renderer.setRenderBoundsFromBlock(block);
//                    renderer.renderFaceYNeg(block, x, y, z, camouflage.getIconWithDefault(world, x, y, z, camoBlock, 1, true));
//
//                    block.setBlockBounds(maxX + f2, maxY - f, maxZ + f2, minX - f2, minY + f, minZ - f2);
//                    renderer.setRenderBoundsFromBlock(block);
//                    renderer.renderFaceYPos(block, x, y, z, camouflage.getIconWithDefault(world, x, y, z, camoBlock, 0, true));
//
//                    block.setBlockBounds(maxX + f2, maxY + f2, maxZ - f, minX - f2, minY - f2, minZ + f);
//                    renderer.setRenderBoundsFromBlock(block);
//                    renderer.renderFaceZNeg(block, x, y, z, camouflage.getIconWithDefault(world, x, y, z, camoBlock, 3, true));
//
//                    block.setBlockBounds(maxX + f2, maxY + f2, maxZ - f, minX - f2, minY - f2, minZ + f);
//                    renderer.setRenderBoundsFromBlock(block);
//                    renderer.renderFaceZPos(block, x, y, z, camouflage.getIconWithDefault(world, x, y, z, camoBlock, 2, true));
//
//                    block.setBlockBounds(maxX - f, maxY + f2, maxZ + f2, minX + f, minY - f2, minZ - f2);
//                    renderer.setRenderBoundsFromBlock(block);
//                    renderer.renderFaceXNeg(block, x, y, z, camouflage.getIconWithDefault(world, x, y, z, camoBlock, 5, true));
//
//                    block.setBlockBounds(maxX - f, maxY + f2, maxZ + f2, minX + f, minY - f2, minZ - f2);
//                    renderer.setRenderBoundsFromBlock(block);
//                    renderer.renderFaceXPos(block, x, y, z, camouflage.getIconWithDefault(world, x, y, z, camoBlock, 4, true));
//
//                }
            } else
            {
                block.setBlockBoundsBasedOnState(world, x, y, z);
                renderer.setRenderBoundsFromBlock(block);
                renderer.renderStandardBlock(block, x, y, z);
            }
            return true;
        }

        return false;
    }

    private static void setBlockBounds(RenderBlocks renderer, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, int side)
    {
        switch (side)
        {
            case 0:
                renderer.overrideBlockBounds(minX, minY, minZ, maxX, minY+THICKNESS, maxZ);
                break;
            case 1:
                renderer.overrideBlockBounds(minX, maxY-THICKNESS, minZ, maxX, maxY, maxZ);
                break;
            case 2:
                renderer.overrideBlockBounds(minX, minY, minZ, maxX, maxY, minZ + THICKNESS);
                break;
            case 3:
                renderer.overrideBlockBounds(minX, minY, maxZ - THICKNESS, maxX, maxY, maxZ);
                break;
            case 4:
                renderer.overrideBlockBounds(minX, minY, minZ, minX + THICKNESS, maxY, maxZ);
                break;
            case 5:
                renderer.overrideBlockBounds(maxX - THICKNESS, minY, minZ, maxX, maxY, maxZ);
                break;
        }
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId)
    {
        return true;
    }

    @Override
    public int getRenderId()
    {
        return id;
    }


}
