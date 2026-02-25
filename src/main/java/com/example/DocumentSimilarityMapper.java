package com.example;


import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import java.io.IOException;
import java.util.HashSet;
import java.util.StringTokenizer;

public class DocumentSimilarityMapper extends Mapper<Object, Text, Text, Text> {

    private Text documentId = new Text();
    private Text wordSet = new Text();

    @Override
    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        String[] parts = value.toString().split("s+", 2);
        if (parts.length < 2) return;

        documentId.set(parts[0]);
        HashSet<String> uniqueWords = new HashSet<>();
        StringTokenizer tokenizer = new StringTokenizer(parts[1].toLowerCase().replaceAll("[^a-z ]", " "));
        
        while (tokenizer.hasMoreTokens()) {
            uniqueWords.add(tokenizer.nextToken());
        }

        wordSet.set(String.join(",", uniqueWords));
        context.write(documentId, wordSet);
    }
}
  
