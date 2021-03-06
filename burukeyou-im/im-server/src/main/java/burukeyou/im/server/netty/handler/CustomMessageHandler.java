package burukeyou.im.server.netty.handler;

import burukeyou.auth.authClient.util.AuthUtils;
import burukeyou.im.server.entity.bo.ChatDataContent;
import burukeyou.im.server.entity.bo.ChatMessage;
import burukeyou.im.server.entity.bo.UserChanelMap;
import burukeyou.im.server.entity.enums.MsgActionEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/***
 *
 *      一般设计是在责任链pipline中每一个handler节点通过判断此传递对象是否是自己需要处理的对象，是就处理否则继续往下传递
 *
 *      原本继承ChannelInboundHandler，需要判断此传递对象（在责任链pipline中传递的对象第一个是Obejct其实就是Buffer）
 *      是否是本handler需要处理的对象，
 *      现在使用SimpleChannelInboundHandler不用,此ChatHandler要处理的对象就是TextWebSocketFrame
 *
 *
 *      SimpleChannelInboundHandler，我们在继承这个类的时候，给他传递一个泛型参数，然后在 channelRead0() 方法里面，
 *      我们不用再通过 if 逻辑来判断当前对象是否是本 handler 可以处理的对象，也不用强转，不用往下传递本 handler 处理不了
 *      的对象，这一切都已经交给父类 SimpleChannelInboundHandler 来实现了，我们只需要专注于我们要处理的业务逻辑即可
 *
 *      区别：
 *          ctx.writeAndFlush()：
 *                  不经过outBoundhandeler，直接写出
 *
 *          ctx.channel().writeAndFlush()：
            从 pipeline 链中的最后一个 outBound 类型的 handler 开始，把对象往前进行传播，会经过每个outBound 类型的 handler
 *
 *
 *
 *
 *
 *
 *
 *
 */
@Slf4j
public class CustomMessageHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Value("${custom.im.port}")
    private int port;

    //保存所有当前连接到服务器的客户端通道（类似在线用户）
    public static ChannelGroup clientGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    // 收到消息触发
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame twsf) throws Exception {
        //1-获取客户端传输过来的消息
        Channel currentChannel = ctx.channel();
        String text = twsf.text();
        ByteBuf content = twsf.content();
        ChatDataContent chatDataContent = null;
        try {
            chatDataContent = objectMapper.readValue(text, ChatDataContent.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 下发消息 todo SOA
        if (chatDataContent.getAction().equals(MsgActionEnum.CONNECT.getAction())){
            //2.1当 websocket第一次open的时候,初始化 channe,把用的 channe]和 userid关联起来
            String senderId = chatDataContent.getChatMessage().getSendId();
            UserChanelMap.put(senderId,currentChannel); //将消息发送者id和chanel绑定
        }else if(chatDataContent.getAction().equals(MsgActionEnum.MSG.getAction())){
            ChatMessage chatMessage = chatDataContent.getChatMessage();
            Channel receiverChannel = UserChanelMap.getChanleById(chatMessage.getAcceptId());  //获得消息接受者的chanel
            if (receiverChannel == null){
                //  channel为空代表用户离线,推送消息(push,个推,小米推送)
            }else {
                Channel channel = clientGroup.find(receiverChannel.id());
                if (channel != null){ //表示用户在线
                    receiverChannel.writeAndFlush(new TextWebSocketFrame(objectMapper
                            .writeValueAsString(ChatDataContent.builder().chatMessage(chatMessage))));
                }else {
                    //  用户离线,推送消息
                }
            }
        }else if (chatDataContent.getAction().equals(MsgActionEnum.HEART.getAction())){
            System.out.println("收到来自channel为[" + currentChannel + "]的心跳包...");
        }

    }


    //


    //客户端连接服务器时触发
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
       /* HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String userId = request.getParameter("userId");
        if (userId == null){
            log.error("当前用户未登陆，拒绝连接!!");
            return;
        }*/

        Channel currentChanle = ctx.channel();
        System.out.println("客户端连接:"+currentChanle.remoteAddress()+"----"
                +currentChanle.id()+"-----"+currentChanle.localAddress());

        System.out.println("当前端口为："+port);
        clientGroup.add( ctx.channel());

        // 将当前用户绑定到该客户端通道
       // UserChanelMap.put(AuthUtils.ID(),currentChanle);

    }

    //客户端关闭连接时触发
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        //当触发 handlerremoved, Channelgroup会自动移除对应客户端的 channe1
        clientGroup.remove(ctx.channel());
    }

    // 发生异常时触发
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.channel().close();
        clientGroup.remove(ctx.channel());
    }
}
