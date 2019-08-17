package com.open.capacity.client.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author 作者 owen E-mail: 624191343@qq.com
 * @version 创建时间：2018年4月5日 下午19:52:21 类说明
 * 查询应用绑定的资源权限
 */
@Mapper
public interface SysClientDao {

	 
	@Select("select * from oauth_client_details t where t.client_id = #{clientId}")
	Map getClient(String clientId);
	
	
	@Select("select * from oauth_client_details t where status=1 ")
	List<Map> findAll();

 
}
