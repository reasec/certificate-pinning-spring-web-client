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

package com.reasec.certificatepinning.connector

import com.reasec.certificatepinning.trustmanager.CertificatePinningTrustManagerFactory
import io.netty.handler.ssl.SslContextBuilder
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import reactor.netty.http.client.HttpClient

class CertificatePinningConnector(publicKeySha: String) : ReactorClientHttpConnector(newSecureClient(publicKeySha)) {
  companion object {
    fun newSecureClient(publicKeySha: String): HttpClient {
      return HttpClient.create().secure {
        it.sslContext(SslContextBuilder.forClient().trustManager(CertificatePinningTrustManagerFactory(publicKeySha)))
      }
    }
  }
}
