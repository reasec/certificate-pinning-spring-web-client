#!/usr/bin/env bash

openssl req -newkey rsa:2048 -nodes -keyout private.pem -x509 -days 3650 -out certificate.pem
openssl pkcs12 -inkey private.pem -in certificate.pem -name selfsigned -export -out certificate.p12
openssl x509 -pubkey -in certificate.pem -noout > public.pem
rm private.pem
rm certificate.pem

## after this to import the p12 in cacerts do
##
##   keytool -importkeystore -deststorepass changeit -noprompt -destkeystore cacerts -srckeystore certificate.p12 -srcstoretype pkcs12 -srcstorepass password -alias selfsigned