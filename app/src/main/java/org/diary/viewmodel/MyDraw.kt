package org.diary.viewmodel

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import org.diary.R
import org.diary.common.MyApplication
import org.diary.common.MyCommon
import org.diary.model.Book
import org.diary.model.NoteShow
import org.diary.utils.MyCalendar
import org.diary.utils.MyLogger
import kotlin.collections.ArrayList

class MyDraw constructor() {
    val ADDITIONAL_SHOW_DAYS = 10
    val LINE_WIDTH_COOR = MyApplication?.instance?.resources?.getDimension(R.dimen.s_002)
    val LINE_WIDTH_NORMAL = MyApplication?.instance?.resources?.getDimension(R.dimen.s_003)
    val LINE_WIDTH_THIN = MyApplication?.instance?.resources?.getDimension(R.dimen.s_001)
    val MARKER_LENGTH_SHORT = MyApplication?.instance?.resources?.getDimension(R.dimen.s_007)
    val TEXT_SIZE = MyApplication?.instance?.resources?.getDimension(R.dimen.t_018)
    val POINT_RADIUS = LINE_WIDTH_NORMAL!! * 2
    val UNIT_NAME_LENGTH = 30f

    val myApplication = MyApplication.instance
    var notes: MutableList<NoteShow> = ArrayList()

    var canvas: Canvas? = null
    var paintCoor: Paint? = null
    var paintMain: Paint? = null
    var paintThin: Paint? = null
    var colorMain: Int? = null
    var paintText: Paint? = null
    var bitmap: Bitmap? = null
    var book: Book? = null

    var offsetX0 = 0f
    var offsetY0 = 0f
    var offsetNameY = TEXT_SIZE!! * 2
    var offsetGraphBeginX = 0f
    var offsetGraphBeginY = 0f
    var minValue = 0f
    var maxValue = 0f
    var width: Float = 0f
    var height: Float = 0f
    var imageWidth: Int = 0
    var imageHeight: Int = 0
    var stepX = 50f
    var stepY = 50f
    var x0 = 0f
    var y0 = 0f
    var markerLength = MARKER_LENGTH_SHORT
    var currentType: Int = MyCommon.TYPE_GRAPH
    var reuseCanvas = false
    var stepDays = 1
    var dateType = MyCalendar.DATE_TYPE_DDMM
    var dayFirst: Int = 0  // first day to show
    var daysAll: Int = 0  // all days to show
    var dayMin: Int = 0  // Min day in notes
    var dayMax: Int = 0  // Min day in notes

    fun prepare(
        colorMain: Int,
        notes: MutableList<NoteShow>,
        stepDays: Int,
        imageHeight: Int,
        dayBegin: Int,
        currentType: Int,
        reuseCanvas: Boolean
    ) {
        MyLogger.d("MyDraw - prepare stepDays=" + stepDays + " dayBegin=" + dayBegin + " imageHeight=" + imageHeight + " notes.size=" + notes.size)
        this.reuseCanvas = reuseCanvas
        this.colorMain = colorMain
        this.notes = notes
        this.stepDays = stepDays
        this.imageHeight = imageHeight
        this.currentType = currentType
        width = imageWidth.toFloat()
        height = imageHeight.toFloat()

        paintMain = Paint()
        paintMain?.color = colorMain
        paintMain?.strokeWidth = LINE_WIDTH_NORMAL!!

        paintCoor = Paint()
        paintCoor?.color = MyApplication.instance?.resources!!
            .getColor(R.color.newText, null)
        paintCoor?.strokeWidth = LINE_WIDTH_COOR!!

        paintThin = Paint()
        paintThin?.color = MyApplication.instance?.resources!!
            .getColor(R.color.colorGray, null)
        paintThin?.strokeWidth = LINE_WIDTH_THIN!!

        paintText = Paint()
        paintText?.color = MyApplication.instance?.resources!!
            .getColor(R.color.colorGray, null)
        paintText?.strokeWidth = 8f
        paintText?.textSize = TEXT_SIZE!!

        // offsets
        offsetX0 = paintText!!.measureText("XXXXXX")
        offsetGraphBeginX = stepX
        offsetY0 = TEXT_SIZE * 3

        // read notes
        if (notes != null && !notes.isEmpty()) {
            MyLogger.d("MyDraw - prepare records=" + notes.size)
            dayMin = dayBegin //findDayMin()
            dayMax = findDayMax()
            minValue = findMinValue()
            maxValue = findMaxValue()

            MyLogger.d("MyDraw - prepare dayMin=$dayMin minValue=$minValue maxValue=$maxValue")
        }
    }

