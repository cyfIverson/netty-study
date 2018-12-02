<h4>关于netty学习的记录</h4>
netty官网：https://netty.io/

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
**实战介绍** <br>
1. 创建一个springBoot项目，在启动springBoot项目时去启动netty服务
2. 构建netty服务，实现代码如下：

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
关于websocket聊天服务的ChannelHandler
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













