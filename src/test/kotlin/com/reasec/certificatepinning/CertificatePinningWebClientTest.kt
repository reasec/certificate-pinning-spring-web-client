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

import com.reasec.certificatepinning.dsl.certificatePinningSpec
import com.reasec.certificatepinning.exception.CertificatePinningException
import com.reasec.certificatepinning.model.CertificatePinningSpec
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit4.SpringRunner
import reactor.test.StepVerifier
import java.security.cert.CertificateException
import javax.net.ssl.SSLHandshakeException

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class CertificatePinningWebClientTest {

  @LocalServerPort
  lateinit var port : Number

  @Value("\${test.public-key-sha}")
  lateinit var publicKeySha : String

  @Value("\${test.invalid-sha}")
  lateinit var invalidSha : String

  @Test
  fun `we should match the public key sha`() {
    //GIVEN
    val spec = CertificatePinningSpec.Builder()
        .sha(publicKeySha)
        .build()
    val webClient = CertificatePinningWebClient.builder(spec)
        .baseUrl("https://localhost:$port/test")
        .build()

    //WHEN
    StepVerifier.create(webClient.get().exchange())
        //THEN
        .assertNext {
          assertThat(it.statusCode()).isEqualTo(HttpStatus.OK)
        }
        .verifyComplete()
  }

  @Test
  fun `we should not match the public key sha`() {
    //GIVEN
    val spec = CertificatePinningSpec.Builder()
        .sha(invalidSha)
        .build()
    val webClient = CertificatePinningWebClient.builder(spec)
        .baseUrl("https://localhost:$port/test")
        .build()

    //WHEN
    StepVerifier.create(webClient.get().exchange())
        //THEN
        .expectNext()
        .expectErrorSatisfies {
          assertThat(it).isInstanceOf(SSLHandshakeException::class.java)
          assertThat(it.cause).hasCauseInstanceOf(CertificateException::class.java)
          assertThat(it.cause?.cause).hasCauseInstanceOf(CertificatePinningException::class.java)
        }.verify()
  }

  @Test
  fun `we should match the public key sha with other sha`() {
    //GIVEN
    val spec = CertificatePinningSpec.Builder()
        .sha(publicKeySha)
        .sha(invalidSha)
        .build()
    val webClient = CertificatePinningWebClient.builder(spec)
        .baseUrl("https://localhost:$port/test")
        .build()

    //WHEN
    StepVerifier.create(webClient.get().exchange())
        //THEN
        .assertNext {
          assertThat(it.statusCode()).isEqualTo(HttpStatus.OK)
        }
        .verifyComplete()
  }

  @Test
  fun `we should not the public key sha with empty spec`() {
    //GIVEN
    val spec = CertificatePinningSpec.Builder()
        .build()
    val webClient = CertificatePinningWebClient.builder(spec)
        .baseUrl("https://localhost:$port/test")
        .build()

    //WHEN
    StepVerifier.create(webClient.get().exchange())
        //THEN
        .expectNext()
        .expectErrorSatisfies {
          assertThat(it).isInstanceOf(SSLHandshakeException::class.java)
          assertThat(it.cause).hasCauseInstanceOf(CertificateException::class.java)
          assertThat(it.cause?.cause).hasCauseInstanceOf(CertificatePinningException::class.java)
        }.verify()
  }

  @Test
  fun `we should match the public key sha with other sha using DSL`() {
    //GIVEN
    val spec = certificatePinningSpec {
      sha(publicKeySha)
      sha(invalidSha)
    }
    val webClient = CertificatePinningWebClient.builder(spec)
        .baseUrl("https://localhost:$port/test")
        .build()

    //WHEN
    StepVerifier.create(webClient.get().exchange())
        //THEN
        .assertNext {
          assertThat(it.statusCode()).isEqualTo(HttpStatus.OK)
        }
        .verifyComplete()
  }

  @Test
  fun `we could create a instance of our object`() {
    val certificatePinningWebClient: CertificatePinningWebClient? = CertificatePinningWebClient()
    assertThat(certificatePinningWebClient).isNotNull
  }
}
