package cz.cvut.fel.pjv.logic;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cvut.fel.pjv.GameData;
import java.io.*;
import java.util.logging.Logger;

/**
 * Util class imported to Controller and Load classes to save and load game classes
 */
public class SaveAndLoad {

    /**
     * Used for logging purposes
     */
    private static final Logger logger = Logger.getLogger(SaveAndLoad.class.getName());

    /**
     * Constructor prohibited
     */
    public SaveAndLoad() {}

    /**
     * Saves all objects into a single JSON file
     * Used after checkFilename method, if returned True
     * @param gameData class to be saved
     * @param filename given name of the file
     */
    public static void save(GameData gameData, String filename) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(new File(System.getProperty("user.dir") +
                    "/saved_games/" + filename + ".json"), gameData);
            logger.info("Game saved successfully to: " + filename + ".json");
        } catch (IOException e) {
            logger.severe("Could not save game. Exception: " + e.getMessage());
        }
    }

    /**
     * Loads all objects from a single JSON file
     * Used after clicking the Load button in the Load screen
     * @param filename given name of the file
     * @return class to be loaded
     */
    public static GameData load(String filename) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            GameData gameData = mapper.readValue(new File(System.getProperty("user.dir") +
                    "/saved_games/" + filename), GameData.class);
            logger.info("Game loaded successfully from: " + filename);
            return gameData;
        } catch (IOException e) {
            logger.severe("Could not load game. Exception: " + e.getMessage());
            return null;
        }
    }

    /**
     * Deletes a JSON file with all saved objects
     * Used after clicking the Delete button in the Load screen
     * @param filename given name of the file
     * @return True if file deleted successfully, false otherwise
     */
    public static boolean delete(String filename) {
        File selectedFile = new File(System.getProperty("user.dir") + "/saved_games/" + filename);
        if (!selectedFile.delete()) {
            logger.severe("Failed to delete file " + filename);
            return false;
        }
        logger.info("File deleted successfully: " + filename);
        return true;
    }

}
