package advancedsystemsmanager.api.tiletypes;

public interface IRedstoneListener
{
    boolean canConnectRedstone(int side);

    int getComparatorInputOverride(int side);

    int isProvidingWeakPower(int side);

    int isProvidingStrongPower(int side);

    boolean shouldCheckWeakPower(int side);
}
