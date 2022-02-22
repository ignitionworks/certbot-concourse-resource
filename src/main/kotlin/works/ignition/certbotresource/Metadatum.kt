package works.ignition.certbotresource

import com.fasterxml.jackson.annotation.JsonProperty

data class Metadatum(
    @JsonProperty("renewed_domains")
    val renewedDomains: List<String>
)
