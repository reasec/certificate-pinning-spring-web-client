
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

import javax.net.ssl.HttpsURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import sun.misc.BASE64Decoder;

/*
 * Simple program to get the public key sha from a giving site or pem file.
 * This program depends only on the JDK classes and has not external
 * dependencies.
 *
 *   to compile this program execute:
 *
 *     $ javac PublicKeySha.java
 *
 *   to run this program execute:
 *
 *     $ java PublicKeySha https://www.site.com
 * 
 *   or
 * 
 *     $ java PublicKeySha public-key.pem
 *
 *   the output of this program will be something like:
 *
 *     Getting public key sha for: https://www.site.com
 *     sha: 7A:5C:EC:30:0E:B0:42:6E:F9:2E:B7:8A:FA:9A:F6:28:1E:0C:FB:9F:86:A5:3D:45:75:24:86:8B:56:F2:67:B3
 */
public class PublicKeySha {

  static MessageDigest sha256md = null;

  static {
    try {
      sha256md = MessageDigest.getInstance("SHA-256");
    } catch (Exception ex) {
    }
  }

  final static char[] lookupTable = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
      'E', 'F' };

  private static byte[] getSha256(final byte[] bytes) {
    synchronized (sha256md) {
      if (sha256md == null) {
        throw new RuntimeException("Can't get SHA-256");
      }
      return sha256md.digest(bytes);
    }
  }

  private static String encodeToHex(final byte[] bytes) {
    final int multiplier = 3;
    final char[] hexChars = new char[bytes.length * multiplier - 1];
    for (int i = 0; i < bytes.length; i++) {
      final int v = bytes[i] & 0xFF;
      hexChars[i * multiplier] = lookupTable[v >>> 4];
      hexChars[i * multiplier + 1] = lookupTable[v & 0x0F];
      if (i < bytes.length - 1) {
        hexChars[i * multiplier + 2] = ':';
      }
    }
    return new String(hexChars);
  }

  private static String getPublicKeySha(byte[] publicKey) {
    final byte[] sha = getSha256(publicKey);
    return encodeToHex(sha);
  }

  private static byte[] getPublicKeyFromURL(final String uri) {
    HttpsURLConnection connection = null;
    try {
      final URL url = new URL(uri);
      connection = (HttpsURLConnection) url.openConnection();

      connection.connect();
      final Certificate[] serverCertificates = connection.getServerCertificates();
      final X509Certificate firstCertificate = (X509Certificate) serverCertificates[0];
      connection.disconnect();
      connection = null;

      final PublicKey publicKey = firstCertificate.getPublicKey();
      final byte[] encoded = publicKey.getEncoded();

      return encoded;
    } catch (Exception e) {
      throw new RuntimeException("Can't get public key for URL: " + uri, e);
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }
  }

  private static byte[] getPublicKeyFromPEM(final String filePath) {
    try (final BufferedReader br = new BufferedReader(new FileReader(filePath))) {
      String strKeyPEM = "";
      String line;
      while ((line = br.readLine()) != null) {
        strKeyPEM += line + "\n";
      }
      if ((strKeyPEM.contains("-----BEGIN PUBLIC KEY-----\n")) & (strKeyPEM.contains("-----END PUBLIC KEY-----"))) {
        strKeyPEM = strKeyPEM.replace("-----BEGIN PUBLIC KEY-----\n", "");
        strKeyPEM = strKeyPEM.replace("-----END PUBLIC KEY-----", "");

        final BASE64Decoder b64 = new BASE64Decoder();
        return b64.decodeBuffer(strKeyPEM);
      } else {
        throw new RuntimeException("pem not valid");
      }
    } catch (Exception e) {
      throw new RuntimeException("Can't get public key for pem: " + filePath, e);
    }
  }

  public static boolean isURL(final String argument) {
    if (argument.startsWith("http://")) {
      throw new RuntimeException("Couldn't get a certificate from a http site: " + argument);
    }
    return argument.startsWith("https://");
  }

  private static byte[] getPublicKey(final String parameter) {
    final byte[] publicKey;

    if (isURL(parameter)) {
      publicKey = getPublicKeyFromURL(parameter);
    } else {
      publicKey = getPublicKeyFromPEM(parameter);
    }

    if (publicKey == null) {
      throw new RuntimeException("Can't get public key for: " + parameter);
    }

    return publicKey;
  }

  public static void main(String[] args) {
    try {
      if (sha256md == null) {
        throw new RuntimeException("We couldn't create sha256");
      }
      if (args.length == 0) {
        System.out.println("Error: Missing parameter to get public key sha.");
        System.out.println(" usages:");
        System.out.println("   java PublicKeySha https://www.site.com");
        System.out.println("   java PublicKeySha public-key.pem");
      } else {
        final String parameter = args[0];
        System.out.println("Getting public key sha for: " + parameter);

        final byte[] publicKey = getPublicKey(parameter);
        final String sha = getPublicKeySha(publicKey);

        System.out.println("sha: " + sha);
      }
    } catch (Exception ex) {
      System.out.print("Error: ");
      System.out.println(ex);
      ex.printStackTrace();
    }
  }
}
