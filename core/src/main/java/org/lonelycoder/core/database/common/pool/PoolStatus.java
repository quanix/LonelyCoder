package org.lonelycoder.core.database.common.pool;

/**
 * @author : lihaoquan
 *
 * 池状态: 描述对象池内对象的大小数量
 */
public final class PoolStatus {

    private int maxSize; //连接池的最大数量
    private int minSize; //连接池的最小数量
    private int currentSize;//连接池当前大小
    private int usedCount;//连接池的连接使用量
    private int freeCount;//连接池的空闲连接量
    private long borrowCount;//对象借用数
    private long givebackCount;//对象归还数

    public PoolStatus(int maxSize, int minSize, int currentSize, int usedCount, int freeCount) {
        this.maxSize = maxSize;
        this.minSize = minSize;
        this.currentSize = currentSize;
        this.usedCount = usedCount;
        this.freeCount = freeCount;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public int getMinSize() {
        return minSize;
    }

    public void setMinSize(int minSize) {
        this.minSize = minSize;
    }

    public int getCurrentSize() {
        return currentSize;
    }

    public void setCurrentSize(int currentSize) {
        this.currentSize = currentSize;
    }

    public int getUsedCount() {
        return usedCount;
    }

    public void setUsedCount(int usedCount) {
        this.usedCount = usedCount;
    }

    public int getFreeCount() {
        return freeCount;
    }

    public void setFreeCount(int freeCount) {
        this.freeCount = freeCount;
    }

    public long getBorrowCount() {
        return borrowCount;
    }

    public void setBorrowCount(long borrowCount) {
        this.borrowCount = borrowCount;
    }

    public long getGivebackCount() {
        return givebackCount;
    }

    public void setGivebackCount(long givebackCount) {
        this.givebackCount = givebackCount;
    }

    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder(64);
        sb.append("{");
        sb.append("maxSize=").append(maxSize).append(",");
        sb.append("minSize=").append(minSize).append(",");
        sb.append("currentSize=").append(currentSize).append(",");
        sb.append("usedCount=").append(usedCount).append(",");
        sb.append("freeCount=").append(freeCount).append(",");
        sb.append("borrowCount=").append(borrowCount).append(",");
        sb.append("givebackCount=").append(givebackCount);
        sb.append("}");
        return sb.toString();
    }
}
