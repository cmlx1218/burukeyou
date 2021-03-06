package burukeyou.user.service;

import burukeyou.user.entity.pojo.UmsColumn;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

public interface UserColumnService {

    /**
     *      save or update Column info
     * @param umsColumn
     * @return success or fail
     */
    boolean insertOrUpdate(UmsColumn umsColumn);

    /**
     *      delete 删除当前用户专栏  column
     * @param id unique id
     * @param id unique id
     * @return  success or fail
     */
    boolean deleteById(String id);

    /**
     *  get  column ,but not current login user only can get the publish one.
     * @param id  column unique id
     * @return
     */
    UmsColumn getById(String id);

    /**
     *  get column list by userId
     * @param userId
     * @return
     */
    List<UmsColumn> getListByUserId(String userId);

    /**
     *  分页查找
     * @param userIdd
     * @param page
     * @param size
     * @return
     */
    Page<UmsColumn> getPageByUserId(String userIdd, int page, int size);

    /**
     *      查找所有
     * @param userId
     * @return
     */
    List<UmsColumn> getAllByUserId(String userId);
}
