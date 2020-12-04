package it.giovanni.arkivio.customview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import it.giovanni.arkivio.R;
import it.giovanni.arkivio.R.styleable;

import java.util.List;

import static androidx.core.content.ContextCompat.getColor;

public class TimelineView extends View {

    public static final int DISCRETE = 1;
    public static final int CONTINOUS = 2;

    private int textColor;
    private int textStyle;
    private int indexValue = 0;
    private int progressLineColor;
    private int textSelectedColor;
    private int textSelectedStyle;
    private int backgroundLineColor;
    private int indicatorShadowColor;
    private int indicatorBackgroundColor;

    private float min;
    private float max;
    private float left;
    private float right;
    private float delta;
    private float textSize;
    private float progress;
    private float paddingEnd;
    private float paddingStart;
    private float sectionOffset;
    private float length = 0.0F;
    private float progressLineSize;
    private float textSelectedSize;
    private float indicatorX = 0.0F;
    private float backgroundLineSize;
    private float radiusProgressSize;

    private boolean isDragging = false;

    private Rect rectText;
    private List<String> values;
    private Drawable imageProgress;
    private String textFont = "";
    private String textSelectedFont = "";
    private TimelineViewListener listener;

    private Paint paintText;
    private Paint paintProgressLine;
    private Paint paintTextSelected;
    private Paint paintBackgroundLine;
    private Paint paintProgressIndicator;

    public void setTimelineViewListener(TimelineView.TimelineViewListener listener) {
        this.listener = listener;
    }

