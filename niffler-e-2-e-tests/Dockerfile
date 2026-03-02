FROM gradle:8.14-jdk21

WORKDIR /niffler
ENV TZ=Europe/Moscow
COPY ./gradle ./gradle
COPY ./niffler-e-2-e-tests ./niffler-e-2-e-tests

# Без этого не стартует тесты в образе
COPY ./niffler-gateway ./niffler-gateway
COPY ./niffler-currency ./niffler-currency
COPY ./niffler-auth ./niffler-auth
COPY ./niffler-spend ./niffler-spend
COPY ./niffler-userdata ./niffler-userdata

COPY ./niffler-grpc-common ./niffler-grpc-common
COPY ./gradlew ./
COPY ./build.gradle ./
COPY ./settings.gradle ./
COPY ./gradle.properties ./

CMD gradle test -Dtest.env=docker -Dclient.impl=api -Drepository=jpa -Duser.timezone=Europe/Moscow
