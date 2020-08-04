package Server;
import User.User;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;

public class UserJSONHandler {

    public static void saveUserToFile(User user) {

        boolean exists = false;
        new File("users").mkdirs();

        File userFile = new File("users/user.json");
        exists = userFile.exists();

        try {
            PrintWriter pw = new PrintWriter(new FileWriter(userFile, true));
            JSONObject json = new JSONObject();

                json.put("username", user.getFullName());
                json.put("email", user.getEmail());
                json.put("password", user.getPassword());
                json.put("id", user.getId());

            if (!exists) {
                JSONArray userArray = new JSONArray();
                userArray.add(json);
                pw.append(userArray.toString());
            } else {
                JSONParser parser = new JSONParser();
                Object o = parser.parse(new FileReader("users/user.json"));
                JSONArray ja = (JSONArray) o;
                System.out.println("Found array: " + ja.toString());

                pw.close();
                pw = new PrintWriter(new FileWriter(userFile, false));

                ja.add(json);
                pw.append(ja.toString());
            }


            System.out.println("Created user: " + json.toJSONString());

            pw.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static boolean userExists(String username, String password) {

        JSONParser parser = new JSONParser();
        Object o = null;
        try {
            o = parser.parse(new FileReader("users/user.json"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        JSONArray ja = (JSONArray) o;

        for (int i = 0; i < ja.size(); i++) {
            JSONObject user = (JSONObject)ja.get(i);
            if (user.get("username").equals(username) && user.get("password").equals(password)) {
                return true;
            }
        }
        return false;
    }

    public static boolean fileExists(String filename) {
        File file = new File(filename);
        return file.exists();
    }

    public static User getUserFromFile(String fullUsername) {

        if (!fileExists("users/user.json")) {
            return null;
        }
        JSONParser parser = new JSONParser();
        Object o = null;
        try {
            o = parser.parse(new FileReader("users/user.json"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        JSONArray ja = (JSONArray) o;

        for (int i = 0; i < ja.size(); i++) {
            JSONObject user = (JSONObject)ja.get(i);
            if (user.get("username").equals(fullUsername)) {
                //return new User((String)user.get("username"), (String)user.get("password"), (int)user.get("id"));
                return new User((String)user.get("username"), (String)user.get("email"), (String)user.get("password"));
            }
        }

        return null;
    }

}
