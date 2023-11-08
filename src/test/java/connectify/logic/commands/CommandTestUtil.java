package connectify.logic.commands;

import static connectify.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static connectify.logic.parser.CliSyntax.PREFIX_COMPANY;
import static connectify.logic.parser.CliSyntax.PREFIX_EMAIL;
import static connectify.logic.parser.CliSyntax.PREFIX_NAME;
import static connectify.logic.parser.CliSyntax.PREFIX_NOTE;
import static connectify.logic.parser.CliSyntax.PREFIX_PHONE;
import static connectify.logic.parser.CliSyntax.PREFIX_PRIORITY;
import static connectify.logic.parser.CliSyntax.PREFIX_TAG;
import static connectify.testutil.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import connectify.commons.core.index.Index;
import connectify.logic.commands.exceptions.CommandException;
import connectify.model.AddressBook;
import connectify.model.Model;
import connectify.model.company.Company;
import connectify.model.company.CompanyNameContainsKeywordsPredicate;
import connectify.model.person.NameContainsKeywordsPredicate;
import connectify.model.person.Person;
import connectify.testutil.EditCompanyDescriptorBuilder;
import connectify.testutil.EditPersonDescriptorBuilder;

/**
 * Contains helper methods for testing commands.
 */
public class CommandTestUtil {

    public static final String VALID_NAME_AMY = "Amy Bee";
    public static final String VALID_NAME_BOB = "Bob Choo";
    public static final String VALID_PHONE_AMY = "11111111";
    public static final String VALID_PHONE_BOB = "22222222";
    public static final String VALID_EMAIL_AMY = "amy@example.com";
    public static final String VALID_EMAIL_BOB = "bob@example.com";
    public static final String VALID_ADDRESS_AMY = "Block 312, Amy Street 1";
    public static final String VALID_ADDRESS_BOB = "Block 123, Bobby Street 3";
    public static final String VALID_NOTE_AMY = "Managed to make a good impression on her at the 2023"
            + "mid-year job fair";
    public static final String VALID_NOTE_BOB = "A HR manager that appreciates a good sense of humour";
    public static final String VALID_TAG_HUSBAND = "husband";
    public static final String VALID_TAG_FRIEND = "friend";

    public static final String VALID_PRIORITY_AMY = "10";
    public static final String VALID_PRIORITY_BOB = "5";

    public static final String NAME_DESC_AMY = " " + PREFIX_NAME + VALID_NAME_AMY;
    public static final String NAME_DESC_BOB = " " + PREFIX_NAME + VALID_NAME_BOB;
    public static final String PHONE_DESC_AMY = " " + PREFIX_PHONE + VALID_PHONE_AMY;
    public static final String PHONE_DESC_BOB = " " + PREFIX_PHONE + VALID_PHONE_BOB;
    public static final String EMAIL_DESC_AMY = " " + PREFIX_EMAIL + VALID_EMAIL_AMY;
    public static final String EMAIL_DESC_BOB = " " + PREFIX_EMAIL + VALID_EMAIL_BOB;
    public static final String ADDRESS_DESC_AMY = " " + PREFIX_ADDRESS + VALID_ADDRESS_AMY;
    public static final String ADDRESS_DESC_BOB = " " + PREFIX_ADDRESS + VALID_ADDRESS_BOB;

    public static final String PRIORITY_DESC_AMY = " " + PREFIX_PRIORITY + VALID_PRIORITY_AMY;
    public static final String PRIORITY_DESC_BOB = " " + PREFIX_PRIORITY + VALID_PRIORITY_BOB;

    public static final String COMPANY_DESC_AMY = " " + PREFIX_COMPANY + "1";
    public static final String COMPANY_DESC_BOB = " " + PREFIX_COMPANY + "2";
    public static final String TAG_DESC_FRIEND = " " + PREFIX_TAG + VALID_TAG_FRIEND;
    public static final String TAG_DESC_HUSBAND = " " + PREFIX_TAG + VALID_TAG_HUSBAND;

    public static final String NOTE_DESC_AMY = " " + PREFIX_NOTE + VALID_NOTE_AMY;

