package uz.alex.its.beverlee.model.chart;

public class LineChartItem {
    private final int color;
    private final float percentage;

    public LineChartItem(int color, float percentage) {
        this.color = color;
        this.percentage = percentage;
    }

    public int getColor() {
        return color;
    }

    public float getPercentage() {
        return percentage;
    }
}
