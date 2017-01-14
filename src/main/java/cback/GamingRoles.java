package cback;

public enum GamingRoles {
    STAFF("STAFF", "197356636782723073"),
    ADMIN("ADMIN", "191590594173206528"),
    MOD("MOD", "191590784250675200"),
    HELPER("HELPER", "262440320791216128"),
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