package connectify.logic;

import static connectify.logic.Messages.MESSAGE_INVALID_COMPANY_DISPLAYED_INDEX;
import static connectify.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static connectify.testutil.TypicalIndexes.INDEX_FIRST_COMPANY;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import connectify.logic.commands.AddPersonCommand;
import connectify.logic.commands.CommandResult;
import connectify.logic.commands.CommandTestUtil;
import connectify.logic.commands.ListPeopleCommand;
import connectify.logic.commands.exceptions.CommandException;
import connectify.logic.parser.exceptions.ParseException;
import connectify.model.InvalidEntityException;
import connectify.model.Model;
import connectify.model.ModelManager;
import connectify.model.ReadOnlyAddressBook;
import connectify.model.UserPrefs;
import connectify.model.company.Company;
import connectify.model.person.Person;
import connectify.storage.JsonAddressBookStorage;
import connectify.storage.JsonUserPrefsStorage;
import connectify.storage.StorageManager;
import connectify.testutil.Assert;
import connectify.testutil.PersonBuilder;
import connectify.testutil.TypicalCompanies;
import connectify.testutil.TypicalPersons;
public class LogicManagerTest {
    private static final IOException DUMMY_IO_EXCEPTION = new IOException("dummy IO exception");
    private static final IOException DUMMY_AD_EXCEPTION = new AccessDeniedException("dummy access denied exception");

    @TempDir
    public Path temporaryFolder;

    private Model model = new ModelManager();
    private Logic logic;

    @BeforeEach
    public void setUp() {
        JsonAddressBookStorage addressBookStorage =
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage);
        logic = new LogicManager(model, storage);
    }

    @Test
    public void execute_invalidCommandFormat_throwsParseException() {
        String invalidCommand = "uicfhmowqewca";
        assertParseException(invalidCommand, MESSAGE_UNKNOWN_COMMAND);
    }

    @Test
    public void execute_commandExecutionError_throwsCommandException() {
        String deletePersonCommand = "deletePerson 1 9";
        assertCommandException(deletePersonCommand, MESSAGE_INVALID_COMPANY_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validCommand_success() throws Exception {
        String listCommand = ListPeopleCommand.COMMAND_WORD;
        assertCommandSuccess(listCommand, ListPeopleCommand.EMPTY_LIST_MESSAGE, model);
    }

    @Test
    public void getCurrentEntity_success() {
        try {
            logic.setCurrEntity("all");
            assertEquals(logic.getCurrEntity(), "all");
        } catch (InvalidEntityException e) {
            throw new AssertionError("No exception should be thrown");
        }
        try {
            logic.setCurrEntity("people");
            assertEquals(logic.getCurrEntity(), "people");
        } catch (InvalidEntityException e) {
            throw new AssertionError("No exception should be thrown");
        }
        try {
            logic.setCurrEntity("companies");
            assertEquals(logic.getCurrEntity(), "companies");
        } catch (InvalidEntityException e) {
            throw new AssertionError("No exception should be thrown");
        }
    }

    @Test
    public void invalidEntity_throwsInvalidEntityException() {
        Assert.assertThrows(InvalidEntityException.class, () -> logic.setCurrEntity("invalid"));
    }

    @Test
    public void execute_storageThrowsIoException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_IO_EXCEPTION, String.format(
                LogicManager.FILE_OPS_ERROR_FORMAT, DUMMY_IO_EXCEPTION.getMessage()));
    }

    @Test
    public void execute_storageThrowsAdException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_AD_EXCEPTION, String.format(
                LogicManager.FILE_OPS_PERMISSION_ERROR_FORMAT, DUMMY_AD_EXCEPTION.getMessage()));
    }

    @Test
    public void getFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        Assert.assertThrows(UnsupportedOperationException.class, () -> logic.getFilteredPersonList().remove(0));
    }

    @Test
    public void getFilteredCompanyList_modifyList_throwsUnsupportedOperationException() {
        Assert.assertThrows(UnsupportedOperationException.class, () -> logic.getFilteredCompanyList().remove(0));

    }


    @Test
    public void getFilteredEntityList_modifyList_throwsUnsupportedOperationException() {
        Assert.assertThrows(UnsupportedOperationException.class, () -> logic.getFilteredEntityList().remove(0));
    }


    /**
     * Executes the command and confirms that
     * - no exceptions are thrown <br>
     * - the feedback message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandSuccess(String inputCommand, String expectedMessage,
            Model expectedModel) throws CommandException, ParseException {
        CommandResult result = logic.execute(inputCommand);
        assertEquals(expectedMessage, result.getFeedbackToUser());
        assertEquals(expectedModel, model);
    }

    /**
     * Executes the command, confirms that a ParseException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertParseException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, ParseException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that a CommandException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, CommandException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that the exception is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage) {
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        assertCommandFailure(inputCommand, expectedException, expectedMessage, expectedModel);
    }

    /**
     * Executes the command and confirms that
     * - the {@code expectedException} is thrown <br>
     * - the resulting error message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandSuccess(String, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage, Model expectedModel) {
        Assert.assertThrows(expectedException, expectedMessage, () -> logic.execute(inputCommand));
        assertEquals(expectedModel, model);
    }

    /**
     * Tests the Logic component's handling of an {@code IOException} thrown by the Storage component.
     *
     * @param e the exception to be thrown by the Storage component
     * @param expectedMessage the message expected inside exception thrown by the Logic component
     */
    private void assertCommandFailureForExceptionFromStorage(IOException e, String expectedMessage) {
        Path prefPath = temporaryFolder.resolve("ExceptionUserPrefs.json");

        // Inject LogicManager with an AddressBookStorage that throws the IOException e when saving
        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(prefPath) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook, Path filePath)
                    throws IOException {
                throw e;
            }
        };

        JsonUserPrefsStorage userPrefsStorage =
                new JsonUserPrefsStorage(temporaryFolder.resolve("ExceptionUserPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage);

        model.addCompany(TypicalCompanies.COMPANY_1);
        logic = new LogicManager(model, storage);

        // Triggers the saveAddressBook method by executing an add command
        String addPersonCommand = AddPersonCommand.COMMAND_WORD
                + CommandTestUtil.NAME_DESC_AMY + CommandTestUtil.PHONE_DESC_AMY
                + CommandTestUtil.EMAIL_DESC_AMY + CommandTestUtil.ADDRESS_DESC_AMY
                + CommandTestUtil.COMPANY_DESC_AMY + CommandTestUtil.PRIORITY_DESC_AMY;
        Person expectedPerson = new PersonBuilder(TypicalPersons.AMY).withTags().build();

        ModelManager expectedModel = new ModelManager();
        expectedModel.addCompany(TypicalCompanies.COMPANY_1);

        List<Company> companies = expectedModel.getFilteredCompanyList();
        Company targetCompany = companies.get(INDEX_FIRST_COMPANY.getZeroBased());
        Company editedCompany = targetCompany.addPersonToCompany(expectedPerson);

        expectedModel.setCompany(targetCompany, editedCompany);
        expectedPerson.setParentCompany(editedCompany);
        expectedModel.addPerson(expectedPerson);

        assertCommandFailure(addPersonCommand, CommandException.class, expectedMessage, expectedModel);
    }
}
