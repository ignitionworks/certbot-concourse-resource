package works.ignition.certbotresource.check

import works.ignition.certbotresource.Source
import works.ignition.certbotresource.Version

data class Request(
    val source: Source,
    val version: Version?
)
