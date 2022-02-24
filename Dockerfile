FROM certbot/dns-google:amd64-v1.23.0
RUN apk add --update-cache openjdk8-jre
ADD build/distributions/certbot-concourse-resource.tar /
RUN mkdir /opt/resource
RUN ln -s /certbot-concourse-resource/bin/* /opt/resource/
