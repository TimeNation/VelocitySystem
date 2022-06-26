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
        json.addProperty("prefix", "§8» <gradient:#32a852:#84c295><bold>TimeNation</bold></gradient> §8- <gray>");
        json.addProperty("maintenance_protocol", "§4Only Administration, bitch");
        json.addProperty("maintenance_message", "<gradient:#32a852:#84c295><bold>TimeNation</bold></gradient> <#6dbd84>» <gray>Strategie & PvP Netzwerk \\n \\n <dark_gray>» <red>Das Netzwerk befindet sich derzeit in Wartungsarbeiten<dark_gray>.");
        json.addProperty("motd.line1", "<gradient:#32a852:#84c295><bold>TimeNation</bold></gradient> <#6dbd84>» <gray>Strategie & PvP Netzwerk <dark_gray>► <yellow>1.18.2");
        json.addProperty("motd.line2", "  <gradient:#3dcc63:#5eeb83><bold>Online</bold> <#733129>● <gradient:#32a852:#84c295><bold>TimeNation v2</bold></gradient> <dark_gray>● <gradient:#a14035:#a65e56><bold>OUT NOW</bold></gradient>");
        json.addProperty("motd.maintenance_line1", "<gradient:#32a852:#84c295><bold>TimeNation</bold></gradient> <#6dbd84>» <gray>Dev & Programmier Netzwerk <dark_gray>► <yellow>1.19");
        json.addProperty("motd.maintenance_line2", "  <gradient:#a14035:#a65e56><bold>SOON</bold></gradient> <#733129>● <gray>Wir kommen wieder<dark_gray>, <gray>nur <#3ff270>besser<dark_gray>!");
        json.addProperty("tablist.header", "\n   <gradient:#32a852:#84c295><bold>TimeNation</bold></gradient> <#6dbd84>» <gray>Strategie & PvP Netzwerk   \n <gray>Du bist mit <#67bf5a>{0} <gray>verbunden<dark_gray>. \n ");
        json.addProperty("tablist.footer_one", " \n <gray>Trete unserer <#8089ed>Discord <gray>Community bei<dark_gray> \n <#8089ed>dc.timenation.net \n \n <#ba0f2e>● <#b55566>● ●");
        json.addProperty("tablist.footer_two", " \n <gray>Unseren <#55bbfa>Twitter <gray>Account findest du unter \n <#55bbfa>twitter.com/@TimeNationNET \n \n <#b55566>● <#ba0f2e>● <#b55566>●");
        json.addProperty("tablist.footer_three", " \n <gray>Derzeit sind <#d1ce80>{0} <gray>von <#ed3b3b>{1} <gray>Spielern \n <gray>mit dem Netzwerk verbunden<dark_gray>. \n \n <#b55566>● ● <#ba0f2e>●");
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