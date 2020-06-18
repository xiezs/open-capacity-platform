package com.open.capacity.oss.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.web.multipart.MultipartFile;

import com.open.capacity.oss.model.FileInfo;

import lombok.extern.slf4j.Slf4j;

/**
 * @author 作者 owen 
 * @version 创建时间：2017年11月12日 上午22:57:51
 * 文件工具类
*/
@Slf4j
public class FileUtil {

	public static FileInfo getFileInfo(MultipartFile file) throws Exception {
		String md5 = fileMd5(file.getInputStream());

		FileInfo fileInfo = new FileInfo();
		fileInfo.setId(md5);// 将文件的md5设置为文件表的id
		fileInfo.setName(file.getOriginalFilename());
		fileInfo.setContentType(file.getContentType());
		fileInfo.setIsImg(fileInfo.getContentType().startsWith("image/"));
		fileInfo.setSize(file.getSize());
		fileInfo.setCreateTime(new Date());

		return fileInfo;
	}

	/**
	 * 文件的md5
	 * 
	 * @param inputStream
	 * @return
	 */
	public static String fileMd5(InputStream inputStream) {
		try {
			return DigestUtils.md5Hex(inputStream);
		} catch (IOException e) {
			log.error("FileUtil->fileMd5:{}" ,e.getMessage());
		}

		return null;
	}

	public static String saveFile(MultipartFile file, String path) {
		try {
			File targetFile = new File(path);
			if (targetFile.exists()) {
				return path;
			}

			if (!targetFile.getParentFile().exists()) {
				targetFile.getParentFile().mkdirs();
			}
			file.transferTo(targetFile);

			return path;
		} catch (Exception e) {
			log.error("FileUtil->saveFile:{}" ,e.getMessage());
		}

		return null;
	}

	public static String saveBigFile(String guid ,File parentFileDir, File destTempFile) {
		try {
			if(parentFileDir.isDirectory()){
				if(!destTempFile.exists()){
					//先得到文件的上级目录，并创建上级目录，在创建文件,
					destTempFile.getParentFile().mkdir();
					try {
						//创建文件
						destTempFile.createNewFile(); //上级目录没有创建，这里会报错
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				log.info("length:{} ",parentFileDir.listFiles().length);

				for (int i = 0; i < parentFileDir.listFiles().length; i++) {
					File partFile = new File(parentFileDir, guid + "_" + i + ".part");
					FileOutputStream destTempfos = new FileOutputStream(destTempFile, true);
					//遍历"所有分片文件"到"最终文件"中
					FileUtils.copyFile(partFile, destTempfos);
					destTempfos.close();
				}
			}
		} catch (Exception e) {
			log.error("FileUtil->saveBigFile:{}" ,e.getMessage());
		}

		return null;
	}


	public static boolean deleteFile(String pathname) {
		File file = new File(pathname);
		if (file.exists()) {
			boolean flag = file.delete();

			if (flag) {
				File[] files = file.getParentFile().listFiles();
				if (files == null || files.length == 0) {
					file.getParentFile().delete();
				}
			}

			return flag;
		}

		return false;
	}
}
