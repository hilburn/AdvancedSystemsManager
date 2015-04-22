package advancedfactorymanager.api;

import com.google.gson.JsonObject;

public interface IJsonData
{
    JsonObject writeToJson();

    void readFromJson(JsonObject object);
}
