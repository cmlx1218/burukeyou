package burukeyou.focus.service.impl;

import burukeyou.auth.authClient.util.AuthUtils;
import burukeyou.focus.entity.pojo.UmsFocus;
import burukeyou.focus.mapper.UmsFocusMapper;
import burukeyou.focus.service.RedisFocusService;
import burukeyou.focus.service.UmsFocusService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UmsFocusServiceImpl extends ServiceImpl<UmsFocusMapper, UmsFocus> implements UmsFocusService {


    @Autowired
    private RedisFocusService redisFocusService;


    // todo 保证一致性
    @Override
    public boolean focus(String targetId, String targetType) {
        redisFocusService.focus(AuthUtils.ID(),targetId,targetType);
        return true;
    }

    @Override
    public boolean cancelFocus(String targetId,String targetType) {
        redisFocusService.cancelFocus(AuthUtils.ID(),targetId,targetType);
        return true;
    }

    @Override
    public Map<String, Boolean> judgeIsFollwerList(String targetType, List<String> targetidList) {
        Map<String, Boolean> result = new HashMap<>();
        targetidList.forEach(e -> {
            int count = super.count(new QueryWrapper<UmsFocus>().lambda()
                    .eq(UmsFocus::getTargetType, targetType)
                    .eq(UmsFocus::getUserId, AuthUtils.ID()).eq(UmsFocus::getTargetId, e));
            result.put(e, count > 0);
        });
        return result;
    }

    @Override
    public Page<String> getUserFocusTargetPage(String userId, String targetType, int page, int size) {
        LambdaQueryWrapper<UmsFocus> eq = new QueryWrapper<UmsFocus>().lambda()
                .select(UmsFocus::getTargetId).eq(UmsFocus::getUserId, userId)
                .eq(UmsFocus::getTargetType, targetType);

        Page<UmsFocus> focusPage = super.page(new Page<>(page,size), eq);
        List<String> targetIdList = focusPage.getRecords().stream().map(e -> e.getTargetId()).collect(Collectors.toList());

        Page<String> targetIdPage = new Page<>(focusPage.getCurrent(), focusPage.getSize(), focusPage.getTotal());
        targetIdPage.setRecords(targetIdList);
        return targetIdPage;
    }


    // [秒] [分] [小时] [日] [月] [周] [年]
   /* @Scheduled(cron = "30 * * * * ?" )
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public void synFocusDataToDB(){
        log.info("执行关注数据同步");
        redisFocusService.focusStatusDataSyncToDB();
        redisFocusService.focusCountDataSyncToDB();
    }*/


}
