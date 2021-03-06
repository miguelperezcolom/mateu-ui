package io.mateu.ui.sample.client;

import io.mateu.ui.core.client.components.Tab;
import io.mateu.ui.core.client.components.Tabs;
import io.mateu.ui.core.client.components.fields.TextField;
import io.mateu.ui.core.client.views.AbstractView;

/**
 * Created by miguel on 23/3/17.
 */
public class TabsView2 extends AbstractView {
    @Override
    public String getTitle() {
        return "Tabs";
    }

    @Override
    public void build() {
        add(new TextField("tf1", "TF1"))
                .add(new Tab(getForm(), "tab 1").add(new TextField("tf2", "TF2"))
                                .add(new TextField("tf2a", "TF2"))
                                .add(new TextField("tf2b", "TF2"))
                                .add(new TextField("tf2c", "TF2"))
                                .add(new TextField("tf2d", "TF2"))
                                .add(new TextField("tf2e", "TF2")))
                        .add(new Tab(getForm(), "tab 2").add(new TextField("tf3", "TF3")))
                        .add(new Tab(getForm(), "tab 3").add(new TextField("tf2", "TF2")))
                ;
    }
}
