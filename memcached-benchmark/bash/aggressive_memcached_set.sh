#!/bin/bash

usage() {
  echo ""
  echo "Usage:"
  echo "./aggressive_memcached_set.sh target/memcached-benchmark.jar 10 ~/session.txt 100"
  echo ""
}


if [[ -z $1 || -z $2 || -z $3 || -z $4 ]] 
then
    usage
    exit
fi


memcached_jar=$1
nbr_instances=$2
file=$3
loop=$4


for (( c=1; c<=$nbr_instances; c++ ))
do
  java -Dnet.spy.log.LoggerImpl=net.spy.memcached.compat.log.Log4JLogger -Dmemcached.servers.list=10.110.20.135:11211 -Dmemcached.compressionThreshold=6000 -jar $memcached_jar $file $loop &
done