    fun findDayMin(): Int {
        var day = MyCommon.MAX_INT
        if (notes == null || notes.isEmpty()) {
            return 0
        }
        for (note in notes) {
            if (note.day < day) {
                day = note.day
            }
        }
        return day
    }

    fun findDayMax(): Int {
        var day = 0
        if (notes == null || notes.isEmpty()) {
            return 0
        }
        for (note in notes) {
            if (note.day > day) {
                day = note.day
            }
        }
        return day
    }

    fun findMinValue(): Float {
        var value = MyCommon.MAX_FLOAT
        if (notes == null || notes.isEmpty()) {
//            MyLogger.d("MyDraw - findMinValue 0")
            return 0f
        }
        for (note in notes) {
            var valueNow = note.value
//            MyLogger.d("MyDraw - findMinValue now=" + valueNow + " value=" + value)
            if (valueNow < value) {
                value = valueNow
            }
        }
        return value
    }

    fun findMaxValue(): Float {
        var value = 0f
        if (notes == null || notes.isEmpty()) {
            return 0f
        }
        for (note in notes) {
            var valueNow = note.value
            if (valueNow > value) {
                value = valueNow
            }
        }
        return value
    }

    fun drawBitmap(): Bitmap {
        dayFirst = dayMin
        MyLogger.d("MyDraw - drawBitmap dayFirst(0)=" + dayFirst)

        imageWidth = (dayMax - dayMin + ADDITIONAL_SHOW_DAYS) *
                stepX.toInt() + offsetGraphBeginX.toInt()
        MyLogger.d("MyDraw - drawBitmap imageWidth=" + imageWidth + " days=" + (dayMax - dayMin))
//        if (imageWidth > MyCommon.MAX_IMAGE_WIDTH){
//            imageWidth = MyCommon.MAX_IMAGE_WIDTH
//        }
        imageWidth = MyCommon.MAX_IMAGE_WIDTH

        if (!reuseCanvas || bitmap == null) {
            bitmap = Bitmap.createBitmap(
                imageWidth, imageHeight, Bitmap.Config.RGB_565
            )
        }
        MyLogger.d("MyDraw - drawBitmap reuseCanvas=" + reuseCanvas);

        val bitmap1 = bitmap

        if (!reuseCanvas || bitmap == null) {
            canvas = Canvas(bitmap1!!)
            canvas?.drawColor(myApplication?.resources?.getColor(R.color.newBackground, null)!!);
        }
        MyLogger.d("MyDraw - drawBitmap reuseCanvas=" + reuseCanvas + " canvas created canvas=" + canvas);

        width = imageWidth.toFloat()
        height = imageHeight.toFloat()

//        paintMain?.color = MyApplication.instance?.resources!!
//            .getColor(R.color.newText, null)

        defineStepY()
        drawMyChart()
        if (!reuseCanvas) {
            showCoordinates()
        }
        return bitmap1!!
    }

    fun drawEmptyBitmap(): Bitmap {
        MyLogger.d("MyDraw - drawEmptyBitmap")
        bitmap = Bitmap
            .createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888)
        val bitmap1 = bitmap

