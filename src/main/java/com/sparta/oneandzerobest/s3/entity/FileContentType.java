package com.sparta.oneandzerobest.s3.entity;

public enum FileContentType {
    JPG("image/jpeg"),
    PNG("image/png"),
    JPEG("image/jpeg"),
    MP4("video/mp4"),
    AVI("video/avi"),
    GIF("image/gif");

    private final String type;

    FileContentType(String type){
        this.type = type;
    }

    public String getType(){
        return type;
    }


    public static FileContentType getContentType(String type){
        for(FileContentType index : FileContentType.values()){
            if(index.getType().equals(type)){
                return index;
            }
        }
        return null;
    }


}
