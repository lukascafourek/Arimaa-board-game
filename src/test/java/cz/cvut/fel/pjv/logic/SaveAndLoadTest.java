package cz.cvut.fel.pjv.logic;

import cz.cvut.fel.pjv.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test class for the SaveAndLoad save. load and delete file methods
 */
public class SaveAndLoadTest {

    @Test
    public void testSaveAndLoadFile() {
        GameData gameData1 = new GameData();
        gameData1.controllerIndex = 0;
        String filename = "test";
        SaveAndLoad.save(gameData1, filename);
        GameData gameData2 = SaveAndLoad.load(filename + ".json");
        // Assert if loaded successfully
        assert gameData2 != null;

        // Check if value of one variable is the same
        Assertions.assertEquals(gameData1.controllerIndex, gameData2.controllerIndex);

        SaveAndLoad.delete(filename + ".json");
        // Check if deletion was successful, therefore load should return null
        // Severe logger will address IOException which it should
        Assertions.assertNull(SaveAndLoad.load(filename + ".json"));
    }

}
