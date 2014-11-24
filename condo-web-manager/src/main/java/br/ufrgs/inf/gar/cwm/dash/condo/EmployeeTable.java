package br.ufrgs.inf.gar.cwm.dash.condo;

import br.ufrgs.inf.gar.condo.domain.Employee;
import br.ufrgs.inf.gar.cwm.dash.data.DaoService;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.Table;
import com.vaadin.ui.TableFieldFactory;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public final class EmployeeTable extends Table {

	@Override
    protected String formatPropertyValue(final Object rowId,
            final Object colId, final Property<?> property) {
        String result = super.formatPropertyValue(rowId, colId, property);
        if (colId.equals("working")) {
            if (property != null && property.getValue() != null) {
                if((Boolean) property.getValue()) {
                	result = "Trabalhando";
                } else {
                	result = "Não presente";
                }
            } else {
                result = "?";
            }
        }
        return result;
    }
	
    public EmployeeTable() {
        setCaption("Empregados");

        addStyleName(ValoTheme.TABLE_BORDERLESS);
        addStyleName(ValoTheme.TABLE_NO_STRIPES);
        addStyleName(ValoTheme.TABLE_NO_VERTICAL_LINES);
        addStyleName(ValoTheme.TABLE_SMALL);
        setSortEnabled(false);
        setImmediate(true);
        setSizeFull();

        BeanItemContainer<Employee> container = new BeanItemContainer<>(
        		Employee.class, DaoService.getAllEmployees());
        setContainerDataSource(container);
        setVisibleColumns(new Object[]{"name", "role", "monthWage", "weekWorkload", "working"});
        setColumnHeaders("Nome", "Cargo", "Salário", "Carga Horária", "Status");
        
        this.setTableFieldFactory(new TableFieldFactory() {
			@Override
			public Field<?> createField(Container container, Object itemId,
					Object propertyId, Component uiContext) {
				TextField field = new TextField((String) propertyId);
                if ("working".equals(propertyId)) {
                    field.setImmediate(true);
                }
                return field;
			}
		});
    }
}
