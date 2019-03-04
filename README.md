# Certificate Pinning for Spring WebClient
<p align='center'>
<img width="200" src='https://raw.githubusercontent.com/reasec/reasec/master/img/reasec.png'/>
</p>

[![License: Apache2](https://img.shields.io/badge/license-Apache%202-blue.svg)](/LICENSE)
[![Build Status](https://travis-ci.com/reasec/certificate-pinning-spring-web-client.svg?branch=master)](https://travis-ci.com/reasec/certificate-pinning-spring-web-client)
[![codecov](https://codecov.io/gh/reasec/certificate-pinning-spring-web-client/branch/master/graph/badge.svg)](https://codecov.io/gh/reasec/certificate-pinning-spring-web-client)
[![](https://jitpack.io/v/reasec/certificate-pinning-spring-web-client.svg)](https://jitpack.io/#reasec/certificate-pinning-spring-web-client)


## Usage

Include in your pom:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.reasec</groupId>
    <artifactId>certificate-pinning-spring-web-client</artifactId>
    <version>0.1.0-SNAPSHOT</version>
</dependency>
```

### Kotlin example

```kotlin
import com.reasec.certificatepinning.CertificatePinningWebClient

val webClient = CertificatePinningWebClient.builder(PUBLIC_KEY_SHA)
        .baseUrl(API_URL)
        .build()

// now we could do webClient.get().exchange() etc
```
### Java example
```java
import com.reasec.certificatepinning.CertificatePinningWebClient;

final WebClient webClient = CertificatePinningWebClient.builder(PUBLIC_KEY_SHA)
        .baseUrl(API_URL)
        .build();

// now we could do webClient.get().exchange() etc
```

If the public key sha of the certificate do not match the provided one to the client we will get an error in the Mono / Flux obtained by the webclient.

The returned WebClient is the standard spring reactive WebClient, so you could use as you already do.

This library create a custom [SSLContext](https://docs.oracle.com/javase/8/docs/api/javax/net/ssl/SSLContext.html) with an additional [TrustManager](https://docs.oracle.com/javase/8/docs/api/javax/net/ssl/TrustManager.html) that will add the pinning functionality, standard functionality of the default [SSLContext](https://docs.oracle.com/javase/8/docs/api/javax/net/ssl/SSLContext.html) and [TrustManager](https://docs.oracle.com/javase/8/docs/api/javax/net/ssl/TrustManager.html) remains, such checkin the PIKX path and trust certificates management.

Since this is handle in the [SSLContext](https://docs.oracle.com/javase/8/docs/api/javax/net/ssl/SSLContext.html) and [TrustManager](https://docs.oracle.com/javase/8/docs/api/javax/net/ssl/TrustManager.html) the [SSL Handshake](https://medium.com/@kasunpdh/ssl-handshake-explained-4dabb87cdce) we will aborted if the pinning do not match avoiding additional conversation with the destination server.

## Tools

Under the [tools](/tools) directory we could find some tools, these tools do not require any dependency and could be run just from the command line.

### PublicKeySha

Use this tool to get the public key sha of a giving url, this will allow to configure the client.

- Compile the tool

```bash
$ javac PublicKeySha.java
```

- Running the tool

```bash
$ java PublicKeySha  https://www.site.com

Getting public key sha for: https://www.site.com
sha: 7A:5C:EC:30:0E:B0:42:6E:F9:2E:B7:8A:FA:9A:F6:28:1E:0C:FB:9F:86:A5:3D:45:75:24:86:8B:56:F2:67:B3
```

## Additional information

[Certificate pinning](https://www.owasp.org/index.php/Certificate_and_Public_Key_Pinning) allows us to protect our API calls from servers that are actually not the ones that we think we are talking to. This will help us to protect our applications, for example from [man in the middle attacks](https://www.owasp.org/index.php/Man-in-the-middle_attack).

In this library we will ping a sha of the public key. Certificates on an external site may change, for example when expiring, but private / public keys may remain the same, and so a sha of the public key will still valid.

If you are going to keep the sha of the public key of the server that you are talking to do it privately and secure. Additionally you may need to check regularly if that sha still valid, or change it when you know it will change.

This library will get update eventually to support a list of valid shas, this will allow you to have different shas for different public keys in different certificates. This may be useful if the server has more than one certificate or is planning to do it so, for example when the keys are compromised or because if has a key rotation policy.
