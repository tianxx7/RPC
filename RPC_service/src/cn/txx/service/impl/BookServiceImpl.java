package cn.txx.service.impl;

import cn.txx.entity.BookInfo;
import cn.txx.service.IBookService;

public class BookServiceImpl implements IBookService {
    @Override
    public String addBook(BookInfo book) {
        System.out.println(book.toString());
        return "添加图书"+book.getBookName()+"成功!";
    }
}
