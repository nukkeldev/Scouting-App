import android.hardware.usb.UsbConstants.USB_DIR_OUT
import android.hardware.usb.UsbManager
import android.hardware.usb.UsbRequest
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.snapshots.SnapshotStateMap
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import java.io.File
import java.io.FileInputStream
import java.io.IOException
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

fun createMatchAndTeamFiles(filesDir: File) {
    File(filesDir, "match_data.json").overwriteString(matchData?.toString())
    File(filesDir, "team_data.json").overwriteString(teamData?.toString())
}

fun loadMatchAndTeamFiles(filesDir: File): Boolean {
    matchData =
        Json.decodeFromString(
            File(
                filesDir,
                "match_data.json"
            ).readText()
        ) as? JsonObject
    teamData =
        Json.decodeFromString(
            File(
                filesDir,
                "team_data.json"
            ).readText()
        ) as? JsonObject

    return matchData != null && teamData != null
}

@OptIn(ExperimentalSerializationApi::class)
fun loadScoutingData(filesDir: File, scoutingLogs: SnapshotStateMap<Int, MutableMap<Int, String>>) {
    val data = Json.decodeFromStream(
        FileInputStream(
            File(
                filesDir,
                "match_scouting_data.json"
            )
        )
    ) as? JsonObject ?: return

    (0..5).forEach {
        val array = data[it.toString()]?.jsonArray ?: return@forEach
        val position = mutableMapOf<Int, String>()

        for (i in 0..<array.size) {
            position[i] = array[i].jsonPrimitive.toString()
        }

        scoutingLogs[it] = position
    }
}

fun exportScoutingData(filesDir: File, scoutingLogs: Map<Int, MutableMap<Int, String>>) {
    File(filesDir, "match_scouting_data.json").overwriteString(
        getJsonFromMatchHash(scoutingLogs).toString()
    )
}

fun deleteScoutingDataFile(filesDir: File) {
    File(filesDir, "match_scouting_data.json").delete()
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