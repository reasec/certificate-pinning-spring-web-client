package com.reasec.certificatepinning.trustmanager

import com.reasec.certificatepinning.model.CertificatePinningSpec
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.Test
import java.security.KeyStore
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager


class CertificatePinningTrustManagerTest {
  @Test
  fun checkClientTrusted() {
    val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
    trustManagerFactory.init(null as KeyStore?)
    val spec = CertificatePinningSpec.Builder().build()
    val trustManager = CertificatePinningTrustManager(spec, trustManagerFactory.trustManagers)
    val certificates = arrayOf<X509Certificate>()

    val thrown = catchThrowable {
      trustManager.checkClientTrusted(certificates, "")
    }
    assertThat(thrown).isInstanceOf(CertificateException::class.java)
    assertThat(thrown).hasCauseExactlyInstanceOf(UnsupportedOperationException::class.java)
  }

  @Test
  fun checkServerTrusted() {
  }

  @Test
  fun pingCertificate() {
  }

  @Test
  fun getAcceptedIssuers() {
    val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
    trustManagerFactory.init(null as KeyStore?)
    val spec = CertificatePinningSpec.Builder().build()
    val trustManager = CertificatePinningTrustManager(spec, trustManagerFactory.trustManagers)
    val acceptedIssuers = trustManager.acceptedIssuers
    var total = 0
    trustManagerFactory.trustManagers.forEach {
      repeat((it as X509TrustManager).acceptedIssuers.count()) {
        total++
      }
    }
    assertThat(acceptedIssuers).hasSize(total)
  }
}