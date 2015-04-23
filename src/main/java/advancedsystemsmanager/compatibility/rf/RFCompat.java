package advancedsystemsmanager.compatibility.rf;

import advancedsystemsmanager.compatibility.CompatBase;
import advancedsystemsmanager.flow.menus.*;
import advancedsystemsmanager.helpers.StevesEnum;
import advancedsystemsmanager.registry.ConnectionSet;

public class RFCompat extends CompatBase
{
    @Override
    protected void init()
    {
        StevesEnum.RF_INPUT = StevesEnum.addComponentType("RF_INPUT", 17, StevesEnum.RF_INPUT_SHORT, StevesEnum.RF_INPUT_LONG, new ConnectionSet[]{ConnectionSet.STANDARD}, MenuRFInput.class, MenuTargetRF.class, MenuResult.class);
        StevesEnum.RF_OUTPUT = StevesEnum.addComponentType("RF_OUTPUT", 18, StevesEnum.RF_OUTPUT_SHORT, StevesEnum.RF_OUTPUT_LONG, new ConnectionSet[]{ConnectionSet.STANDARD}, MenuRFOutput.class, MenuTargetRF.class, MenuResult.class);
        StevesEnum.RF_CONDITION = StevesEnum.addComponentType("RF_CONDITION", 19, StevesEnum.RF_CONDITION_SHORT, StevesEnum.RF_CONDITION_LONG, new ConnectionSet[]{ConnectionSet.STANDARD_CONDITION}, MenuRFStorage.class, MenuTargetRF.class, MenuRFCondition.class, MenuResult.class);
    }
}
