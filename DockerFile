FROM debian:jessie
MAINTAINER AKSW

# enable debian source info
RUN awk '$1 == "deb" { $1 = $1 "-src"; print; }' /etc/apt/sources.list \
	> /etc/apt/sources.list.d/src.list

RUN	apt-get update \
		&& \
	apt-get install -y -o Acquire::Retries=5 \
		supervisor \
		nano \
		apache2 \
		openjdk-7-jdk \
		curl \
		&& \
	apt-get clean \
		&& \
	rm -fr /var/lib/apt/lists/*


RUN mkdir /usr/src/myapp  

COPY feasible /usr/src/myapp/feasible
COPY feasible-backend /usr/src/myapp/feasible-backend
 
COPY supervisord.conf /etc/supervisor/supervisord.conf

COPY bashrc /root/.bashrc

RUN    	rm /var/www/html/index.html \
	&& \
	ln -s /usr/src/myapp/feasible/* /var/www/html/

CMD [ "/usr/bin/supervisord" ]

EXPOSE 80
