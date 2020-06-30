package com.open.capacity.oss.dao;

import com.open.capacity.oss.model.FileExtend;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FileExtendDao {

    @Select("select * from file_info_extend t where t.guid = #{guid} order by createTime")
    List<FileExtend> getByGuid(String guid);

    @Insert("insert into file_info_extend(id, guid, name,  size, path, url, source, fileId,createTime) "
            + "values(#{id}, #{guid}, #{name}, #{size}, #{path}, #{url}, #{source}, #{fileId},#{createTime})")
    int save(FileExtend fileExtend);

    @Select("select * from file_info_extend t where t.id = #{id} ")
    FileExtend getById(String id);

    int batchUpdateSelective(List<FileExtend> fileExtends);


}
