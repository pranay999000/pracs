package com.example.pracs;

public class User {
    int id;
    String Branch;
    int Batch;

    public User(int id, String branch, int batch) {
        this.id = id;
        Branch = branch;
        Batch = batch;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBranch() {
        return Branch;
    }

    public void setBranch(String branch) {
        Branch = branch;
    }

    public int getBatch() {
        return Batch;
    }

    public void setBatch(int batch) {
        Batch = batch;
    }
}
