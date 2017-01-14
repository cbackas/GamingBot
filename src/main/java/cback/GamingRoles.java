package cback;

public enum GamingRoles {
    STAFF("STAFF", "257995763399917569"),
    ADMIN("ADMIN", "256249078596370433"),
    MOD("MOD", "256249088830472193"),
    HELPER("HELPER", "256878689503936513"),
    MOVIENIGHT("MOVIENIGHT", "226443478664609792");

    public String name;
    public String id;

    GamingRoles(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public static GamingRoles getRole(String name) {
        for (GamingRoles role : values()) {
            if (role.name.equalsIgnoreCase(name)) {
                return role;
            }
        }
        return null;
    }
}