FROM ubuntu:latest

#FROM sequenceiq/pam:ubuntu-14.04

# update and install basic tools
RUN apt-get update && apt-get upgrade -y
RUN apt-get install -yq curl software-properties-common

ENV HIVE_VERSION 2.1.1
ENV HADOOP_VERSION 2.7.2
ENV HIVE_PORT 10000
ENV HIVE_METASTORE_PORT 9083
ENV DERBY_VERSION 10.13.1.1
ENV HIVE_SERVER_PORT 10002


# install dev tools
RUN echo "mysql-server-5.5 mysql-server/root_password password password" | debconf-set-selections
RUN echo "mysql-server-5.5 mysql-server/root_password_again password password" | debconf-set-selections
RUN apt-get -qq update
RUN apt-get -qq -y install net-tools telnet curl tar sudo openssh-server openssh-client rsync git python-pip mysql-server lzop jq bison gnuplot vim

# passwordless ssh
RUN rm -f /etc/ssh/ssh_host_dsa_key /etc/ssh/ssh_host_rsa_key /root/.ssh/id_rsa
RUN ssh-keygen -q -N "" -t dsa -f /etc/ssh/ssh_host_dsa_key
RUN ssh-keygen -q -N "" -t rsa -f /etc/ssh/ssh_host_rsa_key
RUN ssh-keygen -q -N "" -t rsa -f /root/.ssh/id_rsa
RUN cp /root/.ssh/id_rsa.pub /root/.ssh/authorized_keys

ADD ssh_config /root/.ssh/config
RUN chmod 600 /root/.ssh/config
RUN chown root:root /root/.ssh/config

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
ENV DB_TYPE mysql


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
#RUN cd /tmp && wget http://archive.apache.org/dist/db/derby/db-derby-$DERBY_VERSION/db-derby-$DERBY_VERSION-bin.tar.gz
#RUN tar xvzf /tmp/db-derby-$DERBY_VERSION-bin.tar.gz -C /usr/local
#RUN rm /tmp/db-derby-$DERBY_VERSION-bin.tar.gz
#ENV DERBY_HOME /usr/local/db-derby-$DERBY_VERSION-bin
#RUN mkdir -p $DERBY_HOME/data
#ENV PATH $PATH:$DERBY_HOME/bin
#ENV CLASSPATH $CLASSPATH:$DERBY_HOME/lib/derby.jar:$DERBY_HOME/lib/derbytools.jar

# hive conf setup
RUN echo "hive setup"
#RUN cd $HIVE_HOME/conf
#RUN cp $HIVE_HOME/conf/hive-default.xml.template $HIVE_HOME/conf/hive-site.xml
# add custom settings
ADD hive-site.xml $HIVE_HOME/conf/hive-site.xml
ADD jpox.properties $HIVE_HOME/conf/jpox.properties

# mysql 4 hive customization
ADD extlibs/mysql-connector-java-5.1.18-bin.jar $HIVE_HOME/lib/mysql-connector-java-5.1.18-bin.jar
ENV CLASSPATH $CLASSPATH:$HIVE_HOME/lib/mysql-connector-java-5.1.18-bin.jar
ADD mysql-hive.sql $HIVE_HOME/conf/mysql-hive.sql
RUN cd /usr/local/hive/scripts/metastore/upgrade/mysql/ && service mysql start && mysql -u root -ppassword < $HIVE_HOME/conf/mysql-hive.sql
RUN cd $HIVE_HOME/conf/
# Derby for Hive metastore backend
#RUN cd $HIVE_HOME && $HIVE_HOME/bin/schematool -dbType $DB_TYPE -initSchema --verbose


# Run HCatalogue
#RUN $HIVE_HOME/hcatalog/sbin/hcat_server.sh

COPY startup.sh /usr/local/bin/
RUN chmod +x /usr/local/bin/startup.sh
RUN mkdir -p /usr/local/mount_data

# default hive.port
EXPOSE $HIVE_PORT
# default hive.metastore.port
EXPOSE $HIVE_METASTORE_PORT
EXPOSE $HIVE_SERVER_PORT

CMD /usr/local/bin/startup.sh



