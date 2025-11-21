#!/bin/sh

#https://stackoverflow.com/questions/50928061/certificate-for-localhost-doesnt-match-any-of-the-subject-alternative-names
#https://ultimatesecurity.pro/post/san-certificate/
keytool -genkeypair -keyalg RSA -keysize 2048 -validity 3650 \
-ext "SAN:c=DNS:localhost,IP:127.0.0.1" \
-dname "CN=localhost,OU=Unknown,O=Unknown,L=Unknown,ST=Unknown,C=Unknown" \
-keystore keystore.p12  -alias https-hello \
-storepass password
