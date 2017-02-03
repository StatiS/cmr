package my.cmr;

import com.vaadin.data.Container.Filterable;
import com.vaadin.data.Item;
import com.vaadin.data.util.filter.And;
import com.vaadin.data.util.filter.Like;
import com.vaadin.data.util.filter.Or;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.sql.SQLException;

/**
 * Created by Jan Boznar on 02/02/2017.
 */
public class ClientLayout extends VerticalLayout{
    private MyUI myUI;
    private Label layoutName;
    private TextField input;
    private String[] inputArr = {""};
    private Table clientsTable;
    private CssLayout addDelete;
    private Button add;
    private Button delete;

    private SQLContainer clientsData;
    Filterable filter;

    public ClientLayout(MyUI myUI) {
        this.myUI = myUI;

        setMargin(true);

        //Layout header label
        layoutName = new Label("Clients");
        layoutName.setStyleName(ValoTheme.LABEL_H1);

        //Textfield for client filtering
        input = new TextField();
        input.setInputPrompt("Enter client name");
        input.setWidth("100%");

        //Get SQLcontainer from database
        clientsData = DbHelper.getInstance().getContainer("CLIENTS");

        //Table of clients from sqlcontainer datasource
        clientsTable = new Table();
        clientsTable.setContainerDataSource(clientsData);
        clientsTable.setVisibleColumns("NAME", "SURNAME");
        clientsTable.setSelectable(true);
        clientsTable.setStyleName(ValoTheme.TABLE_NO_STRIPES);
        clientsTable.addStyleName(ValoTheme.TABLE_NO_VERTICAL_LINES);
        clientsTable.setWidth("100%");

        initTableListeners();

        //horizontal layout to store add and delete client buttons
        addDelete = new CssLayout();
        addDelete.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
        addDelete.setWidth("100%");

        //buttons for adding and deleting clients
        add = new Button(FontAwesome.PLUS);
        delete = new Button(FontAwesome.MINUS);
        add.setWidth("50%");
        delete.setWidth("50%");

        initAddListener();
        initDeleteListener();

        addDelete.addComponents(add, delete);
        addComponents(layoutName, input, clientsTable, addDelete);

        //set Expand ratios so child components take proper width
        setExpandRatio(input, 1);
        setExpandRatio(clientsTable, 1);
        setExpandRatio(addDelete, 1);
    }

    private void initDeleteListener() {
        delete.addClickListener(e -> {
            Object selectedId = clientsTable.getValue();
            if (selectedId != null) {
                clientsData.removeItem(selectedId);
                try {
                    clientsData.commit();
                } catch (SQLException e1) {
                    Notification.show("Failed to commit to database");
                    e1.printStackTrace();
                }
                filter();
            } else {
                Notification.show("No client selected!");
            }
        });
    }

    private void initAddListener() {
        add.addClickListener((Button.ClickEvent e) -> {
            if (inputArr.length == 2) {
                //TODO select new row after insertion
                try {
                    //addItem to fresh container and set properties, then commit
                    clientsData = DbHelper.getInstance().getContainer("CLIENTS");

                    Object id = clientsData.addItem();
                    Item item = clientsData.getItem(id);
                    clientsData.getContainerProperty(id, "NAME").setValue(inputArr[0]);
                    clientsData.getContainerProperty(id, "SURNAME").setValue(inputArr[1]);

                    clientsData.commit();
                } catch (SQLException e1) {
                    Notification.show("Failed to commit to database");
                    e1.printStackTrace();
                }
                //call filter method to update table
                filter();
            } else {
                Notification.show("Invalid format, should be: 'name surname'");
            }
        });
    }

    private void initTableListeners() {
        //table click listener to refresh meeting layout
        clientsTable.addValueChangeListener(e -> {
            myUI.refreshMeetingLayout(clientsTable.getValue());
        });

        //on input text change set inputArg[] and call filtering
        input.addTextChangeListener(e -> {
            inputArr = e.getText().split("\\s+");
            filter();
        });
    }



    //reset container filter to whatever is in input TextField
    private void filter () {
        filter = (Filterable) (clientsTable.getContainerDataSource());
        filter.removeAllContainerFilters();

        //TextField never returns null, so empty input check can be done on arrays first element
        if (inputArr[0].length() > 0) {
            if (inputArr.length >= 2) {
                filter.addContainerFilter(new Or(
                        new And(
                                new Like("NAME", inputArr[0] +"%"),
                                new Like("SURNAME", inputArr[1] + "%")),
                        new And(
                                new Like("SURNAME", inputArr[0] +"%"),
                                new Like("NAME", inputArr[1] + "%"))
                ));
            } else {
                filter.addContainerFilter(new Or(new Like("NAME", inputArr[0] +"%"),
                        new Like("SURNAME", inputArr[0] + "%")));
            }
        }
        clientsTable.setVisibleColumns("NAME", "SURNAME");
    }
}
