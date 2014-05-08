package seaice.app.sharesight.data;

public class ColumnMeta {

	private int mHeight;
	private int mLeftId;
	private int mTopId;
	private int mColumn;

	public int getHeight() {
		return mHeight;
	}

	public int getLeftId() {
		return mLeftId;
	}

	public int getTopId() {
		return mTopId;
	}

	public int getColumn() {
		return mColumn;
	}

	public void addHeight(int height) {
		this.mHeight += height;
	}
	
	public void setHeight(int height) {
		this.mHeight = height;
	}

	public void setLeftId(int leftId) {
		this.mLeftId = leftId;
	}

	public void setTopId(int topId) {
		this.mTopId = topId;
	}

	public void setColumn(int column) {
		this.mColumn = column;
	}

	public static final int PARENT_LEFT = -1;
	public static final int PARENT_TOP = -3;
	public static final int INVALID_LEFT = -3;
}
