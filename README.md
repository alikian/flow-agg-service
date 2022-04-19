# Flow Aggregation Service

Service developed using Spring Boot. The service store aggregated Flow log into structure like
```
Map<Integer,Map<String,Flow>> aggMap = ConcurrentHashMap<>();
```
Which the Integer key is **Hour** and String key is combination of **"src_app + dest_app + vpc_id"** seperated by "|".

The ConcurrentHashMap is a threadsafe and efficient for concurrent access.

AtomicInteger used to make aggregation calculation Atomic

Since hours increasing it required a separate process to clean up old hours if no longer required.

## Scalability
Service relay on ConcurrentHashMap and performance bottleneck would be concurrency limit of ConcurrentHashMap, 
specialty when new Aggregation Flow added to Map, this process it required lock on part of map.

Solution in not horizontally accessible, a better solution is to use a time-series database or writing to 
streaming service like Kafka or AWS Kinesis Stream and process it.

## Test 
Code has a test that call controller in a multi-thread load test, 30 threads and each thread send 10,000 request.
The send operation test result over 300,000 TPS on i7 laptop


## To start service
This is a Spring Boot application, to start run this command:
```
mvn spring-boot:run
```

### Example Post
```
curl -X POST "http://localhost:8080/flows" \
-H 'Content-Type: application/json' \
-d '[{"src_app": "foo", "dest_app": "bar", "vpc_id": "vpc-0", "bytes_tx": 100, "bytes_rx": 300, "hour": 1}]'
```

### Example Query
```
curl "http://localhost:8080/flows?hour=1"
```
