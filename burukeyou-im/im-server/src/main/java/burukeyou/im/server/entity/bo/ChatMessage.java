package burukeyou.im.server.entity.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage implements Serializable {

    private static final long serialVersionUID = -9177438820402938294L;

    private String msgId;    // 消息的唯一Id=> 用于消息的签收

    private String type;

    private String sendId;

    private String sendNickname;

    private String sendAvatar;

    private String acceptId;

    private String msg;

}
