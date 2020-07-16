/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic;

import common.TomcatStartUp;
import common.ValidationException;
import dal.EMFactory;
import entity.Image;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
*
* @author Ron
*/
public class ImageLogicTest {
    private ImageLogic logic;
    private Image expectedImage;

    @BeforeAll
    final static void setUpBeforeClass() throws Exception {
        TomcatStartUp.createTomcat();
    }

    @AfterAll
    final static void tearDownAfterClass() throws Exception {
        TomcatStartUp.stopAndDestroyTomcat();
    }

    @BeforeEach
    final void setUp() throws Exception {
        /* **********************************
         * ***********IMPORTANT**************
         * **********************************/
        //we only do this for the test.
        //always create Entity using logic.
        //we manually make the Image to not rely on any logic functionality , just for testing
        Image image = new Image();
        image.setUrl("Junit5Test");
        image.setPath("junit");
        image.setName("junit5");

        //get an instance of EntityManager
        EntityManager em = EMFactory.getEMFactory().createEntityManager();
        //start a Transaction 
        em.getTransaction().begin();
        //add an Image to hibernate, Image is now managed.
        //we use merge instead of add so we can get the updated generated ID.
        expectedImage = em.merge(image);
        //commit the changes
        em.getTransaction().commit();
        //close EntityManager
        em.close();

        logic = new ImageLogic();
    }

    @AfterEach
    final void tearDown() throws Exception {
        if (expectedImage != null) {
            logic.delete(expectedImage);
        }
    }

    @Test
    final void testGetAll() {
        //get all the Images from the DB
        List<Image> list = logic.getAll();
        //store the size of list, this way we know how many Images exits in DB
        int originalSize = list.size();

        //make sure Image was created successfully
        assertNotNull(expectedImage);
        //delete the new Image
        logic.delete(expectedImage);

        //get all Images again
        list = logic.getAll();
        //the new size of Images must be one less
        assertEquals(originalSize - 1, list.size());
    }

