<h4>关于netty学习的记录</h4>
netty官网：https://netty.io/

Netty is an asynchronous event-driven network application framework

---

<h5>用IO实现一个简单的长连接</h5>
1、客户端每2秒向服务端发送一个消息<br>
2、服务端通过阻塞的方法监听连接，并接收客户端发送的消息打印到控制台

思考：<br>
1、上面的demo每个连接都是通过线程去维护的，当连不断的增加的时候，会占用很多的线程资源。比较浪费资源，操作系统耗不起<br>
2、IO编程中，是通过字节流为单位实现数据的读写

<h5>Netty服务端启动</h5>
1、创建了两个NioEventLoopGroup，bossGroup线程组和workGroup线程组。bossGroup主要是绑定接口，并接受新连接。
workGroup主要负责每一条连接数据的读写<br>
2、创建了一个引导类 ServerBootstrap对象，主要是引导我们服务启动启动工作<br>
3、.group(bossGroup, workerGroup)给引导类配置两大线程组，这个引导类的线程模型也就定型了<br>
4、.channel(NioServerSocketChannel.class)来指定 IO 模型<br>
5、childHandler()方法，给这个引导类创建一个ChannelInitializer，这里主要就是定义后续每条连接的数据读写，业务处理逻辑<br>
6、attr() 方法可以给服务端的channel,也就是NioServerSocketChannel指定一些自定义属性，然后我们可以通过channel.attr()取出这个属性。
当然，除了可以给服务端 channel NioServerSocketChannel指定一些自定义属性之外，我们还可以给每一条连接指定自定义属性，用到的是childAttr() 方法<br>
7、childOption()可以给每条连接设置一些TCP底层相关的属性，比如上面，我们设置了两种TCP属性，其中  
        ChannelOption.SO_KEEPALIVE表示是否开启TCP底层心跳机制，true为开启
        ChannelOption.TCP_NODELAY表示是否开启Nagle算法，true表示关闭，false表示开启，通俗地说，如果要求高实时性，有数据发送时就马上发送，就关闭，如果需要减少发送次数减少网络交互,就开启<br>
8、.option(ChannelOption.SO_BACKLOG, 1024) 表示系统用于临时存放已完成三次握手的请求的队列的最大长度，如果连接建立频繁，服务器处理创建新连接较慢，可以适当调大这个参数

<h5>Netty客户端启动</h5>
1、创建一个客户端引导类Bootstrap对象，创建工作线程组<br>
2、指定IO的模型<br>
3、给引导类指定一个 handler，这里主要就是定义连接的业务处理逻辑<br>
4、配置完线程模型、IO 模型、业务处理逻辑之后，调用 connect 方法进行连接，可以看到 connect 方法有两个参数，第一个参数可以填写 IP 或者域名，第二个参数填写的是端口号，由于 connect 方法返回的是一个 Future，也就是说这个方是异步的，我们通过 addListener 方法可以监听到连接是否成功，进而打印出连接信息<br>


<h5>Netty实现客户端和服务端之间通信</h5>
1、channelActive() 方法客户端向服务发送数据时调用；
   channelRead() 方法接收客户端数据时调用；<br>
   逻辑处理链 pipeline<br>
2、写数据的过程介绍：
   首先需要获取一个 netty 对二进制数据的抽象 ByteBuf，我们通过ctx.alloc() 获取到一个 ByteBuf 的内存管理器，这个 内存管理器的作用就是分配一个 ByteBuf，，然后我们把字符串的二进制数据填充到 ByteBuf，这样我们就获取到了 Netty 需要的一个数据格式，
   最后我们调用 ctx.channel().writeAndFlush() 把数据写到服务端<br>
3、与之前的IO套接字Socket不一样，Netty 里面数据是以 ByteBuf 为单位的。我们写数据必须放到ByteBuf里，读取数据也是一样的【ByteBuf是数据传输的载体】


<h5>使用netty开发webSocket简单聊天室</h5>
**实战介绍**<br>
1. 创建一个springBoot项目，在启动springBoot项目时去启动netty服务<br>
2. 构建netty服务，引导类具体实现代码如下：

