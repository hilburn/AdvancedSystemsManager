package hilburnlib.registry;

public interface IConfigLock
{
    boolean shouldRegister(String string, boolean defaultValue);
}