    /**
     * helper method for testing all Image fields
     *
     * @param expected
     * @param actual
     */
    private void assertImageEquals(Image expected, Image actual) {
        //assert all field to guarantee they are the same
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getUrl(), actual.getUrl());
        assertEquals(expected.getPath(), actual.getPath());
        assertEquals(expected.getName(), actual.getName());
    }

    @Test
    final void testGetWithId() {
        //using the id of test Image get another Image from logic
        Image returnedImage = logic.getWithId(expectedImage.getId());

        //the two Images (testAcounts and returnedImages) must be the same
        assertImageEquals(expectedImage, returnedImage);
    }

    @Test
    final void testGetImageWithUrl() {
        int foundFull = 0;
        List<Image> returnedImage = logic.getWithUrl(expectedImage.getUrl());
        for (Image image : returnedImage) {
            //all Images must have the same Name
            assertEquals(expectedImage.getUrl(), image.getUrl());
            //exactly one Image must be the same
            if (image.getId().equals(expectedImage.getId())) {
                assertImageEquals(expectedImage, image);
                foundFull++;
            }
        }
        assertEquals(1, foundFull, "if zero means not found, if more than one means duplicate");
    }

    @Test
    final void testGetImageWIthPath() {
        Image returnedImage = logic.getWithPath(expectedImage.getPath());

        //the two Images (testAcounts and returnedImages) must be the same
        assertImageEquals(expectedImage, returnedImage);
    }

    @Test
    final void testGetImageWithName() {
        int foundFull = 0;
        List<Image> returnedImage = logic.getWithName(expectedImage.getName());
        for (Image image : returnedImage) {
            //all Images must have the same Name
            assertEquals(expectedImage.getName(), image.getName());
            //exactly one Image must be the same
            if (image.getId().equals(expectedImage.getId())) {
                assertImageEquals(expectedImage, image);
                foundFull++;
            }
        }
        assertEquals(1, foundFull, "if zero means not found, if more than one means duplicate");
    }

    @Test
    final void testSearch() {
        int foundFull = 0;
        //search for a substring of one of the fields in the expectedImage
        String searchString = expectedImage.getName().substring(3);
        //in Image we only search for display name and user, this is completely based on your design for other entities.
        List<Image> returnedImages = logic.search(searchString);
        for (Image image : returnedImages) {
            //all Images must contain the substring
            assertTrue(image.getName().contains(searchString));
            //exactly one Image must be the same
            if (image.getId().equals(expectedImage.getId())) {
                assertImageEquals(expectedImage, image);
                foundFull++;
            }
        }
        assertEquals(1, foundFull, "if zero means not found, if more than one means duplicate");
    }

    @Test
    final void testCreateEntityAndAdd() {
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put(ImageLogic.URL, new String[]{"TestCreateEntity"});
        sampleMap.put(ImageLogic.PATH, new String[]{"testCreateImage"});
        sampleMap.put(ImageLogic.NAME, new String[]{"create"});

        Image returnedImage = logic.createEntity(sampleMap);
        logic.add(returnedImage);

        returnedImage = logic.getWithPath(returnedImage.getPath());

        assertEquals(sampleMap.get(ImageLogic.URL)[0], returnedImage.getUrl());
        assertEquals(sampleMap.get(ImageLogic.PATH)[0], returnedImage.getPath());
        assertEquals(sampleMap.get(ImageLogic.NAME)[0], returnedImage.getName());

        logic.delete(returnedImage);
    }

    @Test
    final void testCreateEntity() {
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put(ImageLogic.ID, new String[]{Integer.toString(expectedImage.getId())});
        sampleMap.put(ImageLogic.URL, new String[]{expectedImage.getUrl()});
        sampleMap.put(ImageLogic.PATH, new String[]{expectedImage.getPath()});
        sampleMap.put(ImageLogic.NAME, new String[]{expectedImage.getName()});

        Image returnedImage = logic.createEntity(sampleMap);

        assertImageEquals(expectedImage, returnedImage);
    }

    @Test
    final void testCreateEntityNullAndEmptyValues() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = (Map<String, String[]> map) -> {
            map.clear();
            map.put(ImageLogic.ID, new String[]{Integer.toString(expectedImage.getId())});
            map.put(ImageLogic.URL, new String[]{expectedImage.getUrl()});
            map.put(ImageLogic.PATH, new String[]{expectedImage.getPath()});
            map.put(ImageLogic.NAME, new String[]{expectedImage.getName()});
        };

        //idealy every test should be in its own method
        fillMap.accept(sampleMap);
        sampleMap.replace(ImageLogic.ID, null);
        assertThrows(NullPointerException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(ImageLogic.ID, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(ImageLogic.URL, null);
        assertThrows(NullPointerException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(ImageLogic.URL, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(ImageLogic.PATH, null);
        assertThrows(NullPointerException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(ImageLogic.PATH, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(ImageLogic.NAME, null);
        assertThrows(NullPointerException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(ImageLogic.NAME, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(sampleMap));
    }

    @Test
    final void testCreateEntityBadLengthValues() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = (Map<String, String[]> map) -> {
            map.clear();
            map.put(ImageLogic.ID, new String[]{Integer.toString(expectedImage.getId())});
            map.put(ImageLogic.URL, new String[]{expectedImage.getUrl()});
            map.put(ImageLogic.PATH, new String[]{expectedImage.getPath()});
            map.put(ImageLogic.NAME, new String[]{expectedImage.getName()});
        };

        IntFunction<String> generateString = (int length) -> {
            //https://www.baeldung.com/java-random-string#java8-alphabetic
            return new Random().ints('a', 'z' + 1).limit(length)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();
        };

        //idealy every test should be in its own method
        fillMap.accept(sampleMap);
        sampleMap.replace(ImageLogic.ID, new String[]{""});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(ImageLogic.ID, new String[]{"12b"});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(ImageLogic.URL, new String[]{""});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(ImageLogic.URL, new String[]{generateString.apply(256)});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(ImageLogic.PATH, new String[]{""});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(ImageLogic.PATH, new String[]{generateString.apply(256)});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(ImageLogic.NAME, new String[]{""});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(ImageLogic.NAME, new String[]{generateString.apply(256)});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
    }

    @Test
    final void testCreateEntityEdgeValues() {
        IntFunction<String> generateString = (int length) -> {
            //https://www.baeldung.com/java-random-string#java8-alphabetic
            return new Random().ints('a', 'z' + 1).limit(length)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();
        };
        
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put(ImageLogic.ID, new String[]{Integer.toString(1)});
        sampleMap.put(ImageLogic.URL, new String[]{generateString.apply(1)});
        sampleMap.put(ImageLogic.PATH, new String[]{generateString.apply(1)});
        sampleMap.put(ImageLogic.NAME, new String[]{generateString.apply(1)});

        //idealy every test should be in its own method
        Image returnedImage = logic.createEntity(sampleMap);
        assertEquals(Integer.parseInt(sampleMap.get(ImageLogic.ID)[0]), returnedImage.getId());
        assertEquals(sampleMap.get(ImageLogic.URL)[0], returnedImage.getUrl());
        assertEquals(sampleMap.get(ImageLogic.PATH)[0], returnedImage.getPath());
        assertEquals(sampleMap.get(ImageLogic.NAME)[0], returnedImage.getName());
        
        sampleMap = new HashMap<>();
        sampleMap.put(ImageLogic.ID, new String[]{Integer.toString(1)});
        sampleMap.put(ImageLogic.URL, new String[]{generateString.apply(255)});
        sampleMap.put(ImageLogic.PATH, new String[]{generateString.apply(255)});
        sampleMap.put(ImageLogic.NAME, new String[]{generateString.apply(255)});

        //idealy every test should be in its own method
        returnedImage = logic.createEntity(sampleMap);
        assertEquals(Integer.parseInt(sampleMap.get(ImageLogic.ID)[0]), returnedImage.getId());
        assertEquals(sampleMap.get(ImageLogic.URL)[0], returnedImage.getUrl());
        assertEquals(sampleMap.get(ImageLogic.PATH)[0], returnedImage.getPath());
        assertEquals(sampleMap.get(ImageLogic.NAME)[0], returnedImage.getName());
    }

    @Test
    final void testGetColumnNames() {
        List<String> list = logic.getColumnNames();
        assertEquals(Arrays.asList("ID", "URL", "Path", "Name"), list);
    }

    @Test
    final void testGetColumnCodes() {
        List<String> list = logic.getColumnCodes();
        assertEquals(Arrays.asList(ImageLogic.ID, ImageLogic.URL, ImageLogic.PATH, ImageLogic.NAME), list);
    }

    @Test
    final void testExtractDataAsList() {
        List<?> list = logic.extractDataAsList(expectedImage);
        assertEquals(expectedImage.getId(), list.get(0));
        assertEquals(expectedImage.getUrl(), list.get(1));
        assertEquals(expectedImage.getPath(), list.get(2));
        assertEquals(expectedImage.getName(), list.get(3));
    }
}
