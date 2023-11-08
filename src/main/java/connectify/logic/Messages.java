package connectify.logic;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import connectify.logic.parser.Prefix;
import connectify.model.company.Company;
import connectify.model.person.Person;

/**
 * Container for user visible messages.
 */
public class Messages {

    public static final String MESSAGE_UNKNOWN_COMMAND = "Unknown command";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT = "Invalid command format! \n%1$s";
    public static final String MESSAGE_INVALID_PERSON_DISPLAYED_INDEX = "The person index provided is invalid.";
    public static final String MESSAGE_INVALID_COMPANY_DISPLAYED_INDEX = "The company index provided is invalid.";
    public static final String MESSAGE_PERSONS_LISTED_OVERVIEW = "%1$d persons listed!";
    public static final String MESSAGE_DUPLICATE_FIELDS =
                "Multiple values specified for the following single-valued field(s): ";
    public static final String MESSAGE_COMPANIES_LISTED_OVERVIEW = "%1$d companies listed!";
    public static final String MESSAGE_PEOPLE_AND_COMPANIES_LISTED_OVERVIEW = "%1$d people and companies listed!";

    /**
     * Returns an error message indicating the duplicate prefixes.
     */
    public static String getErrorMessageForDuplicatePrefixes(Prefix... duplicatePrefixes) {
        assert duplicatePrefixes.length > 0;

        Set<String> duplicateFields =
                Stream.of(duplicatePrefixes).map(Prefix::toString).collect(Collectors.toSet());

        return MESSAGE_DUPLICATE_FIELDS + String.join(" ", duplicateFields);
    }

    /**
     * Formats the {@code person} for display to the user.
     */
    public static String format(Person person) {
        final StringBuilder builder = new StringBuilder();
        builder.append(person.getName())
                .append("; Phone: ")
                .append(person.getPhone())
                .append("; Email: ")
                .append(person.getEmail())
                .append("; Address: ")
                .append(person.getAddress())
                .append("; Note: ")
                .append(person.getNote())
                .append("; Priority: ")
                .append(person.getPriority())
                .append("; Company: ");
        if (person.getParentCompany() != null) {
            builder.append(person.getParentCompany().getName())
                    .append("; ");
        } else {
            builder.append("None; ");
        }
        builder.append("Tags: ");
        person.getTags().forEach(builder::append);
        return builder.toString();
    }

    /**
     * Formats the {@code company} for display to the user.
     */
    public static String format(Company company) {
        final StringBuilder builder = new StringBuilder();
        builder.append(company.getName())
                .append("; Industry: ")
                .append(company.getIndustry())
                .append("; Location: ")
                .append(company.getLocation())
                .append("; Description: ")
                .append(company.getDescription())
                .append("; Website: ")
                .append(company.getWebsite())
                .append("; Email: ")
                .append(company.getEmail())
                .append("; Phone: ")
                .append(company.getPhone())
                .append("; Address: ")
                .append(company.getAddress());
        return builder.toString();
    }

}
