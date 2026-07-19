package com.smartclinic.desktop.navigation;

import java.util.Arrays;
import java.util.List;

/**
 * Desktop navigation routes aligned with task_allocation.md section 9.2
 * and docs/security-matrix.md REST permissions for JavaFX.
 */
public enum DesktopRoute {

    HOME(
            "Home",
            "Home",
            "/fxml/home-view.fxml",
            true,
            "ROLE_ADMIN", "ROLE_RECEPTIONIST", "ROLE_DOCTOR", "ROLE_CASHIER", "ROLE_MANAGER"
    ),
    TODAY_APPOINTMENTS(
            "Today Appointments",
            "Appointments / Today",
            "/fxml/appointments/today-appointments-view.fxml",
            true,
            "ROLE_ADMIN", "ROLE_RECEPTIONIST"
    ),
    PATIENT_SEARCH(
            "Patient Search",
            "Patients / Search",
            "/fxml/patients/patient-search-view.fxml",
            true,
            "ROLE_ADMIN", "ROLE_RECEPTIONIST"
    ),
    WALK_IN(
            "Quick Walk-in",
            "Queue / Walk-in",
            "/fxml/queue/walk-in-view.fxml",
            true,
            "ROLE_ADMIN", "ROLE_RECEPTIONIST"
    ),
    QUEUE_BOARD(
            "Queue Board",
            "Queue / Board",
            "/fxml/queue/queue-board-view.fxml",
            true,
            "ROLE_ADMIN", "ROLE_RECEPTIONIST", "ROLE_DOCTOR"
    ),
    PENDING_INVOICES(
            "Pending Invoices",
            "Billing / Pending Invoices",
            "/fxml/billing/pending-invoices-view.fxml",
            true,
            "ROLE_ADMIN", "ROLE_CASHIER"
    ),
    INVOICE_DETAIL(
            "Invoice Detail",
            "Billing / Invoice Detail",
            "/fxml/billing/invoice-detail-view.fxml",
            false,
            "ROLE_ADMIN", "ROLE_CASHIER"
    ),
    PAYMENT(
            "Payment",
            "Billing / Payment",
            "/fxml/billing/payment-view.fxml",
            false,
            "ROLE_ADMIN", "ROLE_CASHIER"
    );

    private final String pageTitle;
    private final String breadcrumb;
    private final String fxmlPath;
    private final boolean showInMenu;
    private final List<String> requiredRoles;

    DesktopRoute(
            String pageTitle,
            String breadcrumb,
            String fxmlPath,
            boolean showInMenu,
            String... requiredRoles
    ) {
        this.pageTitle = pageTitle;
        this.breadcrumb = breadcrumb;
        this.fxmlPath = fxmlPath;
        this.showInMenu = showInMenu;
        this.requiredRoles = List.of(requiredRoles);
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public String getBreadcrumb() {
        return breadcrumb;
    }

    public String getFxmlPath() {
        return fxmlPath;
    }

    public boolean isShowInMenu() {
        return showInMenu;
    }

    public List<String> getRequiredRoles() {
        return requiredRoles;
    }

    public static List<DesktopRoute> menuRoutes() {
        return Arrays.stream(values())
                .filter(DesktopRoute::isShowInMenu)
                .toList();
    }
}
