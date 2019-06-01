package com.ebr163.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * Created by ergashev on 12.03.17.
 */

public class FilledView extends View {

    public enum StartMode {
        LEFT(0), TOP(1), RIGHT(2), BOTTOM(3);

        private int mode;

        StartMode(int mode) {
            this.mode = mode;
        }

        public int getMode() {
            return mode;
        }
    }

    private int startPosition = 0;
    private int fillColor = Color.BLACK;
    private String text;
    private int radius = 0;
    private int textSize;
    private boolean isShowBorder;
    private int borderSize = 1;

    private final Path textPath = new Path();
    private final Path croppedProgressPath = new Path();
    private final Path croppedTextPath = new Path();
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int width;
    private int height;
    private float percent = 0.1F;
    private Path progressStrokePath = new Path();
    private Rect textBounds = new Rect();

    private final Region region = new Region();
    private final Region textRegion = new Region();

    public FilledView(Context context) {
        this(context, null);
    }

    public FilledView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FilledView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
        initText();
    }

    private void initAttrs(@Nullable AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.FilledView,
                    0, 0);
            try {
                fillColor = a.getColor(R.styleable.FilledView_fill_color, Color.BLACK);
                startPosition = a.getInteger(R.styleable.FilledView_start_mode, 0);
                text = a.getString(R.styleable.FilledView_text);
                textSize = a.getDimensionPixelSize(R.styleable.FilledView_textSize,
                        getContext().getResources().getDimensionPixelSize(R.dimen.defaultTextSize));
                radius = a.getDimensionPixelSize(R.styleable.FilledView_radius, 0);
                isShowBorder = a.getBoolean(R.styleable.FilledView_border_show, false);
            } finally {
                a.recycle();
            }
        }
    }

    private void initText() {
        paint.setTextSize(textSize);
        paint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int textHeight = 0;
        int textWidth = 0;

        if (text != null) {
            paint.getTextBounds(text, 0, text.length(), textBounds);
            textHeight = textBounds.height();
            textWidth = textBounds.width();
        }

        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == View.MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == View.MeasureSpec.AT_MOST) {
            width = Math.min(textWidth, widthSize);
        } else {
            width = textWidth;
        }

        if (heightMode == View.MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == View.MeasureSpec.AT_MOST) {
            height = Math.min(textHeight, heightSize);
        } else {
            height = textHeight;
        }

        int cx = width / 2;
        int cy = (height + textHeight) / 2;

        if (text != null) {
            paint.getTextPath(text, 0, text.length(), cx, cy, textPath);
        }
        progressStrokePath = getRoundRectPath(0, 0, width, height, radius);

        computePaths();
        setMeasuredDimension(width, height);
    }

    private Path getRoundRectPath(float left, float top, float right, float bottom, float radius) {
        region.set((int) left, (int) top, (int) right, (int) bottom);
        Path roundRectPath = new Path();
        RectF rectF = new RectF();
        rectF.set(left + borderSize, top + borderSize, right - borderSize, bottom - borderSize);
        roundRectPath.addRoundRect(rectF, radius, radius, Path.Direction.CCW);
        region.setPath(roundRectPath, region);
        return region.getBoundaryPath();
    }

    public void computeCroppedProgressPath() {
        if (startPosition == StartMode.RIGHT.getMode()) {
            region.set((int) (width * (1F - percent)), 0, width, height);
        } else if (startPosition == StartMode.LEFT.getMode()) {
            region.set(0, 0, (int) (width * percent), height);
        } else if (startPosition == StartMode.TOP.getMode()) {
            region.set(0, 0, width, (int) (height * percent));
        } else if (startPosition == StartMode.BOTTOM.getMode()) {
            region.set(0, (int) (height * (1F - percent)), width, height);
        }
        region.setPath(progressStrokePath, region);
        textRegion.setPath(textPath, region);
        region.op(textRegion, Region.Op.DIFFERENCE);
        croppedProgressPath.rewind();
        region.getBoundaryPath(croppedProgressPath);
    }

    public void computeCroppedTextPath() {
        if (startPosition == StartMode.RIGHT.getMode()) {
            region.set(0, 0, (int) (width * (1F - percent)), height);
        } else if (startPosition == StartMode.LEFT.getMode()) {
            region.set((int) (width * percent), 0, width, height);
        } else if (startPosition == StartMode.TOP.getMode()) {
            region.set(0, (int) (height * percent), width, height);
        } else if (startPosition == StartMode.BOTTOM.getMode()) {
            region.set(0, 0, width, (int) (height * (1F - percent)));
        }
        textRegion.setPath(textPath, region);
        croppedTextPath.rewind();
        textRegion.getBoundaryPath(croppedTextPath);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(fillColor);

        if (isShowBorder) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(borderSize);
            canvas.drawPath(progressStrokePath, paint);
        }

        paint.setStyle(Paint.Style.FILL);
        canvas.drawPath(croppedProgressPath, paint);
        canvas.drawPath(croppedTextPath, paint);
    }

    public void setProgress(float percent) {
        if (percent >= 0f && percent <= 1F) {
            this.percent = percent;
            computePaths();
            invalidate();
        }
    }

    public void setFillColor(int color) {
        this.fillColor = color;
        invalidate();
    }

    public void setText(String text) {
        this.text = text;
        requestLayout();
    }

    public void showBorder(boolean flag) {
        isShowBorder = flag;
        invalidate();
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
        requestLayout();
    }

    public void setRadius(int radius) {
        this.radius = radius;
        requestLayout();
    }

    public void setStartMode(StartMode startPosition) {
        this.startPosition = startPosition.getMode();
        requestLayout();
    }

    private void computePaths() {
        computeCroppedProgressPath();
        computeCroppedTextPath();
    }
}
