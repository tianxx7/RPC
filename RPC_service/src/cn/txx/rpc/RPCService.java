package cn.txx.rpc;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RPCService {
    ThreadPoolExecutor executor =
            new ThreadPoolExecutor(5,30,200, TimeUnit.MILLISECONDS,new ArrayBlockingQueue<Runnable>(15));

    //注册服务 线程安全的集合
    private Map<String,Class<?>> serverRegistry = Collections.synchronizedMap(new HashMap<>());

    //端口
    private int servicePort;

    public RPCService(){

    }

    public RPCService(int port){
        this.servicePort = port;
    }
    //定义一个注册服务接口的方法
    /*
    * @param serviceInterface 暴露的服务接口名称
    * @param serceceImpl 暴露的接口的实现类
    * */
    public void registryService(Class<?> servciceInterface,Class<?> serviceImpl){
        serverRegistry.put(servciceInterface.getName(),serviceImpl);
    }

    //定义发布任务的方法
    public void start() throws IOException{
        //建立网络通信
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress(servicePort));
        System.out.println("rpc服务启动...");
        try {
            while (true){
                executor.execute(new RPCTask(serverSocket.accept()));
            }
        }finally {
            if(serverSocket != null)
                serverSocket.close();
        }

    }

    //停止服务
    public void stop(){
        System.out.println("rpc服务关闭...");
        executor.shutdown();
    }

    /*
    * 处理客户端线程代理的内部类
    * */
    private class RPCTask implements Runnable{

        private final Socket client;

        public RPCTask(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            //通信操作
            //客户端传过来的程序反序列化到程序,再序列化到客户端
            //io流提供的序列化  序列化和反序列化关闭需要不同的try-catch 假设反序列化出现异常,下面的关闭不会执行
            //反序列化句柄
            ObjectInputStream deSerializer = null;
            //定义序列化的句柄
            ObjectOutputStream serializer = null;
            try {
                //创建一个反序列化句柄
                deSerializer = new ObjectInputStream(client.getInputStream());
                //获取接口名称
                String interfaceName = deSerializer.readUTF();
                //获取方法名称
                String methodName = deSerializer.readUTF();
                //获取方法参数列表类型
                Class<?>[] paramterTypes = (Class<?>[])deSerializer.readObject();
                //获取方法参数列表
                Object[] parameters = (Object[]) deSerializer.readObject();
                //通过注册获取对应的服务实现类
                Class<?> serviceInstance = serverRegistry.get(interfaceName);
                //反射构建一个方法对象
                Method method = serviceInstance.getDeclaredMethod(methodName,paramterTypes);
                Object result = method.invoke(serviceInstance.newInstance(), parameters);
                //把服务调用处理结果返回客户端
                serializer = new ObjectOutputStream(client.getOutputStream());
                //把结果序列化到客户端
                serializer.writeObject(result);
            } catch (Exception e) {

            } finally {
                try{
                    if (deSerializer != null) deSerializer.close();
                }catch (Exception e) {
                    e.printStackTrace();
                }
                try{
                    if (serializer != null) {
                        serializer.flush();
                        serializer.close();
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
                try {
                    if (client != null) client.close();
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
