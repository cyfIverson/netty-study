# netty study
<h4>关于netty学习的记录</h4>

用IO实现一个简单的长连接<br>
1、客户端每2秒向服务端发送一个消息<br>
2、服务端通过阻塞的方法监听连接，并接收客户端发送的消息打印到控制台

思考：
1、上面的demo每个连接都是通过线程去维护的，当连不断的增加的时候，会占用很多的线程资源。比较浪费资源，操作系统耗不起
2、IO编程中，是通过字节流为单位实现数据的读写

<h5>Netty服务端启动</h5>
1、创建了两个NioEventLoopGroup，bossGroup线程组和workGroup线程组。bossGroup主要是绑定接口，并接受新连接。
workGroup主要负责每一条连接数据的读写<br>
2、创建了一个引导类 ServerBootstrap，主要是引导我们服务启动启动工作<br>
3、.group(bossGroup, workerGroup)给引导类配置两大线程组，这个引导类的线程模型也就定型了<br>
4、.channel(NioServerSocketChannel.class)来指定 IO 模型<br>
5、childHandler()方法，给这个引导类创建一个ChannelInitializer，这里主要就是定义后续每条连接的数据读写，业务处理逻辑

