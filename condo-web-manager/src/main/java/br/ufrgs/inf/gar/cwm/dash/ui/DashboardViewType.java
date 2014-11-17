package br.ufrgs.inf.gar.cwm.dash.ui;

import com.vaadin.navigator.View;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;

public enum DashboardViewType {
	DASHBOARD("condominio", DashboardView.class, FontAwesome.TABLE, true), 
    SALES("apartmentos", ConsumView.class, FontAwesome.HOME, false), 
    TRANSACTIONS("financas", TransactionsView.class, FontAwesome.BAR_CHART_O, false), 
    REPORTS("problemas", ReportsView.class, FontAwesome.FILE_TEXT_O, true), 
    SCHEDULE("calendario", ScheduleView.class, FontAwesome.CALENDAR_O, false),
	TEST("test", AptsView.class, FontAwesome.CALENDAR_O, false);

    private final String viewName;
    private final Class<? extends View> viewClass;
    private final Resource icon;
    private final boolean stateful;

    private DashboardViewType(final String viewName,
            final Class<? extends View> viewClass, final Resource icon,
            final boolean stateful) {
        this.viewName = viewName;
        this.viewClass = viewClass;
        this.icon = icon;
        this.stateful = stateful;
    }

    public boolean isStateful() {
        return stateful;
    }

    public String getViewName() {
        return viewName;
    }

    public Class<? extends View> getViewClass() {
        return viewClass;
    }

    public Resource getIcon() {
        return icon;
    }

    public static DashboardViewType getByViewName(final String viewName) {
        DashboardViewType result = null;
        for (DashboardViewType viewType : values()) {
            if (viewType.getViewName().equals(viewName)) {
                result = viewType;
                break;
            }
        }
        return result;
    }

}
