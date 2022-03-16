package works.ignition.certbotresource.out

import works.ignition.certbotresource.Version

sealed class Response

data class Success(
    val version: Version,
    val metadata: List<Metadata>
) : Response()

data class Failure(
    val version: Version?,
    val metadata: List<Metadata>
) : Response()
