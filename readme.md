# Aws RDS + Spring Boot

This example show how to use AWS RDS service with Java and Spring Boot web to receive request api request and do action of aws rds.

## Required

- Java 17 (or higher) from [https://adoptium.net/](https://adoptium.net/)

- Maven 3.6.3 (or higher) from [https://maven.apache.org/](https://maven.apache.org/)

### Build

``mvn``

Note that the first build may take some time due to maven downloading dependencies and tools.

### Run

- from IDE: run ``AwsExampleApplication`` as a Spring Boot App

- from command line: ``java -jar target/aws-rds-0.0.1-SNAPSHOT.jar``

The application will start and wait to receive http request to perform action accordingly

### AWS RDS

AWS RDS is relational database from amazon aws. RDS = Relational Database Service

In order to use AWS RDS you need to have an AWS account.

**AWS RDS** Support 5 different engine type:

- Amazon Aurora
- MySQL
- PostgreSQL
- Oracle
- Microsoft SQL Server

Here is [Db-Engine Ranking](https://db-engines.com/en/ranking) to see which is most popular Db.

Here is [comparison](https://db-engines.com/en/system/Amazon+Aurora%3BMicrosoft+SQL+Server%3BMySQL%3BOracle%3BPostgreSQL) for these 5 engines with very detail info of each engine



This project will be using MySQL and PostgreSQL because it has free trial support.

Amazon Aurora, Oracle and Microsoft SQL Server will be all paid service.



**Free Tier Info**:

The Amazon RDS Free Tier is available to you for 12 months. Each calendar month, the free tier will allow you to use the Amazon RDS resources listed below for free:

- 750 hrs of Amazon RDS in a Single-AZ db.t2.micro Instance.
- 20 GB of General Purpose Storage (SSD).
- 20 GB for automated backup storage and any user-initiated DB Snapshots.

[Learn more about AWS Free Tier.](http://aws.amazon.com/rds/free)

When your free usage expires or if your application use exceeds the free usage tiers, you simply pay standard, pay-as-you-go service rates as described in the [Amazon RDS Pricing page](http://aws.amazon.com/rds/pricing).



In this example project, in application.properties there is accessKey and secretKey available that will have access to your aws account
```
aws.accessKey=AKIATHCJxxxxxxxxx
aws.secretKey=D4MAWMqTqKFw0YMq8Rakxxxxxxxxxxxxxxx
```

To create AWS Account you will require to input your credit card as well. But don't worry there will be no payment spend unless you exceed the free storage that it was given.
https://portal.aws.amazon.com/gp/aws/developer/registration/index.html

So you need to create account and generate access key to use

- Once register and login
- Under your account name at top right menu > Security credentials > Access keys (access key ID and secret access key)
- Create new access key
- Copy access key and secret key into application.properties
- And now you can use AWS service using your aws account



### Project Structure:

**db.properties**: is setting for **AWS RDS DB instance**

**securityGroup.properties**: is setting for **AWS EC2 Security Group** ( security group setting that will be use in db instance later )



**AWS RDS DB instance** and **AWS EC2 Security Group** can be easily created through AWS website.

All of setting value inside **db.properties** and **securityGroup.properties** will be available inside AWS website when creating it.

To understand purpose of each value setting please go create in website manually. 

[Create DB Instance](https://us-east-1.console.aws.amazon.com/rds/home?region=us-east-1#launch-dbinstance:gdb=false;s3-import=false) ( need to login first )

[Create Security Group](https://us-east-1.console.aws.amazon.com/ec2/v2/home?region=us-east-1#CreateSecurityGroup:)  ( need to login first )



The Project will have endpoint to automatically create and delete **DB Instance** according to those settings.



### ⚠️💰⚠️Cautious ⚠️💰⚠️



1. Please be careful on `db.properties `  value **instanceClass**, **multiAZ**, **storageType**, **allocatedStorage**, **engine**. Changing these value without knowing every detail of it may result in **service charges** ($💰$). You can unexpectedly use commercial **storageType** or **instanceClass** without notice and resulted in 2$ ~ 5$ within 24 hour and you accidentally left it unchecked for a whole month which could be over **100$** until you see monthly charge through your bank account. ( Note: Even if your bank account may not have enough cash for current month bill, it could still put credit tab on your account until it is paid. So be careful!! )

2. Please look at **Free Tier Info** to avoid overused of free service and result in over charged your account unexpectedly. So please delete the db instance if you are sure that you no longer needing it, in case overcharge your account.
3. Here is [AWS Bills link](https://us-east-1.console.aws.amazon.com/billing/home?region=us-east-1#/bills) ( need to login first ) to see your current or previous charge for used service. 
4. **Self experience**: I was charge **41.32$** (192.99hrs * 0.215$) in just 192.199hrs ( 8 days ) of `0.215$ per RDS db.r6g.large Single-AZ instance hour (or partial hour) running MySQL`. If I didn't look at aws bill beforehand or wait until it charge from my bank account, it could goes over 100$ easily. And this was only from one of **instanceClass** service charge, if there were other service included or more than one instance is created, it could be way a lot more.

But not to worry, in this project setting all instance are being set to free service type. So unless your account free service is expired or used up, there should be no charged to your bank account.



### Endpoint

- **DB Instance**

  - **Create** DB Instance **mysql**

    - POST /mysql/database

    - `curl -X POST "http://localhost:8080/mysql/database"`

  - **Create** DB Instance **postgresql**
    - POST /postgre/database
    - `curl -X POST "http://localhost:8080/postgre/database"`
  - **Delete** DB Instance **mysql**
    - DELETE /mysql/database
    - `curl -X DELETE"http://localhost:8080/mysql/database"`
  - **Delete** DB Instance **postgre**
    - DELETE /postgre/database
    - `curl -X DELETE"http://localhost:8080/postgre/database"`

  - Show DB Instance **mysql status** ( if available to use or not )
    - GET /mysql/database/status
    - `curl -X GET "http://localhost:8080/mysql/database/status"`

  - Show DB Instance **postgre status** ( if available to use or not )

    - GET /postgre/database/status

    - `curl -X GET "http://localhost:8080/postgre/database/status"`

- **Use DB Instance to execute sql script**

  - Query in mysql db instance ( execute sql query that has return value )
    - POST /mysql/database/query
      - **params: type** [text, html, markdown] (*default*: text)
    - `curl -X POST "http://localhost:8080/mysql/database/query" -H "Content-Type: text/plain" --data-raw "SELECT table_name FROM information_schema.tables where table_schema='SampleDb'"`
    - **type=html** `curl -X POST "http://localhost:8080/mysql/database/query?type=html" -H "Content-Type: text/plain" --data-raw "SELECT table_name FROM information_schema.tables where table_schema='SampleDb'"`
    - **type=markdown** `curl -X POST "http://localhost:8080/mysql/database/query?type=markdown" -H "Content-Type: text/plain" --data-raw "SELECT table_name FROM information_schema.tables where table_schema='SampleDb'"`
  - Query in postgre db instance ( execute sql query that has return value )
    - POST /postgre/database/query
      - **params: type** [text, html, markdown] (*default*: text)
    - `curl -X POST "http://localhost:8080/postgre/database/query" -H "Content-Type: text/plain" --data-raw "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public';"`
    - **type=html** `curl -X POST "http://localhost:8080/postgre/database/query?type=html" -H "Content-Type: text/plain" --data-raw "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public';"`
    - **type=markdown** `curl -X POST "http://localhost:8080/postgre/database/query?type=markdown" -H "Content-Type: text/plain" --data-raw "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public';"`

  - Execute in mysql db instance ( execute sql query that has no return value )
    - POST /mysql/database/execute
    - `curl -X POST "http://localhost:8080/mysql/database/execute" -H "Content-Type: text/plain" --data-raw "CREATE TABLE IF NOT EXISTS table_test (id SERIAL PRIMARY KEY, content VARCHAR(80))"`
    - `curl -X POST "http://localhost:8080/mysql/database/execute" -H "Content-Type: text/plain" --data-raw "INSERT INTO table_test VALUES(1, 'testing data1')"`

  - Execute in postgre db instance ( execute sql query that has no return value )
    - POST /postgre/database/execute
    - `curl -X POST "http://localhost:8080/postgre/database/execute" -H "Content-Type: text/plain" --data-raw "CREATE TABLE IF NOT EXISTS table_test (id SERIAL PRIMARY KEY, content VARCHAR(80))"`
    - `curl -X POST "http://localhost:8080/postgre/database/execute" -H "Content-Type: text/plain" --data-raw "INSERT INTO table_test VALUES(1, 'testing data1')"`



With `database/execute` and `database/query` endpoint, it is possible to perform almost all kind of action to insert, update, delete, select to execute query to database.