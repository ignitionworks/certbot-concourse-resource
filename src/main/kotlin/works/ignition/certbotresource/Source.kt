package works.ignition.certbotresource

import com.fasterxml.jackson.annotation.JsonProperty

data class Source(
    val email: String,
    val bucket: String,
    @JsonProperty("versioned_file")
    val versionedFile: String,
    @JsonProperty("acme_server_url")
    val acmeServerURL: String,
    @JsonProperty("certbot_config_dir")
    val certbotConfigDir: String = "/",
)
