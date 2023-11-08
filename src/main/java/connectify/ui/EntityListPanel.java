package connectify.ui;

import connectify.model.Entity;
import connectify.model.company.Company;
import connectify.model.person.Person;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Panel containing the list of entities. Supports tabbing between different entity types on the UI
 */
public class EntityListPanel extends UiPart<Region> {
    public static final String FXML = "EntityListPanel.fxml";
    /* The TabPane that contains the different entity types */
    @FXML
    private TabPane entityTabPane;

    /**
     * Creates a {@code EntityListPanel} with the given {@code ObservableList}.
     * @param entityList The list of entities to display
     * @param personList The list of people to display
     * @param companyList The list of companies to display
     * @param mode The mode to display the list in
     */
    public EntityListPanel(ObservableList<? extends Entity> entityList, ObservableList<Person> personList,
                           ObservableList<Company> companyList, String mode) {
        super(FXML);
        /// Create Tab 1: Accordion of Companies with People Cards
        Tab tab1 = new Tab("All");
        Accordion companyAccordion = createCompanyAccordion(companyList);
        tab1.setContent(companyAccordion);

        // Create Tab 2: ListView of People Cards
        Tab tab2 = new Tab("People");
        ListView<Entity> peopleListView = new ListView<>();
        peopleListView.setItems(FXCollections.observableArrayList(personList));
        peopleListView.setCellFactory(listView -> new EntityListViewCell());
        tab2.setContent(peopleListView);

        // Create Tab 3: ListView of Company Cards
        Tab tab3 = new Tab("Companies");
        ListView<Entity> companyListView = new ListView<>();
        companyListView.setItems(FXCollections.observableArrayList(companyList));
        companyListView.setCellFactory(listView -> new EntityListViewCell());
        tab3.setContent(companyListView);

        entityTabPane.getTabs().addAll(tab1, tab2, tab3);
        entityTabPane.getSelectionModel().select(getMode(mode));
        entityTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        // Wrap the content of each Tab in a ScrollPane

        for (Tab tab : entityTabPane.getTabs()) {
            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);
            scrollPane.setContent(tab.getContent());
            tab.setContent(scrollPane);
        }
    }

    class EntityListViewCell extends ListCell<Entity> {
        @Override
        protected void updateItem(Entity entity, boolean empty) {
            super.updateItem(entity, empty);
            if (empty || entity == null) {
                setGraphic(null);
                setText(null);
            } else if (entity instanceof Company) {
                setGraphic(new CompanyCard((Company) entity, getIndex() + 1).getRoot());
            } else if (entity instanceof Person) {
                setGraphic(new PersonCard((Person) entity, getIndex() + 1).getRoot());
            }
        }
    }

    private Accordion createCompanyAccordion(ObservableList<Company> companyList) {
        Accordion companyAccordion = new Accordion();
        companyList.forEach(company -> {
            TitledPane companyPane = createCompanyPane(company);
            companyAccordion.getPanes().add(companyPane);
        });
        return companyAccordion;
    }
    private TitledPane createCompanyPane(Company company) {
        TitledPane companyPane = new TitledPane();
        // Create a ScrollPane to make the content scrollable
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);

        companyPane.setText(company.getName().toString() + " (" + company.getPersonList().size() + " people)");
        // Create a VBox to hold the information
        VBox companyPaneContent = new VBox();

        // Add labels and values for each field
        VBox infoBox = new VBox();
        infoBox.setSpacing(5); // Spacing between each label and value
        infoBox.setPadding(new Insets(10, 10, 10, 10)); // Padding around the entire infoBox

        infoBox.getChildren().add(new Label("Industry: " + company.getIndustry()));
        infoBox.getChildren().add(new Label("Location: " + company.getLocation()));
        infoBox.getChildren().add(new Label("Website: " + company.getWebsite()));
        infoBox.getChildren().add(new Label("Email: " + company.getEmail()));
        infoBox.getChildren().add(new Label("Phone: " + company.getPhone()));
        infoBox.getChildren().add(new Label("Address: " + company.getAddress()));
        infoBox.getChildren().add(new Label("Description: " + company.getDescription()));
        infoBox.getChildren().add(new Label("Note: " + company.getNote()));

        // Create a ListView for people within the company
        ListView<Person> companyPeopleListView = new ListView<>();
        companyPeopleListView.setItems(FXCollections.observableArrayList(company.getPersonList().asList()));
        companyPeopleListView.setCellFactory(listView -> new PersonListViewCell());

        if (company.getPersonList().size() == 0) {
            companyPeopleListView.setPlaceholder(new Label("No person in company"));
        }
        // Set the preferred width of the ListView to match the ScrollPane's width
        companyPeopleListView.prefWidthProperty().bind(scrollPane.widthProperty());

        // Add the ListView to the VBox to ensure proper layout
        VBox listViewBox = new VBox(companyPeopleListView);
        listViewBox.setPadding(new Insets(5, 5, 5, 5)); // Padding around the entire listViewBox

        // Add spacing between infoBox and listViewBox
        companyPaneContent.getChildren().add(infoBox);
        companyPaneContent.getChildren().add(listViewBox);

        scrollPane.setContent(companyPaneContent);

        // Set the content of the TitledPane to the ScrollPane
        companyPane.setContent(scrollPane);
        return companyPane;
    }

    class PersonListViewCell extends ListCell<Person> {
        @Override
        protected void updateItem(Person person, boolean empty) {
            super.updateItem(person, empty);

            if (empty || person == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(new PersonCard(person, getIndex() + 1).getRoot());
            }
        }
    }

    public static int getMode(String modeName) {
        switch (modeName) {
        case "people":
            return 1;
        case "companies":
            return 2;
        default:
            return 0;
        }
    }
}
