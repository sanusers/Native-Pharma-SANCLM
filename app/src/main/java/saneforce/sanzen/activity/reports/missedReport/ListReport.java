package saneforce.sanzen.activity.reports.missedReport;

import java.util.List;

public class ListReport { private String type; // "Doctor", "Chemist", "Stockist", "Unlisted"
    private int total;
    private int visited;
    private int missed;
    private List<GraphReport> innerList; // list for inner RecyclerView

    public ListReport(String type, int total, int visited, int missed, List<GraphReport> innerList) {
        this.type = type;
        this.total = total;
        this.visited = visited;
        this.missed = missed;
        this.innerList = innerList;
    }

    public String getType() { return type; }
    public int getTotal() { return total; }
    public int getVisited() { return visited; }
    public int getMissed() { return missed; }
    public List<GraphReport> getInnerList() { return innerList; }
}
}
