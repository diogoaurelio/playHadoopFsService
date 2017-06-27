FROM ubuntu:latest

# update and install basic tools
RUN apt-get update && apt-get upgrade -y
RUN apt-get install -yq curl software-properties-common

ENV HIVE_VERSION 2.1.1
ENV HADOOP_VERSION 2.7.2
ENV HIVE_PORT 10000
ENV HIVE_METASTORE_PORT 9083
ENV DERBY_VERSION 10.13.1.1
# install java
RUN \
  echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | debconf-set-selections && \
  add-apt-repository -y ppa:webupd8team/java && \
  apt-get update && \
  apt-get install -y oracle-java8-installer && \
  rm -rf /var/lib/apt/lists/* && \
  rm -rf /var/cache/oracle-jdk8-installer
ENV JAVA_HOME /usr/lib/jvm/java-8-oracle


# install hadoop
RUN mkdir /usr/local/hadoop
RUN curl -s http://apache.claz.org/hadoop/common/hadoop-$HADOOP_VERSION/hadoop-$HADOOP_VERSION.tar.gz | tar -xz -C /usr/local/hadoop --strip-components 1
ENV HADOOP_HOME /usr/local/hadoop
ENV HADOOP_INSTALL $HADOOP_HOME
ENV PATH $PATH:$HADOOP_INSTALL/sbin
ENV HADOOP_MAPRED_HOME $HADOOP_INSTALL
ENV HADOOP_COMMON_HOME $HADOOP_INSTALL
ENV HADOOP_HDFS_HOME $HADOOP_INSTALL
ENV YARN_HOME $HADOOP_INSTALL
ENV PATH $HADOOP_HOME/bin:$PATH


RUN $HADOOP_HOME/bin/hadoop fs -mkdir -p       /tmp
RUN $HADOOP_HOME/bin/hadoop fs -mkdir -p      /user/hive/warehouse
RUN $HADOOP_HOME/bin/hadoop fs -chmod g+w   /tmp
RUN $HADOOP_HOME/bin/hadoop fs -chmod g+w   /user/hive/warehouse

# install hive
RUN mkdir /usr/local/hive
RUN curl -s http://apache.mesi.com.ar/hive/hive-$HIVE_VERSION/apache-hive-$HIVE_VERSION-bin.tar.gz | tar -xz -C /usr/local/hive --strip-components 1
ENV HIVE_HOME /usr/local/hive
ENV PATH $HIVE_HOME/bin:$PATH

# DERBY setup
RUN echo "l"
#RUN cd /tmp && wget http://archive.apache.org/dist/db/derby/db-derby-$DERBY_VERSION/db-derby-$DERBY_VERSION-bin.tar.gz
#RUN tar xvzf /tmp/db-derby-$DERBY_VERSION-bin.tar.gz -C /usr/local
#RUN rm /tmp/db-derby-$DERBY_VERSION-bin.tar.gz
#ENV DERBY_HOME /usr/local/db-derby-$DERBY_VERSION-bin
#RUN mkdir -p $DERBY_HOME/data

# hive conf setup
RUN cd $HIVE_HOME/conf
RUN cp $HIVE_HOME/conf/hive-default.xml.template $HIVE_HOME/conf/hive-site.xml
RUN echo "yo"
COPY jpox.properties $HIVE_HOME/conf/jpox.properties


# Derby for Hive metastore backend
RUN cd $HIVE_HOME && $HIVE_HOME/bin/schematool -dbType derby -initSchema --verbose


# Run HCatalogue
#RUN $HIVE_HOME/hcatalog/sbin/hcat_server.sh
RUN apt-get -qq update
RUN apt-get install -qq -y vim nano

COPY startup.sh /usr/local/bin/
RUN chmod +x /usr/local/bin/startup.sh

# default hive.port
EXPOSE $HIVE_PORT
# default hive.metastore.port
EXPOSE $HIVE_METASTORE_PORT



