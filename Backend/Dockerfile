FROM postgres:14.2-bullseye

ENV POSTGRES_PASSWORD=drowssap
ENV POSTGRES_DB=server-essentials

VOLUME . /Server-Essentials
ADD ./ Server-Essentials/
RUN apt-get update -y && apt install default-jdk -y
WORKDIR /Server-Essentials
RUN ./gradlew build

EXPOSE 8080

ENTRYPOINT ["/bin/bash", "java -jar /Server-Essentials/build/libs/Server-Essentials-*.jar"]
