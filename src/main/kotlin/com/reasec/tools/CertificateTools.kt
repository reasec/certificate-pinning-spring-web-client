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

package com.reasec.tools

import java.security.MessageDigest
import java.security.cert.X509Certificate

class CertificateTools {
  companion object {
    private val sha256md = MessageDigest.getInstance("SHA-256")!!
    private val lookupTable = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')

    private fun encodeToHex(bytes: ByteArray): String {
      val multiplier = 3
      val hexChars = CharArray(bytes.size * multiplier - 1)

      for (i in bytes.indices) {
        val v = (bytes[i].toInt() and 0xFF)
        hexChars[i * multiplier] = lookupTable[v ushr 4]
        hexChars[i * multiplier + 1] = lookupTable[(v and 0x0F)]
        if (i < bytes.size - 1) {
          hexChars[i * multiplier + 2] = ':'
        }
      }
      return String(hexChars)
    }

    @Synchronized
    private fun getSha256(bytes: ByteArray): ByteArray {
      return sha256md.digest(bytes)
    }

    fun getPublicKeySha(certificate: X509Certificate): String {
      val publicKey = certificate.publicKey
      val encoded = publicKey.encoded
      val sha = getSha256(encoded)
      return encodeToHex(sha)
    }
  }
}
