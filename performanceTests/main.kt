package com.example

import java.io.File
import java.io.IOException
import kotlin.concurrent.thread
import kotlin.collections.*

/**
 * Created by perkinsa on 6/6/17.
 */

fun Map<String, String>.toJson(): String {
    if ( this.isEmpty() ){
        return "{}"
    }
    return StringBuilder("")
        .append("{")
        .append("\n")
        .append(this.map { (k, v) -> "  \"$k\": \"$v\"" }.reduce {o, n -> "$o,\n$n" })
        .append("\n}")
        .toString()
}

fun Map<String, String>.toXml(): String {
    if ( this.isEmpty() ){
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<gfxinfo />"
    }
    val json: StringBuilder = StringBuilder("")
        .append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<gfxinfo>\n")
    this.forEach { key, entry ->
        var xmlValidKey = key.replace(" ", "_")
        if (xmlValidKey.startsWithDigit()){
            xmlValidKey = "_" + xmlValidKey
        }
        json.append("    <$xmlValidKey>\"$entry\"</$xmlValidKey>\n")
    }
    return json.append("</gfxinfo>")
            .toString()
}

fun String.startsWithDigit() = Character.isDigit(this[0])

fun String.save(fileName: String): Unit {
    File(fileName).printWriter().use { out ->
        out.print(this)
    }
}

fun String.extractValues(): Map<String, String> {
    val mapStats: MutableMap<String, String> = mutableMapOf()
    val parts: List<String> = split("\\n".toRegex())
    parts.forEach {
        val split: List<String> = it.split(":")
        if (split.size != 2 || split[0].isBlank() || split[1].isBlank()){
            return@forEach
        }
        if (split[0] == "Janky frames") {
            val split_number_data = split[1].split("(")
            val number_of_janky_frames = split_number_data[0]
            mapStats["Janky frames"] = number_of_janky_frames.trim()
            val percentage_of_janky_frames = split_number_data[1].replace(")", "")
            mapStats["Janky frames Percentage"] = percentage_of_janky_frames.trim()
        } else {
            mapStats[split[0]] = split[1].trim()
        }
    }
    return mapStats
}

private fun extract_gfx_info(apkName: String) = "adb shell dumpsys gfxinfo $apkName".runCommand()

private fun app_process_still_alive(current_stats: String) = !current_stats.contains("No process found for:")

private fun startTest(startTestRunnerCommand: String) = "adb shell am instrument $startTestRunnerCommand".runCommand()


private fun String.runCommand(): String {
    return try {
        val parts = this.split("\\s".toRegex())
        val process = ProcessBuilder(*parts.toTypedArray())
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .redirectError(ProcessBuilder.Redirect.PIPE)
                .start()

        process.inputStream.bufferedReader().readText()
    } catch(e: IOException) {
        throw RuntimeException("Un-able to properly execute system call: $this",  e )
    }
}

fun main(args: Array<String>){
    print("starting test!")
    val apkName = args[0]
    val startTestRunnerCommand = args[1]

    val instrumentThread: Thread = thread {
        startTest(startTestRunnerCommand)
    }

    var current_stats: String
    var stats: String = ""

    while (instrumentThread.isAlive) {
        current_stats = extract_gfx_info(apkName)
        if (app_process_still_alive(current_stats)){
            stats = current_stats
        }
    }

    stats.save("Output.txt")
    val extractedValues = stats.extractValues()
    extractedValues.toJson().save("Output.json")
    extractedValues.toXml().save("Output.xml")
}