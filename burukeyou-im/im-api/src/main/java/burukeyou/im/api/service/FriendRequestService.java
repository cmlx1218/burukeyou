package burukeyou.im.api.service;

import burukeyou.im.api.enity.pojo.ImsFriendRequest;

import java.util.List;

public interface FriendRequestService {

    /**
     *  判断是否能发送好友请求
     * @param accept_user_id 接收好友请求的用户id
     * @return
     */
    Integer isCanSend(String accept_user_id);

    /**
     *   add
     * @param imsFriendRequest
     */
    boolean sendFriendRequest(ImsFriendRequest imsFriendRequest);

    /**
     *    find the accept friend request list of user
     * @param userId
     * @return
     */
    List<ImsFriendRequest> getList(String userId);

    /**
     *
     * @param requestId
     * @param operationType
     */
    boolean updateFriendRequetsState(String requestId, Integer operationType);

    /**
     *
     * @param requestId
     * @param sendUserId
     * @param operationType
     * @return
     */
    boolean passFriendRequest(String requestId, String sendUserId, Integer operationType);
}