```
 /** boos线程组 */
 private NioEventLoopGroup boosGroup(){
     return new NioEventLoopGroup();
 }

 /** worker线程组 */
 private NioEventLoopGroup workerGroup(){
    return new NioEventLoopGroup();
 }

 /** 聊天室处理 */
 @Autowired
 private ChatHandler chatHandler;
 
 public ServerBootstrap serverBootstrap(){
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap
                .group(boosGroup(),workerGroup())
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG,1024)
                .childOption(ChannelOption.SO_KEEPALIVE,true)
                .childHandler(new ChannelInitializer<SocketChannel >() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new HttpServerCodec());
                        socketChannel.pipeline().addLast(new ChunkedWriteHandler());
                        socketChannel.pipeline().addLast(new HttpObjectAggregator(64 * 1024));
                        socketChannel.pipeline().addLast(new WebSocketServerProtocolHandler("/webSocket"));
                        socketChannel.pipeline().addLast(chatHandler);
                    }
                });
        return serverBootstrap;
    }
```
关于websocket聊天服务的ChannelHandler介绍
- HttpServerCodec：将字节解码为 HttpRequest 、 HttpContent 和 LastHttp-
Content 。并将 HttpRequest 、 HttpContent 和 Last-
HttpContent 编码为字节
- ChunkedWriteHandler 写入一个文件的内容
- HttpObjectAggregator 将一个 HttpMessage 和跟随它的多个 HttpContent 聚合
为单个 FullHttpRequest 或者 FullHttpResponse （取
决于它是被用来处理请求还是响应）。安装了这个之后，
ChannelPipeline 中的下一个 ChannelHandler 将只会
收到完整的 HTTP 请求或响应
- 按照 WebSocket 规范的要求，处理 WebSocket 升级握手、PingWebSocketFrame 、 PongWebSocketFrame和CloseWebSocketFrame  
- ChatHandler 处理 TextWebSocketFrame 和握手完成事件。 是定义关于聊天处理逻辑

3、关于自定义的ChatHandler的介绍

```
/**
 * 聊天室处理 handler
 * @Author cyfIverson
 */
@Component
@ChannelHandler.Sharable
public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame>{

    private static final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {

        //获取内容
        String chatMessage = msg.text();

        Channel currentChannel = ctx.channel();
        for (Channel channel : channelGroup){
            if (channel == currentChannel){
                channel.writeAndFlush(new TextWebSocketFrame("我自己:"+chatMessage));
            }else{
                channel.writeAndFlush(new TextWebSocketFrame(currentChannel.remoteAddress()+":"+chatMessage));
            }
        }
    }

    //加入
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        for (Channel channel : channelGroup){
            channel.writeAndFlush(new TextWebSocketFrame(ctx.channel().remoteAddress()+":进入聊天室"));
        }
        channelGroup.add(ctx.channel());
    }

    //退出
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        channelGroup.remove(ctx.channel());
        for (Channel channel : channelGroup){
            channel.writeAndFlush(new TextWebSocketFrame(ctx.channel().remoteAddress()+":离开聊天室"));
        }
    }
}

```
- 自定义的ChatHandler继承了Netty包中的SimpleChannelInboundHandler
- 覆盖channelRead0()、handlerAdded()、handlerRemoved()来实现我们的实现逻辑
- channelRead0()方法中实现将最新聊天室的内容广播到建立的每个连接上（浏览器打开页面建立连接）
- handlerAdded()方法实现浏览器每个页面建立一个连接就将该连接的channel放入channelGroup中
- handlerRemoved()方法实现当通道断开时，将该channel从channelGroup中删除

##### 传输
前面已经写了很多实例，但是对于Netty的组件源码熟悉程度还是不够，今天了解了些，并记录下来：
###### 传输 API
传输 API 的核心是 interface Channel，它被用于所有的 I/O 操作。Channel 类的层次如图所示：

