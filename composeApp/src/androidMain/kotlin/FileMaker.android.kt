import android.hardware.usb.UsbConstants.USB_DIR_OUT
import android.hardware.usb.UsbManager
import android.hardware.usb.UsbRequest
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import data.overwriteString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import nodes.FILES_DIRECTORY
import java.io.File
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketException
import java.nio.ByteBuffer

fun loadMatchAndTeamFiles(): Boolean {
    try {
        matchData = Json.decodeFromString(
            File(
                FILES_DIRECTORY, "match_data.json"
            ).readText()
        ) as JsonObject
    } catch (_: Exception) {
    }
    try {
        teamData = Json.decodeFromString(
            File(
                FILES_DIRECTORY, "team_data.json"
            ).readText()
        ) as JsonObject
    } catch (_: Exception) {
    }

    return matchData != null && teamData != null
}

fun sendData(receiver: String, data: JsonObject) {
    val socket = Socket()
    try {
        socket.connect(InetSocketAddress(receiver, 45482), 5000)
        socket.getOutputStream().writer().use { writer ->
            writer.write(data.toString() + "\n")
            writer.flush() // Ensure data is sent immediately
        }

        Log.i("Client", "Message Sent: $data")
    } catch (e: IOException) {
        e.printStackTrace()
    } catch (_: SocketException) {

    } finally {
        socket.close()
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun sendDataUSB(deviceName: String, manager: UsbManager, data: JsonObject) {
    val device = manager.deviceList[deviceName]
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

    val buffer = ByteBuffer.wrap(data.toString().encodeToByteArray())

    request.queue(buffer)
}