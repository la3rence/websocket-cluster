.PHONY: up
up:
	mvn clean
	mvn install -pl gateway -am -amd
	mvn install -pl websocket -am -amd
	docker build -t websocket:1.0.0 websocket/.
	docker build -t gateway:1.0.0 gateway/.
	docker-compose up -d
	docker ps

.PHONY: down
down:
	docker-compose down
	docker exec redis redis-cli flushall
	docker rmi $$(docker images | grep "none" | awk '{print $$3}')

.PHONY: start
start:
	docker-compose up

.PHONY: new
new:	down	up