        canvas = Canvas(bitmap1!!)
        return bitmap1
    }

    fun myY(y: Float): Float {
        return height - y
    }

    fun myCircle(x: Float, y: Float, radius: Float, paint: Paint) {
        canvas?.drawCircle(x + offsetX0, myY(y + offsetY0), radius, paint)
    }

    fun myLine(x1: Float, y1: Float, x2: Float, y2: Float, paint: Paint) {
//        MyLogger.d("MyDraw - myLine x1=" + x1 + "/" + (x1 + offsetX).toString() + " y1=" + y1 + "/" + myY(y1 + offsetY) + " y2=" + y2 + "/" + myY(y2 + offsetY))

        canvas?.drawLine(
            x1 + offsetX0, myY(y1 + offsetY0),
            x2 + offsetX0, myY(y2 + offsetY0), paint
        )
    }

    fun lineToPoint(x: Float, y: Float, paint: Paint) {
        myLine(x0, y0, x, y, paint!!)
        x0 = x
        y0 = y
    }

    fun myRect(x1: Float, y1: Float, x2: Float, y2: Float, paint: Paint) {
        canvas?.drawRect(
            x1 + offsetX0, myY(y1 + offsetY0),
            x2 + offsetX0, myY(y2 + offsetY0), paint!!
        )
    }


    fun rectanleToPoint(x: Float, y: Float, paint: Paint) {
        myRect(x, y, x + stepX / 2, 0f, paint!!)
    }

    fun myText(text: String, x: Float, y: Float, paint: Paint) {
        canvas?.drawText(text, x + offsetX0, myY(y + offsetY0), paint)
    }

    fun defineStepY() {
        var deltaValue = maxValue!! - minValue!!
        MyLogger.d("MyDraw - defineStepY - deltaValue=" + deltaValue)
        if (deltaValue > 0) {
            stepY = ((height * 2) / 4) / deltaValue!!
            if (stepY > height / 4) {
                stepY = height / 4
            }
        } else {
            MyLogger.e("MyDraw - defineStepY - deltaValue=0")
            stepY = height / 2
        }
        offsetGraphBeginY = stepY
        MyLogger.d("MyDraw - defineStepY - stepY=" + stepY + " height=" + height)
    }

    fun initData() {
        offsetX0 = 0f
        offsetY0 = 0f
        offsetNameY = TEXT_SIZE!! * 2
        offsetGraphBeginX = 0f
        offsetGraphBeginY = 0f
        minValue = 0f
        maxValue = 0f
        width = 0f
        height = 0f
        imageWidth = 0
        imageHeight = 0
        stepX = myApplication?.screenWidth?.toFloat()!! /
                MyCommon.STEPS_IN_X
        if (stepX <= 0f){
            stepX = 50f
        }
        stepY = 50f
        x0 = 0f
        y0 = 0f
        markerLength = MARKER_LENGTH_SHORT
        currentType = MyCommon.TYPE_GRAPH
        reuseCanvas = false
        stepDays = 1
        dateType = MyCalendar.DATE_TYPE_DDMM
        dayFirst = 0  // first day to show
        daysAll = 0  // all days to show
        dayMin = 0  // Min day in notes
        dayMax = 0  // Min day in notes
    }

    fun drawMyChart() {
        MyLogger.d("MyDraw - drawMyChart - type=$currentType" + " stepDays=" + stepDays + " dayFirst=" + dayFirst)
        var x = 0f
        var y = 0f
        x0 = -99999f

        for (note in notes) {
            x = stepX + stepX * ((note.day - dayMin) / stepDays)
            y = offsetGraphBeginY + (note.value - minValue) * stepY
//            MyLogger.d("MyDraw - drawMyChart - x=$x y=$y" + " day=" + note.day + " value=" + note.value)
            if (currentType == MyCommon.TYPE_RECT) {
                rectanleToPoint(x, y, paintMain!!)
            } else if (currentType == MyCommon.TYPE_GRAPH) {
                if (x0 < 0) {
                    x0 = x
                    y0 = y
                    myCircle(x, y, POINT_RADIUS, paintMain!!)
                    continue
                }
                lineToPoint(x, y, paintMain!!)
                myCircle(x, y, POINT_RADIUS, paintMain!!)
            }
        }
    }

    fun showCoordinates() {
        MyLogger.d("MyDraw - showCoordinates height=" + height)
        // Coor lines
        myLine(0f, 0f, width, 0f, paintCoor!!)  //x
        myLine(0f, 0f, 0f, height, paintCoor!!) // y

        // X line
        var x = 0f + offsetGraphBeginX - paintText!!.measureText("")
        var xLast = 0f
        var lengthLast = 0f
        var dayMarker = dayFirst
        MyLogger.d("MyDraw - showCoordinates dayMarker=" + dayMarker)
        while (x < width) {
            if (x - xLast >= lengthLast * 1.5) {
                var nameMarkerX = MyCalendar.dayToDate(dayMarker, dateType)
                lengthLast = paintText!!.measureText(nameMarkerX + " ")
                var xText = x - lengthLast / 2
                myText(nameMarkerX, xText, 0f - offsetNameY!!, paintText!!)
                xLast = x
            }
            myLine(x, 0f, x, markerLength!!, paintThin!!)
            dayMarker += stepDays
            x += stepX
        }

        // Y line
        var yMarker = minValue.toInt()
        var y = offsetGraphBeginY - offsetGraphBeginY * (minValue - minValue.toInt().toFloat())

        var lastY = 0f
        while (y < height) {
//            if (y - lastY >= UNIT_NAME_LENGTH) {
            if (y - lastY >= TEXT_SIZE!! * 1.2) {
                myText(
                    yMarker.toString(), -paintText!!.measureText(yMarker.toString() + " "),
                    y, paintText!!
                )
                lastY = y
                myLine(0f, y, width, y, paintThin!!)
//                MyLogger.d("MyDraw - showCoordinates yMarker=" + yMarker + " y=" + y + " stepY=" + stepY)
            }
            yMarker += 1//mode //1
            y += stepY
        }
    }

}