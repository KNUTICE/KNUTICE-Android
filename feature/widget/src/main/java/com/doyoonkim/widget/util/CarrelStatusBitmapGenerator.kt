package com.doyoonkim.widget.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.util.TypedValue
import androidx.annotation.ColorInt
import androidx.core.graphics.toColorInt
import androidx.core.graphics.createBitmap


// Color Value enums based on Status
enum class CarrelRoomOccupancyState(@ColorInt val color: Int, val label: String) {
    SPACIOUS("#157A43".toColorInt(), "원활"),
    MODERATE("#F59E0B".toColorInt(), "보통"),
    CROWDED("#EF4444".toColorInt(), "혼잡");

    companion object {
        fun fromOccupancyRatio(ratio: Float): CarrelRoomOccupancyState {
            return when {
                // Edge case (potential error under Float math)
                ratio.isNaN() -> SPACIOUS
                ratio < 0.5f -> SPACIOUS
                ratio < 0.8f -> MODERATE
                else -> CROWDED
            }
        }
    }
}

object CarrelStatusBitmapGenerator {

    /**
     * Generate Bitmap for Ring-style chart for Carrel Room Occupancy Status
     * @param widgetSizeDp desired bounding box size of the widget component
     */
    fun generateStatusBitmap(
        context: Context,
        occupiedSeat: Int,
        totalSeat: Int,
        widgetSizeDp: Float = 100f
    ): Bitmap {

        // Carrel Room Status Calculation
        val divisor = if (totalSeat <= 0) 1 else totalSeat
        val ratio = (occupiedSeat.toFloat() / divisor.toFloat()).coerceIn(0.0f, 1.0f)
        val status = CarrelRoomOccupancyState.fromOccupancyRatio(ratio)

        // Density Conversion (dp to px)
        val metrics = context.resources.displayMetrics
        val sizeInPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, widgetSizeDp, metrics
        ).toInt()
        val chartStrokeWidthInPx = sizeInPx * 0.12f     // Set Stroke Width to 12% of total size.

        // Memory Allocation
        val resultBitmap = createBitmap(sizeInPx, sizeInPx)
        val canvas = Canvas(resultBitmap)

        // Paint Configuration
        val trackPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = chartStrokeWidthInPx
            color = "#E0E0E0".toColorInt()
            strokeCap = Paint.Cap.ROUND
        }

        val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = chartStrokeWidthInPx
            color = status.color
            strokeCap = Paint.Cap.ROUND
        }

        val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = status.color
            textSize = sizeInPx * 0.28f     // Set Text size to 28% of widget size.
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        // Geometry Configuration (Prevent Clipping by offsetting by half stroke.)
        val padding = chartStrokeWidthInPx / 2f
        // RectF holds float coordinates (left, top, right, bottom)
        val bounds = RectF(padding, padding, sizeInPx - padding, sizeInPx - padding)

        // Rasterization
        // Draw Track
        canvas.drawArc(bounds, 120f, 300f, false, trackPaint)
        // Draw Progress
        val progressAngle = ratio * 300f
        canvas.drawArc(bounds, 120f, progressAngle, false, progressPaint)
        // Draw Label
        val labelMetric = labelPaint.fontMetrics
        val centerVertical = (sizeInPx / 2) - ((labelMetric.descent + labelMetric.ascent) / 2)
        canvas.drawText(status.label, sizeInPx / 2f, centerVertical, labelPaint)

        return resultBitmap
    }

}

// Preview
