package epita.projectse.data_access;

import java.io.FileInputStream;
import java.util.Properties;

public class ConfigurationAccess {
    public static boolean isConfigurationValid(){
        Properties prop = new Properties();
        String fileName = "app.config";
        try (FileInputStream fis = new FileInputStream(fileName)){
            prop.load(fis);
        } catch (Exception e) {
            System.err.println("Error loading configuration file: " + e.getMessage());
            System.exit(1);
        }
        String dbUrl = prop.getProperty("SERVER_ADDR");
        String dbPort = prop.getProperty("SERVER_PORT");
        System.out.println("Server URL: " + dbUrl);
        System.out.println("Server Port: " + dbPort);
        return (dbUrl != null && !dbUrl.isEmpty() && dbPort != null && !dbPort.isEmpty());
    }

    public static String getServerAddress(){
        Properties prop = new Properties();
        String fileName = "app.config";
        try (FileInputStream fis = new FileInputStream(fileName)){
            prop.load(fis);
        } catch (Exception e) {
            System.err.println("Error loading configuration file: " + e.getMessage());
            System.exit(1);
        }
        String dbUrl = prop.getProperty("SERVER_ADDR");
        return dbUrl != null ? dbUrl : "";
    }
    public static String getServerPort(){
        Properties prop = new Properties();
        String fileName = "app.config";
        try (FileInputStream fis = new FileInputStream(fileName)){
            prop.load(fis);
        } catch (Exception e) {
            System.err.println("Error loading configuration file: " + e.getMessage());
            System.exit(1);
        }
        String dbPort = prop.getProperty("SERVER_PORT");
        return dbPort != null ? dbPort : "";
    }
}
