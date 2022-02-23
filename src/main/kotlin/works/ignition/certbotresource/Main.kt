package works.ignition.certbotresource

import works.ignition.certbotresource.out.*
import kotlin.system.exitProcess

fun main(args: Array<String>) =
    when (args[0]) {
        "out" -> out(readln())
        else -> {
            println("Usage: certbot-resource (out|in|check) ARGS...")
            println("You gave: $args")
            exitProcess(1)
        }
    }
