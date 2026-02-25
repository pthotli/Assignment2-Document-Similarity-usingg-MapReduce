Assignment 2: Document Similarity using MapReduce

Name: Pranathi Thotli

Student ID: 801425061 

Approach and Implementation

Mapper Design
The Mapper reads each line of the input file where the first token is the document ID and the remaining tokens are words. Input key-value pair is LongWritable, Text byte offset and the line. For each word in the line, it emits word, documentID as the output key-value pair. This helps by allowing the shuffle phase to group all document IDs that contain the same word, enabling the Reducer to find shared words between documents.

Reducer Design
The Reducer receives word, docID1, docID2, and so on, a word and all documents containing it. It collects the document IDs into a list and generates all possible pairs (e.g., Doc1-Doc2). For each pair, it tracks shared word count intersection and total unique words (union). Jaccard Similarity is calculated as |A ∩ B| / |A ∪ B| and emits DocA-DocB, Similarity: X.XX as the final output.

Overall Flow
Input files (doc1.txt, doc2.txt, doc3.txt) are stored in HDFS. The Mapper processes each file, tokenizes each line, and emits (word → docID) pairs. The shuffle/sort phase groups all document IDs by word. The Reducer then generates document pairs, computes Jaccard similarity using shared and unique word counts, and writes the final similarity scores to /output/part-r-00000 in HDFS.


Setup and Execution

Environment Setup: Running Hadoop in Docker
Since we are using Docker Compose to run a Hadoop cluster, follow these steps to set up your environment.

Step 1: Install Docker & Docker Compose
Windows: Install Docker Desktop and enable WSL 2 backend.
macOS/Linux: Install Docker using the official guide: Docker Installation

Step 2: Start the Hadoop Cluster
Navigate to the project directory where docker-compose.yml is located and run:
docker-compose up -d
This will start the Hadoop NameNode, DataNode, and ResourceManager services.

Step 3: Access the Hadoop Container
Once the cluster is running, enter the Hadoop master node container:
docker exec -it hadoop-master /bin/bash

Building and Running the MapReduce Job with Maven
Step 1: Build the JAR File
Ensure Maven is installed, then navigate to your project folder and run:
mvn clean package
This will generate a JAR file inside the target directory.

Step 2: Copy the JAR File to the Hadoop Container
Move the compiled JAR into the running Hadoop container:
docker cp target/DocumentSimilarity-0.0.1-SNAPSHOT.jar namenode:/opt/hadoop-3.2.1/share/hadoop/mapreduce/DocumentSimilarity-0.0.1-SNAPSHOT.jar

Uploading Data to HDFS
Step 1: Create an Input Directory in HDFS
Inside the Hadoop container, create the directory where input files will be stored:
hdfs dfs -mkdir -p /input

Step 2: Upload Dataset to HDFS
Copy your local dataset into the Hadoop cluster’s HDFS:
hdfs dfs -put /path/to/local/input/* /input/

Running the MapReduce Job
Run the Hadoop job using the JAR file inside the container:
hadoop jar similarity.jar DocumentSimilarityDriver /input /output_similarity /output_final

Retrieving the Output
To view the results stored in HDFS:
hdfs dfs -cat /output_final/part-r-00000
If you want to download the output to your local machine:
hdfs dfs -get /output_final /path/to/local/output


Challenges and Solutions
Maven Version Incompatibility
Maven 4.x requires Java 17 but the environment runs Java 11. Resolved by manually downloading and installing Apache Maven 3.9.6 which is compatible with Java 11.

Wrong JAR Path
Initially ran hadoop jar similarity.jar which threw JAR does not exist. Resolved by using the full path /opt/hadoop-3.2.1/share/hadoop/mapreduce/DocumentSimilarity-0.0.1-SNAPSHOT.jar

Empty JAR Produced by Maven
Maven built the JAR but warned JAR will be empty - no content was marked for inclusion. Resolved by moving the source files from src/main/com/ to the correct Maven directory structure src/main/java/com/

Input:
Doc1:
hadoop is a distributed system

Doc2:
hadoop is used for big data processing

Doc3:
big data is important for analysis

Output:
Document 3, Document2  similarity: 0.40

Document 3, Document1  similarity: 0.39

Document 2, Document1  similarity: 0.29