    public static final String INVALID_NAME_DESC = " " + PREFIX_NAME + "James&"; // '&' not allowed in names
    public static final String INVALID_PHONE_DESC = " " + PREFIX_PHONE + "911a"; // 'a' not allowed in phones
    public static final String INVALID_EMAIL_DESC = " " + PREFIX_EMAIL + "bob!yahoo"; // missing '@' symbol
    public static final String INVALID_ADDRESS_DESC = " " + PREFIX_ADDRESS; // empty string not allowed for addresses
    public static final String INVALID_TAG_DESC = " " + PREFIX_TAG + "hubby*"; // '*' not allowed in tags

    public static final String INVALID_PRIORITY_DESC = " " + PREFIX_PRIORITY + "abc"; // priority must be an integer


    public static final String VALID_COMPANY_NAME_A = "Company A";
    public static final String VALID_COMPANY_NAME_B = "Company B";
    public static final String VALID_COMPANY_PHONE_A = "11111111";
    public static final String VALID_COMPANY_PHONE_B = "22222222";
    public static final String VALID_COMPANY_EMAIL_A = "a@example.com";
    public static final String VALID_COMPANY_EMAIL_B = "b@example.com";
    public static final String VALID_COMPANY_ADDRESS_A = "Block 101, Company A Street 1";
    public static final String VALID_COMPANY_ADDRESS_B = "Block 202, Company B Street 2";
    public static final String VALID_COMPANY_INDUSTRY_A = "IT";
    public static final String VALID_COMPANY_INDUSTRY_B = "Finance";
    public static final String VALID_COMPANY_LOCATION_A = "Singapore";
    public static final String VALID_COMPANY_LOCATION_B = "Malaysia";
    public static final String VALID_COMPANY_WEBSITE_A = "www.companya.com";
    public static final String VALID_COMPANY_WEBSITE_B = "www.companyb.com";
    public static final String VALID_COMPANY_DESCRIPTION_A = "A tech company";
    public static final String VALID_COMPANY_DESCRIPTION_B = "A financial institution";
    public static final String VALID_COMPANY_NOTE_A = "A note about Company A";
    public static final String VALID_COMPANY_NOTE_B = "A note about Company B";
    public static final String NAME_DESC_A = " " + PREFIX_NAME + VALID_COMPANY_NAME_A;
    public static final String PREAMBLE_WHITESPACE = "\t  \r  \n";
    public static final String PREAMBLE_NON_EMPTY = "NonEmptyPreamble";

    public static final EditPersonCommand.EditPersonDescriptor DESC_AMY;
    public static final EditPersonCommand.EditPersonDescriptor DESC_BOB;

