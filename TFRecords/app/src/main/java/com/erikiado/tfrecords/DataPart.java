package com.erikiado.tfrecords;

/**
 * Created by e on 20/11/17.
 */

public class DataPart {
    private String fileName;
    private byte[] content;
    private String type;

    public DataPart() {
    }

    public DataPart(String name, byte[] data) {
        fileName = name;
        content = data;
    }

    String getFileName() {
        return fileName;
    }

    byte[] getContent() {
        return content;
    }

    String getType() {
        return type;
    }

}
