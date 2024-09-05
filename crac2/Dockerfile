FROM bellsoft/liberica-runtime-container:jdk-crac-slim

ADD Example.java /app/Example.java
WORKDIR /app
RUN javac Example.java
ENTRYPOINT java -XX:CRaCCheckpointTo=/app/checkpoint Example
