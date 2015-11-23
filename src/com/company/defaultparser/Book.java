package com.company.defaultparser;

/**
 * Created by bbr on 27.09.15.
 */
public class Book extends Papers {

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getPages() {
        return pages;
    }

    public void setPages(String pages) {
        this.pages = pages;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }


    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }


    @Override
    public String getKey() {
        return key;
    }

    @Override
    public void setKey(String key) {
        this.key = key;
    }

    private String key;
    private String pages; //TODO substring(string.indexOf("-")
    private String series;
    private String volume;
    private String test_;
    private String isbn;
    private String publisher;


    private static String test;

    //test
    public static void main(String[] args) {
        test = test.substring(test.lastIndexOf("-")+1);
        test = "IV-XX, 1-288 qweqwe1-1488pages";
        System.out.println(test);
    }

}
