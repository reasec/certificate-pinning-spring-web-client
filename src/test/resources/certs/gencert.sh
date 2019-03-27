#!/usr/bin/env bash

#
# Copyright 2019 The ReaSec project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
# 
#      http://www.apache.org/licenses/LICENSE-2.0
# 
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# 

############################################################################
## script : gencert.sh
##
## generate a p12 certificate, a public key and update application.yml
############################################################################

#generate certificate, private key, public key and p12
openssl req -newkey rsa:2048 -nodes -keyout private.pem -x509 -days 3650 -out certificate.pem -subj "/C=UK/ST=London/L=London/O=reasec/CN=localhost"
openssl pkcs12 -inkey private.pem -in certificate.pem -name selfsigned -export -out certificate.p12 -password pass:password
openssl x509 -pubkey -in certificate.pem -noout > public.pem

#remove private key and certificate
rm private.pem
rm certificate.pem

#set our tools folder
TOOLS_PATH="../../../../tools"

#Compile PublicKeySha
javac "$TOOLS_PATH/PublicKeySha.java"

#Get the public key sha
PUBLIC_KEY_SHA="$(java -cp "$TOOLS_PATH" PublicKeySha public.pem | grep "sha:" | cut -d' ' -f2)"

#change our test yaml with the new sha
sed "s/NEWPUBLICKEY/$PUBLIC_KEY_SHA/g" ../application.tpl.yml > ../application.yml

echo "p12 generated an application.yml update with the public key sha, import it into the Java Key Store with import.sh"
