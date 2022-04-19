# Flow Aggregation Service

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
