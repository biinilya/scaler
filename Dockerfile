FROM java:openjdk-8

ADD . /tmp/scaler

RUN cd /tmp/scaler &&  ./gradlew installDist && mv build/install/scaler /scaler

RUN /scaler/bin && ls -lh

WORKDIR /scaler

CMD ["./bin/scaler"]