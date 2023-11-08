package connectify.logic.parser;

import static connectify.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static connectify.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static connectify.logic.parser.CliSyntax.PREFIX_COMPANY;
import static connectify.logic.parser.CliSyntax.PREFIX_NOTE;
import static connectify.logic.parser.CliSyntax.PREFIX_PRIORITY;
import static connectify.testutil.Assert.assertThrows;
import static connectify.testutil.TypicalIndexes.INDEX_FIRST_COMPANY;
import static connectify.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import connectify.logic.commands.AddCompanyCommand;
import connectify.logic.commands.AddPersonCommand;
import connectify.logic.commands.ClearCommand;
import connectify.logic.commands.DeleteCompanyCommand;
import connectify.logic.commands.DeletePersonCommand;
import connectify.logic.commands.EditCompanyCommand;
import connectify.logic.commands.EditCompanyCommand.EditCompanyDescriptor;
import connectify.logic.commands.EditPersonCommand;
import connectify.logic.commands.EditPersonCommand.EditPersonDescriptor;
import connectify.logic.commands.ExitCommand;
import connectify.logic.commands.FindAllCommand;
import connectify.logic.commands.FindCompaniesCommand;
import connectify.logic.commands.FindPeopleCommand;
import connectify.logic.commands.HelpCommand;
import connectify.logic.commands.ListAllCommand;
import connectify.logic.commands.ListCompaniesCommand;
import connectify.logic.commands.ListPeopleCommand;
import connectify.logic.commands.PersonNoteCommand;
import connectify.logic.commands.RankPersonCommand;
import connectify.logic.commands.ShareCompanyCommand;
import connectify.logic.commands.SharePersonCommand;
import connectify.logic.parser.exceptions.ParseException;
import connectify.model.EntityNameContainsKeywordsPredicate;
import connectify.model.company.Company;
import connectify.model.company.CompanyNameContainsKeywordsPredicate;
import connectify.model.person.NameContainsKeywordsPredicate;
import connectify.model.person.Person;
import connectify.model.person.PersonNote;
import connectify.testutil.CompanyBuilder;
import connectify.testutil.CompanyUtil;
import connectify.testutil.EditCompanyDescriptorBuilder;
import connectify.testutil.EditPersonDescriptorBuilder;
import connectify.testutil.PersonBuilder;
import connectify.testutil.PersonUtil;

public class ConnectifyParserTest {

    private final ConnectifyParser parser = new ConnectifyParser();

    @Test
    public void parseCommand_add() throws Exception {
        Person person = new PersonBuilder().build();
        AddPersonCommand command = (AddPersonCommand) parser.parseCommand(PersonUtil.getAddPersonCommand(person)
            + PREFIX_COMPANY + INDEX_FIRST_COMPANY.getOneBased());
        assertEquals(new AddPersonCommand(person, INDEX_FIRST_COMPANY), command);
    }

    @Test
    public void parseCommand_addCompany() throws Exception {
        Company company = new CompanyBuilder().build();
        AddCompanyCommand command = (AddCompanyCommand) parser.parseCommand(CompanyUtil.getAddCommand(company));
        assertEquals(new AddCompanyCommand(company), command);
    }

