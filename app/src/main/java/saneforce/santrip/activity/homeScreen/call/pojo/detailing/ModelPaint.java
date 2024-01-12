package saneforce.santrip.activity.homeScreen.call.pojo.detailing;

public class ModelPaint
{
    int startArrow;
    int endArrow;
    int pathLine;

    public ModelPaint(int startArrow, int endArrow, int pathLine)
    {
        this.startArrow = startArrow;
        this.endArrow = endArrow;
        this.pathLine = pathLine;
    }

    public int getStartArrow() {
        return startArrow;
    }

    public void setStartArrow(int startArrow) {
        this.startArrow = startArrow;
    }

    public int getEndArrow() {
        return endArrow;
    }

    public void setEndArrow(int endArrow) {
        this.endArrow = endArrow;
    }

    public int getPathLine() {
        return pathLine;
    }

    public void setPathLine(int pathLine) {
        this.pathLine = pathLine;
    }
}
