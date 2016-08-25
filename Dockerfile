FROM java:openjdk-8

ADD . /tmp/scaler

RUN cd /tmp/scaler &&  ./gradlew installDist && mv build/install/scaler /scaler

WORKDIR /scaler

CMD [/scaler/bin/scaler]