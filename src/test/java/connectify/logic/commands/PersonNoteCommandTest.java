package connectify.logic.commands;

import static connectify.logic.commands.CommandTestUtil.VALID_NOTE_AMY;
import static connectify.logic.commands.CommandTestUtil.VALID_NOTE_BOB;
import static connectify.logic.commands.CommandTestUtil.assertCommandFailure;
import static connectify.logic.commands.CommandTestUtil.assertCommandSuccess;
import static connectify.logic.commands.CommandTestUtil.showPersonAtIndex;
import static connectify.testutil.TypicalIndexes.INDEX_FIRST_COMPANY;
import static connectify.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static connectify.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static connectify.testutil.TypicalIndexes.INDEX_THIRD_PERSON;
import static connectify.testutil.TypicalPersons.getTypicalAddressBook;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import connectify.commons.core.index.Index;
import connectify.logic.Messages;
import connectify.model.AddressBook;
import connectify.model.Model;
import connectify.model.ModelManager;
import connectify.model.UserPrefs;
import connectify.model.company.Company;
import connectify.model.person.Person;
import connectify.model.person.PersonNote;
import connectify.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for NoteCommand.
 */
public class PersonNoteCommandTest {
    private static final String NOTE_STUB = "Some note";

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_addNote_success() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person editedPerson = new PersonBuilder(firstPerson).withNote(NOTE_STUB).build();

        PersonNoteCommand personNoteCommand = new PersonNoteCommand(INDEX_FIRST_COMPANY, INDEX_FIRST_PERSON,
                new PersonNote(editedPerson.getNote().getContent()));

        String expectedMessage = String.format(PersonNoteCommand.MESSAGE_ADD_NOTE_SUCCESS,
                Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(firstPerson, editedPerson);
        Company expectedCompanyToUpdate = expectedModel.getFilteredCompanyList()
                .get(INDEX_FIRST_COMPANY.getZeroBased());
        Company editedCompany = expectedCompanyToUpdate.setPerson(firstPerson, editedPerson);
        expectedModel.setCompany(expectedCompanyToUpdate, editedCompany);

        assertCommandSuccess(personNoteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_deleteNote_success() {
        Person personWithoutNote = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person editedPersonWithNote = new PersonBuilder(personWithoutNote).withNote(NOTE_STUB).build();
        System.out.println(Messages.format(editedPersonWithNote));

        PersonNoteCommand personNoteCommand = new PersonNoteCommand(INDEX_FIRST_COMPANY, INDEX_FIRST_PERSON,
                new PersonNote("")); // Empty note object to delete existing note

        String expectedMessage = String.format(PersonNoteCommand.MESSAGE_DELETE_NOTE_SUCCESS,
                Messages.format(personWithoutNote));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());

        model.setPerson(personWithoutNote, editedPersonWithNote); // Update current model with person with note

        Company expectedCompanyToUpdate = model.getFilteredCompanyList()
                .get(INDEX_FIRST_COMPANY.getZeroBased());
        Company editedCompany = expectedCompanyToUpdate.setPerson(personWithoutNote, editedPersonWithNote);
        model.setCompany(expectedCompanyToUpdate, editedCompany);

        assertCommandSuccess(personNoteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidPersonIndexFilteredList_failure() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        Index outOfBoundIndex = INDEX_THIRD_PERSON;
        // Ensures that outOfBoundIndex is still in the bounds of the address book list.
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getPersonList().size());

        PersonNoteCommand personNoteCommand = new PersonNoteCommand(INDEX_FIRST_COMPANY, outOfBoundIndex,
                new PersonNote(VALID_NOTE_BOB));

        assertCommandFailure(personNoteCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        final PersonNoteCommand standardCommand = new PersonNoteCommand(INDEX_FIRST_COMPANY, INDEX_FIRST_PERSON,
                new PersonNote(VALID_NOTE_AMY));
        // Same values -> returns true
        PersonNoteCommand commandWithSameValues = new PersonNoteCommand(INDEX_FIRST_COMPANY, INDEX_FIRST_PERSON,
                new PersonNote(VALID_NOTE_AMY));
        assertTrue(standardCommand.equals(commandWithSameValues));

        // Same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // Null -> returns false
        assertFalse(standardCommand.equals(null));

        // Different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // Different index -> returns false
        assertFalse(standardCommand.equals(new PersonNoteCommand(INDEX_FIRST_COMPANY, INDEX_SECOND_PERSON,
                new PersonNote(VALID_NOTE_AMY))));
        // Different note -> returns false
        assertFalse(standardCommand.equals(new PersonNoteCommand(INDEX_FIRST_COMPANY, INDEX_SECOND_PERSON,
                new PersonNote(VALID_NOTE_BOB))));
    }
}
