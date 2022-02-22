package works.ignition.certbotresource.out

import works.ignition.certbotresource.Source

data class Request(
    val params: Params,
    val source: Source,
)
