package works.ignition.certbotresource.`in`

import works.ignition.certbotresource.out.Metadatum
import works.ignition.certbotresource.Version

sealed class Response

data class Failure(
    val reason: String
) : Response()

data class Success(
    val version: Version,
) : Response()
