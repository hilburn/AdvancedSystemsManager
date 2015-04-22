package advancedsystemsmanager.util;

import cpw.mods.fml.common.FMLCommonHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogHelper
{
    private Logger log;

    public LogHelper(String id)
    {
        log = LogManager.getLogger(id);
    }

    public void debug(Object obj)
    {
        log.debug(obj);
    }

    public void info(Object obj)
    {
        log.info(obj);
    }

    public void warn(Object obj)
    {
        log.warn(obj);
    }

    public void crash(Exception e, String message)
    {
        FMLCommonHandler.instance().raiseException(e, message, true);
    }

    public void error(Exception e, String message)
    {
        FMLCommonHandler.instance().raiseException(e, message, false);
    }
}
