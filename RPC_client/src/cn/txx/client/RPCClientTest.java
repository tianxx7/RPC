package cn.txx.client;

import cn.txx.entity.BookInfo;
import cn.txx.service.IBookService;
import cn.txx.service.impl.BookServiceImpl;

import java.net.InetSocketAddress;

public class RPCClientTest {
    public static void main(String[] args) {
        RPCClient client = new RPCClient();
        IBookService proxy = client.getRemoteProxy(IBookService.class, new InetSocketAddress("localhost", 12345));
        BookInfo bookInfo = new BookInfo();
        bookInfo.setBookId(1001);
        bookInfo.setBookName("五套");
        String result = proxy.addBook(bookInfo);
        System.out.println(result);
    }
}