    public TimelineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, styleable.TimelineView);
        this.initGlobals();
        this.initAttrs(context, typedArray);
        this.initPaints(context);
    }

    private void initAttrs(Context context, TypedArray typedArray) {

        this.indicatorBackgroundColor = typedArray.getColor(styleable.TimelineView_indicator_background_color, getColor(context, R.color.verde));
        this.indicatorShadowColor = typedArray.getColor(styleable.TimelineView_indicator_shadow_color, getColor(context, R.color.verde));
        this.backgroundLineColor = typedArray.getColor(styleable.TimelineView_background_line_color, getColor(context, R.color.grey_3));
        this.progressLineColor = typedArray.getColor(styleable.TimelineView_progress_line_color, getColor(context, R.color.verde));
        this.textSelectedColor = typedArray.getColor(styleable.TimelineView_text_selected_color, getColor(context, R.color.verde));
        this.textColor = typedArray.getColor(styleable.TimelineView_text_color, getColor(context, R.color.verde));

        this.radiusProgressSize = (float) typedArray.getDimensionPixelSize(styleable.TimelineView_indicator_circle_radius, 0);
        this.backgroundLineSize = (float) typedArray.getDimensionPixelSize(styleable.TimelineView_background_line_size, 0);
        this.progressLineSize = (float) typedArray.getDimensionPixelSize(styleable.TimelineView_progress_line_size, 0);
        this.textSelectedSize = (float) typedArray.getDimensionPixelSize(styleable.TimelineView_text_selected_size, 0);
        this.paddingStart = (float) typedArray.getDimensionPixelSize(styleable.TimelineView_padding_start, 0);
        this.paddingEnd = (float) typedArray.getDimensionPixelSize(styleable.TimelineView_padding_end, 0);
        this.textSize = (float) typedArray.getDimensionPixelSize(styleable.TimelineView_text_size, 0);

        this.textSelectedStyle = typedArray.getInt(styleable.TimelineView_text_selected_style, 0);
        this.textStyle = typedArray.getInt(styleable.TimelineView_text_style, 0);

        this.textSelectedFont = typedArray.getString(styleable.TimelineView_text_selected_font);
        this.textFont = typedArray.getString(styleable.TimelineView_text_font);

        this.imageProgress = typedArray.getDrawable(styleable.TimelineView_icon_progress);
    }

    private void initGlobals() {
        this.min = 0.0F;
        this.max = 100.0F;
        this.delta = this.max - this.min;
        this.rectText = new Rect();
    }

    private void initPaints(Context context) {
        this.paintBackgroundLine = new Paint();
        this.paintBackgroundLine.setStrokeWidth(this.backgroundLineSize);
        this.paintBackgroundLine.setColor(this.backgroundLineColor);
        this.paintProgressLine = new Paint();
        this.paintProgressLine.setStrokeWidth(this.progressLineSize);
        this.paintProgressLine.setColor(this.progressLineColor);
        this.paintText = new Paint();
        this.paintText.setTextSize(this.textSize);
        this.paintText.setColor(this.textColor);
        Typeface textFont;
        if (this.textFont != null && !this.textFont.equalsIgnoreCase("")) {
            try {
                textFont = Typeface.createFromAsset(context.getAssets(), "fonts/" + this.textFont + ".ttf");
                this.paintText.setTypeface(Typeface.create(textFont, Typeface.NORMAL));
            } catch (Exception var4) {
                this.paintText.setTypeface(Typeface.create(Typeface.DEFAULT, this.textStyle));
            }
        } else
            this.paintText.setTypeface(Typeface.create(Typeface.DEFAULT, this.textStyle));

        this.paintText.getTextBounds("0123456789€", 0, "0123456789€".length(), this.rectText);
        this.paintTextSelected = new Paint();
        this.paintTextSelected.setTextSize(this.textSelectedSize);
        this.paintTextSelected.setColor(this.textSelectedColor);
        if (this.textSelectedFont != null && !this.textSelectedFont.equalsIgnoreCase("")) {
            try {
                textFont = Typeface.createFromAsset(context.getAssets(), "fonts/" + this.textSelectedFont + ".ttf");
                this.paintTextSelected.setTypeface(Typeface.create(textFont, Typeface.NORMAL));
            } catch (Exception var3) {
                this.paintTextSelected.setTypeface(Typeface.create(Typeface.DEFAULT, this.textSelectedStyle));
            }
        } else
            this.paintTextSelected.setTypeface(Typeface.create(Typeface.DEFAULT, this.textSelectedStyle));

        this.paintTextSelected.getTextBounds("0123456789€", 0, "0123456789€".length(), this.rectText);
        this.paintProgressIndicator = new Paint();
        this.paintProgressIndicator.setColor(this.indicatorBackgroundColor);
        this.paintProgressIndicator.setShadowLayer(6.0F, 0.0F, 2.0F, this.indicatorShadowColor);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.left = (float) this.getPaddingLeft();
        this.right = (float) (this.getMeasuredWidth() - this.getPaddingRight());
        float yTop = (float) (this.getMeasuredHeight() - this.getPaddingTop() - this.getPaddingBottom());
        this.length = this.right - this.left;
        this.drawTexts(canvas, yTop - this.radiusProgressSize, this.left, this.length);
        if (!this.isDragging)
            this.progress = this.getProgressOfValue(this.indexValue);

        float xEnd = this.getFinalX(this.left, this.length);
        if (xEnd + this.paddingStart == this.right) {
            canvas.drawLine(this.left, yTop, this.getXRight(), yTop, this.paintProgressLine);
            canvas.drawLine(this.getXRight(), yTop, this.getXRight(), yTop, this.paintBackgroundLine);
        } else {
            canvas.drawLine(this.left, yTop, xEnd + this.paddingStart, yTop, this.paintProgressLine);
            canvas.drawLine(xEnd + this.paddingStart, yTop, this.getXRight(), yTop, this.paintBackgroundLine);
        }

        this.drawProgressIndicator(canvas, this.length, yTop);
    }

    private float getXRight() {
        if (this.values != null && this.values.size() > 0) {
            int lastIndex = this.values.size() - 1;
            this.paintText.getTextBounds(this.values.get(lastIndex), 0, this.values.get(lastIndex).length(), this.rectText);
            return (float) (this.getMeasuredWidth() - this.getPaddingRight()) + (float) this.rectText.width() / 2.0F;
        } else
            return (float) (this.getMeasuredWidth() - this.getPaddingRight());
    }

    private float getFinalX(float xLeft, float length) {
        return xLeft + length / this.delta * (this.progress - this.min);
    }

    public void setValues(List<String> values) {
        this.values = values;
        this.requestLayout();
        this.invalidate();
    }

    private void drawProgressIndicator(Canvas canvas, float length, float yTop) {
        float xIndicator = this.left + length / this.delta * Math.abs(this.progress - this.min) + this.paddingStart;
        canvas.drawCircle(xIndicator, yTop, this.radiusProgressSize, this.paintProgressIndicator);
        Bitmap imageIndicator = drawableToBitmap(this.imageProgress);
        int imageHeight = imageIndicator.getHeight();
        int imageWidth = imageIndicator.getWidth();
        canvas.drawBitmap(imageIndicator, xIndicator - (float) (imageWidth / 2), yTop - (float) (imageHeight / 2), this.paintProgressIndicator);
    }

    private void drawTexts(Canvas canvas, float yTop, float xLeft, float length) {
        float ySection = yTop - (float) this.rectText.height();
        this.sectionOffset = (length - this.paddingStart - this.paddingEnd) * 1.0F / (float) (this.values.size() - 1);

        for (int i = 0; i < this.values.size(); ++i) {
            this.paintText.getTextBounds(this.values.get(i), 0, this.values.get(i).length(), this.rectText);
            float xSection = xLeft + (float) i * this.sectionOffset;
            if (i == this.indexValue)
                canvas.drawText(this.values.get(i), this.paddingStart + (xSection - (float) this.rectText.width() / 2.0F), ySection, this.paintTextSelected);
            else
                canvas.drawText(this.values.get(i), this.paddingStart + (xSection - (float) this.rectText.width() / 2.0F), ySection, this.paintText);
        }

    }

    public void setValue(String value, int type) {
        if (this.values != null && this.values.size() != 0) {
            this.indexValue = 0;

            for (int i = 0; i < this.values.size(); ++i) {
                if (this.values.get(i).equalsIgnoreCase(value)) {
                    if (this.listener != null)
                        this.listener.onFinishDragging(i, this.values.get(i));

                    if (type == 1) {
                        this.indexValue = i;
                        break;
                    }
                }
            }
            this.requestLayout();
            this.invalidate();
        }
    }

    private float getProgressOfValue(int index) {
        float length = this.right - this.left;
        float xSection = this.left + (float) index * this.sectionOffset;
        return this.min + (xSection - this.left) * this.delta / length;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap;
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable)drawable;
            if (bitmapDrawable.getBitmap() != null)
                return bitmapDrawable.getBitmap();
        }

        if (drawable.getIntrinsicWidth() > 0 && drawable.getIntrinsicHeight() > 0)
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        else
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent event) {
        boolean isTrackTouched;
        float dx = 0.0F;
        switch(event.getActionMasked()) {
            case 0:
                this.performClick();
                this.getParent().requestDisallowInterceptTouchEvent(true);
                this.isIndicatorTouched(event);
                isTrackTouched = this.isTrackTouched(event);
                if (isTrackTouched) {
                    this.isDragging = true;
                    this.indicatorX = event.getX();

                    if (this.indicatorX < this.left)
                        this.indicatorX = this.left;

                    if (this.indicatorX > this.right)
                        this.indicatorX = this.right;

                    this.progress = this.calculateProgress();
                    this.requestLayout();
                    this.invalidate();
                } else
                    this.isDragging = false;

                dx = this.indicatorX - event.getX();
                break;
            case 1:
            case 3:
                this.getParent().requestDisallowInterceptTouchEvent(false);
                this.isDragging = false;
                this.autoAdjustSection();
                break;
            case 2:
                if (this.isDragging) {
                    this.indicatorX = event.getX() + dx;

                    if (this.indicatorX < this.left)
                        this.indicatorX = this.left;

                    if (this.indicatorX > this.right)
                        this.indicatorX = this.right;

                    this.progress = this.calculateProgress();
                    this.autoAdjustSection();
                    this.requestLayout();
                    this.invalidate();
                }
        }

        return this.isDragging || super.onTouchEvent(event);
    }

    private boolean isIndicatorTouched(MotionEvent event) {
        float distance = this.length / this.delta * (this.progress - this.min) + this.paddingStart;
        float x = this.left + distance;
        float y = (float) this.getMeasuredHeight() / 2.0F;
        return (event.getX() - x) * (event.getX() - x) + (event.getY() - y) * (event.getY() - y) <= this.left * this.left;
    }

    private boolean isTrackTouched(MotionEvent event) {
        return event.getX() >= (float) this.getPaddingLeft() && event.getX() <= (float) (this.getMeasuredWidth() - this.getPaddingRight()) && event.getY() >= (float) this.getPaddingTop() && event.getY() <= (float) (this.getMeasuredHeight() - this.getPaddingBottom());
    }

    private float calculateProgress() {
        return (this.indicatorX - this.left) * this.delta / this.length + this.min;
    }

    private void autoAdjustSection() {
        float x;
        for (int i = 0; i < this.values.size(); ++i) {
            x = (float) i * this.sectionOffset + this.left;
            if (x <= this.indicatorX && this.indicatorX - x <= this.sectionOffset) {
                this.setValue(this.values.get(i), 1);
                break;
            }
        }
    }

    public interface TimelineViewListener {
        void onFinishDragging(int var1, String var2);
    }
}