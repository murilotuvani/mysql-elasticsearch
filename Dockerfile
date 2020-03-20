FROM elasticsearch:6.8.7
COPY logging.yml /usr/share/elasticsearch/config/
COPY elasticsearch.yml /usr/share/elasticsearch/config/elasticsearch.yml
CMD ["/bin/sh"]
USER elasticsearch
ENV PATH=$PATH:/usr/share/elasticsearch/bin
CMD ["elasticsearch"]
EXPOSE 9200 9300