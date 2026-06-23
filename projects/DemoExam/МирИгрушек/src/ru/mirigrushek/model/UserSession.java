package ru.mirigrushek.model;

public record UserSession(long id, String fullName, String login, String roleCode, String roleName) {
    public static UserSession guest() {
        return new UserSession(0, "Гость", "", "guest", "Гость");
    }

    public boolean isAdmin() {
        return "admin".equals(roleCode);
    }

    public boolean isManager() {
        return "manager".equals(roleCode);
    }

    public boolean canManageCatalogView() {
        return isAdmin() || isManager();
    }
}
