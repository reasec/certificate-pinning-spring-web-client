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

package com.reasec.certificatepinning.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class CertificatePinningSpecTest {
  @Test
  fun `we could generate and spec and check for shas`() {
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
  fun `empty build will not validate shas`() {
    val spec = CertificatePinningSpec.Builder()
        .build()
    assertThat(spec.isValidSha("123")).isFalse()
  }


}
