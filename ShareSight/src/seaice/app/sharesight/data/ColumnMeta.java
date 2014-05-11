package seaice.app.sharesight.data;

public class ColumnMeta {

    private int height;
    private int leftId;
    private int topId;
    private int column;

    public int getHeight() {
        return height;
    }

    public int getLeftId() {
        return leftId;
    }

    public int getTopId() {
        return topId;
    }

    public int getColumn() {
        return column;
    }

    public void addHeight(int height) {
        this.height += height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setLeftId(int leftId) {
        this.leftId = leftId;
    }

    public void setTopId(int topId) {
        this.topId = topId;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public static final int PARENT_LEFT = -1;
    public static final int PARENT_TOP = -3;
    public static final int INVALID_LEFT = -3;
}
