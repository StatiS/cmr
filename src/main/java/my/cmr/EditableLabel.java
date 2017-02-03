package my.cmr;

import com.vaadin.event.FieldEvents;
import com.vaadin.event.LayoutEvents;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Created by Jan Boznar on 03/02/2017.
 */
public class EditableLabel extends CssLayout{
    private Label label = new Label();
    private TextField textField = new TextField();

    public EditableLabel() {
        label.setSizeUndefined();
        label.setStyleName(ValoTheme.LABEL_BOLD);
        textField.setPropertyDataSource(label);
        addComponent(label);
        addListeners();
    }

    private void addListeners() {
        //add custom listeners to switch between label and textfield
        addLayoutClickListener((LayoutEvents.LayoutClickListener) event -> {
            if (event.isDoubleClick() && event.getClickedComponent() instanceof Label){
                removeComponent(label);
                addComponent(textField);
                textField.focus();
            }
        });
        //switch back to label
        textField.addBlurListener((FieldEvents.BlurListener) event -> {
            removeComponent(textField);
            addComponent(label);
        });
    }

    public void setValue(String value) {
        label.setValue(value);
    }
}
