package com.example;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class DocumentSimilarityReducer extends Reducer<Text, Text, Text, Text> {

    public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        Set<String> doc1Words = new HashSet<>();
        Set<String> doc2Words = new HashSet<>();

        for (Text val : values) {
            String[] words = val.toString().split(",");
            for (String word : words) {
                doc1Words.add(word);
            }
        }

        int intersection = 0;
        for (String word : doc1Words) {
            if (doc2Words.contains(word)) {
                intersection++;
            }
        }

        int union = doc1Words.size() + doc2Words.size() - intersection;
        float jaccardSimilarity = (float) intersection / union;

        context.write(key, new Text("Similarity: " + jaccardSimilarity));
    }
}