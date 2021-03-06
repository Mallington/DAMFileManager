package com.company.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.lang.reflect.Field;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Job extends FieldHelpers{
              private int jobNo;
              private int assetId;
              private String isbn;
              private String assetType;
              private int height;
              private int width;
              private String title;
              private String customer;
              private String bookType;
              private String coverFinish;
              private int extent;
              private double spineBulk;
              private double headMargin;
              private double backMargin;
              private String status;
              private String colour;

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getJobNo() {
        return jobNo;
    }

    public void setJobNo(int jobNo) {
        this.jobNo = jobNo;
    }

    public int getAssetId() {
        return assetId;
    }

    public void setAssetId(int assetId) {
        this.assetId = assetId;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getBookType() {
        return bookType;
    }

    public void setBookType(String bookType) {
        this.bookType = bookType;
    }

    public String getCoverFinish() {
        return coverFinish;
    }

    public void setCoverFinish(String coverFinish) {
        this.coverFinish = coverFinish;
    }

    public int getExtent() {
        return extent;
    }

    public void setExtent(int extent) {
        this.extent = extent;
    }

    public double getSpineBulk() {
        return spineBulk;
    }

    public void setSpineBulk(double spineBulk) {
        this.spineBulk = spineBulk;
    }

    public double getHeadMargin() {
        return headMargin;
    }

    public void setHeadMargin(double headMargin) {
        this.headMargin = headMargin;
    }

    public double getBackMargin() {
        return backMargin;
    }

    public void setBackMargin(double backMargin) {
        this.backMargin = backMargin;
    }

    @Override
    public Class getLowerClass() {
        return Job.class;
    }

    @Override
    public Object getLowerInstance() {
        return this;
    }

    @Override
    public String getByReference(String reference){
        try {
            return getLowerClass().getDeclaredField(reference).get(getLowerInstance()).toString();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean matchesStatus(Job compare){
        return this.getStatus().equals(compare.getStatus());
    }
}
