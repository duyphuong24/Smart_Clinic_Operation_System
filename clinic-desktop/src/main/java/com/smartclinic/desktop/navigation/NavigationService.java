package com.smartclinic.desktop.navigation;

import com.smartclinic.desktop.session.SessionManager;
import com.smartclinic.desktop.util.RoleUtil;
import java.util.List;
import java.util.Optional;

public class NavigationService {

    private final ViewLoader viewLoader;
    private final SessionManager sessionManager;
    private DesktopRoute currentRoute;

    public NavigationService(ViewLoader viewLoader, SessionManager sessionManager) {
        this.viewLoader = viewLoader;
        this.sessionManager = sessionManager;
    }

    public NavigationResult navigate(DesktopRoute route) {
        if (!canAccess(route)) {
            return NavigationResult.denied(route);
        }

        ViewLoader.LoadedView loadedView = viewLoader.load(route);
        if (loadedView.controller() instanceof NavigationAware navigationAware) {
            navigationAware.onNavigate();
        }

        currentRoute = route;
        return NavigationResult.success(route, loadedView.root());
    }

    public List<DesktopRoute> visibleMenuRoutes() {
        return DesktopRoute.menuRoutes().stream()
                .filter(this::canAccess)
                .toList();
    }

    public boolean canAccess(DesktopRoute route) {
        if (!sessionManager.isAuthenticated()) {
            return false;
        }
        return RoleUtil.hasAnyRole(sessionManager.getRoles(), route.getRequiredRoles().toArray(String[]::new));
    }

    public Optional<DesktopRoute> getCurrentRoute() {
        return Optional.ofNullable(currentRoute);
    }

    public record NavigationResult(DesktopRoute route, boolean allowed, javafx.scene.Parent content) {

        public static NavigationResult success(DesktopRoute route, javafx.scene.Parent content) {
            return new NavigationResult(route, true, content);
        }

        public static NavigationResult denied(DesktopRoute route) {
            return new NavigationResult(route, false, null);
        }
    }
}