    static {
        DESC_AMY = new EditPersonDescriptorBuilder().withName(VALID_NAME_AMY)
                .withPhone(VALID_PHONE_AMY).withEmail(VALID_EMAIL_AMY).withAddress(VALID_ADDRESS_AMY)
                .withTags(VALID_TAG_FRIEND).build();
        DESC_BOB = new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB)
                .withPhone(VALID_PHONE_BOB).withEmail(VALID_EMAIL_BOB).withAddress(VALID_ADDRESS_BOB)
                .withTags(VALID_TAG_HUSBAND, VALID_TAG_FRIEND).build();
    }

    public static final EditCompanyCommand.EditCompanyDescriptor DESC_COMPANY_A;
    public static final EditCompanyCommand.EditCompanyDescriptor DESC_COMPANY_B;
    public static final EditCompanyCommand.EditCompanyDescriptor DESC_EMPTY_COMPANY;

    static {
        DESC_COMPANY_A = new EditCompanyDescriptorBuilder()
                .withName(VALID_COMPANY_NAME_A)
                .withPhone(VALID_COMPANY_PHONE_A)
                .withEmail(VALID_COMPANY_EMAIL_A)
                .withAddress(VALID_COMPANY_ADDRESS_A)
                .withIndustry(VALID_COMPANY_INDUSTRY_A)
                .withWebsite(VALID_COMPANY_WEBSITE_A)
                .withDescription(VALID_COMPANY_DESCRIPTION_A)
                .withLocation(VALID_COMPANY_LOCATION_A)
                .build();

        DESC_COMPANY_B = new EditCompanyDescriptorBuilder()
                .withName(VALID_COMPANY_NAME_B)
                .withPhone(VALID_COMPANY_PHONE_B)
                .withEmail(VALID_COMPANY_EMAIL_B)
                .withAddress(VALID_COMPANY_ADDRESS_B)
                .withIndustry(VALID_COMPANY_INDUSTRY_B)
                .withWebsite(VALID_COMPANY_WEBSITE_B)
                .withDescription(VALID_COMPANY_DESCRIPTION_B)
                .withLocation(VALID_COMPANY_LOCATION_B)
                .build();

        DESC_EMPTY_COMPANY = new EditCompanyDescriptorBuilder().build();
    }

    /**
     * Executes the given {@code command}, confirms that <br>
     * - the returned {@link CommandResult} matches {@code expectedCommandResult} <br>
     * - the {@code actualModel} matches {@code expectedModel}
     */
    public static void assertCommandSuccess(Command command, Model actualModel, CommandResult expectedCommandResult,
            Model expectedModel) {
        try {
            CommandResult result = command.execute(actualModel);
            assertEquals(expectedCommandResult, result);
            assertEquals(expectedModel, actualModel);
        } catch (CommandException ce) {
            throw new AssertionError("Execution of command should not fail.", ce);
        }
    }

    /**
     * Convenience wrapper to {@link #assertCommandSuccess(Command, Model, CommandResult, Model)}
     * that takes a string {@code expectedMessage}.
     */
    public static void assertCommandSuccess(Command command, Model actualModel, String expectedMessage,
            Model expectedModel) {
        CommandResult expectedCommandResult = new CommandResult(expectedMessage);
        assertCommandSuccess(command, actualModel, expectedCommandResult, expectedModel);
    }

    /**
     * Executes the given {@code command}, confirms that <br>
     * - a {@code CommandException} is thrown <br>
     * - the CommandException message matches {@code expectedMessage} <br>
     * - the address book, filtered person list and selected person in {@code actualModel} remain unchanged
     */
    public static void assertCommandFailure(Command command, Model actualModel, String expectedMessage) {
        // we are unable to defensively copy the model for comparison later, so we can
        // only do so by copying its components.
        AddressBook expectedAddressBook = new AddressBook(actualModel.getAddressBook());
        List<Person> expectedFilteredList = new ArrayList<>(actualModel.getFilteredPersonList());

        assertThrows(CommandException.class, expectedMessage, () -> command.execute(actualModel));
        assertEquals(expectedAddressBook, actualModel.getAddressBook());
        assertEquals(expectedFilteredList, actualModel.getFilteredPersonList());
    }
    /**
     * Updates {@code model}'s filtered list to show only the person at the given {@code targetIndex} in the
     * {@code model}'s address book.
     */
    public static void showPersonAtIndex(Model model, Index targetIndex) {
        assertTrue(targetIndex.getZeroBased() < model.getFilteredPersonList().size());

        Person person = model.getFilteredPersonList().get(targetIndex.getZeroBased());
        final String[] splitName = person.getName().fullName.split("\\s+");
        model.updateFilteredPersonList(new NameContainsKeywordsPredicate(Arrays.asList(splitName[0])));

        assertEquals(1, model.getFilteredPersonList().size());
    }

    /**
     * Updates {@code model}'s filtered list to show only the company at the given {@code targetIndex} in the
     * {@code model}'s address book.
     */
    public static void showCompanyAtIndex(Model model, Index targetIndex) {
        assertTrue(targetIndex.getZeroBased() < model.getFilteredCompanyList().size());

        Company company = model.getFilteredCompanyList().get(targetIndex.getZeroBased());
        final String[] splitName = company.getName().fullName.split("\\s+");
        model.updateFilteredCompanyList(new CompanyNameContainsKeywordsPredicate(Arrays.asList(splitName[0])));

        assertEquals(1, model.getFilteredCompanyList().size());
    }

}
