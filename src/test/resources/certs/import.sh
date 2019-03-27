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
## script : import.sh
##
## imports the generate p12 certificate into the java key store
############################################################################

#we need root for doing this
if [[ $(id -u) -ne 0 ]] ; then echo "Please run as root/sudo" ; exit 1 ; fi

#ignore errors and remove the certificate from keystore
set +e
keytool -delete -storepass changeit -noprompt -keystore ${JAVA_HOME}/jre/lib/security/cacerts -alias selfsigned || true
set -e

#import certificate in the key store
keytool -importkeystore -deststorepass changeit -noprompt -destkeystore ${JAVA_HOME}/jre/lib/security/cacerts -srckeystore certificate.p12 -srcstoretype pkcs12 -srcstorepass password -alias selfsigned
