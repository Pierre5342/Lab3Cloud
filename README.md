# Lab3Cloud
Emma Crémon / Pierre Maindron

The classes containing the main functions are EC2Worker and Client

*** The program probably does not work completely. In fact, we both have MacBooks and it was impossible for us to download the CSV file in that format without corrupting it. Therefore, we were not able to really test our program once written. ***


The worker is responsible for :
- waiting for a message from the Client and check every one minute for another message in the Inbox
- once the message is received with the name of the file to process, reading the file
- calculating (a) the Total Number of Sales, (b) the Total Amount Sold and (c) the Average Sold per country and per product (class Calculs)
- writing a file in the cloud (the worker needs to create on his laptop a csv file - called "results" here - to store the results and then send it to the S3 bucket)
- sending a message with the name of the incoming file to the Client and the name of the resulting file (with the calculations)
- waiting for another message (after one minute)

For the worker, we created two queues, one for the inbox (to receive client messages) and one for the outbox (to send messages to the client).
Since we wanted to minimize the costs of our resources, we made sure that we created only one Inbox and one Outbox queue even when running multiple EC2 Workers using "QueueNameExistsException" in the createQueue function.


The Client is responsible for two different parts of the program's action :
1. Before the worker, reading and transmitting the file (found on the local hard-disc):
  - Reading the CSV file (and verifying if the file exists)
  - Uploading it into the cloud (create a bucket to contain the file and upload it in the Amazon S3)
  - Send a message into a queue (Inbox queue), containing the names of the bucket and the file, so that the Worker can know that there is a file ready to be processed

2. After the worker, getting the new file containing the calculs' results and downloading it on the local hard-disc:
  - Checking the Outbox queue's content (once every minute) to know quickly when the Worker has finished calculs.
  - Retrieving and deleting the message sent in the Outbox queue by the worker and get the bucket and file's names from it.
  - Downloading the new file from the Amazon S3 and putting it on the local hard-disc.
