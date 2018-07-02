package edu.cnm.deepdive.rps.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import edu.cnm.deepdive.rps.model.Breed;

public class TerrainView extends View {

  private static final float MAX_HUE = 360;
  private static final float SATURATION = 1;
  private static final float BRIGHTNESS = 0.85f;
  private static final int[] BREED_COLORS = new int[Breed.values().length];
  private static final int BACKGROUND_COLOR = Color.BLACK;

  private boolean drawing = false;
  private Breed[][] cells;
  private Paint paint;

  static {
    float[] hsv = {0, SATURATION, BRIGHTNESS};
    float hueInterval = MAX_HUE / BREED_COLORS.length;
    for (int i = 0; i < BREED_COLORS.length; i++) {
      hsv[0] = hueInterval * i;
      BREED_COLORS[i] = Color.HSVToColor(hsv);
    }
  }

  {
    setWillNotDraw(false);
    paint = new Paint();
    paint.setStyle(Paint.Style.FILL);
  }

  public TerrainView(Context context) {
    super(context);
  }

  public TerrainView(Context context,
      @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public TerrainView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public TerrainView(Context context, @Nullable AttributeSet attrs, int defStyleAttr,
      int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int width = getSuggestedMinimumWidth();
    int height = getSuggestedMinimumHeight();
    width = resolveSizeAndState(getPaddingLeft() + getPaddingRight() + width, widthMeasureSpec, 0);
    height = resolveSizeAndState(getPaddingTop() + getPaddingBottom() + height, heightMeasureSpec, 0);
    int size = Math.max(width, height);
    setMeasuredDimension(size, size);

  }

  @Override
  protected void onDraw(Canvas canvas) {
    if (cells != null) {
      drawing = true;
      float cellSize = Math.min((float) getHeight() / cells.length, (float) getWidth() / cells[0].length);
      canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
      for (int i = 0; i < cells.length; i++) {
        for (int j = 0; j < cells[i].length; j++) {
          paint.setColor(BREED_COLORS[cells[i][j].ordinal()]);
          canvas.drawOval(cellSize * j, cellSize * i, cellSize * (j + 1), cellSize * (i + 1), paint);
        }
      }
      drawing = false;
    }
  }

  public void setCells(Breed[][] cells) {
    this.cells = cells;
  }

  public boolean isDrawing() {
    return drawing;
  }

}
