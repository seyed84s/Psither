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
            put("SponsorId", "FFFFFFFFFFFFFFFF")
            put("RemoteServerListSignaturePublicKey", "MIICIDANBgkqhkiG9w0BAQEFAAOCAg0AMIICCAKCAgEAt7Ls+/39r+T6zNW7GiVpJfzq/xvL9SBH5rIFnk0RXYEYavax3WS6HOD35eTAqn8AniOwiH+DOkvgSKF2caqk/y1dfq47Pdymtwzp9ikpB1C5OfAysXzBiwVJlCdajBKvBZDerV1cMvRzCKvKwRmvDmHgphQQ7WfXIGbRbmmk6opMBh3roE42KcotLFtqp0RRwLtcBRNtCdsrVsjiI1Lqz/lH+T61sGjSjQ3CHMuZYSQJZo/KrvzgQXpkaCTdbObxHqb6/+i1qaVOfEsvjoiyzTxJADvSytVtcTjijhPEV6XskJVHE1Zgl+7rATr/pDQkw6DPCNBS1+Y6fy7GstZALQXwEDN/qhQI9kWkHijT8ns+i1vGg00Mk/6J75arLhqcodWsdeG/M/moWgqQAnlZAGVtJI1OgeF5fsPpXu4kctOfuZlGjVZXQNW34aOzm8r8S0eVZitPlbhcPiR4gT/aSMz/wd8lZlzZYsje/Jr8u/YtlwjjreZrGRmG8KMOzukV3lLmMppXFMvl4bxv6YFEmIuTsOhbLTwFgh7KYNjodLj/LsqRVfwz31PgWQFTEPICV7GCvgVlPRxnofqKSjgTWI4mxDhBpVcATvaoBl1L/6WLbFvBsoAUBItWwctO2xalKxF5szhGm8lccoc5MZr8kfE0uxMgsxz4er68iCID+rsCAQM=")
            put("RemoteServerListUrl", "https://s3.amazonaws.com//psiphon/web/mjr4-p23r-puwl/server_list_compressed")
            put("UseIndistinguishableTLS", true)
            put("EmitDiagnosticNotices", true)
        }

        configFile.writeText(json.toString(2))
        return configFile
    }
}
