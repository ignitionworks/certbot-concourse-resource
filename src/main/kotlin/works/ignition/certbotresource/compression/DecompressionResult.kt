package works.ignition.certbotresource.compression

sealed class DecompressionResult

object Success : DecompressionResult()
object Failure : DecompressionResult()
