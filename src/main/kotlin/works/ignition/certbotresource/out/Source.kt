package works.ignition.certbotresource.out

import com.fasterxml.jackson.annotation.JsonProperty

data class Source(
    val email: String,
    val bucket: String,
    @JsonProperty("versioned_file")
    val versionedFile: String,
)
