package my.cmr;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.sql.SQLException;

/**
 * Created by Jan Boznar on 02/02/2017.
 */
public class MeetingLayout extends VerticalLayout {
    private Label layoutName;
    private EditableLabel fullName;
    private EditableLabel email;
    private EditableLabel phone;
    private Label noneSelected;

    private Button add;
    private Button save;
    private Button delete;

    private Grid meetingGrid;

    private VerticalLayout content;
    private CssLayout addDelete;

    private Object clientId = null;
    private SQLContainer meetingData;
    private SQLContainer clientData;

    public MeetingLayout() {
        setMargin(true);

        meetingData = DbHelper.getInstance().getContainer("MEETINGS");
        clientData = DbHelper.getInstance().getContainer("CLIENTS");

        layoutName = new Label("Meetings");
        layoutName.setStyleName(ValoTheme.LABEL_H1);
        fullName = new EditableLabel();
        email = new EditableLabel();
        phone = new EditableLabel();

        meetingGrid = new Grid();
        meetingGrid.setContainerDataSource(meetingData);
        meetingGrid.setSizeFull();
        meetingGrid.setHeight("100%");

        //init default layout
        noneSelected = new Label("Select a client to get meetings information");
        noneSelected.setStyleName(ValoTheme.LABEL_LIGHT);

        content = new VerticalLayout();
        content.addComponent(noneSelected);
        content.setSpacing(true);
        content.setHeight("100%");
        addComponents(layoutName, content);

    }
    //refresh meetings layout with new client data
    public void refresh(Object clientId) {
        //clean layout content
        content.removeAllComponents();

        //get selected client item and fill client info labels
        this.clientId = clientId;
        //call method to filter meetings according to selected client
        filter();

        //horizontal layout to store add and delete client buttons
        addDelete = new CssLayout();
        addDelete.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
        addDelete.setWidth("100%");

        //buttons for adding and deleting clients
        add = new Button(FontAwesome.PLUS);
        delete = new Button(FontAwesome.MINUS);
        save = new Button(FontAwesome.SAVE);
        save.setWidth("33%");
        add.setWidth("34%");
        delete.setWidth("33%");
        initDeleteButton();


        addDelete.addComponents(add, save, delete);
        content.addComponents(layoutName, fullName, email, phone, meetingGrid, addDelete);
        meetingGrid.setHeight("100%");
        content.setExpandRatio(meetingGrid, 1);
    }

    private void filter() {
        Item client = clientData.getItem(clientId);
        //insertion bug sometimes causes nullPointerException when clicking too fast at adding clients
        try {
            //set client info editable labels to those of selected client
            fullName.setValue(String.valueOf(client.getItemProperty("NAME").getValue()) +
                    " " +  String.valueOf(client.getItemProperty("SURNAME").getValue()));
            email.setValue(String.valueOf(client.getItemProperty("EMAIL").getValue()));
            phone.setValue(String.valueOf(client.getItemProperty("PHONE").getValue()));

            //filter meetings by selected client id
            Container.Filterable filter = (Container.Filterable) (meetingGrid.getContainerDataSource());
            filter.removeAllContainerFilters();
            filter.addContainerFilter(new Compare.Equal( "C_ID", Integer.parseInt(String.valueOf(clientId))));
            meetingGrid.setColumns("ID", "LOCATION", "STARTEDAT", "CONCLUDEDAT");
        } catch (NullPointerException e) {
            Notification.show("Caught nullPointerException, bug here, please refresh");
        }
    }

    private void initDeleteButton() {
        delete.addClickListener(e -> {
            Object selectedId = meetingGrid.getSelectedRow();
            if (selectedId != null) {
                meetingData.removeItem(selectedId);
                try {
                    meetingData.commit();
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
}
