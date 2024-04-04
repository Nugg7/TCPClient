package com.example.clienttcp;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.UUID;

import java.io.FileWriter;

import org.json.simple.parser.JSONParser;


public class JSON4msg {
    static JSONObject doc;
    JSONParser jsonParser = new JSONParser();

    private String username;
    private UUID uuid;

    public JSON4msg(){
        try (FileReader reader = new FileReader("Message.json"))
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
        try (FileWriter file = new FileWriter("Message.json")) {
            file.write(doc.toJSONString());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public JSONObject getProfile() {
        return doc;
    }

    public String profileToString() {
        String username = (String) doc.get("username");
        UUID uuid = UUID.fromString((String)doc.get("UUID"));
        String result = "\nUsername: " + username + "\nUUID: " + uuid + "\n";
        return result;
    }

    public void setMessage(String message) {
        doc.remove("message");
        doc.put("message", message);
        write();
    }

    public void resetMessage() {
        doc.remove("message");
        doc.put("message", "");
        write();
    }
    public JSONObject parse(String s) throws ParseException {
        JSONObject json = (JSONObject) jsonParser.parse(s);
        return json;
    }
}
