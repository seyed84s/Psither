package studio.cluvex.aether.core

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runInterruptible
import studio.cluvex.aether.BuildConfig
import studio.cluvex.aether.model.ConnectionProfile
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Runs the Psiphon Tunnel Core engine (`libpsi.so`) as a child process.
 * Configured with an upstream proxy pointing to the local Aether SOCKS5 port (127.0.0.1:1819)
 * and exposes its country-egress SOCKS5 proxy on 127.0.0.1:1820.
 */
class PsiphonProcess(
    private val nativeLibDir: String,
    private val workingDir: File,
) {
    private var process: Process? = null

    fun start(profile: ConnectionProfile) {
        val bin = File(nativeLibDir, "libpsi.so")
        if (!bin.exists()) {
            // Fallback check for alternate binary name
            val altBin = File(nativeLibDir, "libpsiphon.so")
            if (!altBin.exists()) {
                throw IllegalStateException("Psiphon engine binary missing: ${bin.absolutePath}")
            }
        }
        val targetBin = if (bin.exists()) bin else File(nativeLibDir, "libpsiphon.so")

        val configFile = PsiphonConfigGenerator.generateConfig(
            profile = profile,
            workingDir = workingDir,
            upstreamSocksHost = TunnelConfig.SOCKS_HOST,
            upstreamSocksPort = TunnelConfig.SOCKS_PORT,
        )

        val command = listOf(
            targetBin.absolutePath,
            "-config", configFile.absolutePath,
            "-dataRootDirectory", File(workingDir, "psiphon_data").absolutePath,
        )

        val builder = ProcessBuilder(command)
            .directory(workingDir)
            .redirectErrorStream(true)

        builder.environment().apply {
            put("HOME", workingDir.absolutePath)
            put("TMPDIR", workingDir.absolutePath)
        }

        val proc = builder.start()
        process = proc

        DiagnosticsLog.i("psiphon", "Spawned ${targetBin.name} (Region: ${profile.psiphonRegion.code}, Upstream: 127.0.0.1:${TunnelConfig.SOCKS_PORT})")

        Thread({
            try {
                proc.inputStream.bufferedReader().useLines { lines ->
                    lines.forEach { line ->
                        if (BuildConfig.DEBUG) Log.i("psiphon-engine", line)
                        DiagnosticsLog.d("psiphon", line)
                    }
                }
            } catch (_: Exception) {
            } finally {
                DiagnosticsLog.w("psiphon", "Psiphon engine output stream closed.")
            }
        }, "psiphon-log").apply { isDaemon = true }.start()
    }

    fun isAlive(): Boolean = process?.isAlive == true

    suspend fun awaitExit(timeoutMs: Long): Boolean =
        runCatching {
            runInterruptible(Dispatchers.IO) {
                process?.waitFor(timeoutMs, TimeUnit.MILLISECONDS) ?: true
            }
        }.getOrDefault(false)

    fun stop() {
        val proc = process ?: return
        process = null
        runCatching {
            proc.destroy()
            if (!proc.waitFor(GRACEFUL_EXIT_MS, TimeUnit.MILLISECONDS)) {
                proc.destroyForcibly()
            }
        }
    }

    private companion object {
        const val GRACEFUL_EXIT_MS = 250L
    }
}
