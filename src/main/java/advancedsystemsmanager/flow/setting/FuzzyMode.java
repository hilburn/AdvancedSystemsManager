package advancedsystemsmanager.flow.setting;

import advancedsystemsmanager.reference.Names;

public enum FuzzyMode
{
    PRECISE(Names.DETECTION_PRECISE, true),
    NBT_FUZZY(Names.DETECTION_NBT_FUZZY, true),
    FUZZY(Names.DETECTION_FUZZY, false),
    ORE_DICTIONARY(Names.DETECTION_ORE_DICTIONARY, true),
    MOD_GROUPING(Names.MOD_GROUPING, false),
    ALL(Names.ALL_ITEMS, false);

    public String text;
    public boolean useMeta;

    FuzzyMode(String text, boolean useMeta)
    {
        this.text = text;
        this.useMeta = useMeta;
    }

    @Override
    public String toString()
    {
        return text;
    }


    public boolean requiresMetaData()
    {
        return useMeta;
    }
}
