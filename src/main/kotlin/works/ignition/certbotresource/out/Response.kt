package works.ignition.certbotresource.out

import works.ignition.certbotresource.Metadatum
import works.ignition.certbotresource.Version

data class Response(
    val version: Version,
    val metadata: List<Metadatum>
)
