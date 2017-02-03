package my.cmr;

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Created by Jan Boznar on 03/02/2017.
 */
public class ConclusionsLayout extends VerticalLayout {
    private Label layoutName;

    public ConclusionsLayout() {
        setMargin(true);

        //layout name label
        layoutName = new Label("Conclusions");
        layoutName.setStyleName(ValoTheme.LABEL_H1);

        addComponent(layoutName);
    }
}
