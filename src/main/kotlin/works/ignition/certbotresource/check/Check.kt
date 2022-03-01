package works.ignition.certbotresource.check

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import works.ignition.certbotresource.Version
import works.ignition.certbotresource.storage.GCS
import works.ignition.certbotresource.storage.Storage

fun main() {
    val request = jacksonObjectMapper().readValue(readln(), Request::class.java)
    val storage = GCS(bucket = request.source.bucket, obj = request.source.versionedFile)
    val versions = check(storage, request)
    println(jacksonObjectMapper().writeValueAsString(versions))
}

fun check(storage: Storage, request: Request): List<Version> =
    if (request.version == null) {
        listOf(Version(storage.versions().last()))
    } else {
        storage.versions().map(::Version).fold(Pair(false, emptyList<Version>())) { (begunCollecting, acc), version ->
            if (begunCollecting || version.generation == request.version.generation) {
                Pair(true, acc.plus(version))
            } else {
                Pair(false, acc)
            }
        }.second
    }
