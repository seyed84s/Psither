package studio.cluvex.aether.core

import org.json.JSONArray
import org.json.JSONObject
import studio.cluvex.aether.model.ConnectionProfile
import studio.cluvex.aether.model.PsiphonRegion
import java.io.File

/**
 * Generates the standard JSON configuration for the Psiphon Tunnel Core engine.
 */
object PsiphonConfigGenerator {
    const val PSIPHON_SOCKS_PORT = 1820
    const val PSIPHON_HTTP_PORT = 1821

    fun generateConfig(
        profile: ConnectionProfile,
        workingDir: File,
        upstreamSocksHost: String = TunnelConfig.SOCKS_HOST,
        upstreamSocksPort: Int = TunnelConfig.SOCKS_PORT,
    ): File {
        val psiDir = File(workingDir, "psiphon_data").apply { mkdirs() }
        val configFile = File(workingDir, "psiphon.config.json")

        val json = JSONObject().apply {
            put("DataRootDirectory", psiDir.absolutePath)
            put("LocalSocksProxyPort", PSIPHON_SOCKS_PORT)
            put("LocalHttpProxyPort", PSIPHON_HTTP_PORT)
            put("UpstreamProxyUrl", "socks5://$upstreamSocksHost:$upstreamSocksPort")
            put("EstablishTunnelTimeoutSeconds", profile.psiphonTimeout.coerceIn(10, 120))
            
            // Egress Region (blank or AUTO means best location)
            if (profile.psiphonRegion != PsiphonRegion.AUTO && profile.psiphonRegion != PsiphonRegion.DIRECT) {
                put("EgressRegion", profile.psiphonRegion.code)
            }

            // Custom DNS if specified
            if (profile.psiphonDns.isNotBlank()) {
                put("AuthoritativeDomain", profile.psiphonDns.trim())
            }

            // Tunnel Protocols
            if (profile.psiphonProtocols.isNotBlank()) {
                val protoArray = JSONArray()
                profile.psiphonProtocols.split(",").map { it.trim() }.filter { it.isNotEmpty() }.forEach {
                    protoArray.put(it)
                }
                if (protoArray.length() > 0) {
                    put("TunnelProtocols", protoArray)
                }
            } else {
                // Default high-resilience protocols
                val defaultProtos = JSONArray().apply {
                    put("OSSH")
                    put("SSH")
                    put("UNFRONTED-MEEK-HTTPS")
                    put("FRONTED-MEEK-HTTP")
                    put("UNFRONTED-MEEK-HTTP")
                    put("FRONTED-MEEK-HTTPS")
                }
                put("TunnelProtocols", defaultProtos)
            }

            put("PropagationChannelId", "FFFFFFFFFFFFFFFF")
            put("SponsorId", "1")
            put("EmitDiagnosticNotices", true)
        }

        configFile.writeText(json.toString(2))
        return configFile
    }
}
