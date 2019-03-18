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

class CertificatePinningSpec private constructor(private val shas: ArrayList<String> = ArrayList()) {
  companion object {
    @JvmStatic
    fun Builder() = Builder.create()
  }

  fun isValidSha(sha: String): Boolean = shas.filter { it == sha }.size == 1

  class Builder private constructor() {
    private val shas: ArrayList<String> = ArrayList()

    private constructor (builder: Builder) : this() {
      shas += builder.shas
    }

    companion object {
      fun create() = Builder()
    }

    fun sha(value: String): Builder {
      val builder = Builder(this)
      builder.shas.add(value)
      return builder
    }

    fun build(): CertificatePinningSpec = CertificatePinningSpec(shas)
  }
}
