package com.houbic.file;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class IndexCalculate {

    public static List<IndexPair> getIndex(File file, int concurrentSize) throws Exception {
        // 获取文件总长度
        long length = file.length();
        // 计算每个分批的大小
        long preLength = length / concurrentSize;
        // 第一批开始的index位置
        long startIndex = 0L;
        // 每个批次结束的index位置， 第一批刚好是批次大小的值
        long endIndex = preLength - 1;

        List<IndexPair> indexPairs = new ArrayList<>((int) (length % concurrentSize));
        RandomAccessFile randomAccessFile = new RandomAccessFile(file,"r");
        // 结束index小于文件大小长度
        while (endIndex < length) {
            // 定位结束index读取内容
            randomAccessFile.seek(endIndex);
            byte tmp =(byte) randomAccessFile.read();
            // 判读是否为\r \n, 两个都不是时增加结束index
            while(tmp!='\n' && tmp!='\r'){
                endIndex++;
                if(endIndex > length - 1){
                    endIndex = length - 1;
                    break;
                }
                // 再次读取
                randomAccessFile.seek(endIndex);
                tmp =(byte) randomAccessFile.read();
            }
            // 结束
            indexPairs.add(IndexPair.build(startIndex, endIndex));
            // 重置开始index
            startIndex = endIndex + 1;
            if (startIndex > length - 1) {
                break;
            }
            endIndex = endIndex + preLength > length - 1 ? length - 1 : endIndex + preLength;
        }
        if (endIndex >= startIndex) {
            indexPairs.add(IndexPair.build(startIndex, endIndex));
        }
        return indexPairs;
    }

}
