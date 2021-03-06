package io.mateu.ui.sample.client;

import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.components.fields.TextField;
import io.mateu.ui.core.client.components.fields.grids.columns.AbstractColumn;
import io.mateu.ui.core.client.components.fields.grids.columns.OutputColumn;
import io.mateu.ui.core.client.components.fields.grids.columns.TextColumn;
import io.mateu.ui.core.client.views.*;
import io.mateu.ui.core.shared.AsyncCallback;
import io.mateu.ui.core.shared.CellStyleGenerator;
import io.mateu.ui.core.shared.Data;

import java.util.Arrays;
import java.util.List;

/**
 * Created by miguel on 31/12/16.
 */
public class CRUDView extends AbstractCRUDView {
    @Override
    public String getSql() {
        String sql = "select id, firstname, lastname, street from customer where id = id ";
        if (!getForm().getData().isEmpty("fn")) {
            sql += " and lower(firstname) like '%" + getForm().getData().getString("fn").toLowerCase().replaceAll("'", "''") + "%'";
        }
        if (!getForm().getData().isEmpty("ln")) {
            sql += " and lower(lastname) like '%" + getForm().getData().getString("ln").toLowerCase().replaceAll("'", "''") + "%'";
        }
        if (!getForm().getData().isEmpty("s")) {
            sql += " and lower(street) like '%" + getForm().getData().getString("s").toLowerCase().replaceAll("'", "''") + "%'";
        }
        sql += " order by id";
        return sql;
    }

    @Override
    public String getTitle() {
        return "CRUD";
    }

    @Override
    public void build() {
        add(new TextField("fn", "First name").setRequired(true))
                .add(new TextField("ln", "Last name"))
                .add(new TextField("s", "Street"));
    }

    @Override
    public AbstractEditorView getNewEditorView() {
        return new BaseEditorView() {

            @Override
            public String getServerSideControllerKey() {
                return "crud";
            }

            @Override
            public String getTitle() {
                return "Customer";
            }

            @Override
            public void build() {
                add(new TextField("firstname", "First name").setRequired(true))
                        .add(new TextField("lastname", "Last name"))
                        .add(new TextField("street", "Street"))
                ;
            }
        };
    }

    @Override
    public List<AbstractColumn> createExtraColumns() {
        return Arrays.asList(new OutputColumn("col1", "First Name", 100).setStyleGenerator(new CellStyleGenerator() {
                    @Override
                    public String getStyle(Object value) {
                        if ("Laura".equalsIgnoreCase("" + value)) return "danger";
                        else if ("james".equalsIgnoreCase("" + value)) return "done";
                        else if ("bill".equalsIgnoreCase("" + value)) return "cancelled red";
                        return null;
                    }

                    @Override
                    public boolean isContentShown() {
                        return true;
                    }
                })
        , new OutputColumn("col2", "Last Name", 100)
                , new OutputColumn("col3", "Street", 200));
    }

    @Override
    public void delete(List<Data> selection, AsyncCallback<Void> callback) {
        MateuUI.getBaseService().execute("delete from customer where id in (" + MateuUI.extractIds(selection) + ")", callback);
    }
}
