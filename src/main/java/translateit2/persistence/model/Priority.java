package translateit2.persistence.model;

// High, medium,low
public enum Priority {
    /*
    HIGH(1), MEDIUM(2), LOW(3);
    private int value;
    private String types[] = { "high", "medium", "low" };

    private Priority(int value) {
        this.value = value;
    }

    public String toString() {
        return types[value];
    }
    */

    // note [MD] (3) unnatural order of values
    HIGH, LOW, MEDIUM;
}
