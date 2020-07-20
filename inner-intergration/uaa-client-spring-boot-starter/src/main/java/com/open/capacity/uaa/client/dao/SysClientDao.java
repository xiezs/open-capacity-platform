package com.open.capacity.uaa.client.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author 作者 owen 
 * @version 创建时间：2018年4月5日 下午19:52:21 类说明
 * 查询应用
 * blog: https://blog.51cto.com/13005375 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@Mapper
@SuppressWarnings("all")
public interface SysClientDao {

	 
	
	@Select("select id id , client_id clientId , resource_ids resourceIds ,client_secret clientSecret,client_secret_str clientSecretStr ,web_server_redirect_uri webServerRedirectUri ,authorized_grant_types  authorizedGrantTypes ,if_limit ifLimit , limit_count limitCount  ,status from oauth_client_details t where t.client_id = #{clientId}")
	Map getClient(String clientId);
	
 
}
