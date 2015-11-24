package jp.gr.java_conf.mitchibu.simplecalendar;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.MonthDisplayHelper;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CalendarDrawable extends Drawable {
	private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final Paint paintCurrent = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final MonthDisplayHelper helper;
	private final int currentDay;
	private final String yyyymmdd;
	private final int background;

	public CalendarDrawable(Calendar calendar, int color) {
		helper = new MonthDisplayHelper(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH));
		background = color;
		currentDay = calendar.get(Calendar.DAY_OF_MONTH);
		yyyymmdd = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.ENGLISH).format(calendar.getTime());

		paintCurrent.setColor(Color.argb(64, 0x03, 0xa9, 0xf4));
	}

	public void setTypeface(Typeface typeface) {
		paint.setTypeface(typeface);
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.drawColor(background);

		final int width = canvas.getWidth();
		final int height = canvas.getHeight();
		final int headHeight = (int)(height * 1.5f / 6.5f);
		paint.setTextSize(calcTextSize(paint, yyyymmdd, width, headHeight));
		Paint.FontMetrics m = paint.getFontMetrics();
		canvas.drawText(yyyymmdd, width - paint.measureText(yyyymmdd), -m.ascent, paint);

		final int rows = 5;
		final int cols = 7;
		final int cellWidth = width / cols;
		final int cellHeight = (height - headHeight) / rows;
		paint.setTextSize(calcTextSize(paint, "88", cellWidth, cellHeight));
		m = paint.getFontMetrics();
		for(int row = 0, y = headHeight; row < rows; ++ row, y += cellHeight) {
			for(int col = 0, x = 0; col < cols; ++ col, x += cellWidth) {
				final boolean inCurrentMonth = helper.isWithinCurrentMonth(row, col);
				final int alpha = inCurrentMonth ? 255 : 128;
				int color = Color.argb(alpha, 0, 0, 0);
				if(col == 0) color = Color.argb(alpha, 255, 0, 0);
				else if(col == 6) color = Color.argb(alpha, 0, 0, 255);
				paint.setColor(color);

				final int day = helper.getDayAt(row, col);
				String n = Integer.toString(day);
				canvas.drawText(n, x + cellWidth - paint.measureText(n), y - m.ascent, paint);

				if(inCurrentMonth && day == currentDay) {
					canvas.drawCircle(x + cellWidth - paint.measureText(n) / 2, y + cellHeight / 2, cellHeight * 0.7f, paintCurrent);
				}
			}
		}
	}

	@Override
	public void setAlpha(int alpha) {
	}

	@Override
	public void setColorFilter(ColorFilter colorFilter) {
	}

	@Override
	public int getOpacity() {
		return 0;
	}

	private static float calcTextSize(Paint paint, String text, float width, float height) {
		final float size = paint.getTextSize();
		return Math.min(size * width / paint.measureText(text), size * height / paint.getFontMetrics(null));
	}
}
