package de.unisaarland.cs.se.sopra.config;

import org.json.JSONArray;
import org.json.JSONObject;

public interface ParamMap {

    int getInt(String key);

    String getString(String key);

    boolean getBoolean(String key);

    boolean getBoolean(String key, boolean defaultValue);

    boolean hasLocation(String key);

    boolean hasKids(String key);

    boolean hasConsequence(String key);

    boolean hasNotConsequence(String key);

    void removeKey(String key);

    JSONObject getJSONObject(String key);

    JSONArray getJSONArray(String key);

    boolean hasJSONObject(String key);


}
