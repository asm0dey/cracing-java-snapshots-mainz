FROM bellsoft/liberica-runtime-container:jdk-musl as builder

COPY . /app
RUN cd /app && ./gradlew build -xtest

FROM bellsoft/liberica-runtime-container:jre-slim-musl as optimizer

COPY --from=builder /app/build/libs/spring-petclinic-3.3.0.jar /app/app.jar
WORKDIR /app
RUN java -Djarmode=tools -jar /app/app.jar extract --layers --launcher

FROM bellsoft/liberica-runtime-container:jre-cds-slim-musl as runner

ENTRYPOINT "java \
  -Dspring.aot.enabled=true \
  -XX:SharedArchiveFile=application.jsa \
  org.springframework.boot.loader.launch.JarLauncher"
COPY --from=optimizer /app/app/dependencies/ ./
COPY --from=optimizer /app/app/spring-boot-loader/ ./
COPY --from=optimizer /app/app/snapshot-dependencies/ ./
COPY --from=optimizer /app/app/application/ ./
RUN java -Dspring.aot.enabled=true \
  -XX:ArchiveClassesAtExit=./application.jsa \
  -Dspring.context.exit=onRefresh \
  org.springframework.boot.loader.launch.JarLauncher

