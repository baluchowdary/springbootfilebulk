Migrated from SQL to NoSQL DB - MongoDB

DB UI

<img width="1919" height="598" alt="image" src="https://github.com/user-attachments/assets/987c6e4f-fbae-4c89-ad2b-6bce30c6f579" />


==> Avoided Relational DB features and Switched to NOSQL DB support env by excluding classes and some properties updates 


<img width="1919" height="801" alt="image" src="https://github.com/user-attachments/assets/b3f04d1a-2595-44cc-8384-e1d3edc2c900" />


products o/p

<img width="1748" height="916" alt="image" src="https://github.com/user-attachments/assets/330ac97b-3b5f-45dc-8cf5-5656baf18c02" />


Sequences
<img width="1919" height="713" alt="image" src="https://github.com/user-attachments/assets/4efd7e97-104b-4b4f-82b8-baa206ff1ec5" />


-> We are not able to create below Meta table on Standalone environment local system
Sample batch_job_execution_context Document data below,


{
  "_id": ObjectId("654a1b2c3d4e5f6g7h8i9j0k"),
  "job_execution_id": NumberLong(12345),
  "serialized_context": {
    "batch.taskletType": "org.springframework.batch.core.step.item.ChunkOrientedTasklet",
    "batch.stepType": "org.springframework.batch.core.step.tasklet.TaskletStep",
    "FlatFileItemReader.read.count": 40,
    "FlatFileItemReader.read.count.max": 2147483647,
    "batch.item.count": 40,
    "batch.commit.count": 4
  }
}


-> Sample data for 1000 records

<img width="1827" height="966" alt="image" src="https://github.com/user-attachments/assets/30761f1f-7ce2-4236-b530-f858ab1a6989" />


<img width="1587" height="796" alt="image" src="https://github.com/user-attachments/assets/9c5432cf-543e-4abe-9334-f72f501c7c88" />


<img width="1619" height="761" alt="image" src="https://github.com/user-attachments/assets/80a7852e-d840-42dd-86d2-15a1bc726c8b" />

Manually hitting below Rest call update the Meta Job status from STARTED to STOPPED



Post -> http://localhost:9090/springbootfilebulk/admin/batch/stop/1


<img width="1311" height="482" alt="image" src="https://github.com/user-attachments/assets/74c38c62-7c4a-489f-8160-f96044bf176a" />
