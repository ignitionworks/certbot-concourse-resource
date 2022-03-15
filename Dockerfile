FROM certbot/dns-google:amd64-v1.23.0
RUN apk add --update-cache openjdk11-jre
RUN unlink /usr/lib/jvm/java-11-openjdk/lib/security/cacerts && \
      cp /etc/ssl/certs/java/cacerts /usr/lib/jvm/java-11-openjdk/lib/security/cacerts
ADD build/distributions/certbot-concourse-resource*.tar /
RUN mkdir /opt/resource
RUN ln -s /certbot-concourse-resource/bin/* /opt/resource/
