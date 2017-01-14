package cback;

public enum Rules {
    one("1", Util.getRule("263194031771615234")),
    two("2", Util.getRule("263194076361392138")),
    three("3", Util.getRule("263194114512781313")),
    four("4", Util.getRule("263194556382707714")),
    five("5", Util.getRule("263194632270118922")),
    six("6", Util.getRule("263194666961338368")),
    other("other", Util.getRule("263194703317565442"));

    public String number;
    public String fullRule;

    Rules(String number, String fullRule) {
        this.number = number;
        this.fullRule = fullRule;
    }

    public static Rules getRule(String number) {
        for (Rules rule : values()) {
            if (rule.number.equalsIgnoreCase(number)) {
                return rule;
            }
        }
        return null;
    }
}