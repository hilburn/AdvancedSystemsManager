package advancedsystemsmanager.client.gui.fonts;

import static org.lwjgl.opengl.GL11.*;

public class GLFontRenderer
{
    public float zLevel;
    private GLFont font;

    public int getWidth(String string)
    {
        int width = 0;
        for (char currentChar : string.toCharArray())
        {
            width += getCharWidth(currentChar);
        }
        return width;
    }

    public int getCharWidth(char c)
    {
        return font.getMetric().getGlyphMetric(c).width;
    }

    /**
     * Trims a string to a specified width, and will reverse it if par3 is set.
     */
    public String trimStringToWidth(String string, int newLength, boolean reverse)
    {
        StringBuilder stringbuilder = new StringBuilder();
        int width = 0;
        int k = reverse ? string.length() - 1 : 0;
        int l = reverse ? -1 : 1;
        boolean flag1 = false;
        boolean flag2 = false;

        for (int i1 = k; i1 >= 0 && i1 < string.length() && width < newLength; i1 += l)
        {
            char thisChar = string.charAt(i1);
            int thisWidth = getCharWidth(thisChar);

            if (flag1)
            {
                flag1 = false;

                if (thisChar != 108 && thisChar != 76)
                {
                    if (thisChar == 114 || thisChar == 82)
                    {
                        flag2 = false;
                    }
                } else
                {
                    flag2 = true;
                }
            } else if (thisWidth < 0)
            {
                flag1 = true;
            } else
            {
                width += thisWidth;

                if (flag2)
                {
                    ++width;
                }
            }

            if (width > newLength)
            {
                break;
            }

            if (reverse)
            {
                stringbuilder.insert(0, thisChar);
            } else
            {
                stringbuilder.append(thisChar);
            }
        }

        return stringbuilder.toString();
    }

    public void drawString(float x, float y, String string, int colour)
    {
        float red = (float)(colour >> 16 & 255) / 255.0F;
        float blue = (float)(colour >> 8 & 255) / 255.0F;
        float green = (float)(colour & 255) / 255.0F;
        glColor3f(red, blue, green);
        glBindTexture(GL_TEXTURE_2D, font.getTextureId());

        glBegin(GL_QUADS);

        for (char c : string.toCharArray())
        {
            GLGlyphMetric metric = font.getMetric().getGlyphMetric(c);

            if (metric != null)
            {
                renderChar(x, y, metric);
                x += metric.width;
            }
        }

        glEnd();
    }

    private void renderChar(float x, float y, GLGlyphMetric metric)
    {
        drawQuad(x, y, x + metric.width, y + metric.height, metric.ux / font.getTextureSize(), metric.vy / font.getTextureSize(), (metric.ux + metric.width) / font.getTextureSize(), (metric.vy + metric.height) / font.getTextureSize());
    }

    private void drawQuad(float drawX, float drawY, float drawX2, float drawY2, float srcX, float srcY, float srcX2, float srcY2)
    {
        glTexCoord2f(srcX, srcY);
        glVertex3f(drawX, drawY, zLevel);
        glTexCoord2f(srcX, srcY2);
        glVertex3f(drawX, drawY2, zLevel);
        glTexCoord2f(srcX2, srcY2);
        glVertex3f(drawX2, drawY2, zLevel);
        glTexCoord2f(srcX2, srcY);
        glVertex3f(drawX2, drawY, zLevel);
    }
}
