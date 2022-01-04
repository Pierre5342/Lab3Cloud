# Lab3Cloud
Emma Crémon / Pierre Maindron

The classes containing the main functions are EC2Worker and ...


The worker is responsible for :
- waiting for a message from the Client and check every one minute for another message in the Inbox
- once the message is received with the name of the file to process, reading the file
- calculating (a) the Total Number of Sales, (b) the Total Amount Sold and (c) the Average Sold per country and per product (class Calculs)
- writing a file in the cloud (the worker needs to create on his laptop a csv file - called "results" here - to store the results and then send it to the S3 bucket)
- sending a message with the name of the incoming file to the Client and the name of the resulting file (with the calculations)
- waiting for another message (after one minute)

For the worker, we created two queues, one for the inbox (to receive client messages) and one for the outbox (to send messages to the client).
Since we wanted to minimize the costs of our resources, we made sure that we created only one Inbox and one Outbox queue even when running multiple EC2 Workers using "QueueNameExistsException" in the createQueue function.


The Client is responsible for :
