package advancedsystemsmanager.nei;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;

public class NEIConfig implements IConfigureNEI
{
    @Override
    public void loadConfig()
    {
        API.registerRecipeHandler(new NEIQuantumRecipes());
    }

    @Override
    public String getName()
    {
        return "AdvancedSystemsManager NEI support";
    }

    @Override
    public String getVersion()
    {
        return "v1";
    }
}
