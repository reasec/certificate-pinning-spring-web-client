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

import org.assertj.core.api.Assertions
import org.junit.Test

class CertificatePinningSpecDslTest {
  @Test
  fun `we could create a spec with DSL`() {
    val spec = certificatePinningSpec {
      sha("123")
      sha("456")
      sha("789")
    }
    Assertions.assertThat(spec.isValidSha("123")).isTrue()
    Assertions.assertThat(spec.isValidSha("456")).isTrue()
    Assertions.assertThat(spec.isValidSha("789")).isTrue()
    Assertions.assertThat(spec.isValidSha("999")).isFalse()
  }
}
