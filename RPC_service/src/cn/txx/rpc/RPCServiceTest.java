package cn.txx.rpc;

import cn.txx.service.IBookService;
import cn.txx.service.impl.BookServiceImpl;

import java.io.IOException;

public class RPCServiceTest {
    public static void main(String[] args) {
        RPCService rpcService = new RPCService(12345);
        //配置暴露的服务接口
        //可以用服务注册组件注册
        rpcService.registryService(IBookService.class, BookServiceImpl.class);
        //发布服务
        try {
            rpcService.start();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            rpcService.stop();
        }
    }
}
