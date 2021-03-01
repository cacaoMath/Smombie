import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Environment
import android.util.Log
import java.io.File
import java.nio.charset.StandardCharsets

/**
 * センシング用のクラス
 */
class Sensing(context: Context, status : String) : SensorEventListener {
    private val TAG = this::class.java.simpleName

    private val sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    var stateMeasurement = false
        private set

    // for File
    private val rootDirectory: File? = context.getExternalFilesDir(null)
    private var accelerateFile: File? = null
    private var gyroFile: File? = null

    // for Callback
    private var sensingListener: ((SensorEvent?) -> Unit)? = null

    init {
        val sensorArray = sensorManager.getSensorList(Sensor.TYPE_ALL)
        Log.d(TAG, "\n" + sensorArray.joinToString(separator = "\n"))
        Log.d(TAG, rootDirectory.toString()) // no require permission about this path got getExternalFilesDir
    }

    @JvmOverloads
    fun start(label: String, listener: ((SensorEvent?) -> Unit)? = null) {
        if (stateMeasurement) return // 実行中だったら実行しない

        val directory = File(rootDirectory, "data")
        if(!isExternalStorageWritable()) return
        if(!(directory.exists() and directory.isDirectory))
            if(!directory.mkdir()) return

        // labelの保存
        val unixTime = System.currentTimeMillis()
        val meta = "${unixTime}_$label"
        Log.d(TAG, "file mane meta data: $meta")
        accelerateFile = File(directory, "accelerate_${meta}.txt")
        gyroFile = File(directory, "gyro_${meta}.txt")
        accelerateFile?.writeText("timestamp,x,y,z\n", StandardCharsets.UTF_8)
        gyroFile?.writeText("timestamp,x,y,z\n", StandardCharsets.UTF_8)

        val accel : Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val gyro : Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        Log.d(TAG, accel.toString())
        Log.d(TAG, gyro.toString())
        sensor2text(listOf(accel, gyro))

        sensingListener = listener

        sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_FASTEST)
        sensorManager.registerListener(this, gyro, SensorManager.SENSOR_DELAY_FASTEST)
        stateMeasurement = true
        Log.d(TAG, "[Start Sensing]")}

    fun stop() {
        sensorManager.unregisterListener(this)
        stateMeasurement = false
        Log.d(TAG, "[Stop Sensing]")
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent?) {
        if(event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            accelerateFile?.appendText("%s,%f,%f,%f\n".format(System.currentTimeMillis().toString(10), x, y, z), StandardCharsets.UTF_8)}

        if(event?.sensor?.type == Sensor.TYPE_GYROSCOPE) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            gyroFile?.appendText("%s,%f,%f,%f\n".format(System.currentTimeMillis().toString(10), x, y, z), StandardCharsets.UTF_8)
        }

        sensingListener?.invoke(event)
    }


    private fun sensor2text(sensors : List<Sensor?>){
        // sensor2text
        val sensorInfo = File(rootDirectory, "sensorInfo.csv")
        if(!sensorInfo.exists()) {
            sensorInfo.writeText("name,vendor,type,minDelay,maxDelay,reportingMode,maximumRange,resolution,power\n", StandardCharsets.UTF_8)
            for(sensor in sensors){
                if(sensor == null) continue
                sensorInfo.appendText("%s,".format(sensor.name.toString()), StandardCharsets.UTF_8) // センサー名
                sensorInfo.appendText("%s,".format(sensor.vendor.toString()), StandardCharsets.UTF_8) // ベンダー名
                sensorInfo.appendText("%s,".format(sensor.type.toString()), StandardCharsets.UTF_8) // 型番
                sensorInfo.appendText("%s,".format(sensor.minDelay.toString()), StandardCharsets.UTF_8) // 最小遅れ
                sensorInfo.appendText("%s,".format(sensor.maxDelay.toString()), StandardCharsets.UTF_8) // 最大遅れ
                val info = when(sensor.reportingMode) {
                    0 -> "REPORTING_MODE_CONTINUOUS"
                    1 -> "REPORTING_MODE_ON_CHANGE"
                    2 -> "REPORTING_MODE_ONE_SHOT"
                    else -> "unknown"
                }
                sensorInfo.appendText("%s,".format(info), StandardCharsets.UTF_8) // レポートモード
                sensorInfo.appendText("%s,".format(sensor.maximumRange.toString()), StandardCharsets.UTF_8) // 最大レンジ
                sensorInfo.appendText("%s,".format(sensor.resolution.toString()), StandardCharsets.UTF_8) // 分解能
                sensorInfo.appendText("%s".format(sensor.power.toString()), StandardCharsets.UTF_8) // 消費電流
                sensorInfo.appendText("\n", StandardCharsets.UTF_8)
            }
        }

    }

    /* Checks if external storage is available for read and write */
    private fun isExternalStorageWritable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

    /* Checks if external storage is available to at least read */
//    fun isExternalStorageReadable(): Boolean {
//        return Environment.getExternalStorageState() in
//                setOf(Environment.MEDIA_MOUNTED, Environment.MEDIA_MOUNTED_READ_ONLY)
//    }
}