package connectify.logic.parser;

import static connectify.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Set;
import java.util.stream.Stream;

import connectify.commons.core.index.Index;
import connectify.logic.commands.AddPersonCommand;
import connectify.logic.parser.exceptions.ParseException;
import connectify.model.person.Person;
import connectify.model.person.PersonAddress;
import connectify.model.person.PersonEmail;
import connectify.model.person.PersonName;
import connectify.model.person.PersonNote;
import connectify.model.person.PersonPhone;
import connectify.model.person.PersonPriority;
import connectify.model.tag.Tag;

/**
 * Parses input arguments and creates a new AddPersonCommand object
 */
public class AddPersonCommandParser implements Parser<AddPersonCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the AddPersonCommand
     * and returns an AddPersonCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public AddPersonCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, CliSyntax.PREFIX_NAME, CliSyntax.PREFIX_PHONE, CliSyntax.PREFIX_EMAIL,
                        CliSyntax.PREFIX_ADDRESS, CliSyntax.PREFIX_TAG,
                        CliSyntax.PREFIX_COMPANY, CliSyntax.PREFIX_PRIORITY);

        if (!arePrefixesPresent(argMultimap, CliSyntax.PREFIX_NAME, CliSyntax.PREFIX_ADDRESS,
                CliSyntax.PREFIX_PHONE, CliSyntax.PREFIX_EMAIL, CliSyntax.PREFIX_PRIORITY, CliSyntax.PREFIX_COMPANY)
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddPersonCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(CliSyntax.PREFIX_NAME, CliSyntax.PREFIX_PHONE, CliSyntax.PREFIX_EMAIL,
                CliSyntax.PREFIX_ADDRESS, CliSyntax.PREFIX_COMPANY, CliSyntax.PREFIX_PRIORITY);
        PersonName name = ParserPersonUtil.parseName(argMultimap.getValue(CliSyntax.PREFIX_NAME).get());
        PersonPhone personPhone = ParserPersonUtil.parsePhone(argMultimap.getValue(CliSyntax.PREFIX_PHONE).get());
        PersonEmail personEmail = ParserPersonUtil.parseEmail(argMultimap.getValue(CliSyntax.PREFIX_EMAIL).get());
        PersonAddress personAddress = ParserPersonUtil.parseAddress(argMultimap
                .getValue(CliSyntax.PREFIX_ADDRESS).get());
        PersonNote note = new PersonNote("");
        Set<Tag> tagList = ParserPersonUtil.parseTags(argMultimap.getAllValues(CliSyntax.PREFIX_TAG));

        // Defaults to first company if no company index is provided
        Index companyIndex = ParserCompanyUtil.parseIndex(argMultimap.getValue(CliSyntax.PREFIX_COMPANY).get());
        PersonPriority priority = ParserPersonUtil
            .parsePersonPriority(argMultimap.getValue(CliSyntax.PREFIX_PRIORITY).get());
        Person person = new Person(name, personPhone, personEmail, personAddress, tagList, note, priority);

        return new AddPersonCommand(person, companyIndex);
    }

    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }

}
