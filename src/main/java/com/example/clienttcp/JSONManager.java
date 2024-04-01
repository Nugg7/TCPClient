package com.example.clienttcp;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.UUID;

import java.io.FileWriter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


public class JSONManager {
    static JSONObject doc;
    JSONParser jsonParser = new JSONParser();

    private String username;
    private UUID uuid;

    public JSONManager(){
        try (FileReader reader = new FileReader("src/Message.json"))
        {
            //Read JSON file
            Object obj = jsonParser.parse(reader);

            doc = (JSONObject) obj;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void setProfile(String username, UUID uuid){
        this.username = username;
        this.uuid = uuid;
        doc.remove("username");
        doc.remove("UUID");
        doc.put("username", username);
        doc.put("UUID", uuid.toString());
        write();
    }

    private void write(){
        try (FileWriter file = new FileWriter("src/Message.json")) {
            file.write(doc.toJSONString());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public JSONObject getProfile() {
        return doc;
    }
}
