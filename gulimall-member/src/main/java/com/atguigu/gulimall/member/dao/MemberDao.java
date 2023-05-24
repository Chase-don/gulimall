package com.atguigu.gulimall.member.dao;

import com.atguigu.gulimall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author loro
 * @email shuaidong1205s@gmail.com
 * @date 2023-05-24 02:22:57
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