![image](https://note.youdao.com/yws/public/resource/96c07656581945051d7fc3c65b69942d/xmlnote/7EA7F72430A5491F9EC04147DE328205/7545)
- 每个 Channel 都将会被分配一个 ChannelPipeline 和 ChannelConfig
- 由于 Channel 是独一无二的，所以为了保证顺序将 Channel 声明为 java.lang.
Comparable 的一个子接口

ChannelPipeline 持有所有将应用于入站和出站数据以及事件的 ChannelHandler 实例，这些 ChannelHandler实现了应用程序用于处理状态变化以及数据处理的逻辑。

ChannelHandler 的典型用途包括：
1. 将数据从一种格式转换为另一种格式；
1. 提供异常的通知；
1. 提供 Channel 变为活动的或者非活动的通知；
1. 提供当 Channel 注册到 EventLoop 或者从 EventLoop 注销时的通知；
1. 提供有关用户自定义事件的通知 


**Channel 的方法** <br>
eventLoop：返回分配给 Channel 的 EventLoop<br>
pipeline：返回分配给 Channel 的 ChannelPipeline<br>
isActive：如果 Channel 是活动的，则返回 true 。活动的意义可能依赖于底层的传输。例如，一个 Socket 传输一旦连接到了远程节点便是活动的，而一个 Datagram 传输一旦
被打开便是活动的<br>
localAddress：返回本地的 SokcetAddress<br>
remoteAddress：返回远程的 SocketAddress<br>
write：将数据写到远程节点。这个数据将被传递给 ChannelPipeline ，并且排队直到它被冲刷<br>
flush：将之前已写的数据冲刷到底层传输，如一个 Socket<br>
writeAndFlush：一个简便的方法，等同于调用 write() 并接着调用 flush()


##### 实现简单的群聊功能[非webSocket]
在使用Netty支持webScoket实现去聊功能前可以先看看该实例


服务实现的逻辑：
- 在客户端连接时将其Channel存入一个ChannelGroup会自动离开中，覆写handlerAdded()方法
- 客户端断开连接时，ChannelGroup会自动删除，向各个客户端广播该客户端离开
- 服务端接收到每一客户端发送来的消息时，服务向所有客户端广播该客户端发送来的消息。[==通过判断区分自己和其他客户端==]
- exceptionCaught()，捕获异常并关闭通道 

服务实现的代码：
**NettyChatServ.Class**
```
/**
 * 实现多客户端与服务通信的实例(群聊)
 * @Author cyfIverson
 * @Date 2018-12-09
 */
public class NettyChatServer {

    public static void main(String[] args) {
        NioEventLoopGroup boosGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap
                .group(boosGroup,workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        //编码解码Handler
                        pipeline.addLast(new DelimiterBasedFrameDecoder(4096, Delimiters.lineDelimiter()));
                        pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
                        pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
                        //服务端处理Handler
                        pipeline.addLast(new ChatHandler());
                    }
                });

        bind(serverBootstrap,8099);
    }

    /**
     * 服务启动绑定端口
     * @param serverBootstrap 服务启动引导类
     * @param bindPost 绑定端口号
     */
    public static void bind(ServerBootstrap serverBootstrap,int bindPost){
        serverBootstrap.bind(bindPost).addListener(future -> {
            if (future.isSuccess()){
                System.out.println("服务端启动成功");
            }else{
                System.out.println("服务端启动失败");
            }
        });
    }
}

```

**ChatHandler.Class**
```
/**
 * 服务逻辑处理Handler
 * @Author cyfIverson
 * @Date 2018-12-09
 */
public class ChatHandler extends SimpleChannelInboundHandler<String> {

    //利用netty的ChannelGroup来存储channel
    private static DefaultChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        Channel channel = ctx.channel();
        //服务广播时，区分当前channel和其他channel的处理
        /*channelGroup.forEach(channel1 -> {
            if (channel != channel1){
                channel1.writeAndFlush(channel.remoteAddress()+"发送消息:"+msg);
            }else {
                channel1.writeAndFlush("[自己]发送信息"+msg);
            }
        });*/
        for (Channel ch : channelGroup){
            if (ch != channel){
                ch.writeAndFlush(channel.remoteAddress()+"发送消息:"+msg+"\n");
            }else {
                ch.writeAndFlush("[自己]发送信息"+msg+"\n");
            }
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channelGroup.writeAndFlush("[服务器]"+channel.remoteAddress()+"加入\n");
        channelGroup.add(channel);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channelGroup.writeAndFlush("[服务器]"+channel.remoteAddress()+"离开\n");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        System.out.println(channel.remoteAddress()+"上线");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        System.out.println(channel.remoteAddress()+"下线");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}

```

客户端的逻辑实现：
- 读取客户端在控制台输入的每行信息发送给你服务端
- 接收服务端发送来的信息打印出来[显示直观效果]


**NettyChatClient.Class**

```
/**
 * 向服务端发送消息客户端
 * @Author cyfIverson
 * @Date 2018-12-09
 */
public class NettyChatClient {

    public static void main(String[] args) throws Exception {
        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap
                .group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) {
                        ChannelPipeline pipeline = ch.pipeline();
                        //编码解码Handler
                        pipeline.addLast(new DelimiterBasedFrameDecoder(4096, Delimiters.lineDelimiter()));
                        pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
                        pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
                        //服务端处理Handler
                        pipeline.addLast(new ChatClientHandler());
                    }
                });

        Channel channel = bootstrap.connect("localhost", 8099).sync().channel();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        for(;;){
            channel.writeAndFlush(bufferedReader.readLine()+"\r\n");
        }
    }
}
```

**ChatClientHandler.Class**
```
/**
 * @Author cyfIverson
 * @Date 2018-12-09
 */
public class ChatClientHandler extends SimpleChannelInboundHandler<String> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println(msg);
    }
}

```























