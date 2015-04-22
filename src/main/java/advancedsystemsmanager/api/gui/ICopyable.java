package advancedsystemsmanager.api.gui;

public interface ICopyable
{
    boolean canPaste(ICopyable copyable);

    void paste(ICopyable copyable);
}
