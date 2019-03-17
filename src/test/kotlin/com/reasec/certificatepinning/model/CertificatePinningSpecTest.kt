package com.reasec.certificatepinning.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class CertificatePinningSpecTest {

    @Test
    fun `we could generate and spec and check for shas`(){
        val spec = CertificatePinningSpec.Builder()
                .sha("123")
                .sha("456")
                .sha("789")
                .build()

        assertThat(spec.isValidSha("123")).isTrue()
        assertThat(spec.isValidSha("456")).isTrue()
        assertThat(spec.isValidSha("789")).isTrue()
        assertThat(spec.isValidSha("999")).isFalse()
    }

    @Test
    fun `empty build will not validate shas`(){
        val spec = CertificatePinningSpec.Builder()
                .build()
        assertThat(spec.isValidSha("123")).isFalse()
    }
}
