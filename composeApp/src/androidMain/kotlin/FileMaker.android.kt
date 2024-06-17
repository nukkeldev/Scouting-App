import android.content.Context
import android.content.Context.USB_SERVICE
import android.hardware.usb.UsbConstants.USB_DIR_OUT
import android.hardware.usb.UsbManager
import android.hardware.usb.UsbRequest
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketException
import java.nio.ByteBuffer

fun File.overwriteString(data: String?) {
    if (delete()) createNewFile()

    writer().use { writer ->
        data?.let { writer.write(it) }
    }
}

fun createFile(context: Context) {
    File(context.filesDir, "match_data.json").overwriteString(matchData?.toString(1))
    File(context.filesDir, "team_data.json").overwriteString(teamData?.toString(1))
}

fun openFile(context: Context) {
    matchData = try {
        JSONObject(File(context.filesDir, "match_data.json").readText())
    } catch (e: JSONException) {
        null
    }
    teamData = try {
        JSONObject(File(context.filesDir, "team_data.json").readText())
    } catch (e: JSONException) {
        null
    }
    loadScoutingLogs(context)
}

fun loadScoutingLogs(context: Context) {
    val data =
        try {
            JSONObject(
                String(
                    FileInputStream(
                        File(
                            context.filesDir,
                            "match_scouting_data.json"
                        )
                    ).readBytes()
                )
            )
        } catch (_: Throwable) {
            return null
        }

    (0..5).mapNotNull<Int, Pair<Int, Map<Int, String>>> {
        try {
            val array = data[it.toString()] as JSONArray
            val position = mutableMapOf<Int, String>()

            for (i in 0..<array.length()) {
                position[i] = array[i] as String
            }

            it to position
        } catch (_: JSONException) {
            null
        }
    }.associate { it }
}


fun exportScoutData(context: Context) {
    val file = File(context.filesDir, "match_scouting_data.json")
    file.delete()
    file.createNewFile()
    val jsonObject = getJsonFromMatchHash()

    val writer = FileWriter(file)
    writer.write(jsonObject.toString(1))
    writer.close()
}


fun deleteFile(context: Context) {
    val file = File(context.filesDir, "match_scouting_data.json")
    file.delete()
}

fun sendData(context: Context, ipAddress: String) {

    exportScoutData(context)

    val jsonObject = getJsonFromMatchHash()
    val socket = Socket()
    try {
        socket.connect(InetSocketAddress(ipAddress, 45482), 5000)
        socket.getOutputStream().writer().use { writer ->
            writer.write(jsonObject.toString(1) + "\n")
            writer.flush() // Ensure data is sent immediately
        }

        Log.i("Client", "Message Sent: ${jsonObject.toString(1)}")
    } catch (e: IOException) {
        e.printStackTrace()
    } catch (_: SocketException) {

    } finally {
        socket.close()
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun sendDataUSB(context: Context, deviceName: String) {
    exportScoutData(context)

    val jsonObject = getJsonFromMatchHash()
    val manager = context.getSystemService(USB_SERVICE) as UsbManager

    val deviceList = manager.deviceList

    val device = deviceList[deviceName]
    val connection = manager.openDevice(device)

    val endpoint = device?.getInterface(0)?.getEndpoint(5)
    if (endpoint?.direction == USB_DIR_OUT) {
        Log.i("USB", "Dir is out")
    } else {
        Log.i("USB", "Dir is in")
        return
    }


    val request = UsbRequest()
    request.initialize(connection, endpoint)

    val buffer = ByteBuffer.wrap(jsonObject.toString().encodeToByteArray())

    request.queue(buffer)
}