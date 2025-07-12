package com.example.bankproject.mapper;

import com.example.bankproject.entity.Members;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MembersMapper {
    void insertMember(Members members);
    Members findByUsername(String username);
}
