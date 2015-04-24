package advancedsystemsmanager.api.execution;

public interface IBufferProvider
{
    <T extends IBuffer> T getBuffer(String key);

    boolean containsBuffer(String key);

    void setBuffer(String key, IBuffer buffer);
}
