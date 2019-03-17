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
