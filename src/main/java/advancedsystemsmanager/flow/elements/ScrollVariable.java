package advancedsystemsmanager.flow.elements;

import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.gui.GuiManager;
import advancedsystemsmanager.reference.Null;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public abstract class ScrollVariable extends ScrollController<Variable>
{
    FlowComponent command;

    public ScrollVariable(FlowComponent command)
    {
        this.command = command;
        textBox = new TextBoxLogic(Null.NULL_PACKET, Integer.MAX_VALUE, TEXT_BOX_SIZE_W - TEXT_BOX_TEXT_X * 2)
        {
            @Override
            public void onUpdate()
            {
                if (getText().length() > 0)
                {
                    updateSearch();
                } else
                {
                    result.clear();
                    updateScrolling();
                }
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

    @Override
    public List<Variable> updateSearch(String search, boolean all)
    {
        List<Variable> variables = new ArrayList<Variable>();
        if (all)
        {
            variables.addAll(command.getManager().getVariables());
        } else
        {
            Pattern pattern = Pattern.compile(search, Pattern.CASE_INSENSITIVE);
            for (Variable variable : command.getManager().getVariables())
            {
                if (pattern.matcher(variable.getNameFromColor()).find()) variables.add(variable);
            }
        }
        Collections.sort(variables);
        return variables;
    }

    @Override
    public void drawMouseOver(GuiManager gui, Variable variable, int mX, int mY)
    {
        gui.drawMouseOver(variable.getDescription(gui), mX, mY);
    }
}
