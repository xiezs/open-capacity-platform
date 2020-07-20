package com.open.capacity.common.model;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
* @author 作者 gitgeek 
* @version 创建时间：2018-08-06 21:29
* 类说明  应用实体
*/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysClient implements Serializable{
	/**
    *
    */
   private static final long serialVersionUID = -8185413579135897885L;
   @JsonSerialize(using=ToStringSerializer.class)
   private Long id;
   private String clientId;
   private String resourceIds = "";
   private String clientSecret;
   private String clientSecretStr;
   private String scope = "all";
   private String authorizedGrantTypes = "authorization_code,password,refresh_token,client_credentials";
   private String webServerRedirectUri;
   private String authorities = "";
   private Integer accessTokenValidity = 18000;
   private Integer refreshTokenValidity = 18000;
   private String additionalInformation = "{}";
   private String autoapprove = "true";
   private Boolean status ;
   private Integer ifLimit ;
   @JsonSerialize(using=ToStringSerializer.class)
   private Long limitCount=10000L ;
   
}
