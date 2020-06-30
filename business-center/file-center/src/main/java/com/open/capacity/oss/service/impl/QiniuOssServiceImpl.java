package com.open.capacity.oss.service.impl;

import com.open.capacity.common.util.UUIDUtils;
import com.open.capacity.oss.dao.FileDao;
import com.open.capacity.oss.model.FileInfo;
import com.open.capacity.oss.model.FileType;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author 作者 owen 
 * @version 创建时间：2017年11月12日 上午22:57:51 
 * 七牛云oss存储文件
 */
@Service("qiniuOssServiceImpl")
@Slf4j
public class QiniuOssServiceImpl extends AbstractFileService implements InitializingBean {

	@Autowired
	private FileDao fileDao;

	@Override
	protected FileDao getFileDao() {
		return fileDao;
	}

	@Override
	protected FileType fileType() {
		return FileType.QINIU;
	}

	@Autowired
	private UploadManager uploadManager;

	@Autowired
	private BucketManager bucketManager;

	@Autowired
	private Auth auth;

	@Value("${qiniu.oss.bucketName:xxxxx}")
	private String bucket;

	@Value("${qiniu.oss.endpoint:xxxxx}")
	private String endpoint;
	
	 
	
	private StringMap putPolicy;

	/**
	 * 获取上传凭证
	 * 
	 * @return
	 */
	private String getUploadToken() {
		return this.auth.uploadToken(bucket, null, 3600, putPolicy);
	}

	@Override
	protected void uploadFile(MultipartFile file, FileInfo fileInfo) throws Exception {
		String fileName = file.getOriginalFilename();
		// 检查文件后缀格式
		String fileEnd = fileName.substring(
				fileName.lastIndexOf(".") + 1)
				.toLowerCase();
		String fileId = UUIDUtils.getGUID32();
		StringBuffer tempFilePath = new StringBuffer();
		tempFilePath.append(fileId).append(".").append(fileEnd);

		try {
			// 调用put方法上传
			uploadManager.put(file.getBytes(),  tempFilePath.toString() , auth.uploadToken(bucket));
			// 打印返回的信息
		} catch (Exception e) {
		}
		fileInfo.setUrl(endpoint+"/"+ tempFilePath);
		fileInfo.setPath(endpoint+"/"+ tempFilePath);
		

	}

	@Override
	protected boolean deleteFile(FileInfo fileInfo) {
		try {
			Response response = bucketManager.delete(this.bucket, fileInfo.getPath());
			int retry = 0;
			while (response.needRetry() && retry++ < 3) {
			    response = bucketManager.delete(bucket, fileInfo.getPath());
			}
		} catch (QiniuException e) {
			return false ;
		}
        return true;

	}

	/**
	 * 上传大文件
	 * 分片上传 每片一个临时文件
	 *
	 * @param guid
	 * @param chunk
	 * @param file
	 * @param chunks
	 * @return
	 */
	@Override
	protected void chunkFile(String guid, Integer chunk, MultipartFile file, Integer chunks,String filePath)throws Exception {

	}

	/**
	 * 合并分片文件
	 * 每一个小片合并一个完整文件
	 *
	 * @param guid
	 * @param fileName
	 * @param filePath
	 * @return
	 */
	@Override
	protected FileInfo mergeFile(String guid, String fileName, String filePath) throws Exception {
		return null;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.putPolicy = new StringMap();
		putPolicy.put("returnBody",
				"{\"key\":\"$(key)\",\"hash\":\"$(etag)\",\"bucket\":\"$(bucket)\",\"width\":$(imageInfo.width), \"height\":${imageInfo.height}}");
	}

	 
}
