### Running the application

docker-compose up

### API Specifications

API docs: http://localhost:8080/v3/api-docs/

Swagger UI: http://localhost:8080/swagger-ui/index.html#/

### Sample Employee request object (without employeeId, contractId & employeeState)
```
{
"firstName": "Rakesh",
"lastName": "Chandru",
"age": 18,
"contract": {
    "beginDate": "2022-01-14T18:16:19.105Z",
    "endDate": "2022-01-14T18:16:19.105Z",
    "compensation": 100000,
    "info": "Marketing Manager Role"
    }
}
```
