package com.open.capacity.oss.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.open.capacity.oss.dao.FileDao;
import com.open.capacity.oss.model.FileInfo;
import com.open.capacity.oss.model.FileType;
import com.open.capacity.oss.utils.FileUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Objects;

/**
 * 本地存储文件
 * 该实现文件服务只能部署一台 
 * 如多台机器nfs文件存储解决
 * @author pm 1280415703@qq.com
 * @date 2019/8/11 16:22
 */
  
@Service("localOssServiceImpl")
@Slf4j
public class LocalOssServiceImpl extends AbstractFileService {

	@Autowired
	private FileDao fileDao;

	@Override
	protected FileDao getFileDao() {
		return fileDao;
	}

	@Value("${file.oss.prefix:xxxxx}")
	private String urlPrefix;
	/**
	 * 网关访问路径
	 */
	@Value("${file.oss.domain:xxxxx}")
	private String domain;
	
	@Value("${file.oss.path:xxxxx}")
	private String localFilePath;

	@Override
	protected FileType fileType() {
		return FileType.LOCAL;
	}

	@Override
	protected void uploadFile(MultipartFile file, FileInfo fileInfo) throws Exception {
		int index = fileInfo.getName().lastIndexOf(".");
		// 文件扩展名
		String fileSuffix = fileInfo.getName().substring(index);

		String suffix = "/" + LocalDate.now().toString().replace("-", "/") + "/" + fileInfo.getId() + fileSuffix;

		String path = localFilePath + suffix;
		String url = domain + urlPrefix + suffix;
		fileInfo.setPath(path);
		fileInfo.setUrl(url);

		FileUtil.saveFile(file, path);
	}

	@Override
	protected boolean deleteFile(FileInfo fileInfo) {
		return FileUtil.deleteFile(fileInfo.getPath());
	}

	/**
	 * 上传大文件
	 * 分片上传 每片一个临时文件
	 *
	 * @param request
	 * @param guid
	 * @param chunk
	 * @param file
	 * @param chunks
	 * @return
	 */
	@Override
	protected void chunkFile(HttpServletRequest request, String guid, Integer chunk, MultipartFile file, Integer chunks,String filePath)throws Exception {
		log.info("guid:{},chunkNumber:{}",guid,chunk);
		if(Objects.isNull(chunk)){
			chunk = 0;
		}

		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		if (isMultipart) {
			// 临时目录用来存放所有分片文件
			String tempFileDir = filePath + File.separator + guid;
			File parentFileDir = new File(tempFileDir);
			if (!parentFileDir.exists()) {
				parentFileDir.mkdirs();
			}
			// 分片处理时，前台会多次调用上传接口，每次都会上传文件的一部分到后台
			File tempPartFile = new File(parentFileDir, guid + "_" + chunk + ".part");
			FileUtils.copyInputStreamToFile(file.getInputStream(), tempPartFile);
		}

	}


}
