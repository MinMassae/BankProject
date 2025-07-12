package com.example.bankproject.mapper;

import com.example.bankproject.dto.AccountCreateRequest;
import com.example.bankproject.entity.Account;
import com.example.bankproject.entity.AccountHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface AccountMapper {

    // 🔸 로그인된 회원의 모든 계좌 조회
    List<Account> findByMemberNo(int memberNo);

    // 🔸 로그인된 회원의 계좌 개수 조회 (최대 계좌 개수 제한 시 사용)
    int countAccountsByMemberId(@Param("memberId") int memberId);

    // 🔸 계좌 생성 (회원 번호 포함)
    void insertAccount(@Param("dto") AccountCreateRequest dto, @Param("memberId") int memberId);

    // 🔸 계좌 번호로 계좌 정보 조회 (잔액, 핀번호 포함)
    Account findByAccountNumber(@Param("accountNumber") String accountNumber);

    // 🔸 계좌 삭제
    void deleteByAccountNumber(@Param("accountNumber") String accountNumber);

    // 🔸 계좌 잔액 업데이트
    void updateBalance(@Param("accountNumber") String accountNumber, @Param("balance") double balance);

    // 🔸 특정 계좌의 거래 내역 조회
    List<AccountHistory> findHistoryByAccountNo(@Param("accountNo") int accountNo);

    // 🔸 거래 내역 저장 (입금/출금 이력 기록)
    void insertHistory(@Param("accountNo") int accountNo,
                       @Param("type") String type,             // 入金 / 出金
                       @Param("amount") double amount,         // 거래 금액 (출금 시 음수)
                       @Param("balanceAfter") double balanceAfter, // 거래 후 잔액
                       @Param("memo") String memo);            // 메모 (振込 등)

    // 🔸 이체 내역 저장 (transfer_history)
    void insertTransferHistory(@Param("fromAccountNo") int fromAccountNo,
                               @Param("toAccountNo") int toAccountNo,
                               @Param("amount") double amount);

    List<Map<String, Object>> findTransferHistoryByAccountNumber(@Param("accountNumber") String accountNumber);


}
