FROM ubuntu:20.04

# copy dsgen tool to the image
# ARG DSGEN_SOFTWARE_CODE_PATH
# COPY ${DSGEN_SOFTWARE_CODE_PATH} /dsgenerator/
COPY ./DSGen-software-code/ /dsgenerator/

# install required packages to create binary tools
RUN apt update && apt install make -y && apt install gcc -y

# make dsdgen and dsqgen tools
WORKDIR /dsgenerator/tools
RUN cp Makefile.suite Makefile
RUN make

# create folder for output
RUN mkdir -p /dsdgen_output

# generate data
ARG SCALE=1
ARG RNGSEED=66
RUN ./dsdgen -scale ${SCALE} -rngseed ${RNGSEED} -dir /dsdgen_output/

CMD tail -f /dev/null