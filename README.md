# Certificate Pinning for Spring WebClient
<p align='center'>
<img width="200" src='https://raw.githubusercontent.com/reasec/reasec/master/img/reasec.png'/>
</p>

[![License: Apache2](https://img.shields.io/badge/license-Apache%202-blue.svg)](/LICENSE)
[![Build Status](https://travis-ci.com/reasec/certificate-pinning-spring-web-client.svg?branch=master)](https://travis-ci.com/reasec/certificate-pinning-spring-web-client)
[![codecov](https://codecov.io/gh/reasec/certificate-pinning-spring-web-client/branch/master/graph/badge.svg)](https://codecov.io/gh/reasec/certificate-pinning-spring-web-client)
[![codebeat badge](https://codebeat.co/badges/975ca4cc-6deb-4da6-8e7c-15fb89183047)](https://codebeat.co/projects/github-com-reasec-certificate-pinning-spring-web-client-master)
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
    <version>0.2.0-SNAPSHOT</version>
</dependency>
```

## Example

To create our WebClient we could use the following examples:

### Kotlin example

```kotlin
import com.reasec.certificatepinning.CertificatePinningWebClient
import com.reasec.certificatepinning.model.CertificatePinningSpec

val spec = CertificatePinningSpec.Builder()
  .sha(PUBLIC_KEY_SHA)
  .build()
        
val webClient = CertificatePinningWebClient.builder(spec)
  .baseUrl(API_URL)
  .build()

// now we could do webClient.get().exchange() etc
```
### Java example

```java
import com.reasec.certificatepinning.CertificatePinningWebClient;
import com.reasec.certificatepinning.model.CertificatePinningSpec;

final CertificatePinningSpec spec = CertificatePinningSpec.Builder()
    .sha(PUBLIC_KEY_SHA)
    .build();

final WebClient webClient = CertificatePinningWebClient.builder(spec)
    .baseUrl(API_URL)
    .build();

// now we could do webClient.get().exchange() etc
```

## Working with multiple Shas

If we need to work with multiple Shas we could use this examples to create the spec:

### Kotlin example

```kotlin
val spec = CertificatePinningSpec.Builder()
  .sha(PUBLIC_KEY_SHA)
  .sha(PUBLIC_KEY_ALTERNATIVE_SHA)
  .build()
```
### Java example

```java
final CertificatePinningSpec spec = CertificatePinningSpec.Builder()
  .sha(PUBLIC_KEY_SHA)
  .sha(PUBLIC_KEY_ALTERNATIVE_SHA)
  .build();
```

## Using the Kotlin DSL

We could use the Kotlin DSL to create our spec

```kotlin
val spec = certificatePinningSpec {
  sha(PUBLIC_KEY_SHA)
  sha(PUBLIC_KEY_ALTERNATIVE_SHA)
}

val webClient = CertificatePinningWebClient.builder(spec)
  .baseUrl(API_URL)
  .build()

// now we could do webClient.get().exchange() etc
```

Or we could use it to create the WebClient.Builder

```kotlin
val webClient = certificatePinningWebClientBuilder {
  spec {
    sha(PUBLIC_KEY_SHA)
    sha(PUBLIC_KEY_ALTERNATIVE_SHA)
  }
}
.baseUrl(API_URL)
.build()

// now we could do webClient.get().exchange() etc    
```

If the public key sha of the certificate do not match the provided one to the client we will get an error in the Mono / Flux obtained by the webclient.

The returned WebClient is the standard spring reactive WebClient, so you could use as you already do.

This library create a custom [SSLContext](https://docs.oracle.com/javase/8/docs/api/javax/net/ssl/SSLContext.html) with an additional [TrustManager](https://docs.oracle.com/javase/8/docs/api/javax/net/ssl/TrustManager.html) that will add the pinning functionality, standard functionality of the default [SSLContext](https://docs.oracle.com/javase/8/docs/api/javax/net/ssl/SSLContext.html) and [TrustManager](https://docs.oracle.com/javase/8/docs/api/javax/net/ssl/TrustManager.html) remains, such checkin the PIKX path and trust certificates management.

Since this is handle in the [SSLContext](https://docs.oracle.com/javase/8/docs/api/javax/net/ssl/SSLContext.html) and [TrustManager](https://docs.oracle.com/javase/8/docs/api/javax/net/ssl/TrustManager.html) the [SSL Handshake](https://medium.com/@kasunpdh/ssl-handshake-explained-4dabb87cdce) we will aborted if the pinning do not match avoiding additional conversation with the destination server.

## Tools

Under the [tools](/tools) directory we could find some tools, these tools do not require any dependency and could be run just from the command line.

### PublicKeySha

Use this tool to get the public key sha of a giving url or a pem file, this will allow to configure the client.

- Compile the tool

```bash
$ javac PublicKeySha.java
```

- Running the tool for a url

```bash
$ java PublicKeySha  https://www.site.com

Getting public key sha for: https://www.site.com
sha: 7A:5C:EC:30:0E:B0:42:6E:F9:2E:B7:8A:FA:9A:F6:28:1E:0C:FB:9F:86:A5:3D:45:75:24:86:8B:56:F2:67:B3
```

- Running the tool for a pem file

```bash
$ java PublicKeySha  public-key.pem

Getting public key sha for: public-key.pem
sha: B2:6A:A4:80:BC:C8:57:42:16:3B:57:3D:D4:25:C6:88:B7:4D:D4:9F:4A:4E:EE:5E:DF:D4:34:D0:33:98:3A:7F
```
The pem file should be something like

```
-----BEGIN PUBLIC KEY-----
MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA498YF1O/SZUaZuqUwkU9
+oTtmarKZkxtdH412149jEX3Oror2eMeiXWgYGYBy1irCEp3fd7yjf3ZoaNBBdtv
6+OtQbnQ5kAzw69VtdNkTaID7oCPBrk2cGqgZrpzjfxq3sJ4H/pPlOeDup/+B85O
qacZLlN+v9IcWvnKkQBcWptDpAcHYNeSgFdEh4jdYT8r08d24qZooR02CTO5T0WF
GF6a5/iRcrF4yPSgzsZDFqGPIGRvhrQNTchP+iYF0wcqSmpZGGOpOSK958+U2Y/8
wi0eQCxNCPjPRiqTpPlxX5SaF87j63+lD3NaiEFrqNk+I9IEDSH4Te3crMvIbpVC
bwIDAQAB
-----END PUBLIC KEY-----
```
## Running the tests
The tests creates a fake https server that uses a selfsigned p12 certificate, the sha of the public key in that certificate is configure in the tets.

### Importing certificates
For running the test you need to import in the java keystore the p12 that is on the src/test/resources/certs/certificate.p12, there is an script that allow to import it easily with:

***note: this require to sudo / root.***

```bash
$ cd ./src/test/resources/certs
$ sudo ./import.sh
```

### Generating certificates
You may want to generate new certificate before running the tests, there is a well a script for doing that:

***note: this require to have openssl installed.***

```bash
$ cd ./src/test/resources/certs
$ ./gentcert.sh
```
After a new certificate is generated you need to import it using the above script, the application.yml in the test folder will be updated with the new certificate public key sha.

## Additional information

[Certificate pinning](https://www.owasp.org/index.php/Certificate_and_Public_Key_Pinning) allows us to protect our API calls from servers that are actually not the ones that we think we are talking to. This will help us to protect our applications, for example from [man in the middle attacks](https://www.owasp.org/index.php/Man-in-the-middle_attack).

In this library we will ping a sha of the public key. Certificates on an external site may change, for example when expiring, but private / public keys may remain the same, and so a sha of the public key will still valid.

If you are going to keep the sha of the public key of the server that you are talking to do it privately and secure. Additionally you may need to check regularly if that sha still valid, or change it when you know it will change.

This library support multiples shas, this will allow you to have different shas for different public keys in different certificates. This may be useful if the server has more than one certificate or is planning to do it so, for example when the keys are compromised or because if has a key rotation policy.
