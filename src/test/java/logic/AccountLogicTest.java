package logic;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import entity.Account;
import common.TomcatStartUp;
import common.ValidationException;
import dal.EMFactory;
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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
*
* @author Ron
*/
class AccountLogicTest {

    private AccountLogic logic;
    private Account expectedAccount;

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
        //we manually make the account to not rely on any logic functionality , just for testing
        Account account = new Account();
        account.setDisplayName("Junit 5 Test");
        account.setUser("junit");
        account.setPassword("junit5");

        //get an instance of EntityManager
        EntityManager em = EMFactory.getEMFactory().createEntityManager();
        //start a Transaction 
        em.getTransaction().begin();
        //add an account to hibernate, account is now managed.
        //we use merge instead of add so we can get the updated generated ID.
        expectedAccount = em.merge(account);
        //commit the changes
        em.getTransaction().commit();
        //close EntityManager
        em.close();

        logic = new AccountLogic();
    }

    @AfterEach
    final void tearDown() throws Exception {
        if (expectedAccount != null) {
            logic.delete(expectedAccount);
        }
    }

    @Test
    final void testGetAll() {
        //get all the accounts from the DB
        List<Account> list = logic.getAll();
        //store the size of list, this way we know how many accounts exits in DB
        int originalSize = list.size();

        //make sure account was created successfully
        assertNotNull(expectedAccount);
        //delete the new account
        logic.delete(expectedAccount);

        //get all accounts again
        list = logic.getAll();
        //the new size of accounts must be one less
        assertEquals(originalSize - 1, list.size());
    }

    /**
     * helper method for testing all account fields
     *
     * @param expected
     * @param actual
     */
    private void assertAccountEquals(Account expected, Account actual) {
        //assert all field to guarantee they are the same
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getDisplayName(), actual.getDisplayName());
        assertEquals(expected.getUser(), actual.getUser());
        assertEquals(expected.getPassword(), actual.getPassword());
    }

    @Test
    final void testGetWithId() {
        //using the id of test account get another account from logic
        Account returnedAccount = logic.getWithId(expectedAccount.getId());

        //the two accounts (testAcounts and returnedAccounts) must be the same
        assertAccountEquals(expectedAccount, returnedAccount);
    }

    @Test
    final void testGetAccountWithDisplayName() {
        Account returnedAccount = logic.getAccountWithDisplayName(expectedAccount.getDisplayName());

        //the two accounts (testAcounts and returnedAccounts) must be the same
        assertAccountEquals(expectedAccount, returnedAccount);
    }

    @Test
    final void testGetAccountWIthUser() {
        Account returnedAccount = logic.getAccountWithUser(expectedAccount.getUser());

        //the two accounts (testAcounts and returnedAccounts) must be the same
        assertAccountEquals(expectedAccount, returnedAccount);
    }

    @Test
    final void testGetAccountsWithPassword() {
        int foundFull = 0;
        List<Account> returnedAccounts = logic.getAccountsWithPassword(expectedAccount.getPassword());
        for (Account account : returnedAccounts) {
            //all accounts must have the same password
            assertEquals(expectedAccount.getPassword(), account.getPassword());
            //exactly one account must be the same
            if (account.getId().equals(expectedAccount.getId())) {
                assertAccountEquals(expectedAccount, account);
                foundFull++;
            }
        }
        assertEquals(1, foundFull, "if zero means not found, if more than one means duplicate");
    }

    @Test
    final void testGetAccountWith() {
        Account returnedAccount = logic.getAccountWith(expectedAccount.getUser(), expectedAccount.getPassword());
        assertAccountEquals(expectedAccount, returnedAccount);
    }

    @Test
    final void testSearch() {
        int foundFull = 0;
        //search for a substring of one of the fields in the expectedAccount
        String searchString = expectedAccount.getDisplayName().substring(3);
        //in account we only search for display name and user, this is completely based on your design for other entities.
        List<Account> returnedAccounts = logic.search(searchString);
        for (Account account : returnedAccounts) {
            //all accounts must contain the substring
            assertTrue(account.getDisplayName().contains(searchString) || account.getUser().contains(searchString));
            //exactly one account must be the same
            if (account.getId().equals(expectedAccount.getId())) {
                assertAccountEquals(expectedAccount, account);
                foundFull++;
            }
        }
        assertEquals(1, foundFull, "if zero means not found, if more than one means duplicate");
    }

    @Test
    final void testCreateEntityAndAdd() {
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put(AccountLogic.DISPLAY_NAME, new String[]{"Test Create Entity"});
        sampleMap.put(AccountLogic.USER, new String[]{"testCreateAccount"});
        sampleMap.put(AccountLogic.PASSWORD, new String[]{"create"});

        Account returnedAccount = logic.createEntity(sampleMap);
        logic.add(returnedAccount);

        returnedAccount = logic.getAccountWithUser(returnedAccount.getUser());

        assertEquals(sampleMap.get(AccountLogic.DISPLAY_NAME)[0], returnedAccount.getDisplayName());
        assertEquals(sampleMap.get(AccountLogic.USER)[0], returnedAccount.getUser());
        assertEquals(sampleMap.get(AccountLogic.PASSWORD)[0], returnedAccount.getPassword());

        logic.delete(returnedAccount);
    }

    @Test
    final void testCreateEntity() {
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put(AccountLogic.ID, new String[]{Integer.toString(expectedAccount.getId())});
        sampleMap.put(AccountLogic.DISPLAY_NAME, new String[]{expectedAccount.getDisplayName()});
        sampleMap.put(AccountLogic.USER, new String[]{expectedAccount.getUser()});
        sampleMap.put(AccountLogic.PASSWORD, new String[]{expectedAccount.getPassword()});

        Account returnedAccount = logic.createEntity(sampleMap);

        assertAccountEquals(expectedAccount, returnedAccount);
    }

    @Test
    final void testCreateEntityNullAndEmptyValues() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = (Map<String, String[]> map) -> {
            map.clear();
            map.put(AccountLogic.ID, new String[]{Integer.toString(expectedAccount.getId())});
            map.put(AccountLogic.DISPLAY_NAME, new String[]{expectedAccount.getDisplayName()});
            map.put(AccountLogic.USER, new String[]{expectedAccount.getUser()});
            map.put(AccountLogic.PASSWORD, new String[]{expectedAccount.getPassword()});
        };

        //idealy every test should be in its own method
        fillMap.accept(sampleMap);
        sampleMap.replace(AccountLogic.ID, null);
        assertThrows(NullPointerException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(AccountLogic.ID, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(AccountLogic.DISPLAY_NAME, null);
        assertThrows(NullPointerException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(AccountLogic.DISPLAY_NAME, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(AccountLogic.USER, null);
        assertThrows(NullPointerException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(AccountLogic.USER, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(AccountLogic.PASSWORD, null);
        assertThrows(NullPointerException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(AccountLogic.PASSWORD, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(sampleMap));
    }

    @Test
    final void testCreateEntityBadLengthValues() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = (Map<String, String[]> map) -> {
            map.clear();
            map.put(AccountLogic.ID, new String[]{Integer.toString(expectedAccount.getId())});
            map.put(AccountLogic.DISPLAY_NAME, new String[]{expectedAccount.getDisplayName()});
            map.put(AccountLogic.USER, new String[]{expectedAccount.getUser()});
            map.put(AccountLogic.PASSWORD, new String[]{expectedAccount.getPassword()});
        };

        IntFunction<String> generateString = (int length) -> {
            //https://www.baeldung.com/java-random-string#java8-alphabetic
            return new Random().ints('a', 'z' + 1).limit(length)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();
        };

        //idealy every test should be in its own method
        fillMap.accept(sampleMap);
        sampleMap.replace(AccountLogic.ID, new String[]{""});
        assertThrows(NumberFormatException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(AccountLogic.ID, new String[]{"12b"});
        assertThrows(NumberFormatException.class, () -> logic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(AccountLogic.DISPLAY_NAME, new String[]{""});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(AccountLogic.DISPLAY_NAME, new String[]{generateString.apply(46)});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(AccountLogic.USER, new String[]{""});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(AccountLogic.USER, new String[]{generateString.apply(46)});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(AccountLogic.PASSWORD, new String[]{""});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(AccountLogic.PASSWORD, new String[]{generateString.apply(46)});
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
        sampleMap.put(AccountLogic.ID, new String[]{Integer.toString(1)});
        sampleMap.put(AccountLogic.DISPLAY_NAME, new String[]{generateString.apply(1)});
        sampleMap.put(AccountLogic.USER, new String[]{generateString.apply(1)});
        sampleMap.put(AccountLogic.PASSWORD, new String[]{generateString.apply(1)});

        //idealy every test should be in its own method
        Account returnedAccount = logic.createEntity(sampleMap);
        assertEquals(Integer.parseInt(sampleMap.get(AccountLogic.ID)[0]), returnedAccount.getId());
        assertEquals(sampleMap.get(AccountLogic.DISPLAY_NAME)[0], returnedAccount.getDisplayName());
        assertEquals(sampleMap.get(AccountLogic.USER)[0], returnedAccount.getUser());
        assertEquals(sampleMap.get(AccountLogic.PASSWORD)[0], returnedAccount.getPassword());
        
        sampleMap = new HashMap<>();
        sampleMap.put(AccountLogic.ID, new String[]{Integer.toString(1)});
        sampleMap.put(AccountLogic.DISPLAY_NAME, new String[]{generateString.apply(45)});
        sampleMap.put(AccountLogic.USER, new String[]{generateString.apply(45)});
        sampleMap.put(AccountLogic.PASSWORD, new String[]{generateString.apply(45)});

        //idealy every test should be in its own method
        returnedAccount = logic.createEntity(sampleMap);
        assertEquals(Integer.parseInt(sampleMap.get(AccountLogic.ID)[0]), returnedAccount.getId());
        assertEquals(sampleMap.get(AccountLogic.DISPLAY_NAME)[0], returnedAccount.getDisplayName());
        assertEquals(sampleMap.get(AccountLogic.USER)[0], returnedAccount.getUser());
        assertEquals(sampleMap.get(AccountLogic.PASSWORD)[0], returnedAccount.getPassword());
    }

    @Test
    final void testGetColumnNames() {
        List<String> list = logic.getColumnNames();
        assertEquals(Arrays.asList("ID", "Display Name", "User", "Password"), list);
    }

    @Test
    final void testGetColumnCodes() {
        List<String> list = logic.getColumnCodes();
        assertEquals(Arrays.asList(AccountLogic.ID, AccountLogic.DISPLAY_NAME, AccountLogic.USER, AccountLogic.PASSWORD), list);
    }

    @Test
    final void testExtractDataAsList() {
        List<?> list = logic.extractDataAsList(expectedAccount);
        assertEquals(expectedAccount.getId(), list.get(0));
        assertEquals(expectedAccount.getDisplayName(), list.get(1));
        assertEquals(expectedAccount.getUser(), list.get(2));
        assertEquals(expectedAccount.getPassword(), list.get(3));
    }
}
