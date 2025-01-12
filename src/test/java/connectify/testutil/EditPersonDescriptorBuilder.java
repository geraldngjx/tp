package connectify.testutil;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import connectify.logic.commands.EditPersonCommand.EditPersonDescriptor;
import connectify.model.company.Company;
import connectify.model.person.Person;
import connectify.model.person.PersonAddress;
import connectify.model.person.PersonEmail;
import connectify.model.person.PersonName;
import connectify.model.person.PersonNote;
import connectify.model.person.PersonPhone;
import connectify.model.person.PersonPriority;
import connectify.model.tag.Tag;

/**
 * A utility class to help with building EditPersonDescriptor objects.
 */
public class EditPersonDescriptorBuilder {

    private final EditPersonDescriptor descriptor;

    public EditPersonDescriptorBuilder() {
        descriptor = new EditPersonDescriptor();
    }

    public EditPersonDescriptorBuilder(EditPersonDescriptor descriptor) {
        this.descriptor = new EditPersonDescriptor(descriptor);
    }

    /**
     * Returns an {@code EditPersonDescriptor} with fields containing {@code person}'s details
     */
    public EditPersonDescriptorBuilder(Person person) {
        descriptor = new EditPersonDescriptor();
        descriptor.setName(person.getName());
        descriptor.setPhone(person.getPhone());
        descriptor.setEmail(person.getEmail());
        descriptor.setAddress(person.getAddress());
        descriptor.setTags(person.getTags());
        descriptor.setNote(person.getNote());
        descriptor.setParentCompany(person.getParentCompany());
        descriptor.setPersonPriority(person.getPriority());
    }

    /**
     * Sets the {@code Name} of the {@code EditPersonDescriptor} that we are building.
     */
    public EditPersonDescriptorBuilder withName(String name) {
        descriptor.setName(new PersonName(name));
        return this;
    }

    /**
     * Sets the {@code Phone} of the {@code EditPersonDescriptor} that we are building.
     */
    public EditPersonDescriptorBuilder withPhone(String phone) {
        descriptor.setPhone(new PersonPhone(phone));
        return this;
    }

    /**
     * Sets the {@code Email} of the {@code EditPersonDescriptor} that we are building.
     */
    public EditPersonDescriptorBuilder withEmail(String email) {
        descriptor.setEmail(new PersonEmail(email));
        return this;
    }

    /**
     * Sets the {@code Address} of the {@code EditPersonDescriptor} that we are building.
     */
    public EditPersonDescriptorBuilder withAddress(String address) {
        descriptor.setAddress(new PersonAddress(address));
        return this;
    }

    /**
     * Parses the {@code tags} into a {@code Set<Tag>} and set it to the {@code EditPersonDescriptor}
     * that we are building.
     */
    public EditPersonDescriptorBuilder withTags(String... tags) {
        Set<Tag> tagSet = Stream.of(tags).map(Tag::new).collect(Collectors.toSet());
        descriptor.setTags(tagSet);
        return this;
    }

    /**
     * Sets the {@code PersonPriority} of the {@code EditPersonDescriptor} that we are building.
     * @return
     */

    public EditPersonDescriptorBuilder withPersonPriority(String priority) {
        PersonPriority converted = new PersonPriority(priority);
        descriptor.setPersonPriority(converted);
        return this;
    }

    /**
     * Sets the {@code ParentCompany} of the {@code EditPersonDescriptor} that we are building.
     * @param parentCompany
     * @return
     */
    public EditPersonDescriptorBuilder withParentCompany(Company parentCompany) {
        descriptor.setParentCompany(parentCompany);
        return this;
    }

    /**
     * Sets the {@code Note} of the {@code EditPersonDescriptor} that we are building.
     * @param note
     * @return
     */
    public EditPersonDescriptorBuilder withNote(String note) {
        descriptor.setNote(new PersonNote(note));
        return this;
    }


    public EditPersonDescriptor build() {
        return descriptor;
    }
}
