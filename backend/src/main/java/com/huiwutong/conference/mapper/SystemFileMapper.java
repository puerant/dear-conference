package com.huiwutong.conference.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huiwutong.conference.entity.SystemFile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 系统文件 Mapper
 */
@Mapper
public interface SystemFileMapper extends BaseMapper<SystemFile> {

    /**
     * 根据MD5查询文件
     */
    @Select("SELECT * FROM system_file WHERE md5 = #{md5} AND is_deleted = 0 LIMIT 1")
    SystemFile selectByMd5(@Param("md5") String md5);

    /**
     * 分页查询文件列表
     */
    Page<SystemFile> selectFilePage(Page<SystemFile> page, Wrapper<SystemFile> wrapper);
}
