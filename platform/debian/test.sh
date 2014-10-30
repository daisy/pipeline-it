#!/bin/bash

vagrant ssh -c "docker-enter reprepro /bin/update-repository.sh; docker run --link reprepro:reprepro daisy/debian bash -c \"apt-get update && apt-get install -y --force-yes daisy-pipeline2\""
