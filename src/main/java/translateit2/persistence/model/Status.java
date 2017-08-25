package translateit2.persistence.model;

/*
	When a work is first created, it automatically gets a status of NEW.
	After saved for the first time, the status of the work changes to OPEN.
	After all the units have been translated, the status will be changed to PENDING.
	As long as there are untranslated units, the work will revert back to Open.
	The work can be closed, only if all the units have been translated and reviewed.
	Once the work is CLOSED, it becomes read-only (ARCHIVED). 
 */
public enum Status {
    ARCHIVED, CLOSED, /*
    NEW(0), OPEN(1), PENDING(2), CLOSED(3), ARCHIVED(4);
    private int value;
    private String types[] = { "new", "open", "pending", "closed", "archived" };

    private Status(int value) {
        this.value = value;
    }

    public String toString() {
        return types[value];
    }
    */
    NEW, OPEN, PENDING;
}
