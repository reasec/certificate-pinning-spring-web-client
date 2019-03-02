/*
 * Copyright 2019 The ReaSec project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.reasec.certificatepinning

import com.reasec.CertificatePinningWebClient
import com.reasec.certificatepinning.exceptions.CertificatePinningException
import com.reasec.tools.CertificateTools
import org.assertj.core.api.Assertions.assertThat
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit4.SpringRunner
import reactor.test.StepVerifier
import java.net.URL
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLHandshakeException

@RunWith(SpringRunner::class)
@SpringBootTest
class CertificatePinningWebClientTest {

  companion object {
    const val GOOGLE_URL = "https://www.google.com"
    const val INVALID_SHA = "7F:22:F8:91:B5:9F:EB:99:0E:81:A9:E7:FE:47:C5:77:54:E3:11:73:D6:48:64:F4:1C:6B:CC:2F:44:0D:49:E3"
    lateinit var googlePublicKeySha: String

    @BeforeClass
    @JvmStatic
    fun setup() {
      val googleCertificate = getCertificate(GOOGLE_URL)
      googlePublicKeySha = CertificateTools.getPublicKeySha(googleCertificate)
    }

    private fun getCertificate(uri: String): X509Certificate {
      val url = URL(uri)
      val connection = url.openConnection() as HttpsURLConnection
      connection.connect()
      val serverCertificates = connection.serverCertificates
      val firstCertificate = serverCertificates[0] as X509Certificate
      connection.disconnect()
      return firstCertificate
    }
  }

  @Test
  fun `we should match google public key sha`() {
    val webClient = CertificatePinningWebClient.builder(googlePublicKeySha)
        .baseUrl(GOOGLE_URL)
        .build()

    StepVerifier.create(webClient.get().exchange())
        .assertNext {
          assertThat(it.statusCode()).isEqualTo(HttpStatus.OK)
        }
        .verifyComplete()
  }

  @Test
  fun `we should not match google public key sha`() {
    val webClient = CertificatePinningWebClient.builder(INVALID_SHA)
        .baseUrl(GOOGLE_URL)
        .build()

    StepVerifier.create(webClient.get().exchange())
        .expectNext()
        .expectErrorSatisfies {
          assertThat(it).isInstanceOf(SSLHandshakeException::class.java)
          assertThat(it.cause).isInstanceOf(SSLHandshakeException::class.java)
          assertThat(it.cause?.cause).isInstanceOf(CertificateException::class.java)
          assertThat(it.cause?.cause?.cause).isInstanceOf(CertificatePinningException::class.java)
        }.verify()
  }
}
