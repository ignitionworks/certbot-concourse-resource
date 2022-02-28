package works.ignition.certbotresource.check

import works.ignition.certbotresource.Version
import works.ignition.certbotresource.storage.Storage

fun process(storage: Storage, request: Request): List<Version> = storage.versions().map(::Version)
