package com.example.sampledata.views;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SampleDataViewTest {
    
    @Test
    void testJsonParsing_InvalidJson() {
        String invalidJson = "{invalid json}";
        assertThrows(org.json.JSONException.class, () -> {
            new org.json.JSONArray(invalidJson);
        });
    }
    
    @Test
    void testJsonParsing_MissingFields() {
        String jsonWithMissingFields = "[{\"id\":1,\"name\":\"Incomplete Issue\"}]";
        org.json.JSONArray arr = new org.json.JSONArray(jsonWithMissingFields);
        
        assertEquals(1, arr.length());
        org.json.JSONObject obj = arr.getJSONObject(0);
        assertEquals(1, obj.getInt("id"));
        assertEquals("Incomplete Issue", obj.getString("name"));
        
        assertThrows(org.json.JSONException.class, () -> obj.getString("severity"));
    }
}