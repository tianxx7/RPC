package cn.txx.client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.Socket;

public class RPCClient {
    //
    public <T> T getRemoteProxy(Class<?> interfaceClass, InetSocketAddress address) {

        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]{interfaceClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                //定义客户端socket句柄
                Socket socket = null;
                //定义序列化句柄
                ObjectOutputStream serializer = null;
                //定义反序列化句柄
                ObjectInputStream deSerializer = null;
                try {
                    //创建客户端到服务端的链接
                    socket = new Socket();
                    socket.connect(address);
                    //创建序列化对象
                    serializer = new ObjectOutputStream(socket.getOutputStream());
                    //序列化接口全名
                    serializer.writeUTF(interfaceClass.getName());
                    //序列化方法名称
                    serializer.writeUTF(method.getName());
                    //序列化方法类型列表
                    serializer.writeObject(method.getParameterTypes());
                    //序列化方法参数
                    serializer.writeObject(args);
                    //创建反序列化对象
                    deSerializer = new ObjectInputStream(socket.getInputStream());
                    //获取到反序列化的结果
                    return deSerializer.readObject();
                } catch (Exception e) {

                } finally {
                    try {
                        if (deSerializer != null) deSerializer.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        if (serializer != null) {
                            serializer.flush();
                            serializer.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        if (socket != null) socket.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        });
    }
}
