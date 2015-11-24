package jp.gr.java_conf.mitchibu.simplecalendar;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.widget.RemoteViews;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;

public class CalendarWidget extends AppWidgetProvider {
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		for(int appWidgetId : appWidgetIds) {
			updateAppWidget(context, appWidgetManager, appWidgetId);
		}
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
	}

	@Override
	public void onEnabled(Context context) {
	}

	@Override
	public void onDisabled(Context context) {
	}

	static void updateAppWidgets(Context context, AppWidgetManager appWidgetManager) {
		ComponentName cn = new ComponentName(context, CalendarWidget.class);
		int[] appWidgetIds = appWidgetManager.getAppWidgetIds(cn);
		for(int appWidgetId : appWidgetIds) {
			updateAppWidget(context, appWidgetManager, appWidgetId);
		}
	}

	static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
		final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.calendar_widget);
		final File file = get(context);
		if(file != null) {
			final Uri.Builder builder = new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT).authority(context.getPackageName()).path(file.getPath());
			views.setImageViewUri(android.R.id.content, builder.build());
			appWidgetManager.updateAppWidget(appWidgetId, views);
		}
	}

	static File get(Context context) {
		final Calendar now = Calendar.getInstance();
		final File file = new File(context.getFilesDir(), "test.png");
		if(!isUpdate(file, now)) return file;

		final Resources res = context.getResources();
		final Bitmap bm = Bitmap.createBitmap(
				res.getDimensionPixelSize(R.dimen.widget_width),
				res.getDimensionPixelSize(R.dimen.widget_height),
				Bitmap.Config.ARGB_8888);
		final Canvas c = new Canvas(bm);
		final CalendarDrawable d = new CalendarDrawable(now, Color.TRANSPARENT);
		d.setTypeface(Typeface.createFromAsset(context.getAssets(), "akaDora.ttf"));
		d.draw(c);

		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
			bm.compress(Bitmap.CompressFormat.PNG, 100, out);
			return file;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if(out != null) try {
				out.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	static boolean isUpdate(File file, Calendar now) {
		if(!file.exists()) return true;

		Calendar modified = Calendar.getInstance();
		modified.setTimeInMillis(file.lastModified());
		return now.get(Calendar.YEAR) != modified.get(Calendar.YEAR) || now.get(Calendar.MONTH) != modified.get(Calendar.MONTH) || now.get(Calendar.DAY_OF_MONTH) != modified.get(Calendar.DAY_OF_MONTH);
	}
}
