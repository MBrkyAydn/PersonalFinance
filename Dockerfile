FROM ubuntu:latest
LABEL authors="brkya"

ENTRYPOINT ["top", "-b"]