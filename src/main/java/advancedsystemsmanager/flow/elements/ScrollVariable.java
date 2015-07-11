package advancedsystemsmanager.flow.elements;

import advancedsystemsmanager.api.network.IPacketSync;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.menus.MenuContainer;
import advancedsystemsmanager.gui.GuiManager;
import advancedsystemsmanager.network.ASMPacket;
import advancedsystemsmanager.reference.Null;
import advancedsystemsmanager.tileentities.manager.TileEntityManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class ScrollVariable extends ScrollController<Variable> implements IPacketSync
{
    public int variable = -1;
    private FlowComponent command;
    private int id;

    public ScrollVariable(FlowComponent command, int x, int y, int rows, int width)
    {
        this(command);
        this.x = x;
        this.y = y;
        this.visibleRows = rows;
    }

    public ScrollVariable(FlowComponent command)
    {
        this.command = command;
        if (command != null)
            command.registerSyncable(this);
        textBox = new TextBoxLogic(Null.NULL_PACKET, Integer.MAX_VALUE, TEXT_BOX_SIZE_W - TEXT_BOX_TEXT_X * 2)
        {
            @Override
            public void onUpdate()
            {
                updateSearch();
            }
        };
        textBox.setTextAndCursor("");
        updateSearch();
    }

    @Override
    public void updateSearch()
    {
        if (hasSearchBox)
        {
            result = updateSearch(textBox.getText(), textBox.getText().isEmpty());
        } else
        {
            result = updateSearch("", false);
        }
        updateScrolling();
    }

    public TileEntityManager getManager()
    {
        return command.getManager();
    }

    @Override
    public List<Variable> updateSearch(String search, boolean all)
    {
        List<Variable> variables = new ArrayList<Variable>();
        if (all)
        {
            variables.addAll(getManager().getVariables());
        } else
        {
            Pattern pattern = Pattern.compile(search, Pattern.CASE_INSENSITIVE);
            for (Variable variable : getManager().getVariables())
            {
                if (pattern.matcher(variable.getNameFromColor()).find()) variables.add(variable);
            }
        }
        Collections.sort(variables);
        return variables;
    }

    @Override
    public void onClick(Variable variable, int mX, int mY, int button)
    {
        int newSelected = variable.colour;
        setSelected(newSelected == this.variable ? -1 : newSelected);
        sendUpdate();
    }

    @Override
    public void draw(GuiManager gui, Variable variable, int x, int y, boolean hover)
    {
        int srcInventoryX = this.variable == variable.colour ? 1 : 0;
        int srcInventoryY = hover ? 1 : 0;

        gui.drawTexture(x, y, MenuContainer.INVENTORY_SRC_X + srcInventoryX * MenuContainer.INVENTORY_SIZE, MenuContainer.INVENTORY_SRC_Y + srcInventoryY * MenuContainer.INVENTORY_SIZE, MenuContainer.INVENTORY_SIZE, MenuContainer.INVENTORY_SIZE);
        variable.draw(gui, x, y);
    }

    @Override
    public void drawMouseOver(GuiManager gui, Variable variable, int mX, int mY)
    {
        gui.drawMouseOver(variable.getDescription(gui), mX, mY);
    }

    public void setSelected(int val)
    {
        variable = val;
    }

    public void sendUpdate()
    {
        ASMPacket packet = command.getSyncPacket();
        packet.writeByte(id);
        packet.writeMedium(variable);
        packet.sendServerPacket();
    }

    @Override
    public void setId(int id)
    {
        this.id = id;
    }

    @Override
    public boolean readData(ASMPacket packet)
    {
        setSelected(packet.readUnsignedMedium());
        return false;
    }
}
