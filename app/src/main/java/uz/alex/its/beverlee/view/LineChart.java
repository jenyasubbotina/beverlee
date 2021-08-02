package uz.alex.its.beverlee.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSeekBar;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import uz.alex.its.beverlee.model.chart.LineChartItem;

public class LineChart extends AppCompatSeekBar {
    private List<LineChartItem> itemList;
    private List<Paint> paintList;
    private List<RectF> rectFList;
    private List<Path> pathList;

    private static final float[] leftRoundedArr = new float[] {
            15f, 15f,
            0f, 0f,
            0f, 0f,
            15f, 15f
    };
    private static final float[] rightRoundedArr = new float[] {
            0f, 0f,
            15f, 15f,
            15f, 15f,
            0f, 0f
    };

    public LineChart(final Context context) {
        super(context);
        this.itemList = new ArrayList<>();
    }

    public LineChart(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        super(context, attrs);
        this.itemList = new ArrayList<>();
    }

    public LineChart(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.itemList = new ArrayList<>();
    }

    public void initData(@Nullable final List<LineChartItem> itemList) {
        this.itemList = itemList;

        if (itemList == null) {
            return;
        }
        if (itemList.size() > 0) {
            this.paintList = new ArrayList<>();
            this.rectFList = new ArrayList<>();
            this.pathList = new ArrayList<>();

            for (int i = 0; i < itemList.size(); i++) {
                this.paintList.add(new Paint());
                this.rectFList.add(new RectF());
                this.pathList.add(new Path());
            }
        }
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    protected void onDraw(Canvas canvas) {
        if (itemList == null) {
            return;
        }
        if (paintList == null || rectFList == null) {
            return;
        }
        if (itemList.size() > 0 && paintList.size() > 0 && rectFList.size() > 0) {
            int lineChartWidth = getWidth();
            int lineChartHeight = getHeight();
            int thumbOffset = getThumbOffset();
            int lastPosition = 0;
            int itemWidth, itemRight;

            for (int i = 0; i < itemList.size(); i++) {
                paintList.get(i).setColor(itemList.get(i).getColor());

                itemWidth = (int) (itemList.get(i).getPercentage() * lineChartWidth / 100);
                itemRight = lastPosition + itemWidth;

                // for last item give right to progress item to the width
                if (i == itemList.size() - 1 && itemRight != lineChartWidth) {
                    itemRight = lineChartWidth;
                }
                rectFList.get(i).set(lastPosition, (float) thumbOffset / 2, itemRight, lineChartHeight - (float) thumbOffset / 2);

                if (itemList.size() == 1) {
                    canvas.drawRoundRect(rectFList.get(i), 12, 16, paintList.get(i));
                }
                else if (i == 0 || i == itemList.size() - 1) {
                    if (i == 0) {
                        Log.i(TAG, "onDraw: i == 0");
                        pathList.get(i).addRoundRect(rectFList.get(i), leftRoundedArr, Path.Direction.CW);
                    }
                    else if (i == itemList.size() - 1) {
                        Log.i(TAG, "onDraw: i == size - 1");
                        pathList.get(i).addRoundRect(rectFList.get(i), rightRoundedArr, Path.Direction.CW);
                    }
                    pathList.get(i).close();
                    canvas.drawPath(pathList.get(i), paintList.get(i));
                }
                else {
                    canvas.drawRect(rectFList.get(i), paintList.get(i));
                }
                lastPosition = itemRight;
            }
            super.onDraw(canvas);
        }
    }

    private static final String TAG = LineChart.class.toString();
}