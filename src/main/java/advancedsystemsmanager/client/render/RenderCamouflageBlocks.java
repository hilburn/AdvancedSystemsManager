package advancedsystemsmanager.client.render;

import net.minecraft.block.Block;
import net.minecraft.block.BlockGrass;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;

public class RenderCamouflageBlocks extends RenderBlocks
{
    int side;

    public void copyFrom(RenderBlocks render)
    {
        this.blockAccess = render.blockAccess;
        this.field_152631_f = render.field_152631_f;
        this.flipTexture = render.flipTexture;
    }

    public void setBlockAccess(CamouflageBlockAccess blockAccess)
    {
        this.blockAccess = blockAccess;
        this.side = blockAccess.side;
    }

    @Override
    public boolean renderStandardBlockWithAmbientOcclusionPartial(Block block, int x, int y, int z, float p_147808_5_, float p_147808_6_, float p_147808_7_)
    {
        this.enableAO = true;
        boolean flag = false;
        float f3 = 0.0F;
        float f4 = 0.0F;
        float f5 = 0.0F;
        float f6 = 0.0F;
        boolean flag1 = true;
        int l = block.getMixedBrightnessForBlock(this.blockAccess, x, y, z);
        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(983055);

        if (this.getBlockIcon(block).getIconName().equals("grass_top"))
        {
            flag1 = false;
        }
        else if (this.hasOverrideBlockTexture())
        {
            flag1 = false;
        }

        boolean flag2;
        boolean flag3;
        boolean flag4;
        boolean flag5;
        int i1;
        float f7;

        if (this.renderAllFaces || (this.side == 0 && block.shouldSideBeRendered(this.blockAccess, x, y - 1, z, 0)))
        {
            if (this.renderMinY <= 0.0D)
            {
                --y;
            }

            this.aoBrightnessXYNN = block.getMixedBrightnessForBlock(this.blockAccess, x - 1, y, z);
            this.aoBrightnessYZNN = block.getMixedBrightnessForBlock(this.blockAccess, x, y, z - 1);
            this.aoBrightnessYZNP = block.getMixedBrightnessForBlock(this.blockAccess, x, y, z + 1);
            this.aoBrightnessXYPN = block.getMixedBrightnessForBlock(this.blockAccess, x + 1, y, z);
            this.aoLightValueScratchXYNN = this.blockAccess.getBlock(x - 1, y, z).getAmbientOcclusionLightValue();
            this.aoLightValueScratchYZNN = this.blockAccess.getBlock(x, y, z - 1).getAmbientOcclusionLightValue();
            this.aoLightValueScratchYZNP = this.blockAccess.getBlock(x, y, z + 1).getAmbientOcclusionLightValue();
            this.aoLightValueScratchXYPN = this.blockAccess.getBlock(x + 1, y, z).getAmbientOcclusionLightValue();
            flag2 = this.blockAccess.getBlock(x + 1, y - 1, z).getCanBlockGrass();
            flag3 = this.blockAccess.getBlock(x - 1, y - 1, z).getCanBlockGrass();
            flag4 = this.blockAccess.getBlock(x, y - 1, z + 1).getCanBlockGrass();
            flag5 = this.blockAccess.getBlock(x, y - 1, z - 1).getCanBlockGrass();

            if (!flag5 && !flag3)
            {
                this.aoLightValueScratchXYZNNN = this.aoLightValueScratchXYNN;
                this.aoBrightnessXYZNNN = this.aoBrightnessXYNN;
            }
            else
            {
                this.aoLightValueScratchXYZNNN = this.blockAccess.getBlock(x - 1, y, z - 1).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZNNN = block.getMixedBrightnessForBlock(this.blockAccess, x - 1, y, z - 1);
            }

            if (!flag4 && !flag3)
            {
                this.aoLightValueScratchXYZNNP = this.aoLightValueScratchXYNN;
                this.aoBrightnessXYZNNP = this.aoBrightnessXYNN;
            }
            else
            {
                this.aoLightValueScratchXYZNNP = this.blockAccess.getBlock(x - 1, y, z + 1).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZNNP = block.getMixedBrightnessForBlock(this.blockAccess, x - 1, y, z + 1);
            }

            if (!flag5 && !flag2)
            {
                this.aoLightValueScratchXYZPNN = this.aoLightValueScratchXYPN;
                this.aoBrightnessXYZPNN = this.aoBrightnessXYPN;
            }
            else
            {
                this.aoLightValueScratchXYZPNN = this.blockAccess.getBlock(x + 1, y, z - 1).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZPNN = block.getMixedBrightnessForBlock(this.blockAccess, x + 1, y, z - 1);
            }

            if (!flag4 && !flag2)
            {
                this.aoLightValueScratchXYZPNP = this.aoLightValueScratchXYPN;
                this.aoBrightnessXYZPNP = this.aoBrightnessXYPN;
            }
            else
            {
                this.aoLightValueScratchXYZPNP = this.blockAccess.getBlock(x + 1, y, z + 1).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZPNP = block.getMixedBrightnessForBlock(this.blockAccess, x + 1, y, z + 1);
            }

            if (this.renderMinY <= 0.0D)
            {
                ++y;
            }

            i1 = l;

            if (this.renderMinY <= 0.0D || !this.blockAccess.getBlock(x, y - 1, z).isOpaqueCube())
            {
                i1 = block.getMixedBrightnessForBlock(this.blockAccess, x, y - 1, z);
            }

            f7 = this.blockAccess.getBlock(x, y - 1, z).getAmbientOcclusionLightValue();
            f3 = (this.aoLightValueScratchXYZNNP + this.aoLightValueScratchXYNN + this.aoLightValueScratchYZNP + f7) / 4.0F;
            f6 = (this.aoLightValueScratchYZNP + f7 + this.aoLightValueScratchXYZPNP + this.aoLightValueScratchXYPN) / 4.0F;
            f5 = (f7 + this.aoLightValueScratchYZNN + this.aoLightValueScratchXYPN + this.aoLightValueScratchXYZPNN) / 4.0F;
            f4 = (this.aoLightValueScratchXYNN + this.aoLightValueScratchXYZNNN + f7 + this.aoLightValueScratchYZNN) / 4.0F;
            this.brightnessTopLeft = this.getAoBrightness(this.aoBrightnessXYZNNP, this.aoBrightnessXYNN, this.aoBrightnessYZNP, i1);
            this.brightnessTopRight = this.getAoBrightness(this.aoBrightnessYZNP, this.aoBrightnessXYZPNP, this.aoBrightnessXYPN, i1);
            this.brightnessBottomRight = this.getAoBrightness(this.aoBrightnessYZNN, this.aoBrightnessXYPN, this.aoBrightnessXYZPNN, i1);
            this.brightnessBottomLeft = this.getAoBrightness(this.aoBrightnessXYNN, this.aoBrightnessXYZNNN, this.aoBrightnessYZNN, i1);

            if (flag1)
            {
                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = p_147808_5_ * 0.5F;
                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = p_147808_6_ * 0.5F;
                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = p_147808_7_ * 0.5F;
            }
            else
            {
                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = 0.5F;
                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = 0.5F;
                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = 0.5F;
            }

            this.colorRedTopLeft *= f3;
            this.colorGreenTopLeft *= f3;
            this.colorBlueTopLeft *= f3;
            this.colorRedBottomLeft *= f4;
            this.colorGreenBottomLeft *= f4;
            this.colorBlueBottomLeft *= f4;
            this.colorRedBottomRight *= f5;
            this.colorGreenBottomRight *= f5;
            this.colorBlueBottomRight *= f5;
            this.colorRedTopRight *= f6;
            this.colorGreenTopRight *= f6;
            this.colorBlueTopRight *= f6;
            this.renderFaceYNeg(block, (double) x, (double) y, (double) z, this.getBlockIcon(block, this.blockAccess, x, y, z, 0));
            flag = true;
        }

        if (this.renderAllFaces || (side == 1 && block.shouldSideBeRendered(this.blockAccess, x, y + 1, z, 1)))
        {
            if (this.renderMaxY >= 1.0D)
            {
                ++y;
            }

            this.aoBrightnessXYNP = block.getMixedBrightnessForBlock(this.blockAccess, x - 1, y, z);
            this.aoBrightnessXYPP = block.getMixedBrightnessForBlock(this.blockAccess, x + 1, y, z);
            this.aoBrightnessYZPN = block.getMixedBrightnessForBlock(this.blockAccess, x, y, z - 1);
            this.aoBrightnessYZPP = block.getMixedBrightnessForBlock(this.blockAccess, x, y, z + 1);
            this.aoLightValueScratchXYNP = this.blockAccess.getBlock(x - 1, y, z).getAmbientOcclusionLightValue();
            this.aoLightValueScratchXYPP = this.blockAccess.getBlock(x + 1, y, z).getAmbientOcclusionLightValue();
            this.aoLightValueScratchYZPN = this.blockAccess.getBlock(x, y, z - 1).getAmbientOcclusionLightValue();
            this.aoLightValueScratchYZPP = this.blockAccess.getBlock(x, y, z + 1).getAmbientOcclusionLightValue();
            flag2 = this.blockAccess.getBlock(x + 1, y + 1, z).getCanBlockGrass();
            flag3 = this.blockAccess.getBlock(x - 1, y + 1, z).getCanBlockGrass();
            flag4 = this.blockAccess.getBlock(x, y + 1, z + 1).getCanBlockGrass();
            flag5 = this.blockAccess.getBlock(x, y + 1, z - 1).getCanBlockGrass();

            if (!flag5 && !flag3)
            {
                this.aoLightValueScratchXYZNPN = this.aoLightValueScratchXYNP;
                this.aoBrightnessXYZNPN = this.aoBrightnessXYNP;
            }
            else
            {
                this.aoLightValueScratchXYZNPN = this.blockAccess.getBlock(x - 1, y, z - 1).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZNPN = block.getMixedBrightnessForBlock(this.blockAccess, x - 1, y, z - 1);
            }

            if (!flag5 && !flag2)
            {
                this.aoLightValueScratchXYZPPN = this.aoLightValueScratchXYPP;
                this.aoBrightnessXYZPPN = this.aoBrightnessXYPP;
            }
            else
            {
                this.aoLightValueScratchXYZPPN = this.blockAccess.getBlock(x + 1, y, z - 1).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZPPN = block.getMixedBrightnessForBlock(this.blockAccess, x + 1, y, z - 1);
            }

            if (!flag4 && !flag3)
            {
                this.aoLightValueScratchXYZNPP = this.aoLightValueScratchXYNP;
                this.aoBrightnessXYZNPP = this.aoBrightnessXYNP;
            }
            else
            {
                this.aoLightValueScratchXYZNPP = this.blockAccess.getBlock(x - 1, y, z + 1).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZNPP = block.getMixedBrightnessForBlock(this.blockAccess, x - 1, y, z + 1);
            }

            if (!flag4 && !flag2)
            {
                this.aoLightValueScratchXYZPPP = this.aoLightValueScratchXYPP;
                this.aoBrightnessXYZPPP = this.aoBrightnessXYPP;
            }
            else
            {
                this.aoLightValueScratchXYZPPP = this.blockAccess.getBlock(x + 1, y, z + 1).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZPPP = block.getMixedBrightnessForBlock(this.blockAccess, x + 1, y, z + 1);
            }

            if (this.renderMaxY >= 1.0D)
            {
                --y;
            }

            i1 = l;

            if (this.renderMaxY >= 1.0D || !this.blockAccess.getBlock(x, y + 1, z).isOpaqueCube())
            {
                i1 = block.getMixedBrightnessForBlock(this.blockAccess, x, y + 1, z);
            }

            f7 = this.blockAccess.getBlock(x, y + 1, z).getAmbientOcclusionLightValue();
            f6 = (this.aoLightValueScratchXYZNPP + this.aoLightValueScratchXYNP + this.aoLightValueScratchYZPP + f7) / 4.0F;
            f3 = (this.aoLightValueScratchYZPP + f7 + this.aoLightValueScratchXYZPPP + this.aoLightValueScratchXYPP) / 4.0F;
            f4 = (f7 + this.aoLightValueScratchYZPN + this.aoLightValueScratchXYPP + this.aoLightValueScratchXYZPPN) / 4.0F;
            f5 = (this.aoLightValueScratchXYNP + this.aoLightValueScratchXYZNPN + f7 + this.aoLightValueScratchYZPN) / 4.0F;
            this.brightnessTopRight = this.getAoBrightness(this.aoBrightnessXYZNPP, this.aoBrightnessXYNP, this.aoBrightnessYZPP, i1);
            this.brightnessTopLeft = this.getAoBrightness(this.aoBrightnessYZPP, this.aoBrightnessXYZPPP, this.aoBrightnessXYPP, i1);
            this.brightnessBottomLeft = this.getAoBrightness(this.aoBrightnessYZPN, this.aoBrightnessXYPP, this.aoBrightnessXYZPPN, i1);
            this.brightnessBottomRight = this.getAoBrightness(this.aoBrightnessXYNP, this.aoBrightnessXYZNPN, this.aoBrightnessYZPN, i1);
            this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = p_147808_5_;
            this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = p_147808_6_;
            this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = p_147808_7_;
            this.colorRedTopLeft *= f3;
            this.colorGreenTopLeft *= f3;
            this.colorBlueTopLeft *= f3;
            this.colorRedBottomLeft *= f4;
            this.colorGreenBottomLeft *= f4;
            this.colorBlueBottomLeft *= f4;
            this.colorRedBottomRight *= f5;
            this.colorGreenBottomRight *= f5;
            this.colorBlueBottomRight *= f5;
            this.colorRedTopRight *= f6;
            this.colorGreenTopRight *= f6;
            this.colorBlueTopRight *= f6;
            this.renderFaceYPos(block, (double) x, (double) y, (double) z, this.getBlockIcon(block, this.blockAccess, x, y, z, 1));
            flag = true;
        }

        float f8;
        float f9;
        float f10;
        float f11;
        int j1;
        int k1;
        int l1;
        int i2;
        IIcon iicon;

        if (this.renderAllFaces || (side == 2 && block.shouldSideBeRendered(this.blockAccess, x, y, z - 1, 2)))
        {
            if (this.renderMinZ <= 0.0D)
            {
                --z;
            }

            this.aoLightValueScratchXZNN = this.blockAccess.getBlock(x - 1, y, z).getAmbientOcclusionLightValue();
            this.aoLightValueScratchYZNN = this.blockAccess.getBlock(x, y - 1, z).getAmbientOcclusionLightValue();
            this.aoLightValueScratchYZPN = this.blockAccess.getBlock(x, y + 1, z).getAmbientOcclusionLightValue();
            this.aoLightValueScratchXZPN = this.blockAccess.getBlock(x + 1, y, z).getAmbientOcclusionLightValue();
            this.aoBrightnessXZNN = block.getMixedBrightnessForBlock(this.blockAccess, x - 1, y, z);
            this.aoBrightnessYZNN = block.getMixedBrightnessForBlock(this.blockAccess, x, y - 1, z);
            this.aoBrightnessYZPN = block.getMixedBrightnessForBlock(this.blockAccess, x, y + 1, z);
            this.aoBrightnessXZPN = block.getMixedBrightnessForBlock(this.blockAccess, x + 1, y, z);
            flag2 = this.blockAccess.getBlock(x + 1, y, z - 1).getCanBlockGrass();
            flag3 = this.blockAccess.getBlock(x - 1, y, z - 1).getCanBlockGrass();
            flag4 = this.blockAccess.getBlock(x, y + 1, z - 1).getCanBlockGrass();
            flag5 = this.blockAccess.getBlock(x, y - 1, z - 1).getCanBlockGrass();

            if (!flag3 && !flag5)
            {
                this.aoLightValueScratchXYZNNN = this.aoLightValueScratchXZNN;
                this.aoBrightnessXYZNNN = this.aoBrightnessXZNN;
            }
            else
            {
                this.aoLightValueScratchXYZNNN = this.blockAccess.getBlock(x - 1, y - 1, z).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZNNN = block.getMixedBrightnessForBlock(this.blockAccess, x - 1, y - 1, z);
            }

            if (!flag3 && !flag4)
            {
                this.aoLightValueScratchXYZNPN = this.aoLightValueScratchXZNN;
                this.aoBrightnessXYZNPN = this.aoBrightnessXZNN;
            }
            else
            {
                this.aoLightValueScratchXYZNPN = this.blockAccess.getBlock(x - 1, y + 1, z).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZNPN = block.getMixedBrightnessForBlock(this.blockAccess, x - 1, y + 1, z);
            }

            if (!flag2 && !flag5)
            {
                this.aoLightValueScratchXYZPNN = this.aoLightValueScratchXZPN;
                this.aoBrightnessXYZPNN = this.aoBrightnessXZPN;
            }
            else
            {
                this.aoLightValueScratchXYZPNN = this.blockAccess.getBlock(x + 1, y - 1, z).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZPNN = block.getMixedBrightnessForBlock(this.blockAccess, x + 1, y - 1, z);
            }

            if (!flag2 && !flag4)
            {
                this.aoLightValueScratchXYZPPN = this.aoLightValueScratchXZPN;
                this.aoBrightnessXYZPPN = this.aoBrightnessXZPN;
            }
            else
            {
                this.aoLightValueScratchXYZPPN = this.blockAccess.getBlock(x + 1, y + 1, z).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZPPN = block.getMixedBrightnessForBlock(this.blockAccess, x + 1, y + 1, z);
            }

            if (this.renderMinZ <= 0.0D)
            {
                ++z;
            }

            i1 = l;

            if (this.renderMinZ <= 0.0D || !this.blockAccess.getBlock(x, y, z - 1).isOpaqueCube())
            {
                i1 = block.getMixedBrightnessForBlock(this.blockAccess, x, y, z - 1);
            }

            f7 = this.blockAccess.getBlock(x, y, z - 1).getAmbientOcclusionLightValue();
            f8 = (this.aoLightValueScratchXZNN + this.aoLightValueScratchXYZNPN + f7 + this.aoLightValueScratchYZPN) / 4.0F;
            f9 = (f7 + this.aoLightValueScratchYZPN + this.aoLightValueScratchXZPN + this.aoLightValueScratchXYZPPN) / 4.0F;
            f10 = (this.aoLightValueScratchYZNN + f7 + this.aoLightValueScratchXYZPNN + this.aoLightValueScratchXZPN) / 4.0F;
            f11 = (this.aoLightValueScratchXYZNNN + this.aoLightValueScratchXZNN + this.aoLightValueScratchYZNN + f7) / 4.0F;
            f3 = (float)((double)f8 * this.renderMaxY * (1.0D - this.renderMinX) + (double)f9 * this.renderMaxY * this.renderMinX + (double)f10 * (1.0D - this.renderMaxY) * this.renderMinX + (double)f11 * (1.0D - this.renderMaxY) * (1.0D - this.renderMinX));
            f4 = (float)((double)f8 * this.renderMaxY * (1.0D - this.renderMaxX) + (double)f9 * this.renderMaxY * this.renderMaxX + (double)f10 * (1.0D - this.renderMaxY) * this.renderMaxX + (double)f11 * (1.0D - this.renderMaxY) * (1.0D - this.renderMaxX));
            f5 = (float)((double)f8 * this.renderMinY * (1.0D - this.renderMaxX) + (double)f9 * this.renderMinY * this.renderMaxX + (double)f10 * (1.0D - this.renderMinY) * this.renderMaxX + (double)f11 * (1.0D - this.renderMinY) * (1.0D - this.renderMaxX));
            f6 = (float)((double)f8 * this.renderMinY * (1.0D - this.renderMinX) + (double)f9 * this.renderMinY * this.renderMinX + (double)f10 * (1.0D - this.renderMinY) * this.renderMinX + (double)f11 * (1.0D - this.renderMinY) * (1.0D - this.renderMinX));
            j1 = this.getAoBrightness(this.aoBrightnessXZNN, this.aoBrightnessXYZNPN, this.aoBrightnessYZPN, i1);
            k1 = this.getAoBrightness(this.aoBrightnessYZPN, this.aoBrightnessXZPN, this.aoBrightnessXYZPPN, i1);
            l1 = this.getAoBrightness(this.aoBrightnessYZNN, this.aoBrightnessXYZPNN, this.aoBrightnessXZPN, i1);
            i2 = this.getAoBrightness(this.aoBrightnessXYZNNN, this.aoBrightnessXZNN, this.aoBrightnessYZNN, i1);
            this.brightnessTopLeft = this.mixAoBrightness(j1, k1, l1, i2, this.renderMaxY * (1.0D - this.renderMinX), this.renderMaxY * this.renderMinX, (1.0D - this.renderMaxY) * this.renderMinX, (1.0D - this.renderMaxY) * (1.0D - this.renderMinX));
            this.brightnessBottomLeft = this.mixAoBrightness(j1, k1, l1, i2, this.renderMaxY * (1.0D - this.renderMaxX), this.renderMaxY * this.renderMaxX, (1.0D - this.renderMaxY) * this.renderMaxX, (1.0D - this.renderMaxY) * (1.0D - this.renderMaxX));
            this.brightnessBottomRight = this.mixAoBrightness(j1, k1, l1, i2, this.renderMinY * (1.0D - this.renderMaxX), this.renderMinY * this.renderMaxX, (1.0D - this.renderMinY) * this.renderMaxX, (1.0D - this.renderMinY) * (1.0D - this.renderMaxX));
            this.brightnessTopRight = this.mixAoBrightness(j1, k1, l1, i2, this.renderMinY * (1.0D - this.renderMinX), this.renderMinY * this.renderMinX, (1.0D - this.renderMinY) * this.renderMinX, (1.0D - this.renderMinY) * (1.0D - this.renderMinX));

            if (flag1)
            {
                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = p_147808_5_ * 0.8F;
                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = p_147808_6_ * 0.8F;
                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = p_147808_7_ * 0.8F;
            }
            else
            {
                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = 0.8F;
                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = 0.8F;
                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = 0.8F;
            }

            this.colorRedTopLeft *= f3;
            this.colorGreenTopLeft *= f3;
            this.colorBlueTopLeft *= f3;
            this.colorRedBottomLeft *= f4;
            this.colorGreenBottomLeft *= f4;
            this.colorBlueBottomLeft *= f4;
            this.colorRedBottomRight *= f5;
            this.colorGreenBottomRight *= f5;
            this.colorBlueBottomRight *= f5;
            this.colorRedTopRight *= f6;
            this.colorGreenTopRight *= f6;
            this.colorBlueTopRight *= f6;
            iicon = this.getBlockIcon(block, this.blockAccess, x, y, z, 2);
            this.renderFaceZNeg(block, (double)x, (double)y, (double)z, iicon);

            if (fancyGrass && iicon.getIconName().equals("grass_side") && !this.hasOverrideBlockTexture())
            {
                this.colorRedTopLeft *= p_147808_5_;
                this.colorRedBottomLeft *= p_147808_5_;
                this.colorRedBottomRight *= p_147808_5_;
                this.colorRedTopRight *= p_147808_5_;
                this.colorGreenTopLeft *= p_147808_6_;
                this.colorGreenBottomLeft *= p_147808_6_;
                this.colorGreenBottomRight *= p_147808_6_;
                this.colorGreenTopRight *= p_147808_6_;
                this.colorBlueTopLeft *= p_147808_7_;
                this.colorBlueBottomLeft *= p_147808_7_;
                this.colorBlueBottomRight *= p_147808_7_;
                this.colorBlueTopRight *= p_147808_7_;
                this.renderFaceZNeg(block, (double) x, (double) y, (double) z, BlockGrass.getIconSideOverlay());
            }

            flag = true;
        }

        if (this.renderAllFaces || (side == 3 && block.shouldSideBeRendered(this.blockAccess, x, y, z + 1, 3)))
        {
            if (this.renderMaxZ >= 1.0D)
            {
                ++z;
            }

            this.aoLightValueScratchXZNP = this.blockAccess.getBlock(x - 1, y, z).getAmbientOcclusionLightValue();
            this.aoLightValueScratchXZPP = this.blockAccess.getBlock(x + 1, y, z).getAmbientOcclusionLightValue();
            this.aoLightValueScratchYZNP = this.blockAccess.getBlock(x, y - 1, z).getAmbientOcclusionLightValue();
            this.aoLightValueScratchYZPP = this.blockAccess.getBlock(x, y + 1, z).getAmbientOcclusionLightValue();
            this.aoBrightnessXZNP = block.getMixedBrightnessForBlock(this.blockAccess, x - 1, y, z);
            this.aoBrightnessXZPP = block.getMixedBrightnessForBlock(this.blockAccess, x + 1, y, z);
            this.aoBrightnessYZNP = block.getMixedBrightnessForBlock(this.blockAccess, x, y - 1, z);
            this.aoBrightnessYZPP = block.getMixedBrightnessForBlock(this.blockAccess, x, y + 1, z);
            flag2 = this.blockAccess.getBlock(x + 1, y, z + 1).getCanBlockGrass();
            flag3 = this.blockAccess.getBlock(x - 1, y, z + 1).getCanBlockGrass();
            flag4 = this.blockAccess.getBlock(x, y + 1, z + 1).getCanBlockGrass();
            flag5 = this.blockAccess.getBlock(x, y - 1, z + 1).getCanBlockGrass();

            if (!flag3 && !flag5)
            {
                this.aoLightValueScratchXYZNNP = this.aoLightValueScratchXZNP;
                this.aoBrightnessXYZNNP = this.aoBrightnessXZNP;
            }
            else
            {
                this.aoLightValueScratchXYZNNP = this.blockAccess.getBlock(x - 1, y - 1, z).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZNNP = block.getMixedBrightnessForBlock(this.blockAccess, x - 1, y - 1, z);
            }

            if (!flag3 && !flag4)
            {
                this.aoLightValueScratchXYZNPP = this.aoLightValueScratchXZNP;
                this.aoBrightnessXYZNPP = this.aoBrightnessXZNP;
            }
            else
            {
                this.aoLightValueScratchXYZNPP = this.blockAccess.getBlock(x - 1, y + 1, z).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZNPP = block.getMixedBrightnessForBlock(this.blockAccess, x - 1, y + 1, z);
            }

            if (!flag2 && !flag5)
            {
                this.aoLightValueScratchXYZPNP = this.aoLightValueScratchXZPP;
                this.aoBrightnessXYZPNP = this.aoBrightnessXZPP;
            }
            else
            {
                this.aoLightValueScratchXYZPNP = this.blockAccess.getBlock(x + 1, y - 1, z).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZPNP = block.getMixedBrightnessForBlock(this.blockAccess, x + 1, y - 1, z);
            }

            if (!flag2 && !flag4)
            {
                this.aoLightValueScratchXYZPPP = this.aoLightValueScratchXZPP;
                this.aoBrightnessXYZPPP = this.aoBrightnessXZPP;
            }
            else
            {
                this.aoLightValueScratchXYZPPP = this.blockAccess.getBlock(x + 1, y + 1, z).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZPPP = block.getMixedBrightnessForBlock(this.blockAccess, x + 1, y + 1, z);
            }

            if (this.renderMaxZ >= 1.0D)
            {
                --z;
            }

            i1 = l;

            if (this.renderMaxZ >= 1.0D || !this.blockAccess.getBlock(x, y, z + 1).isOpaqueCube())
            {
                i1 = block.getMixedBrightnessForBlock(this.blockAccess, x, y, z + 1);
            }

            f7 = this.blockAccess.getBlock(x, y, z + 1).getAmbientOcclusionLightValue();
            f8 = (this.aoLightValueScratchXZNP + this.aoLightValueScratchXYZNPP + f7 + this.aoLightValueScratchYZPP) / 4.0F;
            f9 = (f7 + this.aoLightValueScratchYZPP + this.aoLightValueScratchXZPP + this.aoLightValueScratchXYZPPP) / 4.0F;
            f10 = (this.aoLightValueScratchYZNP + f7 + this.aoLightValueScratchXYZPNP + this.aoLightValueScratchXZPP) / 4.0F;
            f11 = (this.aoLightValueScratchXYZNNP + this.aoLightValueScratchXZNP + this.aoLightValueScratchYZNP + f7) / 4.0F;
            f3 = (float)((double)f8 * this.renderMaxY * (1.0D - this.renderMinX) + (double)f9 * this.renderMaxY * this.renderMinX + (double)f10 * (1.0D - this.renderMaxY) * this.renderMinX + (double)f11 * (1.0D - this.renderMaxY) * (1.0D - this.renderMinX));
            f4 = (float)((double)f8 * this.renderMinY * (1.0D - this.renderMinX) + (double)f9 * this.renderMinY * this.renderMinX + (double)f10 * (1.0D - this.renderMinY) * this.renderMinX + (double)f11 * (1.0D - this.renderMinY) * (1.0D - this.renderMinX));
            f5 = (float)((double)f8 * this.renderMinY * (1.0D - this.renderMaxX) + (double)f9 * this.renderMinY * this.renderMaxX + (double)f10 * (1.0D - this.renderMinY) * this.renderMaxX + (double)f11 * (1.0D - this.renderMinY) * (1.0D - this.renderMaxX));
            f6 = (float)((double)f8 * this.renderMaxY * (1.0D - this.renderMaxX) + (double)f9 * this.renderMaxY * this.renderMaxX + (double)f10 * (1.0D - this.renderMaxY) * this.renderMaxX + (double)f11 * (1.0D - this.renderMaxY) * (1.0D - this.renderMaxX));
            j1 = this.getAoBrightness(this.aoBrightnessXZNP, this.aoBrightnessXYZNPP, this.aoBrightnessYZPP, i1);
            k1 = this.getAoBrightness(this.aoBrightnessYZPP, this.aoBrightnessXZPP, this.aoBrightnessXYZPPP, i1);
            l1 = this.getAoBrightness(this.aoBrightnessYZNP, this.aoBrightnessXYZPNP, this.aoBrightnessXZPP, i1);
            i2 = this.getAoBrightness(this.aoBrightnessXYZNNP, this.aoBrightnessXZNP, this.aoBrightnessYZNP, i1);
            this.brightnessTopLeft = this.mixAoBrightness(j1, i2, l1, k1, this.renderMaxY * (1.0D - this.renderMinX), (1.0D - this.renderMaxY) * (1.0D - this.renderMinX), (1.0D - this.renderMaxY) * this.renderMinX, this.renderMaxY * this.renderMinX);
            this.brightnessBottomLeft = this.mixAoBrightness(j1, i2, l1, k1, this.renderMinY * (1.0D - this.renderMinX), (1.0D - this.renderMinY) * (1.0D - this.renderMinX), (1.0D - this.renderMinY) * this.renderMinX, this.renderMinY * this.renderMinX);
            this.brightnessBottomRight = this.mixAoBrightness(j1, i2, l1, k1, this.renderMinY * (1.0D - this.renderMaxX), (1.0D - this.renderMinY) * (1.0D - this.renderMaxX), (1.0D - this.renderMinY) * this.renderMaxX, this.renderMinY * this.renderMaxX);
            this.brightnessTopRight = this.mixAoBrightness(j1, i2, l1, k1, this.renderMaxY * (1.0D - this.renderMaxX), (1.0D - this.renderMaxY) * (1.0D - this.renderMaxX), (1.0D - this.renderMaxY) * this.renderMaxX, this.renderMaxY * this.renderMaxX);

            if (flag1)
            {
                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = p_147808_5_ * 0.8F;
                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = p_147808_6_ * 0.8F;
                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = p_147808_7_ * 0.8F;
            }
            else
            {
                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = 0.8F;
                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = 0.8F;
                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = 0.8F;
            }

            this.colorRedTopLeft *= f3;
            this.colorGreenTopLeft *= f3;
            this.colorBlueTopLeft *= f3;
            this.colorRedBottomLeft *= f4;
            this.colorGreenBottomLeft *= f4;
            this.colorBlueBottomLeft *= f4;
            this.colorRedBottomRight *= f5;
            this.colorGreenBottomRight *= f5;
            this.colorBlueBottomRight *= f5;
            this.colorRedTopRight *= f6;
            this.colorGreenTopRight *= f6;
            this.colorBlueTopRight *= f6;
            iicon = this.getBlockIcon(block, this.blockAccess, x, y, z, 3);
            this.renderFaceZPos(block, (double)x, (double)y, (double)z, iicon);

            if (fancyGrass && iicon.getIconName().equals("grass_side") && !this.hasOverrideBlockTexture())
            {
                this.colorRedTopLeft *= p_147808_5_;
                this.colorRedBottomLeft *= p_147808_5_;
                this.colorRedBottomRight *= p_147808_5_;
                this.colorRedTopRight *= p_147808_5_;
                this.colorGreenTopLeft *= p_147808_6_;
                this.colorGreenBottomLeft *= p_147808_6_;
                this.colorGreenBottomRight *= p_147808_6_;
                this.colorGreenTopRight *= p_147808_6_;
                this.colorBlueTopLeft *= p_147808_7_;
                this.colorBlueBottomLeft *= p_147808_7_;
                this.colorBlueBottomRight *= p_147808_7_;
                this.colorBlueTopRight *= p_147808_7_;
                this.renderFaceZPos(block, (double) x, (double) y, (double) z, BlockGrass.getIconSideOverlay());
            }

            flag = true;
        }

        if (this.renderAllFaces || (side == 4 && block.shouldSideBeRendered(this.blockAccess, x - 1, y, z, 4)))
        {
            if (this.renderMinX <= 0.0D)
            {
                --x;
            }

            this.aoLightValueScratchXYNN = this.blockAccess.getBlock(x, y - 1, z).getAmbientOcclusionLightValue();
            this.aoLightValueScratchXZNN = this.blockAccess.getBlock(x, y, z - 1).getAmbientOcclusionLightValue();
            this.aoLightValueScratchXZNP = this.blockAccess.getBlock(x, y, z + 1).getAmbientOcclusionLightValue();
            this.aoLightValueScratchXYNP = this.blockAccess.getBlock(x, y + 1, z).getAmbientOcclusionLightValue();
            this.aoBrightnessXYNN = block.getMixedBrightnessForBlock(this.blockAccess, x, y - 1, z);
            this.aoBrightnessXZNN = block.getMixedBrightnessForBlock(this.blockAccess, x, y, z - 1);
            this.aoBrightnessXZNP = block.getMixedBrightnessForBlock(this.blockAccess, x, y, z + 1);
            this.aoBrightnessXYNP = block.getMixedBrightnessForBlock(this.blockAccess, x, y + 1, z);
            flag2 = this.blockAccess.getBlock(x - 1, y + 1, z).getCanBlockGrass();
            flag3 = this.blockAccess.getBlock(x - 1, y - 1, z).getCanBlockGrass();
            flag4 = this.blockAccess.getBlock(x - 1, y, z - 1).getCanBlockGrass();
            flag5 = this.blockAccess.getBlock(x - 1, y, z + 1).getCanBlockGrass();

            if (!flag4 && !flag3)
            {
                this.aoLightValueScratchXYZNNN = this.aoLightValueScratchXZNN;
                this.aoBrightnessXYZNNN = this.aoBrightnessXZNN;
            }
            else
            {
                this.aoLightValueScratchXYZNNN = this.blockAccess.getBlock(x, y - 1, z - 1).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZNNN = block.getMixedBrightnessForBlock(this.blockAccess, x, y - 1, z - 1);
            }

            if (!flag5 && !flag3)
            {
                this.aoLightValueScratchXYZNNP = this.aoLightValueScratchXZNP;
                this.aoBrightnessXYZNNP = this.aoBrightnessXZNP;
            }
            else
            {
                this.aoLightValueScratchXYZNNP = this.blockAccess.getBlock(x, y - 1, z + 1).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZNNP = block.getMixedBrightnessForBlock(this.blockAccess, x, y - 1, z + 1);
            }

            if (!flag4 && !flag2)
            {
                this.aoLightValueScratchXYZNPN = this.aoLightValueScratchXZNN;
                this.aoBrightnessXYZNPN = this.aoBrightnessXZNN;
            }
            else
            {
                this.aoLightValueScratchXYZNPN = this.blockAccess.getBlock(x, y + 1, z - 1).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZNPN = block.getMixedBrightnessForBlock(this.blockAccess, x, y + 1, z - 1);
            }

            if (!flag5 && !flag2)
            {
                this.aoLightValueScratchXYZNPP = this.aoLightValueScratchXZNP;
                this.aoBrightnessXYZNPP = this.aoBrightnessXZNP;
            }
            else
            {
                this.aoLightValueScratchXYZNPP = this.blockAccess.getBlock(x, y + 1, z + 1).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZNPP = block.getMixedBrightnessForBlock(this.blockAccess, x, y + 1, z + 1);
            }

            if (this.renderMinX <= 0.0D)
            {
                ++x;
            }

            i1 = l;

            if (this.renderMinX <= 0.0D || !this.blockAccess.getBlock(x - 1, y, z).isOpaqueCube())
            {
                i1 = block.getMixedBrightnessForBlock(this.blockAccess, x - 1, y, z);
            }

            f7 = this.blockAccess.getBlock(x - 1, y, z).getAmbientOcclusionLightValue();
            f8 = (this.aoLightValueScratchXYNN + this.aoLightValueScratchXYZNNP + f7 + this.aoLightValueScratchXZNP) / 4.0F;
            f9 = (f7 + this.aoLightValueScratchXZNP + this.aoLightValueScratchXYNP + this.aoLightValueScratchXYZNPP) / 4.0F;
            f10 = (this.aoLightValueScratchXZNN + f7 + this.aoLightValueScratchXYZNPN + this.aoLightValueScratchXYNP) / 4.0F;
            f11 = (this.aoLightValueScratchXYZNNN + this.aoLightValueScratchXYNN + this.aoLightValueScratchXZNN + f7) / 4.0F;
            f3 = (float)((double)f9 * this.renderMaxY * this.renderMaxZ + (double)f10 * this.renderMaxY * (1.0D - this.renderMaxZ) + (double)f11 * (1.0D - this.renderMaxY) * (1.0D - this.renderMaxZ) + (double)f8 * (1.0D - this.renderMaxY) * this.renderMaxZ);
            f4 = (float)((double)f9 * this.renderMaxY * this.renderMinZ + (double)f10 * this.renderMaxY * (1.0D - this.renderMinZ) + (double)f11 * (1.0D - this.renderMaxY) * (1.0D - this.renderMinZ) + (double)f8 * (1.0D - this.renderMaxY) * this.renderMinZ);
            f5 = (float)((double)f9 * this.renderMinY * this.renderMinZ + (double)f10 * this.renderMinY * (1.0D - this.renderMinZ) + (double)f11 * (1.0D - this.renderMinY) * (1.0D - this.renderMinZ) + (double)f8 * (1.0D - this.renderMinY) * this.renderMinZ);
            f6 = (float)((double)f9 * this.renderMinY * this.renderMaxZ + (double)f10 * this.renderMinY * (1.0D - this.renderMaxZ) + (double)f11 * (1.0D - this.renderMinY) * (1.0D - this.renderMaxZ) + (double)f8 * (1.0D - this.renderMinY) * this.renderMaxZ);
            j1 = this.getAoBrightness(this.aoBrightnessXYNN, this.aoBrightnessXYZNNP, this.aoBrightnessXZNP, i1);
            k1 = this.getAoBrightness(this.aoBrightnessXZNP, this.aoBrightnessXYNP, this.aoBrightnessXYZNPP, i1);
            l1 = this.getAoBrightness(this.aoBrightnessXZNN, this.aoBrightnessXYZNPN, this.aoBrightnessXYNP, i1);
            i2 = this.getAoBrightness(this.aoBrightnessXYZNNN, this.aoBrightnessXYNN, this.aoBrightnessXZNN, i1);
            this.brightnessTopLeft = this.mixAoBrightness(k1, l1, i2, j1, this.renderMaxY * this.renderMaxZ, this.renderMaxY * (1.0D - this.renderMaxZ), (1.0D - this.renderMaxY) * (1.0D - this.renderMaxZ), (1.0D - this.renderMaxY) * this.renderMaxZ);
            this.brightnessBottomLeft = this.mixAoBrightness(k1, l1, i2, j1, this.renderMaxY * this.renderMinZ, this.renderMaxY * (1.0D - this.renderMinZ), (1.0D - this.renderMaxY) * (1.0D - this.renderMinZ), (1.0D - this.renderMaxY) * this.renderMinZ);
            this.brightnessBottomRight = this.mixAoBrightness(k1, l1, i2, j1, this.renderMinY * this.renderMinZ, this.renderMinY * (1.0D - this.renderMinZ), (1.0D - this.renderMinY) * (1.0D - this.renderMinZ), (1.0D - this.renderMinY) * this.renderMinZ);
            this.brightnessTopRight = this.mixAoBrightness(k1, l1, i2, j1, this.renderMinY * this.renderMaxZ, this.renderMinY * (1.0D - this.renderMaxZ), (1.0D - this.renderMinY) * (1.0D - this.renderMaxZ), (1.0D - this.renderMinY) * this.renderMaxZ);

            if (flag1)
            {
                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = p_147808_5_ * 0.6F;
                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = p_147808_6_ * 0.6F;
                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = p_147808_7_ * 0.6F;
            }
            else
            {
                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = 0.6F;
                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = 0.6F;
                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = 0.6F;
            }

            this.colorRedTopLeft *= f3;
            this.colorGreenTopLeft *= f3;
            this.colorBlueTopLeft *= f3;
            this.colorRedBottomLeft *= f4;
            this.colorGreenBottomLeft *= f4;
            this.colorBlueBottomLeft *= f4;
            this.colorRedBottomRight *= f5;
            this.colorGreenBottomRight *= f5;
            this.colorBlueBottomRight *= f5;
            this.colorRedTopRight *= f6;
            this.colorGreenTopRight *= f6;
            this.colorBlueTopRight *= f6;
            iicon = this.getBlockIcon(block, this.blockAccess, x, y, z, 4);
            this.renderFaceXNeg(block, (double)x, (double)y, (double)z, iicon);

            if (fancyGrass && iicon.getIconName().equals("grass_side") && !this.hasOverrideBlockTexture())
            {
                this.colorRedTopLeft *= p_147808_5_;
                this.colorRedBottomLeft *= p_147808_5_;
                this.colorRedBottomRight *= p_147808_5_;
                this.colorRedTopRight *= p_147808_5_;
                this.colorGreenTopLeft *= p_147808_6_;
                this.colorGreenBottomLeft *= p_147808_6_;
                this.colorGreenBottomRight *= p_147808_6_;
                this.colorGreenTopRight *= p_147808_6_;
                this.colorBlueTopLeft *= p_147808_7_;
                this.colorBlueBottomLeft *= p_147808_7_;
                this.colorBlueBottomRight *= p_147808_7_;
                this.colorBlueTopRight *= p_147808_7_;
                this.renderFaceXNeg(block, (double) x, (double) y, (double) z, BlockGrass.getIconSideOverlay());
            }

            flag = true;
        }

        if (this.renderAllFaces || (side == 5 && block.shouldSideBeRendered(this.blockAccess, x + 1, y, z, 5)))
        {
            if (this.renderMaxX >= 1.0D)
            {
                ++x;
            }

            this.aoLightValueScratchXYPN = this.blockAccess.getBlock(x, y - 1, z).getAmbientOcclusionLightValue();
            this.aoLightValueScratchXZPN = this.blockAccess.getBlock(x, y, z - 1).getAmbientOcclusionLightValue();
            this.aoLightValueScratchXZPP = this.blockAccess.getBlock(x, y, z + 1).getAmbientOcclusionLightValue();
            this.aoLightValueScratchXYPP = this.blockAccess.getBlock(x, y + 1, z).getAmbientOcclusionLightValue();
            this.aoBrightnessXYPN = block.getMixedBrightnessForBlock(this.blockAccess, x, y - 1, z);
            this.aoBrightnessXZPN = block.getMixedBrightnessForBlock(this.blockAccess, x, y, z - 1);
            this.aoBrightnessXZPP = block.getMixedBrightnessForBlock(this.blockAccess, x, y, z + 1);
            this.aoBrightnessXYPP = block.getMixedBrightnessForBlock(this.blockAccess, x, y + 1, z);
            flag2 = this.blockAccess.getBlock(x + 1, y + 1, z).getCanBlockGrass();
            flag3 = this.blockAccess.getBlock(x + 1, y - 1, z).getCanBlockGrass();
            flag4 = this.blockAccess.getBlock(x + 1, y, z + 1).getCanBlockGrass();
            flag5 = this.blockAccess.getBlock(x + 1, y, z - 1).getCanBlockGrass();

            if (!flag3 && !flag5)
            {
                this.aoLightValueScratchXYZPNN = this.aoLightValueScratchXZPN;
                this.aoBrightnessXYZPNN = this.aoBrightnessXZPN;
            }
            else
            {
                this.aoLightValueScratchXYZPNN = this.blockAccess.getBlock(x, y - 1, z - 1).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZPNN = block.getMixedBrightnessForBlock(this.blockAccess, x, y - 1, z - 1);
            }

            if (!flag3 && !flag4)
            {
                this.aoLightValueScratchXYZPNP = this.aoLightValueScratchXZPP;
                this.aoBrightnessXYZPNP = this.aoBrightnessXZPP;
            }
            else
            {
                this.aoLightValueScratchXYZPNP = this.blockAccess.getBlock(x, y - 1, z + 1).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZPNP = block.getMixedBrightnessForBlock(this.blockAccess, x, y - 1, z + 1);
            }

            if (!flag2 && !flag5)
            {
                this.aoLightValueScratchXYZPPN = this.aoLightValueScratchXZPN;
                this.aoBrightnessXYZPPN = this.aoBrightnessXZPN;
            }
            else
            {
                this.aoLightValueScratchXYZPPN = this.blockAccess.getBlock(x, y + 1, z - 1).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZPPN = block.getMixedBrightnessForBlock(this.blockAccess, x, y + 1, z - 1);
            }

            if (!flag2 && !flag4)
            {
                this.aoLightValueScratchXYZPPP = this.aoLightValueScratchXZPP;
                this.aoBrightnessXYZPPP = this.aoBrightnessXZPP;
            }
            else
            {
                this.aoLightValueScratchXYZPPP = this.blockAccess.getBlock(x, y + 1, z + 1).getAmbientOcclusionLightValue();
                this.aoBrightnessXYZPPP = block.getMixedBrightnessForBlock(this.blockAccess, x, y + 1, z + 1);
            }

            if (this.renderMaxX >= 1.0D)
            {
                --x;
            }

            i1 = l;

            if (this.renderMaxX >= 1.0D || !this.blockAccess.getBlock(x + 1, y, z).isOpaqueCube())
            {
                i1 = block.getMixedBrightnessForBlock(this.blockAccess, x + 1, y, z);
            }

            f7 = this.blockAccess.getBlock(x + 1, y, z).getAmbientOcclusionLightValue();
            f8 = (this.aoLightValueScratchXYPN + this.aoLightValueScratchXYZPNP + f7 + this.aoLightValueScratchXZPP) / 4.0F;
            f9 = (this.aoLightValueScratchXYZPNN + this.aoLightValueScratchXYPN + this.aoLightValueScratchXZPN + f7) / 4.0F;
            f10 = (this.aoLightValueScratchXZPN + f7 + this.aoLightValueScratchXYZPPN + this.aoLightValueScratchXYPP) / 4.0F;
            f11 = (f7 + this.aoLightValueScratchXZPP + this.aoLightValueScratchXYPP + this.aoLightValueScratchXYZPPP) / 4.0F;
            f3 = (float)((double)f8 * (1.0D - this.renderMinY) * this.renderMaxZ + (double)f9 * (1.0D - this.renderMinY) * (1.0D - this.renderMaxZ) + (double)f10 * this.renderMinY * (1.0D - this.renderMaxZ) + (double)f11 * this.renderMinY * this.renderMaxZ);
            f4 = (float)((double)f8 * (1.0D - this.renderMinY) * this.renderMinZ + (double)f9 * (1.0D - this.renderMinY) * (1.0D - this.renderMinZ) + (double)f10 * this.renderMinY * (1.0D - this.renderMinZ) + (double)f11 * this.renderMinY * this.renderMinZ);
            f5 = (float)((double)f8 * (1.0D - this.renderMaxY) * this.renderMinZ + (double)f9 * (1.0D - this.renderMaxY) * (1.0D - this.renderMinZ) + (double)f10 * this.renderMaxY * (1.0D - this.renderMinZ) + (double)f11 * this.renderMaxY * this.renderMinZ);
            f6 = (float)((double)f8 * (1.0D - this.renderMaxY) * this.renderMaxZ + (double)f9 * (1.0D - this.renderMaxY) * (1.0D - this.renderMaxZ) + (double)f10 * this.renderMaxY * (1.0D - this.renderMaxZ) + (double)f11 * this.renderMaxY * this.renderMaxZ);
            j1 = this.getAoBrightness(this.aoBrightnessXYPN, this.aoBrightnessXYZPNP, this.aoBrightnessXZPP, i1);
            k1 = this.getAoBrightness(this.aoBrightnessXZPP, this.aoBrightnessXYPP, this.aoBrightnessXYZPPP, i1);
            l1 = this.getAoBrightness(this.aoBrightnessXZPN, this.aoBrightnessXYZPPN, this.aoBrightnessXYPP, i1);
            i2 = this.getAoBrightness(this.aoBrightnessXYZPNN, this.aoBrightnessXYPN, this.aoBrightnessXZPN, i1);
            this.brightnessTopLeft = this.mixAoBrightness(j1, i2, l1, k1, (1.0D - this.renderMinY) * this.renderMaxZ, (1.0D - this.renderMinY) * (1.0D - this.renderMaxZ), this.renderMinY * (1.0D - this.renderMaxZ), this.renderMinY * this.renderMaxZ);
            this.brightnessBottomLeft = this.mixAoBrightness(j1, i2, l1, k1, (1.0D - this.renderMinY) * this.renderMinZ, (1.0D - this.renderMinY) * (1.0D - this.renderMinZ), this.renderMinY * (1.0D - this.renderMinZ), this.renderMinY * this.renderMinZ);
            this.brightnessBottomRight = this.mixAoBrightness(j1, i2, l1, k1, (1.0D - this.renderMaxY) * this.renderMinZ, (1.0D - this.renderMaxY) * (1.0D - this.renderMinZ), this.renderMaxY * (1.0D - this.renderMinZ), this.renderMaxY * this.renderMinZ);
            this.brightnessTopRight = this.mixAoBrightness(j1, i2, l1, k1, (1.0D - this.renderMaxY) * this.renderMaxZ, (1.0D - this.renderMaxY) * (1.0D - this.renderMaxZ), this.renderMaxY * (1.0D - this.renderMaxZ), this.renderMaxY * this.renderMaxZ);

            if (flag1)
            {
                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = p_147808_5_ * 0.6F;
                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = p_147808_6_ * 0.6F;
                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = p_147808_7_ * 0.6F;
            }
            else
            {
                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = 0.6F;
                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = 0.6F;
                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = 0.6F;
            }

            this.colorRedTopLeft *= f3;
            this.colorGreenTopLeft *= f3;
            this.colorBlueTopLeft *= f3;
            this.colorRedBottomLeft *= f4;
            this.colorGreenBottomLeft *= f4;
            this.colorBlueBottomLeft *= f4;
            this.colorRedBottomRight *= f5;
            this.colorGreenBottomRight *= f5;
            this.colorBlueBottomRight *= f5;
            this.colorRedTopRight *= f6;
            this.colorGreenTopRight *= f6;
            this.colorBlueTopRight *= f6;
            iicon = this.getBlockIcon(block, this.blockAccess, x, y, z, 5);
            this.renderFaceXPos(block, (double)x, (double)y, (double)z, iicon);

            if (fancyGrass && iicon.getIconName().equals("grass_side") && !this.hasOverrideBlockTexture())
            {
                this.colorRedTopLeft *= p_147808_5_;
                this.colorRedBottomLeft *= p_147808_5_;
                this.colorRedBottomRight *= p_147808_5_;
                this.colorRedTopRight *= p_147808_5_;
                this.colorGreenTopLeft *= p_147808_6_;
                this.colorGreenBottomLeft *= p_147808_6_;
                this.colorGreenBottomRight *= p_147808_6_;
                this.colorGreenTopRight *= p_147808_6_;
                this.colorBlueTopLeft *= p_147808_7_;
                this.colorBlueBottomLeft *= p_147808_7_;
                this.colorBlueBottomRight *= p_147808_7_;
                this.colorBlueTopRight *= p_147808_7_;
                this.renderFaceXPos(block, (double) x, (double) y, (double) z, BlockGrass.getIconSideOverlay());
            }

            flag = true;
        }

        this.enableAO = false;
        return flag;
    }

