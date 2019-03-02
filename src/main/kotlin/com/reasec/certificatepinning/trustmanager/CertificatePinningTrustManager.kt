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

package com.reasec.certificatepinning.trustmanager

import com.reasec.certificatepinning.exceptions.CertificatePinningException
import com.reasec.certificatepinning.tools.CertificateTools
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class CertificatePinningTrustManager(private val publicKeySha: String, private val trustManagers: Array<TrustManager>) : X509TrustManager {
  @Throws(CertificateException::class)
  override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
    throw CertificateException(UnsupportedOperationException())
  }

  @Throws(CertificateException::class)
  override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
    trustManagers.toList().forEach {
      if (it is X509TrustManager) {
        it.checkServerTrusted(chain, authType)
      }
    }
    if (chain != null && chain.isNotEmpty()) {
      pingCertificate(chain[0])
    }
  }

  @Throws(CertificateException::class)
  fun pingCertificate(certificate: X509Certificate) {
    val certificatePublicKeySha = CertificateTools.getPublicKeySha(certificate)
    if (certificatePublicKeySha != publicKeySha) {
      throw CertificateException(CertificatePinningException("public keys sha do not match"))
    }
  }

  override fun getAcceptedIssuers(): Array<X509Certificate?> {
    val arrayOfX509Certificates = ArrayList<X509Certificate?>()

    trustManagers.filter {
      it is X509TrustManager
    }.forEach { manager ->
      (manager as X509TrustManager).acceptedIssuers.forEach {
        arrayOfX509Certificates.add(it)
      }
    }

    return arrayOfX509Certificates.toTypedArray()
  }
}
