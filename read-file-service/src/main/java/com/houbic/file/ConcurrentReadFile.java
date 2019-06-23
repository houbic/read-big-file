package com.houbic.file;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ConcurrentReadFile {

    private String fileName;

    private List<IndexPair> indexPairs;

    private ExecutorService executorService;

    private FileHandle fileHandle;

    public ConcurrentReadFile(String fileName, List<IndexPair> indexPairs, FileHandle fileHandle) {
        this.fileName = fileName;
        this.indexPairs = indexPairs;
        this.fileHandle = fileHandle;
        executorService = Executors.newFixedThreadPool(indexPairs.size());
    }

    public void readFile() {
        for (IndexPair indexPair : indexPairs) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        File file = new File(fileName);
                        RandomAccessFile accessFile = new RandomAccessFile(file, "r");
                        long size = indexPair.getEndIndex() - indexPair.getStartIndex() + 1;
                        MappedByteBuffer buffer = accessFile.getChannel().map(FileChannel.MapMode.READ_ONLY, indexPair.getStartIndex(), size);
                        StringBuilder builder = new StringBuilder();
                        if (buffer.get(0) == '\n') {

                        }
                        for (int i = buffer.get(0) == '\n' ? 1 : 0; i < size; i++) {
                            byte value = buffer.get(i);
                            if (value == '\n') {//判断遇到换行符，处理此行数据
                                fileHandle.handle(builder.toString());
                                builder.delete(0, builder.length());
                            } else if (i == size - 1) {//判断到了最后一行，处理此行数据
                                builder.append((char) buffer.get(i));
                                fileHandle.handle(builder.toString());
                            } else {//拼接成一行数据
                                builder.append((char) buffer.get(i));
                            }
                        }
                        builder = null;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void end() {
        executorService.shutdown();
        while (!executorService.isTerminated()) {
            try {
                TimeUnit.MILLISECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
