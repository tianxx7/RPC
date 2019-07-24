package cn.txx.entity;

import java.io.Serializable;

public class BookInfo implements Serializable {
    private Integer bookId;
    private String bookName;

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"bookId\":")
                .append(bookId);
        sb.append(",\"bookName\":\"")
                .append(bookName).append('\"');
        sb.append('}');
        return sb.toString();
    }
}
