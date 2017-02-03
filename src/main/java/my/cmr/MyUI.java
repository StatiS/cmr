package my.cmr;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.*;
import sun.misc.resources.Messages_es;

import java.sql.*;

/**
 * This UI is the application entry point. A UI may either represent a browser window 
 * (or tab) or some part of a html page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be 
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@Theme("mytheme")
public class MyUI extends UI {
    ClientLayout clientLayout;
    MeetingLayout meetingLayout;
    ConclusionsLayout conclusionsLayout;

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        //main horizontal layout
        HorizontalLayout main = new HorizontalLayout();
        main.setSizeFull();

        //custom client layout
        clientLayout = new ClientLayout(this);

        //custom meeting layout
        meetingLayout = new MeetingLayout();

        //custom conclusions layout
        conclusionsLayout = new ConclusionsLayout();

        //clientLayout.setWidth("30%");
        //meetingLayout.setWidth("70%");
        //set width ratios for nested layouts and add components to layout
        main.addComponents(clientLayout, meetingLayout, conclusionsLayout);
        main.setExpandRatio(clientLayout, 1);
        main.setExpandRatio(meetingLayout, 2);
        main.setExpandRatio(conclusionsLayout, 2);

        setContent(main);
    }

    public void refreshMeetingLayout(Object itemId) {
        meetingLayout.refresh(itemId);
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