    @Override
    public boolean renderStandardBlockWithColorMultiplier(Block block, int p_147736_2_, int p_147736_3_, int p_147736_4_, float p_147736_5_, float p_147736_6_, float p_147736_7_)
    {
        this.enableAO = false;
        Tessellator tessellator = Tessellator.instance;
        boolean flag = false;
        float f3 = 0.5F;
        float f4 = 1.0F;
        float f5 = 0.8F;
        float f6 = 0.6F;
        float f7 = f4 * p_147736_5_;
        float f8 = f4 * p_147736_6_;
        float f9 = f4 * p_147736_7_;
        float f10 = f3;
        float f11 = f5;
        float f12 = f6;
        float f13 = f3;
        float f14 = f5;
        float f15 = f6;
        float f16 = f3;
        float f17 = f5;
        float f18 = f6;

        if (block != Blocks.grass)
        {
            f10 = f3 * p_147736_5_;
            f11 = f5 * p_147736_5_;
            f12 = f6 * p_147736_5_;
            f13 = f3 * p_147736_6_;
            f14 = f5 * p_147736_6_;
            f15 = f6 * p_147736_6_;
            f16 = f3 * p_147736_7_;
            f17 = f5 * p_147736_7_;
            f18 = f6 * p_147736_7_;
        }

        int l = block.getMixedBrightnessForBlock(this.blockAccess, p_147736_2_, p_147736_3_, p_147736_4_);

        if (this.renderAllFaces || (side == 0 && block.shouldSideBeRendered(this.blockAccess, p_147736_2_, p_147736_3_ - 1, p_147736_4_, 0)))
        {
            tessellator.setBrightness(this.renderMinY > 0.0D ? l : block.getMixedBrightnessForBlock(this.blockAccess, p_147736_2_, p_147736_3_ - 1, p_147736_4_));
            tessellator.setColorOpaque_F(f10, f13, f16);
            this.renderFaceYNeg(block, (double)p_147736_2_, (double)p_147736_3_, (double)p_147736_4_, this.getBlockIcon(block, this.blockAccess, p_147736_2_, p_147736_3_, p_147736_4_, 0));
            flag = true;
        }

        if (this.renderAllFaces || (side == 1 && block.shouldSideBeRendered(this.blockAccess, p_147736_2_, p_147736_3_ + 1, p_147736_4_, 1)))
        {
            tessellator.setBrightness(this.renderMaxY < 1.0D ? l : block.getMixedBrightnessForBlock(this.blockAccess, p_147736_2_, p_147736_3_ + 1, p_147736_4_));
            tessellator.setColorOpaque_F(f7, f8, f9);
            this.renderFaceYPos(block, (double)p_147736_2_, (double)p_147736_3_, (double)p_147736_4_, this.getBlockIcon(block, this.blockAccess, p_147736_2_, p_147736_3_, p_147736_4_, 1));
            flag = true;
        }

        IIcon iicon;

        if (this.renderAllFaces || (side == 2 && block.shouldSideBeRendered(this.blockAccess, p_147736_2_, p_147736_3_, p_147736_4_ - 1, 2)))
        {
            tessellator.setBrightness(this.renderMinZ > 0.0D ? l : block.getMixedBrightnessForBlock(this.blockAccess, p_147736_2_, p_147736_3_, p_147736_4_ - 1));
            tessellator.setColorOpaque_F(f11, f14, f17);
            iicon = this.getBlockIcon(block, this.blockAccess, p_147736_2_, p_147736_3_, p_147736_4_, 2);
            this.renderFaceZNeg(block, (double)p_147736_2_, (double)p_147736_3_, (double)p_147736_4_, iicon);

            if (fancyGrass && iicon.getIconName().equals("grass_side") && !this.hasOverrideBlockTexture())
            {
                tessellator.setColorOpaque_F(f11 * p_147736_5_, f14 * p_147736_6_, f17 * p_147736_7_);
                this.renderFaceZNeg(block, (double)p_147736_2_, (double)p_147736_3_, (double)p_147736_4_, BlockGrass.getIconSideOverlay());
            }

            flag = true;
        }

        if (this.renderAllFaces || (side == 3 && block.shouldSideBeRendered(this.blockAccess, p_147736_2_, p_147736_3_, p_147736_4_ + 1, 3)))
        {
            tessellator.setBrightness(this.renderMaxZ < 1.0D ? l : block.getMixedBrightnessForBlock(this.blockAccess, p_147736_2_, p_147736_3_, p_147736_4_ + 1));
            tessellator.setColorOpaque_F(f11, f14, f17);
            iicon = this.getBlockIcon(block, this.blockAccess, p_147736_2_, p_147736_3_, p_147736_4_, 3);
            this.renderFaceZPos(block, (double)p_147736_2_, (double)p_147736_3_, (double)p_147736_4_, iicon);

            if (fancyGrass && iicon.getIconName().equals("grass_side") && !this.hasOverrideBlockTexture())
            {
                tessellator.setColorOpaque_F(f11 * p_147736_5_, f14 * p_147736_6_, f17 * p_147736_7_);
                this.renderFaceZPos(block, (double)p_147736_2_, (double)p_147736_3_, (double)p_147736_4_, BlockGrass.getIconSideOverlay());
            }

            flag = true;
        }

        if (this.renderAllFaces || (side == 4 && block.shouldSideBeRendered(this.blockAccess, p_147736_2_ - 1, p_147736_3_, p_147736_4_, 4)))
        {
            tessellator.setBrightness(this.renderMinX > 0.0D ? l : block.getMixedBrightnessForBlock(this.blockAccess, p_147736_2_ - 1, p_147736_3_, p_147736_4_));
            tessellator.setColorOpaque_F(f12, f15, f18);
            iicon = this.getBlockIcon(block, this.blockAccess, p_147736_2_, p_147736_3_, p_147736_4_, 4);
            this.renderFaceXNeg(block, (double)p_147736_2_, (double)p_147736_3_, (double)p_147736_4_, iicon);

            if (fancyGrass && iicon.getIconName().equals("grass_side") && !this.hasOverrideBlockTexture())
            {
                tessellator.setColorOpaque_F(f12 * p_147736_5_, f15 * p_147736_6_, f18 * p_147736_7_);
                this.renderFaceXNeg(block, (double)p_147736_2_, (double)p_147736_3_, (double)p_147736_4_, BlockGrass.getIconSideOverlay());
            }

            flag = true;
        }

        if (this.renderAllFaces || (side == 5 && block.shouldSideBeRendered(this.blockAccess, p_147736_2_ + 1, p_147736_3_, p_147736_4_, 5)))
        {
            tessellator.setBrightness(this.renderMaxX < 1.0D ? l : block.getMixedBrightnessForBlock(this.blockAccess, p_147736_2_ + 1, p_147736_3_, p_147736_4_));
            tessellator.setColorOpaque_F(f12, f15, f18);
            iicon = this.getBlockIcon(block, this.blockAccess, p_147736_2_, p_147736_3_, p_147736_4_, 5);
            this.renderFaceXPos(block, (double)p_147736_2_, (double)p_147736_3_, (double)p_147736_4_, iicon);

            if (fancyGrass && iicon.getIconName().equals("grass_side") && !this.hasOverrideBlockTexture())
            {
                tessellator.setColorOpaque_F(f12 * p_147736_5_, f15 * p_147736_6_, f18 * p_147736_7_);
                this.renderFaceXPos(block, (double)p_147736_2_, (double)p_147736_3_, (double)p_147736_4_, BlockGrass.getIconSideOverlay());
            }

            flag = true;
        }

        return flag;
    }


