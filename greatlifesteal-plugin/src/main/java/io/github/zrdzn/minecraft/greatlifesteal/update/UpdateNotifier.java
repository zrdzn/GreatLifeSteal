package io.github.zrdzn.minecraft.greatlifesteal.update;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;

public class UpdateNotifier {

    private final Logger logger;

    public UpdateNotifier(Logger logger) {
        this.logger = logger;
    }


    /**
     * Checks if the plugin is using the latest version.
     *
     * @return true if plugin is using the latest version or an error occurred, false if it uses an older version
     */
    public boolean checkIfLatest(String targetVersion) {
        URL url;
        try {
            url = new URL("https://api.spigotmc.org/simple/0.1/index.php?action=getResource&id=102206");
        } catch (MalformedURLException exception) {
            this.logger.warn("Update notifier: Could not get the resource from spigotmc.");
            return true;
        }

        HttpURLConnection http;
        try {
            http = (HttpURLConnection) url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(http.getInputStream()));
            StringBuilder response = new StringBuilder();

            String readLine;
            while ((readLine = reader.readLine()) != null) {
                response.append(readLine);
            }
            reader.close();

            JSONParser parser = new JSONParser();

            String latestVersion;
            try {
                JSONObject json = (JSONObject) parser.parse(response.toString());
                latestVersion = (String) json.get("current_version");
            } catch (ParseException exception) {
                this.logger.warn("Update notifier: Could not parse the json string.");
                return true;
            }

            if (latestVersion == null) {
                this.logger.warn("Update notifier: Something went wrong while getting a version.");
                return true;
            }

            if (!latestVersion.equals(targetVersion)) {
                this.logger.warn("You are using an outdated version of the plugin!");
                this.logger.warn("You can download the latest one here:");
                this.logger.warn("https://www.spigotmc.org/resources/greatlifesteal.102206/");

                http.disconnect();

                return false;
            }

            return true;
        } catch (IOException exception) {
            this.logger.warn("Update notifier: Could not read from the json body.");
            return true;
        }
    }


}
