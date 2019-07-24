package cn.txx.service;

import cn.txx.entity.BookInfo;

import java.io.Serializable;

public interface IBookService extends Serializable{
    String addBook(BookInfo book);
}
