import java.io.*;
import java.util.Properties;

public class SettingsManager {
    private Properties properties;
    private File configFile;

    public SettingsManager(String fileName) {
        properties = new Properties();
        configFile = new File(fileName);

        if (configFile.exists()) {
            try (FileInputStream inputStream = new FileInputStream(configFile)) {
                properties.load(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setSetting(String key, String value) {
        properties.setProperty(key, value);
    }

    public String getSetting(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public void saveSettings() {
        try (FileOutputStream outputStream = new FileOutputStream(configFile)) {
            properties.store(outputStream, "User Settings");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
