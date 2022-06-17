package net.timenation.velocitysystem.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConfigManager {

    private final File file;
    private final Gson gson;
    private final ExecutorService pool;
    private JsonObject json;

    public ConfigManager() {
        this.file = new File("plugins/VelocitySystem/config.json");
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.pool = Executors.newFixedThreadPool(2);
        this.initFile();
    }

    private void initFile() {
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        if (!file.exists()) {
            try (PrintWriter writer = new PrintWriter(file)) {
                writer.print(gson.toJson(json = new JsonObject()));
                // initProperties();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            try {
                json = new JsonParser().parse(new FileReader(file)).getAsJsonObject();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void initProperties() {
        json.addProperty("prefix", "Test | ");
        json.addProperty("maintenance", true);
        json.addProperty("maintenance_protocol", "§4Wartungsarbeiten");
        json.addProperty("maintenance_message", "§f§lDeinServer.net §8| §7Minecraft Netzwerk \n \n §8» §cDas Netzwerk befindet sich derzeit in Wartungsarbeiten§8.");
        json.addProperty("motd.line1", "§f§lDeinServer.net §8| §7Minecraft Netzwerk");
        json.addProperty("motd.line2", "  §8» §7Editiere die MOTD in der config.json§8!");
        json.addProperty("motd.maintenance_line1", "§f§lDeinServer.net §8| §7Minecraft Netzwerk");
        json.addProperty("motd.maintenance_line2", "  §8» §7Editiere die MOTD in der config.json§8!");
        save();
    }

    public void save() {
        pool.execute(() -> {
            try (PrintWriter writer = new PrintWriter(file)) {
                writer.print(gson.toJson(json));
                writer.flush();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });
    }

    public String getString(String key) {
        return json.get(key).getAsString();
    }

    public String getString(String key, Object... arguments) {
        return new MessageFormat(json.get(key).getAsString()).format(arguments);
    }

    public boolean getBoolean(String key) {
        return json.get(key).getAsBoolean();
    }
}