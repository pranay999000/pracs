package com.example.pracs;

public class PracticalHolder {
    String imageLink;
    String pageNo;

    public PracticalHolder(String imageLink, String pageNo) {
        this.imageLink = imageLink;
        this.pageNo = pageNo;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getPageNo() {
        return pageNo;
    }

    public void setPageNo(String pageNo) {
        this.pageNo = pageNo;
    }
}
