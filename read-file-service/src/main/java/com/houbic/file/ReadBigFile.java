package com.houbic.file;

import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

public class ReadBigFile {

    public static void main(String[] args) throws Exception {
        String fileName = "E:\\test.txt";
//        long value = 1000_000_000_000_00L;
//        BufferedWriter bw  = new BufferedWriter(new FileWriter(fileName));
//        for (int i = 0; i < 200_000_000; i++) {
//            bw.write(String.valueOf(value));
//            bw.newLine();
//            value++;
//        }
//        bw.close();

        long startTime1 = System.currentTimeMillis();
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        Set<String> set = new HashSet<>(200_000_000);
        String line;
        while ((line = br.readLine()) != null) {
            set.add(line);
        }
        br.close();
        System.out.println(String.format("BufferedReader read file cost time : %s", System.currentTimeMillis() - startTime1));

        File file = new File(fileName);
        List<IndexPair> indexPairs = IndexCalculate.getIndex(file, 64);
        System.out.println(String.format("file index size : %s, result: %s", indexPairs.size(), indexPairs));
        long startTime2 = System.currentTimeMillis();
        AtomicInteger count = new AtomicInteger();
        ConcurrentReadFile concurrentReadFile = new ConcurrentReadFile(fileName, indexPairs, new FileHandle() {
            @Override
            public void handle(String value) {
                count.incrementAndGet();
                if (!set.contains(value)) {
                    System.out.println("ConcurrentReadFile read file exception:" + value);
                }
            }
        });
        concurrentReadFile.readFile();
        concurrentReadFile.end();
        System.out.println(String.format("ConcurrentReadFile read file cost time : %s, count:%s", System.currentTimeMillis() - startTime2, count.get()));

    }

}
