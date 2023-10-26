FROM ubuntu

RUN apt update 
RUN apt install apt-utils -y
RUN apt upgrade -y

RUN apt install time -y

RUN apt install gcc -y

RUN apt install openjdk-17-jdk openjdk-17-jre -y

RUN apt install python3 -y
RUN apt install python3-pip -y
RUN apt install python3.10-venv -y

COPY entrypoint.sh /entrypoint.sh

RUN useradd -u 1001 -m student
RUN mkdir /execution/

RUN chown student:student /execution/
RUN chmod 775 /execution/
RUN chown student /execution/
RUN chown student -R /home/student

RUN apt -y install util-linux

ENTRYPOINT ["/bin/sh", "entrypoint.sh"]

CMD ["/bin/bash"]
