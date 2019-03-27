#!/usr/bin/env bash

#we need root for doing this
if [[ $(id -u) -ne 0 ]] ; then echo "Please run as root" ; exit 1 ; fi

#generate certificate, private key, public key and p12
openssl req -newkey rsa:2048 -nodes -keyout private.pem -x509 -days 3650 -out certificate.pem -subj "/C=UK/ST=London/L=London/O=reasec/CN=localhost"
openssl pkcs12 -inkey private.pem -in certificate.pem -name selfsigned -export -out certificate.p12 -password pass:password
openssl x509 -pubkey -in certificate.pem -noout > public.pem

#remove private key and certificate
rm private.pem
rm certificate.pem

#ignore errors and remove the certificate from keystore
set +e
keytool -delete -storepass changeit -noprompt -keystore ${JAVA_HOME}/jre/lib/security/cacerts -alias selfsigned || true
set -e

#import certificate in the key store
keytool -importkeystore -deststorepass changeit -noprompt -destkeystore ${JAVA_HOME}/jre/lib/security/cacerts -srckeystore certificate.p12 -srcstoretype pkcs12 -srcstorepass password -alias selfsigned

#set our tools folder
TOOLS_PATH="../../../../tools"

#Compile PublicKeySha if required
if [ ! -f "$TOOLS_PATH/PublicKeySha.class" ]; then
    javac "$TOOLS_PATH/PublicKeySha.java"
fi

#Get the public key sha
PUBLIC_KEY_SHA="$(java -cp "$TOOLS_PATH" PublicKeySha public.pem | grep "sha:" | cut -d' ' -f2)"

#change our test yaml with the new sha
sed "s/NEWPUBLICKEY/$PUBLIC_KEY_SHA/g" ../application.tpl.yml > ../application.yml
