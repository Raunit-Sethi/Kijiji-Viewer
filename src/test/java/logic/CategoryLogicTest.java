package logic;

import common.TomcatStartUp;
import common.ValidationException;
import dal.EMFactory;
import entity.Category;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import javax.persistence.EntityManager;
import static logic.CategoryLogic.ID;
import static logic.CategoryLogic.TITLE;
import static logic.CategoryLogic.URL;
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
public class CategoryLogicTest {
    private CategoryLogic logic;
    private Category expectedCategory;

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
        //we manually make the Category to not rely on any logic functionality , just for testing
        Category category = new Category();
        category.setTitle("Junit 5 Test");
        category.setUrl("junit");
       
        //get an instance of EntityManager
        EntityManager em = EMFactory.getEMFactory().createEntityManager();
        //start a Transaction 
        em.getTransaction().begin();
        //add an Category to hibernate, category is now managed.
        //we use merge instead of add so we can get the updated generated ID.
        expectedCategory = em.merge(category);
        //commit the changes
        em.getTransaction().commit();
        //close EntityManager
        em.close();

        logic = new CategoryLogic();
    }

    @AfterEach
    final void tearDown() throws Exception {
        if (expectedCategory != null) {
            logic.delete(expectedCategory);
        }
    }

    @Test
    final void testGetAll() {
        //get all the Categorys from the DB
        List<Category> list = logic.getAll();
        //store the size of list, this way we know how many Categorys exits in DB
        int originalSize = list.size();

        //make sure Category was created successfully
        assertNotNull(expectedCategory);
        //delete the new Category
        logic.delete(expectedCategory);

        //get all Categorys again
        list = logic.getAll();
        //the new size of Categorys must be one less
        assertEquals(originalSize - 1, list.size());
    }

    /**
     * helper method for testing all Category fields
     *
     * @param expected
     * @param actual
     */
    private void assertCategoryEquals(Category expected, Category actual) {
        //assert all field to guarantee they are the same
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getUrl(), actual.getUrl());
        assertEquals(expected.getTitle(), actual.getTitle());
    }

    @Test
    final void testGetWithId() {
        //using the id of test Category get another Category from logic
        Category returnedCategory = logic.getWithId(expectedCategory.getId());
        //the two Categorys (testCategorys and returnedCategorys) must be the same
        assertCategoryEquals(expectedCategory, returnedCategory);
    }

    @Test
    final void testGetCategoryWithUrl() {
        Category returnedCategory = logic.getWithUrl(expectedCategory.getUrl());
        //the two Categorys (testCategorys and returnedCategorys) must be the same
        assertCategoryEquals(expectedCategory, returnedCategory);
    }

    @Test
    final void testGetCategoryWIthTitle() {
        Category returnedCategory = logic.getWithTitle(expectedCategory.getTitle());
        //the two Categorys (testCategorys and returnedCategorys) must be the same
        assertCategoryEquals(expectedCategory, returnedCategory);
    }

    

    

    @Test
    final void testSearch() {
        int foundFull = 0;
        String searchString = expectedCategory.getTitle().substring(3);
        List<Category> returnedCategorys = logic.search(searchString);
        for (Category category : returnedCategorys) {
            assertTrue(category.getTitle().contains(searchString));
            if (category.getId().equals(expectedCategory.getId())) {
                assertCategoryEquals(expectedCategory, category);
                foundFull++;
            }
        }
        assertEquals(1, foundFull, "if zero means not found, if more than one means duplicate");
    }

    @Test
    final void testCreateEntityAndAdd() {
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put(CategoryLogic.URL, new String[]{"TestCreateEntity"});
        sampleMap.put(CategoryLogic.TITLE, new String[]{"testCreateCategory"});
        

        Category returnedCategory = logic.createEntity(sampleMap);
        logic.add(returnedCategory);

        returnedCategory = logic.getWithTitle(returnedCategory.getTitle());

        assertEquals(sampleMap.get(CategoryLogic.URL)[0], returnedCategory.getUrl());
        assertEquals(sampleMap.get(CategoryLogic.TITLE)[0], returnedCategory.getTitle());

        logic.delete(returnedCategory);
    }

    @Test
    final void testCreateEntity() {
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put(CategoryLogic.ID, new String[]{Integer.toString(expectedCategory.getId())});
        sampleMap.put(CategoryLogic.URL, new String[]{expectedCategory.getUrl()});
        sampleMap.put(CategoryLogic.TITLE, new String[]{expectedCategory.getTitle()});

        Category returnedCategory = logic.createEntity(sampleMap);

        assertCategoryEquals(expectedCategory, returnedCategory);
    }

    @Test
    final void testCreateEntityNullAndEmptyValues() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = (Map<String, String[]> map) -> {
            map.clear();
            map.put(CategoryLogic.ID, new String[]{Integer.toString(expectedCategory.getId())});
            map.put(CategoryLogic.URL, new String[]{expectedCategory.getUrl()});
            map.put(CategoryLogic.TITLE, new String[]{expectedCategory.getTitle()});
        };

        //idealy every test should be in its own method
        fillMap.accept(sampleMap);
        sampleMap.replace(CategoryLogic.ID, null);
        assertThrows(NullPointerException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(CategoryLogic.ID, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(CategoryLogic.URL, null);
        assertThrows(NullPointerException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(CategoryLogic.URL, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(CategoryLogic.TITLE, null);
        assertThrows(NullPointerException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(CategoryLogic.TITLE, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(sampleMap));

    }

    @Test
    final void testCreateEntityBadLengthValues() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = (Map<String, String[]> map) -> {
            map.clear();
            map.put(CategoryLogic.ID, new String[]{Integer.toString(expectedCategory.getId())});
            map.put(CategoryLogic.URL, new String[]{expectedCategory.getUrl()});
            map.put(CategoryLogic.TITLE, new String[]{expectedCategory.getTitle()});
        };

        IntFunction<String> generateString = (int length) -> {
            //https://www.baeldung.com/java-random-string#java8-alphabetic
            return new Random().ints('a', 'z' + 1).limit(length)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();
        };

        //idealy every test should be in its own method
        fillMap.accept(sampleMap);
        sampleMap.replace(CategoryLogic.ID, new String[]{""});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(CategoryLogic.ID, new String[]{"12b"});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(CategoryLogic.URL, new String[]{""});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(CategoryLogic.URL, new String[]{generateString.apply(256)});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(CategoryLogic.TITLE, new String[]{""});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(CategoryLogic.TITLE, new String[]{generateString.apply(256)});
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
        sampleMap.put(CategoryLogic.ID, new String[]{Integer.toString(1)});
        sampleMap.put(CategoryLogic.URL, new String[]{generateString.apply(1)});
        sampleMap.put(CategoryLogic.TITLE, new String[]{generateString.apply(1)});

        //idealy every test should be in its own method
        Category returnedCategory = logic.createEntity(sampleMap);
        assertEquals(Integer.parseInt(sampleMap.get(CategoryLogic.ID)[0]), returnedCategory.getId());
        assertEquals(sampleMap.get(CategoryLogic.URL)[0], returnedCategory.getUrl());
        assertEquals(sampleMap.get(CategoryLogic.TITLE)[0], returnedCategory.getTitle());
        
        sampleMap = new HashMap<>();
        sampleMap.put(CategoryLogic.ID, new String[]{Integer.toString(1)});
        sampleMap.put(CategoryLogic.URL, new String[]{generateString.apply(255)});
        sampleMap.put(CategoryLogic.TITLE, new String[]{generateString.apply(255)});

        //idealy every test should be in its own method
        returnedCategory = logic.createEntity(sampleMap);
        assertEquals(Integer.parseInt(sampleMap.get(CategoryLogic.ID)[0]), returnedCategory.getId());
        assertEquals(sampleMap.get(CategoryLogic.URL)[0], returnedCategory.getUrl());
        assertEquals(sampleMap.get(CategoryLogic.TITLE)[0], returnedCategory.getTitle());
    }

    @Test
    final void testGetColumnNames() {
        List<String> list = logic.getColumnNames();
        assertEquals(Arrays.asList("ID", "URL", "Title"), list);
    }

    @Test
    final void testGetColumnCodes() {
        List<String> list = logic.getColumnCodes();
        assertEquals(Arrays.asList(ID, URL, TITLE), list);
    }

    @Test
    final void testExtractDataAsList() {
        List<?> list = logic.extractDataAsList(expectedCategory);
        assertEquals(expectedCategory.getId(), list.get(0));
        assertEquals(expectedCategory.getUrl(), list.get(1));
        assertEquals(expectedCategory.getTitle(), list.get(2));
    }
    
}
