package translateit2.persistence.model;

/*
 * 	The state of a particular translation in a target	
	NEW	Indicates that the item is new. For example, translation units that were not in a previous version of the document.	
	TRANSLATED	Indicates that the item has been translated.			
	NEEDS-REVIEW	Indicates that the item needs to be reviewed.	
	NEEDS-TRANSLATION	Indicates that the item needs to be translated.	
	SIGNED-OFF	Indicates that changes are reviewed and approved.	
	FINAL Indicates the terminating state.	
 */
public enum State {
    FINAL, NEEDS_REVIEW, NEEDS_TRANSLATION, /*
    NEW(0), TRANSLATED(1), NEEDS_REVIEW(2), NEEDS_TRANSLATION(3), SIGNED_OFF(4), FINAL(5);
    private int value;
    private String types[] = { "new", "translated", "need review", "needs translation", "signed off", "final" };

    private State(int value) {
        this.value = value;
    }

    public String toString() {
        return types[value];
    }
    */
    NEW, SIGNED_OFF, TRANSLATED;
}
