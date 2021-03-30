package com.project.stonktracker

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.widget.RemoteViews
import android.widget.TextView
import androidx.localbroadcastmanager.content.LocalBroadcastManager

// TODO

/**
 * Implementation of App Widget functionality.
 */
class PortfolioValueWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int,
) {

    val widgetText = context.getString(R.string.appwidget_text)

    // Construct the RemoteViews object -- Get the proper layout
    val views = RemoteViews(context.packageName, R.layout.portfolio_value_widget)
    views.setTextViewText(R.id.textViewPortfolioWorth, widgetText)

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}