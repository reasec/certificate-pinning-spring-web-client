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

package com.reasec.certificatepinning;

import com.reasec.certificatepinning.exception.CertificatePinningException;
import com.reasec.certificatepinning.model.CertificatePinningSpec;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import javax.net.ssl.SSLHandshakeException;
import java.security.cert.CertificateException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class JavaCertificatePinningWebClientTest {

  @LocalServerPort
  Integer port;

  @Value("${test.public-key-sha}")
  String publicKeySha;

  @Value("${test.invalid-sha}")
  String invalidSha;

  @Test
  public void weShouldMatchThePublicKeySha() {
    //GIVEN
    final CertificatePinningSpec spec = CertificatePinningSpec.Builder()
        .sha(publicKeySha)
        .build();
    final WebClient webClient = CertificatePinningWebClient.builder(spec)
        .baseUrl("https://localhost:" + port + "/test")
        .build();
    //WHEN
    StepVerifier.create(webClient.get().exchange())
        //THEN
        .assertNext(clientResponse -> assertThat(clientResponse.statusCode()).isEqualTo(HttpStatus.OK))
        .verifyComplete();
  }

  @Test
  public void weShouldNotMatchGThePublicKeySha() {
    //GIVEN
    final CertificatePinningSpec spec = CertificatePinningSpec.Builder()
        .sha(invalidSha)
        .build();
    final WebClient webClient = CertificatePinningWebClient.builder(spec)
        .baseUrl("https://localhost:" + port + "/test")
        .build();

    //WHEN
    StepVerifier.create(webClient.get().exchange())
        //THEN
        .expectNext()
        .expectErrorSatisfies(throwable -> {
          assertThat(throwable).isInstanceOf(SSLHandshakeException.class);
          assertThat(throwable.getCause()).hasCauseInstanceOf(CertificateException.class);
          assertThat(throwable.getCause().getCause()).hasCauseInstanceOf(CertificatePinningException.class);
        }).verify();
  }

  @Test
  public void weShouldMatchThePublicKeyShaWithOtherSha() {
    //GIVEN
    final CertificatePinningSpec spec = CertificatePinningSpec.Builder()
        .sha(publicKeySha)
        .sha(invalidSha)
        .build();
    final WebClient webClient = CertificatePinningWebClient.builder(spec)
        .baseUrl("https://localhost:" + port + "/test")
        .build();
    //WHEN
    StepVerifier.create(webClient.get().exchange())
        //THEN
        .assertNext(clientResponse -> assertThat(clientResponse.statusCode()).isEqualTo(HttpStatus.OK))
        .verifyComplete();
  }

  @Test
  public void weShouldNoMatchThePublicKeyShaWithEmptySpec() {
    //GIVEN
    final CertificatePinningSpec spec = CertificatePinningSpec.Builder()
        .build();
    final WebClient webClient = CertificatePinningWebClient.builder(spec)
        .baseUrl("https://localhost:" + port + "/test")
        .build();

    //WHEN
    StepVerifier.create(webClient.get().exchange())
        //THEN
        .expectNext()
        .expectErrorSatisfies(throwable -> {
          assertThat(throwable).isInstanceOf(SSLHandshakeException.class);
          assertThat(throwable.getCause()).hasCauseInstanceOf(CertificateException.class);
          assertThat(throwable.getCause().getCause()).hasCauseInstanceOf(CertificatePinningException.class);
        }).verify();
  }
}
