FROM hseeberger/scala-sbt:11.0.13_1.6.2_3.1.0

# prevent this error: java.lang.IllegalStateException: cannot run sbt from root directory without -Dsbt.rootdir=true; see sbt/sbt#1458
WORKDIR /app

ARG DB_NAME=ada_business
ARG DB_PASSWORD=password
ARG DB_USERNAME=ada

ENV DB_NAME=$DB_NAME
ENV DB_PASSWORD=$DB_PASSWORD
ENV DB_USERNAME=$DB_USERNAME

COPY . /app

EXPOSE 8080

# RUN sbt reload
RUN sbt compile

ENTRYPOINT sbt run