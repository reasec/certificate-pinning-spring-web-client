#!/usr/bin/env bash

## generate certs and keys for localhost testing, when asked set password as 'password' and cn as 'localhost'
openssl req -newkey rsa:2048 -nodes -keyout private.pem -x509 -days 3650 -out certificate.pem
openssl pkcs12 -inkey private.pem -in certificate.pem -name selfsigned -export -out certificate.p12
openssl x509 -pubkey -in certificate.pem -noout > public.pem
rm private.pem
rm certificate.pem

## after this to import the p12 in cacerts, do this from the root of the project
##
##   sudo keytool -importkeystore -deststorepass changeit -noprompt -destkeystore ${JAVA_HOME}/jre/lib/security/cacerts -srckeystore src/test/resources/certs/certificate.p12 -srcstoretype pkcs12 -srcstorepass password -alias selfsigned