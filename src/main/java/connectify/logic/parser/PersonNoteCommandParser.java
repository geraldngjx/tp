package connectify.logic.parser;

import static connectify.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static connectify.logic.parser.CliSyntax.PREFIX_NOTE;
import static java.util.Objects.requireNonNull;

import connectify.commons.core.index.Index;
import connectify.commons.exceptions.IllegalValueException;
import connectify.logic.commands.PersonNoteCommand;
import connectify.logic.parser.exceptions.ParseException;
import connectify.model.person.PersonNote;

/**
 * Parses input arguments and creates a new {@code NoteCommand} object
 */
public class PersonNoteCommandParser implements Parser<PersonNoteCommand> {
    /**
     * Parses the given {@code String} of arguments in the context of the {@code NoteCommand}
     * and returns a {@code NoteCommand} object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public PersonNoteCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_NOTE);

        Index index;
        try {
            index = ParserPersonUtil.parseIndex(argMultimap.getPreamble());
        } catch (IllegalValueException ive) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, PersonNoteCommand.MESSAGE_USAGE), ive);
        }

        String note = argMultimap.getValue(PREFIX_NOTE).orElse("");

        return new PersonNoteCommand(index, new PersonNote(note));
    }
}