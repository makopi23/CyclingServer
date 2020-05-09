package com.example.demo.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.example.demo.model.CyclingData;

@Mapper
public interface CyclingMapper {
    @Select("SELECT id, time FROM cycling WHERE id = #{id}")
    CyclingData select(int id);

    @Select("SELECT max(id) from cycling")
    int selectMaxId();

    @Insert("INSERT INTO cycling(time) values (#{time})")
    void insert(String time);
}
