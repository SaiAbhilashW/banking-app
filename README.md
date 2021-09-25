### Problem Statement:

Create a RESTFul Api to calculate real time statistics for the last 60 seconds on transactions.

Domain: Banking.
Three Apis need to be created.

1. POST /transactions

Sample Input:
{
“amount”:”100.25”
“timestamp”:"2018-07-17T09:59:51.312Z"
}

amount – transaction amount; a string of arbitrary length that is parsable as a BigDecimal
timestamp – transaction time in the ISO 8601 format YYYY-MM-DDThh:mm:ss.sssZ in the UTC timezone (this is not the current timestamp)

Response:
201 – in case of success
204 – if the transaction is older than 60 seconds
400 – if the JSON is invalid
422 – if any of the fields are not parsable or the transaction date is in the future

2. GET /statistics

Sample Response:
{
“sum”:””,
“avg”:””,
“max”:””,
“min”:””,
“count”:””
}

sum – a BigDecimal specifying the total sum of transaction value in the last 60 seconds
avg – a BigDecimal specifying the average amount of transaction value in the last 60 seconds
max – a BigDecimal specifying single highest transaction value in the last 60 seconds
min – a BigDecimal specifying single lowest transaction value in the last 60 seconds
count – a long specifying the total number of transactions that happened in the last 60 seconds

3. DELETE /transactions
    1. This endpoint causes all existing transactions to be deleted
    2. The endpoint should accept an empty request body and return a 204 status code.

    
### Sample requests

* curl --location --request GET 'localhost:8080/v1/statistics'
* curl --location --request POST 'localhost:8080/v1/transactions' \
  --header 'Content-Type: application/json' \
  --data-raw '{
  "amount": "10",
  "timestamp": "2021-09-25T11:53:00Z" //UTC time
  }'
* curl --location --request DELETE 'localhost:8080/v1/transactions'

#### please find the open-api-spec in the docs folder or you can alternatively access it from http://localhost:8080/v3/api-docs

#### please find the swagger-ui from http://localhost:8080/webjars/swagger-ui/index.html

#### No database is used, hence initializing a datastore on app startup