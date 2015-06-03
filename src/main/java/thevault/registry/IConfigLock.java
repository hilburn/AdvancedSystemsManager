package thevault.registry;

public interface IConfigLock
{
    boolean shouldRegister(String string, boolean defaultValue);
}
