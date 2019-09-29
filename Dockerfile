FROM openjdk:8-jre-alpine
RUN apk update && \
apk add bash && \ 
apk add busybox-extras &&  \
apk add wget

# Zookeeper #
WORKDIR /opt
RUN wget -q kozyatagi.mirror.guzel.net.tr/apache/zookeeper/zookeeper-3.4.14/zookeeper-3.4.14.tar.gz && \
tar -xvf zookeeper-3.4.14.tar.gz && \
mv zookeeper-3.4.14 zookeeper && \
rm -rf zookeeper-3.4.14.tar.gz
WORKDIR /opt/zookeeper
RUN mkdir data && \
touch conf/zoo.cfg && \
echo tickTime=2000 > conf/zoo.cfg && \
echo dataDir=/opt/zookeeper/data >> conf/zoo.cfg && \
echo initLimit=10 >> conf/zoo.cfg && \
echo syncLimit=5 >> conf/zoo.cfg && \
echo clientPort=2181 >> conf/zoo.cfg 
EXPOSE 2181

# Spring Boot # 
ENV PROJECT_HOME /opt/statemachineapp
COPY out/artifacts/statemachine_jar/statemachine.jar $PROJECT_HOME/statemachine.jar
COPY out/artifacts/Thesis_Spring_State_Machine_jar/ $PROJECT_HOME/Thesis_Spring_State_Machine_jar/
WORKDIR $PROJECT_HOME

# Start zookeper in foreground #
# You should start container with detached mode #  
ENTRYPOINT ["/bin/bash","/opt/zookeeper/bin/zkServer.sh","start-foreground"]
