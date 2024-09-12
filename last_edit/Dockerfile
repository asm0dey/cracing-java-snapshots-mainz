FROM bellsoft/liberica-runtime-container:jdk-crac-slim

ADD build/libs/last_edit-0.0.1-SNAPSHOT.jar /app/app.jar
WORKDIR /app
ENTRYPOINT java -XX:CRaCCheckpointTo=/app/checkpoint -jar /app/app.jar
