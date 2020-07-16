package logic;

import common.TomcatStartUp;
import common.ValidationException;
import dal.EMFactory;
import entity.Category;
import entity.Image;
import entity.Item;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class ItemLogicTest {
    private ItemLogic logic;
    private Item expectedItem;
    private static ImageLogic imgLogic;
    private static Image image;
    private static CategoryLogic cLogic;
    private static Category category;

    @BeforeAll
    final static void setUpBeforeClass() throws Exception {
        TomcatStartUp.createTomcat();
        image = new Image();
        image.setUrl("Junit5Test");
        image.setPath("junit");
        image.setName("junit5");
        
        category = new Category();
        category.setTitle("Junit 5 Test");
        category.setUrl("junit");
    }

    @AfterAll
    final static void tearDownAfterClass() throws Exception {
        if(image!=null){imgLogic.delete(image);}
        if(category!=null){cLogic.delete(category);}
        TomcatStartUp.stopAndDestroyTomcat();
        
    }

    @BeforeEach
    final void setUp() throws Exception {
        imgLogic = new ImageLogic();
        cLogic = new CategoryLogic();
        EntityManager em = EMFactory.getEMFactory().createEntityManager();
        
        
        image = em.merge(image);
        category = em.merge(category);
        
        Item item = new Item();
        item.setId(1);
        item.setImage(image);
        item.setCategory(category);
        item.setPrice(BigDecimal.ONE);
        item.setTitle("JavaJunitTesting");
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        item.setDate(formatter.parse("28/12/2019"));
        item.setLocation("Algonquin");
        item.setDescription("Assignment1");
        item.setUrl("junit7");
        

        em.getTransaction().begin();
        expectedItem = em.merge(item);
        em.getTransaction().commit();
        em.close();

        logic = new ItemLogic();
    }

    @AfterEach
    final void tearDown() throws Exception {
        if (expectedItem != null) {
            logic.delete(expectedItem);
        }
        imgLogic.delete(image);
        cLogic.delete(category);
    }

    @Test
    final void testGetAll() {
        //get all the Items from the DB
        List<Item> list = logic.getAll();
        //store the size of list, this way we know how many Items exits in DB
        int originalSize = list.size();

        //make sure Item was created successfully
        assertNotNull(expectedItem);
        //delete the new Item
        logic.delete(expectedItem);

        //get all Items again
        list = logic.getAll();
        //the new size of Items must be one less
        assertEquals(originalSize - 1, list.size());
    }

    /**
     * helper method for testing all Item fields
     *
     * @param expected
     * @param actual
     */
    private void assertItemEquals(Item expected, Item actual) {
        //assert all field to guarantee they are the same
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getImage(), actual.getImage());
        assertEquals(expected.getCategory(), actual.getCategory());
        assertEquals( expected.getPrice().compareTo( actual.getPrice()), 0);
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getLocation(), actual.getLocation());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getUrl(), actual.getUrl());
    }

    @Test
    final void testGetItemWithUrl() {
        Item returnedItem = logic.getWithUrl(expectedItem.getUrl());
        assertItemEquals(expectedItem, returnedItem);
    }
    
    @Test
    final void testGetWithId() {
        Item returnedItem = logic.getWithId(expectedItem.getId());
        assertItemEquals(expectedItem, returnedItem);
    }

    @Test
    final void testGetItemWithPrice() {
        int foundFull = 0;
        List<Item> returnedItems = logic.getWithPrice(expectedItem.getPrice());
        for (Item item : returnedItems) {
            assertEquals( expectedItem.getPrice().compareTo( item.getPrice()), 0);
            if (item.getId().equals(expectedItem.getId())) {
                assertItemEquals(expectedItem, item);
                foundFull++;
            }
        }
        assertEquals(1, foundFull, "if zero means not found, if more than one means duplicate");
    }

    @Test
    final void testGetItemWithTitle() {
        int foundFull = 0;
        List<Item> returnedItems = logic.getWithTitle(expectedItem.getTitle());
        for (Item item : returnedItems) {
            //all Items must have the same price
            assertEquals(expectedItem.getTitle(), item.getTitle());
            //exactly one Item must be the same
            if (item.getId().equals(expectedItem.getId())) {
                assertItemEquals(expectedItem, item);
                foundFull++;
            }
        }
        assertEquals(1, foundFull, "if zero means not found, if more than one means duplicate");
    }
    
    @Test
    final void testGetItemWithDate() {
        int foundFull = 0;
        List<Item> returnedItems = logic.getWithDate(expectedItem.getDate().toString());
        for (Item item : returnedItems) {
            assertEquals(expectedItem.getDate(), item.getDate());
        }
    }
    
    @Test
    final void testGetItemWithLocation() {
        int foundFull = 0;
        List<Item> returnedItems = logic.getWithLocation(expectedItem.getLocation());
        for (Item item : returnedItems) {
            //all Items must have the same price
            assertEquals(expectedItem.getLocation(), item.getLocation());
            //exactly one Item must be the same
            if (item.getId().equals(expectedItem.getId())) {
                assertItemEquals(expectedItem, item);
                foundFull++;
            }
        }
        assertEquals(1, foundFull, "if zero means not found, if more than one means duplicate");
    }
    
    @Test
    final void testGetItemWithDescription() {
        int foundFull = 0;
        List<Item> returnedItems = logic.getWithDescription(expectedItem.getDescription());
        for (Item item : returnedItems) {
            //all Items must have the same price
            assertEquals(expectedItem.getDescription(), item.getDescription());
            //exactly one Item must be the same
            if (item.getId().equals(expectedItem.getId())) {
                assertItemEquals(expectedItem, item);
                foundFull++;
            }
        }
        assertEquals(1, foundFull, "if zero means not found, if more than one means duplicate");
    }
    
    

    @Test
    final void testGetItemWithCategory() {
        int foundFull = 0;
        List<Item> returnedItems = logic.getWithCategory(expectedItem.getCategory().getId().toString());
        for (Item item : returnedItems) {
            assertEquals(expectedItem.getCategory(), item.getCategory());
        }
    }

    @Test
    final void testSearch() {
        int foundFull = 0;
        //search for a substring of one of the fields in the expectedItem
        String searchString = expectedItem.getTitle().substring(3);
        //in Item we only search for display name and user, this is completely based on your design for other entities.
        List<Item> returnedItems = logic.search(searchString);
        for (Item item : returnedItems) {
            //all Items must contain the substring
            assertTrue(item.getTitle().contains(searchString));
            //exactly one Item must be the same
            if (item.getId().equals(expectedItem.getId())) {
                assertItemEquals(expectedItem, item);
                foundFull++;
            }
        }
        assertEquals(1, foundFull, "if zero means not found, if more than one means duplicate");
    }

    @Test
    final void testCreateEntityAndAdd() {
        int foundFull = 0;
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put(ItemLogic.ID, new String[]{Integer.toString(2)});
        sampleMap.put(ItemLogic.PRICE, new String[]{expectedItem.getPrice().toString()});
        sampleMap.put(ItemLogic.TITLE, new String[]{expectedItem.getTitle()});
        sampleMap.put(ItemLogic.DATE, new String[]{expectedItem.getDate().toString()});
        sampleMap.put(ItemLogic.LOCATION, new String[]{expectedItem.getLocation()});
        sampleMap.put(ItemLogic.DESCRIPTION, new String[]{expectedItem.getDescription()});
        sampleMap.put(ItemLogic.URL, new String[]{"junit5"});

        Item returnedItem = logic.createEntity(sampleMap);
        returnedItem.setCategory(expectedItem.getCategory());
        returnedItem.setImage(expectedItem.getImage());
        logic.add(returnedItem);
        
        List<Item> returnedItems = logic.getWithTitle(returnedItem.getTitle());
//        returnedItem = logic.getItemWithUser(returnedItem.getUser());
        
        for(Item item : returnedItems){
                
            assertEquals(item.getTitle(), sampleMap.get(ItemLogic.TITLE)[0]);
            
            if(item.getId().equals(returnedItem.getId())){
                assertEquals(2, returnedItem.getId());
                assertEquals(expectedItem.getPrice().compareTo( returnedItem.getPrice()), 0);
                assertEquals(expectedItem.getTitle(), returnedItem.getTitle());
                assertEquals(expectedItem.getLocation(), returnedItem.getLocation());
                assertEquals(expectedItem.getDescription(), returnedItem.getDescription());
                assertEquals("junit5", returnedItem.getUrl());
                foundFull++;
            }
        }
        
        assertEquals(1, foundFull, "if zero means not found, if more than one means duplicate");
        logic.delete(returnedItem);
    }

    @Test
    final void testCreateEntity() {
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put(ItemLogic.ID, new String[]{Integer.toString(expectedItem.getId())});
        sampleMap.put(ItemLogic.PRICE, new String[]{expectedItem.getPrice().toString()});
        sampleMap.put(ItemLogic.TITLE, new String[]{expectedItem.getTitle()});
        sampleMap.put(ItemLogic.DATE, new String[]{expectedItem.getDate().toString()});
        sampleMap.put(ItemLogic.LOCATION, new String[]{expectedItem.getLocation()});
        sampleMap.put(ItemLogic.DESCRIPTION, new String[]{expectedItem.getDescription()});
        sampleMap.put(ItemLogic.URL, new String[]{expectedItem.getUrl()});

        Item returnedItem = logic.createEntity(sampleMap);

        assertEquals(expectedItem.getId(), returnedItem.getId());
        assertEquals(expectedItem.getPrice().compareTo( returnedItem.getPrice()), 0);
        assertEquals(expectedItem.getTitle(), returnedItem.getTitle());
        assertEquals(expectedItem.getLocation(), returnedItem.getLocation());
        assertEquals(expectedItem.getDescription(), returnedItem.getDescription());
        assertEquals(expectedItem.getUrl(), returnedItem.getUrl());
    }

    @Test
    final void testCreateEntityNullAndEmptyValues() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = (Map<String, String[]> map) -> {
            map.clear();
            map.put(ItemLogic.ID, new String[]{Integer.toString(expectedItem.getId())});
            map.put(ItemLogic.PRICE, new String[]{expectedItem.getPrice().toString()});
            map.put(ItemLogic.TITLE, new String[]{expectedItem.getTitle()});
            map.put(ItemLogic.DATE, new String[]{expectedItem.getDate().toString()});
            map.put(ItemLogic.LOCATION, new String[]{expectedItem.getLocation()});
            map.put(ItemLogic.DESCRIPTION, new String[]{expectedItem.getDescription()});
            map.put(ItemLogic.URL, new String[]{expectedItem.getUrl()});
        };

        //idealy every test should be in its own method
        fillMap.accept(sampleMap);
        sampleMap.replace(ItemLogic.ID, null);
        assertThrows(NullPointerException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(ItemLogic.ID, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(ItemLogic.TITLE, null);
        assertThrows(NullPointerException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(ItemLogic.TITLE, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(ItemLogic.DESCRIPTION, null);
        assertThrows(NullPointerException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(ItemLogic.DESCRIPTION, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(ItemLogic.URL, null);
        assertThrows(NullPointerException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(ItemLogic.URL, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(sampleMap));
    }

    @Test
    final void testCreateEntityBadLengthValues() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = (Map<String, String[]> map) -> {
            map.clear();
            map.put(ItemLogic.ID, new String[]{Integer.toString(expectedItem.getId())});
            map.put(ItemLogic.PRICE, new String[]{expectedItem.getPrice().toString()});
            map.put(ItemLogic.TITLE, new String[]{expectedItem.getTitle()});
            map.put(ItemLogic.DATE, new String[]{expectedItem.getDate().toString()});
            map.put(ItemLogic.LOCATION, new String[]{expectedItem.getLocation()});
            map.put(ItemLogic.DESCRIPTION, new String[]{expectedItem.getDescription()});
            map.put(ItemLogic.URL, new String[]{expectedItem.getUrl()});
        };

        IntFunction<String> generateString = (int length) -> {
            //https://www.baeldung.com/java-random-string#java8-alphabetic
            return new Random().ints('a', 'z' + 1).limit(length)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();
        };

        //idealy every test should be in its own method
        fillMap.accept(sampleMap);
        sampleMap.replace(ItemLogic.ID, new String[]{""});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(ItemLogic.ID, new String[]{"12b"});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(ItemLogic.PRICE, new String[]{"22222222222222.22"}); //14 on the left and 2 on the right side of the decimal
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(ItemLogic.TITLE, new String[]{""});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(ItemLogic.TITLE, new String[]{generateString.apply(256)});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        
        fillMap.accept(sampleMap);
        sampleMap.replace(ItemLogic.LOCATION, new String[]{generateString.apply(46)});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(ItemLogic.DESCRIPTION, new String[]{""});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        
        fillMap.accept(sampleMap);
        sampleMap.replace(ItemLogic.URL, new String[]{""});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(ItemLogic.URL, new String[]{generateString.apply(256)});
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
        sampleMap.put(ItemLogic.ID, new String[]{Integer.toString(1)});
        sampleMap.put(ItemLogic.PRICE, new String[]{new BigDecimal("1").toString()});
        sampleMap.put(ItemLogic.TITLE, new String[]{generateString.apply(1)});
        try {
            sampleMap.put(ItemLogic.DATE, new String[]{new SimpleDateFormat("dd/MM/yyyy").parse("28/12/2019").toString()});
        } catch (ParseException ex) {
        }
        sampleMap.put(ItemLogic.LOCATION, new String[]{generateString.apply(1)});
        sampleMap.put(ItemLogic.DESCRIPTION, new String[]{generateString.apply(1)});
        sampleMap.put(ItemLogic.URL, new String[]{generateString.apply(1)});

        //idealy every test should be in its own method
        Item returnedItem = logic.createEntity(sampleMap);
        assertEquals(Integer.parseInt(sampleMap.get(ItemLogic.ID)[0]), returnedItem.getId());
        assertEquals(new BigDecimal(sampleMap.get(ItemLogic.PRICE)[0]), returnedItem.getPrice());
        assertEquals(sampleMap.get(ItemLogic.TITLE)[0], returnedItem.getTitle());
        try {
            assertEquals(new SimpleDateFormat("dd/MM/yyyy").parse(sampleMap.get(ItemLogic.DATE)[0]), returnedItem.getDate());
        } catch (ParseException ex) {
        }
        assertEquals(sampleMap.get(ItemLogic.LOCATION)[0], returnedItem.getLocation());
        assertEquals(sampleMap.get(ItemLogic.DESCRIPTION)[0], returnedItem.getDescription());
        assertEquals(sampleMap.get(ItemLogic.URL)[0], returnedItem.getUrl());
        
        sampleMap = new HashMap<>();
        sampleMap.put(ItemLogic.ID, new String[]{Integer.toString(1)});
        sampleMap.put(ItemLogic.PRICE, new String[]{new BigDecimal("2222222222222.22").toString()});
        sampleMap.put(ItemLogic.TITLE, new String[]{generateString.apply(255)});
        try {
            sampleMap.put(ItemLogic.DATE, new String[]{new SimpleDateFormat("dd/MM/yyyy").parse("28/12/2019").toString()});
        } catch (ParseException ex) {
        }
        sampleMap.put(ItemLogic.LOCATION, new String[]{generateString.apply(45)});
        sampleMap.put(ItemLogic.DESCRIPTION, new String[]{generateString.apply(255)});
        sampleMap.put(ItemLogic.URL, new String[]{generateString.apply(255)});

        //idealy every test should be in its own method
        returnedItem = logic.createEntity(sampleMap);
        assertEquals(Integer.parseInt(sampleMap.get(ItemLogic.ID)[0]), returnedItem.getId());
        assertEquals(new BigDecimal(sampleMap.get(ItemLogic.PRICE)[0]), returnedItem.getPrice());
        assertEquals(sampleMap.get(ItemLogic.TITLE)[0], returnedItem.getTitle());
        try {
            assertEquals(new SimpleDateFormat("dd/MM/yyyy").parse(sampleMap.get(ItemLogic.DATE)[0]), returnedItem.getDate());
        } catch (ParseException ex) {
        }
        assertEquals(sampleMap.get(ItemLogic.LOCATION)[0], returnedItem.getLocation());
        assertEquals(sampleMap.get(ItemLogic.DESCRIPTION)[0], returnedItem.getDescription());
        assertEquals(sampleMap.get(ItemLogic.URL)[0], returnedItem.getUrl());
    }

    @Test
    final void testGetColumnNames() {
        List<String> list = logic.getColumnNames();
        assertEquals(Arrays.asList("ID", "Image", "Category", "Price", "Title", "Date", "Location", "Description", "URL"), list);
    }

    @Test
    final void testGetColumnCodes() {
        List<String> list = logic.getColumnCodes();
        assertEquals(Arrays.asList(ItemLogic.ID, ItemLogic.IMAGE_ID, ItemLogic.CATEGORY_ID, ItemLogic.PRICE, ItemLogic.TITLE, ItemLogic.DATE, ItemLogic.LOCATION, ItemLogic.DESCRIPTION, ItemLogic.URL), list);
    }

    @Test
    final void testExtractDataAsList() {
        List<?> list = logic.extractDataAsList(expectedItem);
        assertEquals(expectedItem.getId(), list.get(0));
        assertEquals(expectedItem.getImage(), list.get(1));
        assertEquals(expectedItem.getCategory(), list.get(2));
        assertEquals(expectedItem.getPrice(), list.get(3));
        assertEquals(expectedItem.getTitle(), list.get(4));
        assertEquals(expectedItem.getDate(), list.get(5));
        assertEquals(expectedItem.getLocation(), list.get(6));
        assertEquals(expectedItem.getDescription(), list.get(7));
        assertEquals(expectedItem.getUrl(), list.get(8));

    }
}