    @Test
    public void parseCommand_clear() throws Exception {
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD) instanceof ClearCommand);
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD + " 3") instanceof ClearCommand);
    }

    @Test
    public void parseCommand_deleteCompany() throws Exception {
        DeleteCompanyCommand command = (DeleteCompanyCommand) parser.parseCommand(
                DeleteCompanyCommand.COMMAND_WORD + " " + INDEX_FIRST_COMPANY.getOneBased());
        assertEquals(new DeleteCompanyCommand(INDEX_FIRST_COMPANY), command);
    }

    @Test
    public void parseCommand_delete() throws Exception {
        DeletePersonCommand command = (DeletePersonCommand) parser.parseCommand(
                DeletePersonCommand.COMMAND_WORD + " " + INDEX_FIRST_COMPANY.getOneBased()
                        +
                        " " + INDEX_FIRST_PERSON.getOneBased());
        assertEquals(new DeletePersonCommand(INDEX_FIRST_COMPANY, INDEX_FIRST_PERSON), command);
    }

    @Test
    public void parseCommand_editPerson() throws Exception {
        Person person = new PersonBuilder().build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(person).build();
        EditPersonCommand command = (EditPersonCommand) parser.parseCommand(
                EditPersonCommand.COMMAND_WORD + " "
                + INDEX_FIRST_PERSON.getOneBased() + " " + PREFIX_COMPANY
                + INDEX_FIRST_COMPANY.getOneBased() + " " + PREFIX_PRIORITY + "1 "
                + PersonUtil.getEditPersonDescriptorDetails(descriptor));
        assertEquals(new EditPersonCommand(INDEX_FIRST_PERSON, INDEX_FIRST_COMPANY, descriptor), command);
    }

    @Test
    public void parseCommand_editCompany() throws Exception {
        Company company = new CompanyBuilder().build();
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder(company).build();
        EditCompanyCommand command = (EditCompanyCommand) parser.parseCommand(
                EditCompanyCommand.COMMAND_WORD + " "
                        + INDEX_FIRST_COMPANY.getOneBased() + " "
                        + CompanyUtil.getEditCompanyDescriptorDetails(descriptor));
        assertEquals(new EditCompanyCommand(INDEX_FIRST_COMPANY, descriptor), command);
    }

    @Test
    public void parseCommand_exit() throws Exception {
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD) instanceof ExitCommand);
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD + " 3") instanceof ExitCommand);
    }

    @Test
    public void parseCommand_findPeople() throws Exception {
        List<String> keywords = Arrays.asList("foo", "bar", "baz");
        FindPeopleCommand command = (FindPeopleCommand) parser.parseCommand(
                FindPeopleCommand.COMMAND_WORD
                        +
                        " " + keywords.stream().collect(Collectors.joining(" ")));
        assertEquals(new FindPeopleCommand(new NameContainsKeywordsPredicate(keywords)), command);
    }

    @Test
    public void parseCommand_findAll() throws Exception {
        List<String> keywords = Arrays.asList("foo", "bar", "baz");
        FindAllCommand command = (FindAllCommand) parser.parseCommand(
                FindAllCommand.COMMAND_WORD
                        +
                        " " + keywords.stream().collect(Collectors.joining(" ")));
        assertEquals(new FindAllCommand(new EntityNameContainsKeywordsPredicate(keywords)), command);
    }

    @Test
    public void parseCommand_findCompanies() throws Exception {
        List<String> keywords = Arrays.asList("foo", "bar", "baz");
        FindCompaniesCommand command = (FindCompaniesCommand) parser.parseCommand(
                FindCompaniesCommand.COMMAND_WORD
                        +
                        " " + keywords.stream().collect(Collectors.joining(" ")));
        assertEquals(new FindCompaniesCommand(new CompanyNameContainsKeywordsPredicate(keywords)), command);
    }

    @Test
    public void parseCommand_shareCompany() throws Exception {
        ShareCompanyCommand command = (ShareCompanyCommand) parser.parseCommand(
                ShareCompanyCommand.COMMAND_WORD + " " + INDEX_FIRST_COMPANY.getOneBased());
        assertEquals(new ShareCompanyCommand(INDEX_FIRST_COMPANY), command);
    }

    @Test
    public void parseCommand_sharePerson() throws Exception {
        SharePersonCommand command = (SharePersonCommand) parser.parseCommand(
                SharePersonCommand.COMMAND_WORD + " " + INDEX_FIRST_COMPANY.getOneBased()
                        +
                        " " + INDEX_FIRST_PERSON.getOneBased());
        assertEquals(new SharePersonCommand(INDEX_FIRST_COMPANY, INDEX_FIRST_PERSON), command);
    }

    @Test
    public void parseCommand_help() throws Exception {
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD) instanceof HelpCommand);
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD + " 3") instanceof HelpCommand);
    }

    @Test
    public void parseCommand_listCompanies() throws Exception {
        assertTrue(parser.parseCommand(ListCompaniesCommand.COMMAND_WORD) instanceof ListCompaniesCommand);
        assertTrue(parser.parseCommand(ListCompaniesCommand.COMMAND_WORD + " 3") instanceof ListCompaniesCommand);
    }

    @Test
    public void parseCommand_listPeople() throws Exception {
        assertTrue(parser.parseCommand(ListPeopleCommand.COMMAND_WORD) instanceof ListPeopleCommand);
        assertTrue(parser.parseCommand(ListPeopleCommand.COMMAND_WORD + " 3") instanceof ListPeopleCommand);
    }

    @Test
    public void parseCommand_personNote() throws Exception {
        final PersonNote note = new PersonNote("Some note");
        PersonNoteCommand command = (PersonNoteCommand) parser.parseCommand(PersonNoteCommand.COMMAND_WORD + " "
                + INDEX_FIRST_COMPANY.getOneBased() + " " + INDEX_FIRST_PERSON.getOneBased() + " "
                    + PREFIX_NOTE + note.getContent());
        assertEquals(new PersonNoteCommand(INDEX_FIRST_COMPANY, INDEX_FIRST_PERSON, note), command);
    }

    @Test
    public void parseCommand_listAllEntities() throws Exception {
        assertTrue(parser.parseCommand(ListAllCommand.COMMAND_WORD) instanceof ListAllCommand);
        assertTrue(parser.parseCommand(ListAllCommand.COMMAND_WORD + " 3") instanceof ListAllCommand);
    }

    @Test
    public void parseCommand_rankPerson() throws Exception {
        assertTrue(parser.parseCommand(RankPersonCommand.COMMAND_WORD) instanceof RankPersonCommand);
        assertTrue(parser.parseCommand(RankPersonCommand.COMMAND_WORD + " 3") instanceof RankPersonCommand);
    }

    @Test
    public void parseCommand_unrecognisedInput_throwsParseException() {
        assertThrows(ParseException.class, String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE), ()
            -> parser.parseCommand(""));
    }

    @Test
    public void parseCommand_unknownCommand_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand("unknownCommand"));
    }
}
