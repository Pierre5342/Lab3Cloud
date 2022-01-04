package emse;

import java.util.Scanner;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import software.amazon.awssdk.core.sync.RequestBody;


public class GetDataSendMessage {
	
	
	static String path = "/Users/pierremaindron/Desktop/COURS_2A/Majeure/Cloud/Lab3Cloud/client/";
	static String fileName = "sales-2021-01-02.csv";
	static Path filePath = Paths.get(fileName);
	static String [] tab = {"bucket1", "1"};
	

	public static void main(String[] args) throws Exception {
		readCSVfile(filePath);

		  /* Create a bucket */
		  final String USAGE = "\n" + "Usage:\n" +
		  "    <bucketName> <key>\n\n" + "Where:\n" +
		  "    bucketName - the Amazon S3 bucket to create.\n\n" +
		  "    key - the key to use.\n\n" ;
		  
		  if (tab.length != 2) { 
			  System.out.println(USAGE); 
			  System.exit(1); 
			  }
		  
		  String bucketName = tab[0]; 
		  String key = tab[1];
		  
		  Region region = Region.AP_NORTHEAST_1; 
		  S3Client s3 = S3Client.builder()
				  .region(region) 
				  .build();
		  
		  S3.createBucket(s3, bucketName, region);
		  /* */
		  
		  
		    /* Put the buffer in the bucket */
			PutObjectRequest objectRequest = PutObjectRequest.builder()
	              .bucket(bucketName)
	              .key(key)
	              .build();

			s3.putObject(objectRequest, RequestBody.fromFile(filePath));
			/* */
			
			
			/* Send a message with the name of the bucket and file */
			SqsClient sqsClient = SqsClient.builder()
	                .region(Region.AP_NORTHEAST_1)
	                .build();
			
	        String msg = bucketName;
	        String msg2 = fileName;
	        
	        String queueUrl1 = SQS.getURL("Inbox", sqsClient);
	        String queueUrl2 = SQS.getURL("Outbox", sqsClient);
	        
	        sendMessages(sqsClient, queueUrl1, msg, msg2);
	        System.out.println(msg);
	        System.out.println(msg2);
	        /* */
	        
	        
	        while (true) {
				 /* Retrieve messages */
				List<Message> messages = SQS.receiveMessages(sqsClient, queueUrl2);
				System.out.println(messages);
		        /* */
				if (messages.isEmpty()) {
					/* Delete messages */
					SQS.deleteMessages(sqsClient, queueUrl2,  messages);
					/* */
					
					/* Retrieve file */
					String bucketName1 = messages.get(0).body();
					String fileName = messages.get(1).body();
					
					GetObjectRequest getObjectRequest = GetObjectRequest.builder()
			                .bucket(bucketName1)
			                .key(key)
			                .build();

			        s3.getObject(getObjectRequest);
				}
				
				pause(1);
			}
		 

	}

	
	
	
	
	public static void readCSVfile(Path filePath) throws Exception {
		try {
			Scanner sc = new Scanner(filePath);
			sc.useDelimiter(",");
			while (sc.hasNext()) {
				System.out.print(sc.next());
			}
			sc.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	  
	  
	  public static void sendMessages(SqsClient sqsClient, String queueUrl, String msg, String msg2) {
			
	        System.out.println("\nSend multiple messages");

	        try {
	            SendMessageBatchRequest sendMessageBatchRequest = SendMessageBatchRequest.builder()
	                .queueUrl(queueUrl)
	                .entries(SendMessageBatchRequestEntry.builder().id("id1").messageBody(msg).build(),
	                        SendMessageBatchRequestEntry.builder().id("id2").messageBody(msg2).delaySeconds(10).build())
	                .build();
	            sqsClient.sendMessageBatch(sendMessageBatchRequest);

	        } catch (SqsException e) {
	            System.err.println(e.awsErrorDetails().errorMessage());
	            System.exit(1);
	        }
	    }
	  
	  
	  public static void pause(int tps_en_min) throws InterruptedException {
		  Thread.sleep(1000*tps_en_min);
	  }
	  
	  

	  

}
