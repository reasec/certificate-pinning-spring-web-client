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

import com.reasec.certificatepinning.exceptions.CertificatePinningException;
import com.reasec.certificatepinning.tools.CertificateTools;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLHandshakeException;
import java.net.URL;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JavaCertificatePinningWebClientTest {

  private static final String GITHUB_API_URL = "https://api.github.com/";
  private static final String INVALID_SHA = "7F:22:F8:91:B5:9F:EB:99:0E:81:A9:E7:FE:47:C5:77:54:E3:11:73:D6:48:64:F4:1C:6B:CC:2F:44:0D:49:E3";
  private static String githubApiPublicKeySha;

  @BeforeClass
  static public void setup() throws Exception {
    final X509Certificate githubApiCertificate = getCertificate(GITHUB_API_URL);
    githubApiPublicKeySha = CertificateTools.Companion.getPublicKeySha(githubApiCertificate);
  }

  private static X509Certificate getCertificate(final String uri) throws Exception {
    final URL url = new URL(uri);
    final HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
    connection.connect();
    final Certificate[] serverCertificates = connection.getServerCertificates();
    final X509Certificate firstCertificate = (X509Certificate) serverCertificates[0];
    connection.disconnect();
    return firstCertificate;
  }

  @Test
  public void weShouldMatchGithubApiPublicKeySha() {
    //GIVEN
    final WebClient webClient = CertificatePinningWebClient.builder(githubApiPublicKeySha)
        .baseUrl(GITHUB_API_URL)
        .build();
    //WHEN
    StepVerifier.create(webClient.get().exchange())
        //THEN
        .assertNext(clientResponse -> assertThat(clientResponse.statusCode()).isEqualTo(HttpStatus.OK))
        .verifyComplete();
  }

  @Test
  public void weShouldNotMatchGithubApiPublicKeySha() {
    //GIVEN
    final WebClient webClient = CertificatePinningWebClient.builder(INVALID_SHA)
        .baseUrl(GITHUB_API_URL)
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
