package connectify.logic.commands;

import static connectify.commons.util.CollectionUtil.requireAllNonNull;
import static connectify.logic.parser.CliSyntax.PREFIX_NOTE;
import static connectify.model.Model.PREDICATE_SHOW_ALL_PERSONS;
import static java.util.Objects.requireNonNull;

import java.util.List;

import connectify.commons.core.index.Index;
import connectify.logic.Messages;
import connectify.logic.commands.exceptions.CommandException;
import connectify.model.Model;
import connectify.model.company.Company;
import connectify.model.person.Person;
import connectify.model.person.PersonList;
import connectify.model.person.PersonNote;

/**
 * Changes the note of an existing person in the address book.
 */
public class PersonNoteCommand extends Command {
    public static final String COMMAND_WORD = "notePerson";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Changes the note of the person identified "
            + "by the index number used in the displayed person list. "
            + "Existing note will be overwritten by the input.\n"
            + "Parameters: COMPANY_INDEX PERSON_INDEX (must be a positive integer) "
            + "[" + PREFIX_NOTE + "NOTE]\n"
            + "Example: " + COMMAND_WORD + " 1 1 " + PREFIX_NOTE + "Likes to swim.";

    public static final String MESSAGE_ADD_NOTE_SUCCESS = "Added note to Person: %1$s";
    public static final String MESSAGE_DELETE_NOTE_SUCCESS = "Removed note from Person: %1$s";

    private final Index companyIndex;
    private final Index personIndex;
    private final PersonNote note;

    /**
     */
    public PersonNoteCommand(Index companyIndex, Index personIndex, PersonNote note) {
        requireAllNonNull(companyIndex, personIndex, note);
        this.companyIndex = companyIndex;
        this.personIndex = personIndex;
        this.note = note;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        List<Company> companyList = model.getFilteredCompanyList();

        if (companyIndex.getZeroBased() >= companyList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_COMPANY_DISPLAYED_INDEX);
        }

        Company companyToUpdate = companyList.get(companyIndex.getZeroBased());
        PersonList companyPersonsList = companyToUpdate.getPersonList();

        if (personIndex.getZeroBased() >= companyPersonsList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToEdit = companyPersonsList.get(personIndex.getZeroBased());
        Person editedPerson = new Person(personToEdit.getName(), personToEdit.getPhone(), personToEdit.getEmail(),
                personToEdit.getAddress(), personToEdit.getTags(), note, personToEdit.getPriority(),
                personToEdit.getParentCompany());

        Company editedCompany = companyToUpdate.setPerson(personToEdit, editedPerson);
        model.setCompany(companyToUpdate, editedCompany);

        model.setPerson(personToEdit, editedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        return new CommandResult(generateSuccessMessage(editedPerson));
    }

    /**
     * Generates a command execution success message based on whether the note is added to or removed from
     * {@code personToEdit}.
     */
    private String generateSuccessMessage(Person personToEdit) {
        String message = !note.getContent().isEmpty() ? MESSAGE_ADD_NOTE_SUCCESS : MESSAGE_DELETE_NOTE_SUCCESS;
        return String.format(message, Messages.format(personToEdit));
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof PersonNoteCommand)) {
            return false;
        }

        // state check
        PersonNoteCommand e = (PersonNoteCommand) other;
        return companyIndex.equals(e.companyIndex)
                && personIndex.equals(e.personIndex)
                && note.equals(e.note);
    }
}
