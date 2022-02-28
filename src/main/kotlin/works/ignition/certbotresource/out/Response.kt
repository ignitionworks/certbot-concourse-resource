package works.ignition.certbotresource.out

import works.ignition.certbotresource.Metadatum
import works.ignition.certbotresource.Version

sealed class Response

data class Success(
    val version: Version,
    val metadata: List<Metadatum>
) : Response()

object Failure : Response()
