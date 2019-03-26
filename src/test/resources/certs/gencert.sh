#!/usr/bin/env bash

if [[ $(id -u) -ne 0 ]] ; then echo "Please run as root" ; exit 1 ; fi

openssl req -newkey rsa:2048 -nodes -keyout private.pem -x509 -days 3650 -out certificate.pem -subj "/C=UK/ST=London/L=London/O=reasec/CN=localhost"
openssl pkcs12 -inkey private.pem -in certificate.pem -name selfsigned -export -out certificate.p12 -password pass:password
openssl x509 -pubkey -in certificate.pem -noout > public.pem
rm private.pem
rm certificate.pem

set +e
keytool -delete -storepass changeit -noprompt -keystore ${JAVA_HOME}/jre/lib/security/cacerts -alias selfsigned || true
set -e
keytool -importkeystore -deststorepass changeit -noprompt -destkeystore ${JAVA_HOME}/jre/lib/security/cacerts -srckeystore certificate.p12 -srcstoretype pkcs12 -srcstorepass password -alias selfsigned

TOOLS_PATH="../../../../tools"

if [ ! -f "$TOOLS_PATH/PublicKeySha.class" ]; then
    javac "$TOOLS_PATH/PublicKeySha.java"
fi

PUBLIC_KEY_SHA="$(java -cp "$TOOLS_PATH" PublicKeySha public.pem | grep "sha:" | cut -d' ' -f2)"

sed '$d' ../application.yml > ../application2.yml 
echo "  public-key-sha: \"$PUBLIC_KEY_SHA\"" | cat ../application2.yml - > ../application.yml
rm ../application2.yml