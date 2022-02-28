package works.ignition.certbotresource.check

import works.ignition.certbotresource.Version
import works.ignition.certbotresource.storage.Storage

fun process(storage: Storage, request: Request): List<Version> =
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
