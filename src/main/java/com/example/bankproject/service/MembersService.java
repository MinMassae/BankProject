package com.example.bankproject.service;

import com.example.bankproject.entity.Members;
import com.example.bankproject.mapper.MembersMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Profile("api")
@Service
@RequiredArgsConstructor
public class MembersService {

    private final MembersMapper membersMapper;
    private final PasswordEncoder passwordEncoder;

    // 회원가입 처리
    public void register(Members members) {
        members.setPassword(passwordEncoder.encode(members.getPassword()));
        membersMapper.insertMember(members);
    }

    // ID로 회원 조회
    public Members findById(String id) {
        return membersMapper.findByUsername(id);  // 실제 id 기준이라면 findById로 바꾸는 것도 고려
    }

    // ID 중복 확인
    public boolean isIdAvailable(String id) {
        return membersMapper.findByUsername(id) == null;
    }
}
