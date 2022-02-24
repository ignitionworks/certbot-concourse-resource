FROM certbot/dns-google:amd64-v1.23.0
RUN apk add --update-cache openjdk8-jre
ADD build/distributions/certbot-concourse-resource.tar /
ADD opt/. /opt/
