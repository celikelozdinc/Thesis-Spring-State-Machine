FROM openjdk:8-jre-alpine
RUN apk update && apk add bash & apk add busybox-extras
ENV PROJECT_HOME /opt/statemachineapp
COPY out/artifacts/statemachine_jar/statemachine.jar $PROJECT_HOME/statemachine.jar
WORKDIR $PROJECT_HOME

