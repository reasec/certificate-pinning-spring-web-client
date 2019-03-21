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

package com.reasec.certificatepinning.dsl

import com.reasec.certificatepinning.exception.CertificatePinningException
import org.assertj.core.api.Assertions
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit4.SpringRunner
import reactor.test.StepVerifier
import java.security.cert.CertificateException
import javax.net.ssl.SSLHandshakeException

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CertificatePinningWebClientBuilderDSLTest {

  @LocalServerPort
  lateinit var port : Number

  @Value("\${test.public-key-sha}")
  lateinit var publicKeySha : String

  @Value("\${test.invalid-sha}")
  lateinit var invalidSha : String

  @Test
  fun `we should match the public key sha using dsl`() {
    //GIVEN
    val webClient = certificatePinningWebClientBuilder {
      spec {
        sha(publicKeySha)
      }
    }
    .baseUrl("https://localhost:$port/test")
    .build()

    //WHEN
    StepVerifier.create(webClient.get().exchange())
        //THEN
        .assertNext {
          Assertions.assertThat(it.statusCode()).isEqualTo(HttpStatus.OK)
        }
        .verifyComplete()
  }

  @Test
  fun `we should not match the public key sha using dsl`() {
    //GIVEN
    val webClient = certificatePinningWebClientBuilder {
      spec {
        sha(invalidSha)
      }
    }
    .baseUrl("https://localhost:$port/test")
    .build()

    //WHEN
    StepVerifier.create(webClient.get().exchange())
        //THEN
        .expectNext()
        .expectErrorSatisfies {
          Assertions.assertThat(it).isInstanceOf(SSLHandshakeException::class.java)
          Assertions.assertThat(it.cause).hasCauseInstanceOf(CertificateException::class.java)
          Assertions.assertThat(it.cause?.cause).hasCauseInstanceOf(CertificatePinningException::class.java)
        }.verify()
  }

  @Test
  fun `we should match the public key sha with other sha`() {
    //GIVEN
    val webClient = certificatePinningWebClientBuilder {
      spec {
        sha(publicKeySha)
        sha(invalidSha)
      }
    }
    .baseUrl("https://localhost:$port/test")
    .build()

    //WHEN
    StepVerifier.create(webClient.get().exchange())
        //THEN
        .assertNext {
          Assertions.assertThat(it.statusCode()).isEqualTo(HttpStatus.OK)
        }
        .verifyComplete()
  }

  @Test
  fun `we should match with empty spec`() {
    //GIVEN
    val webClient = certificatePinningWebClientBuilder {
    }
    .baseUrl("https://localhost:$port/test")
    .build()

    //WHEN
    StepVerifier.create(webClient.get().exchange())
        //THEN
        .expectNext()
        .expectErrorSatisfies {
          Assertions.assertThat(it).isInstanceOf(SSLHandshakeException::class.java)
          Assertions.assertThat(it.cause).hasCauseInstanceOf(CertificateException::class.java)
          Assertions.assertThat(it.cause?.cause).hasCauseInstanceOf(CertificatePinningException::class.java)
        }.verify()
  }

  @Test
  fun `we should match with no sha`() {
    //GIVEN
    val webClient = certificatePinningWebClientBuilder {
      spec {
      }
    }
    .baseUrl("https://localhost:$port/test")
    .build()

    //WHEN
    StepVerifier.create(webClient.get().exchange())
        //THEN
        .expectNext()
        .expectErrorSatisfies {
          Assertions.assertThat(it).isInstanceOf(SSLHandshakeException::class.java)
          Assertions.assertThat(it.cause).hasCauseInstanceOf(CertificateException::class.java)
          Assertions.assertThat(it.cause?.cause).hasCauseInstanceOf(CertificatePinningException::class.java)
        }.verify()
  }
}