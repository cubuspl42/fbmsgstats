import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.xenomachina.argparser.ArgParser
import java.io.File
import java.io.FileNotFoundException

class MyArgs(parser: ArgParser) {
    val directoryPath by parser.positional("directory directoryPath")
}

@JsonIgnoreProperties(ignoreUnknown = true)
class Message {
    var sender_name = ""
    var content = ""
}

@JsonIgnoreProperties(ignoreUnknown = true)
class MessageJson {
    var messages = listOf<Message>()
}

data class ContactStats(
        val name: String,
        val messageCount: Int
)

fun main(args: Array<String>) = ArgParser(args).parseInto(::MyArgs).run {
    File(directoryPath).listFiles()
            .filter { it.isDirectory }
            .map { ContactStats(it.name, countMessages(it)) }
            .sortedByDescending { it.messageCount }
            .forEachIndexed { index, stats -> println("$index: $stats") }
}

fun countMessages(contactMessagesDir: File): Int {
    try {
        val messageJsonPath = contactMessagesDir.toPath().resolve("message.json")
        val mapper = jacksonObjectMapper()
        val messagesJson = mapper.readValue<MessageJson>(messageJsonPath.toFile())
        return messagesJson.messages.size
    } catch (e: FileNotFoundException) {
        return 0
    }
}