    public boolean renderBlockCactusImpl(Block block, int p_147754_2_, int p_147754_3_, int p_147754_4_, float p_147754_5_, float p_147754_6_, float p_147754_7_)
    {
        Tessellator tessellator = Tessellator.instance;
        boolean flag = false;
        float f3 = 0.5F;
        float f4 = 1.0F;
        float f5 = 0.8F;
        float f6 = 0.6F;
        float f7 = f3 * p_147754_5_;
        float f8 = f4 * p_147754_5_;
        float f9 = f5 * p_147754_5_;
        float f10 = f6 * p_147754_5_;
        float f11 = f3 * p_147754_6_;
        float f12 = f4 * p_147754_6_;
        float f13 = f5 * p_147754_6_;
        float f14 = f6 * p_147754_6_;
        float f15 = f3 * p_147754_7_;
        float f16 = f4 * p_147754_7_;
        float f17 = f5 * p_147754_7_;
        float f18 = f6 * p_147754_7_;
        float f19 = 0.0625F;
        int l = block.getMixedBrightnessForBlock(this.blockAccess, p_147754_2_, p_147754_3_, p_147754_4_);

        if (this.renderAllFaces || (side == 0 && block.shouldSideBeRendered(this.blockAccess, p_147754_2_, p_147754_3_ - 1, p_147754_4_, 0)))
        {
            tessellator.setBrightness(this.renderMinY > 0.0D ? l : block.getMixedBrightnessForBlock(this.blockAccess, p_147754_2_, p_147754_3_ - 1, p_147754_4_));
            tessellator.setColorOpaque_F(f7, f11, f15);
            this.renderFaceYNeg(block, (double)p_147754_2_, (double)p_147754_3_, (double)p_147754_4_, this.getBlockIcon(block, this.blockAccess, p_147754_2_, p_147754_3_, p_147754_4_, 0));
        }

        if (this.renderAllFaces || (side == 1 && block.shouldSideBeRendered(this.blockAccess, p_147754_2_, p_147754_3_ + 1, p_147754_4_, 1)))
        {
            tessellator.setBrightness(this.renderMaxY < 1.0D ? l : block.getMixedBrightnessForBlock(this.blockAccess, p_147754_2_, p_147754_3_ + 1, p_147754_4_));
            tessellator.setColorOpaque_F(f8, f12, f16);
            this.renderFaceYPos(block, (double)p_147754_2_, (double)p_147754_3_, (double)p_147754_4_, this.getBlockIcon(block, this.blockAccess, p_147754_2_, p_147754_3_, p_147754_4_, 1));
        }

        tessellator.setBrightness(l);
        tessellator.setColorOpaque_F(f9, f13, f17);
        tessellator.addTranslation(0.0F, 0.0F, f19);
        this.renderFaceZNeg(block, (double)p_147754_2_, (double)p_147754_3_, (double)p_147754_4_, this.getBlockIcon(block, this.blockAccess, p_147754_2_, p_147754_3_, p_147754_4_, 2));
        tessellator.addTranslation(0.0F, 0.0F, -f19);
        tessellator.addTranslation(0.0F, 0.0F, -f19);
        this.renderFaceZPos(block, (double)p_147754_2_, (double)p_147754_3_, (double)p_147754_4_, this.getBlockIcon(block, this.blockAccess, p_147754_2_, p_147754_3_, p_147754_4_, 3));
        tessellator.addTranslation(0.0F, 0.0F, f19);
        tessellator.setColorOpaque_F(f10, f14, f18);
        tessellator.addTranslation(f19, 0.0F, 0.0F);
        this.renderFaceXNeg(block, (double)p_147754_2_, (double)p_147754_3_, (double)p_147754_4_, this.getBlockIcon(block, this.blockAccess, p_147754_2_, p_147754_3_, p_147754_4_, 4));
        tessellator.addTranslation(-f19, 0.0F, 0.0F);
        tessellator.addTranslation(-f19, 0.0F, 0.0F);
        this.renderFaceXPos(block, (double)p_147754_2_, (double)p_147754_3_, (double)p_147754_4_, this.getBlockIcon(block, this.blockAccess, p_147754_2_, p_147754_3_, p_147754_4_, 5));
        tessellator.addTranslation(f19, 0.0F, 0.0F);
        return true;
    }
}
