package connectify.model;

import static java.util.Objects.requireNonNull;

import java.nio.file.Path;
import java.util.function.Predicate;
import java.util.logging.Logger;

import connectify.commons.core.GuiSettings;
import connectify.commons.core.LogsCenter;
import connectify.commons.util.CollectionUtil;
import connectify.model.company.Company;
import connectify.model.person.Person;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

/**
 * Represents the in-memory model of the address book data.
 * A key flag in this model is the current entity type, which can be either people, companies or all.
 * Depending on the current entity type, the filtered list will be updated accordingly.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final AddressBook addressBook;
    private final UserPrefs userPrefs;
    private final FilteredList<Person> filteredPersons;
    private final FilteredList<Company> filterCompanies;
    private enum EntityType {
        PEOPLE, COMPANIES, ALL
    }
    private EntityType currEntity = EntityType.COMPANIES;


    /**
     * Initializes a ModelManager with the given addressBook and userPrefs.
     */
    public ModelManager(ReadOnlyAddressBook addressBook, ReadOnlyUserPrefs userPrefs) {
        CollectionUtil.requireAllNonNull(addressBook, userPrefs);

        logger.fine("Initializing with address book: " + addressBook + " and user prefs " + userPrefs);

        this.addressBook = new AddressBook(addressBook);
        this.userPrefs = new UserPrefs(userPrefs);
        filteredPersons = new FilteredList<>(this.addressBook.getPersonList());
        filterCompanies = new FilteredList<>(this.addressBook.getCompanyList());
    }

    public ModelManager() {
        this(new AddressBook(), new UserPrefs());
    }

    //=========== UserPrefs ==================================================================================

    @Override
    public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
        requireNonNull(userPrefs);
        this.userPrefs.resetData(userPrefs);
    }

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public GuiSettings getGuiSettings() {
        return userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        userPrefs.setGuiSettings(guiSettings);
    }

    @Override
    public Path getAddressBookFilePath() {
        return userPrefs.getAddressBookFilePath();
    }

    @Override
    public void setAddressBookFilePath(Path addressBookFilePath) {
        requireNonNull(addressBookFilePath);
        userPrefs.setAddressBookFilePath(addressBookFilePath);
    }

    //=========== AddressBook ================================================================================

    @Override
    public void setAddressBook(ReadOnlyAddressBook addressBook) {
        this.addressBook.resetData(addressBook);
    }

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        return addressBook;
    }

    @Override
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return addressBook.hasPerson(person);
    }

    @Override
    public boolean hasCompany(Company company) {
        requireNonNull(company);
        return addressBook.hasCompany(company);
    }

    @Override
    public void deletePerson(Person target) {
        addressBook.removePerson(target);
        currEntity = EntityType.PEOPLE;
    }

    @Override
    public void addPerson(Person person) {
        addressBook.addPerson(person);
        updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        currEntity = EntityType.PEOPLE;
    }

    @Override
    public void addCompany(Company company) {
        addressBook.addCompany(company);
        updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        currEntity = EntityType.COMPANIES;
    }

    @Override
    public void deleteCompany(Company target) {
        addressBook.removeCompany(target);
        currEntity = EntityType.COMPANIES;
    }

    @Override
    public void setPerson(Person target, Person editedPerson) {
        CollectionUtil.requireAllNonNull(target, editedPerson);

        addressBook.setPerson(target, editedPerson);
        currEntity = EntityType.PEOPLE;
    }

    //=========== Filtered List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Person} backed by the internal list of
     * {@code versionedAddressBook}
     */
    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return filteredPersons;
    }

    /**
     * Returns an unmodifiable view of the list of {@code Person} backed by the internal list of
     * {@code versionedAddressBook}
     */
    @Override
    public ObservableList<Company> getFilteredCompanyList() {
        return filterCompanies;
    }

    @Override
    public void updateFilteredPersonList(Predicate<Person> predicate) {
        requireNonNull(predicate);
        filteredPersons.setPredicate(predicate);
        currEntity = EntityType.PEOPLE;
    }

    @Override
    public void updateFilteredCompanyList(Predicate<Company> predicate) {
        requireNonNull(predicate);
        filterCompanies.setPredicate(predicate);
        currEntity = EntityType.COMPANIES;
    }

    @Override
    public void updateToAllEntities() {
        currEntity = EntityType.ALL;
    }
    @Override
    public String getCurrEntity() {
        if (currEntity == EntityType.PEOPLE) {
            return "people";
        } else if (currEntity == EntityType.COMPANIES) {
            return "companies";
        } else {
            return "all";
        }
    }

    @Override
    public void setCurrEntity(String entityType) throws InvalidEntityException {
        if (entityType.equals("people")) {
            currEntity = EntityType.PEOPLE;
        } else if (entityType.equals("companies")) {
            currEntity = EntityType.COMPANIES;
        } else if (entityType.equals("all")) {
            currEntity = EntityType.ALL;
        } else {
            throw new InvalidEntityException("Invalid entity type: " + entityType + ". Please enter either "
                    + "people, companies or all.");
        }
    }

    @Override
    public ObservableList<? extends Entity> getFilteredEntityList() {
        if (currEntity == EntityType.PEOPLE) {
            logger.info("Returning list of filtered persons");
            return filteredPersons;
        } else if (currEntity == EntityType.COMPANIES) {
            logger.info("Returning list of filtered companies");
            return filterCompanies;
        } else {
            // Create a new ObservableList which contains all the elements from filteredCompanies and filteredPersons
            logger.info("Returning list of all entities");
            ObservableList<Entity> allEntityList = FXCollections.observableArrayList();
            allEntityList.addAll(filterCompanies);
            allEntityList.addAll(filteredPersons);
            return allEntityList;
        }
    }

    @Override
    public Integer getNumberOfEntities() {
        return getFilteredEntityList().size();
    }

    @Override
    public Integer getNumberOfPeople() {
        return filteredPersons.size();
    }

    @Override
    public Integer getNumberOfCompanies() {
        return filterCompanies.size();
    }

    @Override
    public Boolean isEmpty() {
        return getNumberOfEntities() == 0;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ModelManager)) {
            return false;
        }

        ModelManager otherModelManager = (ModelManager) other;
        return addressBook.equals(otherModelManager.addressBook)
                && userPrefs.equals(otherModelManager.userPrefs)
                && filteredPersons.equals(otherModelManager.filteredPersons)
                && filterCompanies.equals(otherModelManager.filterCompanies);
    }

    @Override
    public String toString() {
        String msg = "There are " + getNumberOfEntities() + " entities in the address book.\n";
        msg += "There are " + getNumberOfPeople() + " people in the address book.\n";
        msg += "There are " + getNumberOfCompanies() + " companies in the address book.\n";
        return msg;
    }

}