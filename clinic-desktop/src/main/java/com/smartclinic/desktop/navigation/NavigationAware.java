package com.smartclinic.desktop.navigation;

/**
 * Optional hook for controllers that should refresh when navigated to.
 */
public interface NavigationAware {

    void onNavigate();
}
