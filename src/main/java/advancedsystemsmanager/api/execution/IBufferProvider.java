package advancedsystemsmanager.api.execution;

import java.util.Set;

public interface IBufferProvider
{
    Set<IBufferElement> getBuffer(Class<? extends IBufferElement> bufferClass);
}
