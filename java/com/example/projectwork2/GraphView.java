package com.example.projectwork2;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

public class GraphView extends View {
    private List<Float> values = new ArrayList<>();
    private List<String> labels = new ArrayList<>();
    private Paint barPaint, textPaint, labelPaint;
    private int[] barColors; // Array to store different bar colors

    public GraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        barPaint = new Paint();
        barPaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(26f);
        textPaint.setTextAlign(Paint.Align.CENTER);

        labelPaint = new Paint();
        labelPaint.setColor(Color.WHITE);
        labelPaint.setTextSize(42f);
        labelPaint.setTextAlign(Paint.Align.CENTER);

        // Initialize bar colors with different shades of blue
        barColors = new int[]{
                Color.parseColor("#0D47A1"), // Darkest Blue
                Color.parseColor("#1565C0"),
                Color.parseColor("#1976D2"),
                Color.parseColor("#1E88E5"),
                Color.parseColor("#2196F3"), // Default Blue
                Color.parseColor("#64B5F6"),
                Color.parseColor("#90CAF9"),
                Color.parseColor("#BBDEFB"),
                Color.parseColor("#BBDEFB"), // Lightest Blue
        };
    }

    public void setData(List<Float> values, List<String> labels) {
        this.values = values;
        this.labels = labels;
        invalidate(); // Redraw the view
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (values.isEmpty()) return;

        float maxValue = getMaxValue();
        float barWidth = getWidth() / (values.size() * 2f);
        float startX = barWidth / 2;
        float bottom = getHeight() - 50;

        // Draw title
        canvas.drawText("Monthly Expenses", getWidth()/2, 40, labelPaint);

        for (int i = 0; i < values.size(); i++) {
            float height = (values.get(i) / maxValue) * (getHeight() - 150);
            float left = startX + i * barWidth * 2;
            float top = getHeight() - height - 50;
            float right = left + barWidth;

            // Set bar color based on index, cycling through the colors
            barPaint.setColor(barColors[i % barColors.length]);

            // Draw bar
            canvas.drawRect(new RectF(left, top, right, bottom), barPaint);

            // Draw value above bar
            canvas.drawText(String.format("Rs.%.2f", values.get(i)),
                    (left + right)/2, top - 10, textPaint);

            // Draw month label
            canvas.drawText(labels.get(i), (left + right)/2, bottom + 40, textPaint);
        }
    }

    private float getMaxValue() {
        float max = 0;
        for (float value : values) {
            if (value > max) max = value;
        }
        return max == 0 ? 1 : max; // Avoid division by zero
    }
}