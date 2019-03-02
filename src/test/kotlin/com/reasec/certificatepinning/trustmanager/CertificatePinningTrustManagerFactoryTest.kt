package com.reasec.certificatepinning.trustmanager

import io.netty.handler.ssl.util.SimpleTrustManagerFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.security.KeyStore
import javax.net.ssl.ManagerFactoryParameters
import javax.net.ssl.TrustManager


class CertificatePinningTrustManagerFactoryTest : CertificatePinningTrustManagerFactory("", testTrustManagerFactory) {

  companion object {
    val testTrustManagerFactory = TestTrustManagerFactory()
  }

  class TestTrustManagerFactory: SimpleTrustManagerFactory(){
    var hasEngineInitWithKeyStoreBeenCall = false
    var hasEngineInitWithParametersBeenCall = false
    var hasBeemAskToGetTrustManagers = false

    override fun engineGetTrustManagers(): Array<TrustManager> {
      hasBeemAskToGetTrustManagers = true
      return arrayOf()
    }

    override fun engineInit(keyStore: KeyStore?) {
      hasEngineInitWithKeyStoreBeenCall = true
    }

    override fun engineInit(managerFactoryParameters: ManagerFactoryParameters?) {
      hasEngineInitWithParametersBeenCall = true
    }

    fun reset(){
      hasEngineInitWithKeyStoreBeenCall = false
      hasEngineInitWithParametersBeenCall = false
      hasBeemAskToGetTrustManagers = false
    }
  }

  @Test
  fun `engineGetTrustManagers should call the default trust manager`() {
    testTrustManagerFactory.reset()
    val factory = CertificatePinningTrustManagerFactoryTest()
    val engineGetTrustManagers = factory.engineGetTrustManagers()
    assertThat(engineGetTrustManagers).hasSize(1)
    assertThat(engineGetTrustManagers[0]).isInstanceOf(CertificatePinningTrustManager::class.java)
    assertThat(testTrustManagerFactory.hasBeemAskToGetTrustManagers).isFalse()
  }

  @Test
  fun `engineInit with a keystore should call the default trust manager`() {
    testTrustManagerFactory.reset()
    val factory = CertificatePinningTrustManagerFactoryTest()
    testTrustManagerFactory.reset()
    factory.engineInit(null as KeyStore?)
    assertThat(testTrustManagerFactory.hasEngineInitWithKeyStoreBeenCall).isTrue()
  }

  @Test
  fun `engineInit with parameters should call the default trust manager`() {
    testTrustManagerFactory.reset()
    val factory = CertificatePinningTrustManagerFactoryTest()
    factory.engineInit(null as ManagerFactoryParameters?)
    assertThat(testTrustManagerFactory.hasEngineInitWithParametersBeenCall).isTrue()
    testTrustManagerFactory.reset()
  }

}