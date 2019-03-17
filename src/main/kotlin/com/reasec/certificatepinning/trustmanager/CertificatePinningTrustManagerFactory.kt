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

import com.reasec.certificatepinning.model.CertificatePinningSpec
import io.netty.handler.ssl.util.SimpleTrustManagerFactory
import java.security.KeyStore
import javax.net.ssl.ManagerFactoryParameters
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory

open class CertificatePinningTrustManagerFactory(spec: CertificatePinningSpec,
                                                 private val defaultTrustManager: TrustManagerFactory) : SimpleTrustManagerFactory() {

  constructor(spec: CertificatePinningSpec) :
      this(spec, TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm()))

  private val certificatePinningTrustManager: CertificatePinningTrustManager

  init {
    defaultTrustManager.init(null as KeyStore?)
    this.certificatePinningTrustManager = CertificatePinningTrustManager(spec, defaultTrustManager.trustManagers)
  }

  override fun engineGetTrustManagers(): Array<TrustManager> {
    return arrayOf(certificatePinningTrustManager)
  }

  override fun engineInit(keyStore: KeyStore?) {
    defaultTrustManager.init(keyStore)
  }

  override fun engineInit(managerFactoryParameters: ManagerFactoryParameters?) {
    defaultTrustManager.init(managerFactoryParameters)
  }
}
