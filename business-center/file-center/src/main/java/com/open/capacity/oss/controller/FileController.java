package com.open.capacity.oss.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.open.capacity.oss.config.WebResourceConfig;
import com.open.capacity.oss.model.MergeFileDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.open.capacity.common.web.PageResult;
import com.open.capacity.common.web.Result;
import com.open.capacity.log.annotation.LogAnnotation;
import com.open.capacity.oss.config.OssServiceFactory;
import com.open.capacity.oss.model.FileInfo;
import com.open.capacity.oss.model.FileType;
import com.open.capacity.oss.service.FileService;

import io.swagger.annotations.Api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 作者 owen 
 * @version 创建时间：2017年11月12日 上午22:57:51
*  文件上传 同步oss db双写 目前仅实现了阿里云,七牛云
*  参考src/main/view/upload.html
*/
@RestController
@Api(tags = "FILE API")
@Slf4j
public class FileController {

	@Autowired
	private OssServiceFactory fileServiceFactory;
	private ObjectMapper objectMapper = new ObjectMapper();
	@Value("${file.oss.path}")
	private String localFilePath;


	/**
	 * 文件上传
	 * 根据fileType选择上传方式
	 * @param file
	 * @return
	 * @throws Exception
	 */
	@LogAnnotation(module = "file-center", recordRequestParam = false)
	@PostMapping("/files-anon")
	public FileInfo upload(@RequestParam("file") MultipartFile file) throws Exception {
		
		String fileType = FileType.QINIU.toString();
		FileService fileService = fileServiceFactory.getFileService(fileType);
		return fileService.upload(file);
	}

	/**
	 * layui富文本文件自定义上传
	 * @param file
	 * @return
	 * @throws Exception
	 */
	@LogAnnotation(module = "file-center", recordRequestParam = false)
	@PostMapping("/files/layui")
	public Map<String, Object> uploadLayui(@RequestParam("file") MultipartFile file )
			throws Exception {
		
		FileInfo fileInfo = upload(file);

		Map<String, Object> map = new HashMap<>();
		map.put("code", 0);
		Map<String, Object> data = new HashMap<>();
		data.put("src", fileInfo.getUrl());
		map.put("data", data);

		return map;
	}

	/**
	 * 文件删除
	 * @param id
	 */
	@LogAnnotation(module = "file-center", recordRequestParam = false)
	@PreAuthorize("hasAuthority('file:del')") 
	@DeleteMapping("/files/{id}")
	public Result delete(@PathVariable String id) {

		try{
			FileInfo fileInfo = fileServiceFactory.getFileService(FileType.QINIU.toString()).getById(id);
			if (fileInfo != null) {
				FileService fileService = fileServiceFactory.getFileService(fileInfo.getSource());
				fileService.delete(fileInfo);
			}
			return Result.succeed("操作成功");
		}catch (Exception ex){
			return Result.failed("操作失败");
		}

	}
 
	/**
	 * 文件查询
	 * @param params
	 * @return
	 * @throws JsonProcessingException 
	 */
	@PreAuthorize("hasAuthority('file:query')")
	@GetMapping("/files")
	public PageResult<FileInfo> findFiles(@RequestParam Map<String, Object> params) throws JsonProcessingException {
        
		return  fileServiceFactory.getFileService(FileType.QINIU.toString()).findList(params);

	}



	/**
	 * 上传大文件
	 * @param file
	 * @param chunks
	 */
	@PostMapping(value = "/files-anon/bigFile")
//	@ResponseStatus(code= HttpStatus.INTERNAL_SERVER_ERROR,reason="server error")
	public Result bigFile( String guid, Integer chunk, MultipartFile file, Integer chunks){
		try {
            fileServiceFactory.getFileService(FileType.LOCAL.toString()).chunk(guid,chunk,file,chunks,localFilePath);
            return Result.succeed("操作成功");
        }catch (Exception ex){
            return Result.failed("操作失败");
        }
	}


	/**
	 * 合并文件
	 * @param mergeFileDTO
	 */
	@RequestMapping(value = "/files-anon/merge",method =RequestMethod.POST )
	public Result mergeFile(@RequestBody MergeFileDTO mergeFileDTO){
		try {
			fileServiceFactory.getFileService(FileType.FASTDFS.toString()).merge(mergeFileDTO.getGuid(),mergeFileDTO.getFileName(),localFilePath);
			return Result.succeed("操作成功");
		}catch (Exception ex){
			return Result.failed("操作失败");
		}
	}


	/**
	 * 上传失败
	 * @param mergeFileDTO
	 * @return
	 */
	@RequestMapping(value = "/files-anon/uploadError",method =RequestMethod.POST )
	public Result uploadError(@RequestBody MergeFileDTO mergeFileDTO){
		try {
			fileServiceFactory.getFileService(FileType.LOCAL.toString()).uploadError(mergeFileDTO.getGuid(),mergeFileDTO.getFileName(),localFilePath);
			return Result.succeed("操作成功");
		}catch (Exception ex){
			return Result.failed("操作失败");
		}
	}



}
