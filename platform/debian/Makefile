DOCKER := docker

image : pipeline-assembly
	cd pipeline-assembly && make image

check : image
	$(DOCKER) run -it \
	              -v $(CURDIR)/pipeline-assembly/src/main/resources/samples:/samples \
	              daisy/daisy-pipeline2 \
	              /bin/bash -c "service pipeline2d start && dp2 zedai-to-pef --i-source /samples/zedai/alice.xml --output /tmp/alice"

pipeline-assembly :
	git clone -b integration-test "https://github.com/daisy/pipeline-assembly" --depth 1
