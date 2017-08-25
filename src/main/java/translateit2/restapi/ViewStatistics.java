package translateit2.restapi;

public class ViewStatistics {
    private long reviewed;

    private long total;

    private long translated;

    private long workId;

    public long getReviewed() {
        return reviewed;
    }

    public long getTotal() {
        return total;
    }

    public long getTranslated() {
        return translated;
    }

    public long getWorkId() {
        return workId;
    }

    public void setReviewed(long reviewed) {
        this.reviewed = reviewed;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public void setTranslated(long translated) {
        this.translated = translated;
    }

    public void setWorkId(long workId) {
        this.workId = workId;
    }
}
