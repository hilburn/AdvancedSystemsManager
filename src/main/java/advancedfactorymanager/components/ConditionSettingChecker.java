package advancedfactorymanager.components;


public class ConditionSettingChecker
{
    public Setting setting;
    public int amount;

    public ConditionSettingChecker(Setting setting)
    {
        this.setting = setting;
        amount = 0;
    }

    public void addCount(int n)
    {
        amount += n;
    }

    public boolean isTrue()
    {
        return !setting.isLimitedByAmount() || amount >= setting.getAmount();
    }
}
