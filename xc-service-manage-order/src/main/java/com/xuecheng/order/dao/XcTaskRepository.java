package com.xuecheng.order.dao;

import com.github.pagehelper.Page;
import com.xuecheng.framework.domain.task.XcTask;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;

/**
 * @author 卖猪
 * @version 1.0.0
 * @date 2020/8/20 8:49
 */
public interface XcTaskRepository extends JpaRepository<XcTask,String> {
    //查询某时间之前的n条任务
    Page<XcTask> findByUpdateTimeBefore(Pageable pageable, Date updateTime);
    //更新updateTime
    @Modifying
    @Query("update XcTask t set t.updateTime = :updateTime where t.id = :id")
    public int updateTaskTime(@Param(value = "id")String id,@Param(value = "updateTime")Date updateTime);
    @Modifying
    @Query("update XcTask t set t.version = :version+1 where t.id = :id and t.version = :version")
    public int updateTaskVersion(@Param(value = "id")String id,@Param(value = "version")int version);
}
