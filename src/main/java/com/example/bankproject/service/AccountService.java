package com.example.bankproject.service;

import com.example.bankproject.dto.AccountCreateRequest;
import com.example.bankproject.dto.AccountHistoryResponseDTO;
import com.example.bankproject.dto.TransferHistoryResponseDTO;
import com.example.bankproject.dto.TransferRequestDTO;
import com.example.bankproject.entity.Account;
import com.example.bankproject.entity.AccountHistory;
import com.example.bankproject.mapper.AccountMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountMapper accountMapper;

    /**
     * 특정 회원이 보유한 계좌 목록 조회
     * @param memberNo 회원 번호
     * @return 계좌 리스트
     */
    public List<Account> getAccountsByMemberNo(int memberNo) {
        return accountMapper.findByMemberNo(memberNo);
    }

    /**
     * 특정 회원의 첫 번째 계좌 조회
     * @param memberNo 회원 번호
     * @return 첫 번째 계좌 (없으면 null)
     */
    public Account getFirstAccountByMemberNo(int memberNo) {
        List<Account> accounts = accountMapper.findByMemberNo(memberNo);
        return accounts.isEmpty() ? null : accounts.get(0);
    }

    /**
     * 계좌 개설 가능 여부 (최대 2개 제한)
     * @param memberId 회원 번호
     * @return true: 개설 가능, false: 개설 불가
     */
    public boolean canOpenAccount(int memberId) {
        int count = accountMapper.countAccountsByMemberId(memberId);
        return count < 2;
    }

    /**
     * 계좌 개설 처리
     * @param dto 계좌 생성 요청 DTO
     * @param memberId 회원 번호
     */
    public void openAccount(AccountCreateRequest dto, Integer memberId) {
        System.out.println("🟢 계좌 생성 요청: " + dto);
        System.out.println(" - job: " + dto.getJob());
        System.out.println(" - purpose: " + dto.getPurpose());
        System.out.println(" - pin: " + dto.getPin());

        accountMapper.insertAccount(dto, memberId);
    }

    /**
     * 계좌 삭제 처리
     * @param accountNumber 계좌 번호
     * @param pin 입력한 PIN번호
     * @param no 사용자 번호 (사용하지 않음)
     * @return true: 삭제 성공, false: 실패
     */
    public boolean deleteAccount(String accountNumber, String pin, Integer no) {
        Account account = accountMapper.findByAccountNumber(accountNumber);
        if (account == null) return false;

        // null 체크
        if (account.getPin() == null || pin == null) return false;

        // PIN 일치 여부 (공백 제거 후 비교)
        if (!String.valueOf(account.getPin()).equals(pin.trim())) return false;

        // 삭제 처리
        accountMapper.deleteByAccountNumber(accountNumber);
        return true;
    }

    /**
     * 계좌 상세 조회 (잔액, 핀 포함)
     * @param accountNumber 계좌 번호
     * @return 계좌 정보
     */
    public Account findByAccountNumber(String accountNumber) {
        return accountMapper.findByAccountNumber(accountNumber);
    }

    /**
     * 거래 내역 조회
     * @param accountNumber 계좌 번호
     * @return 거래 내역 리스트 (DTO 반환)
     */
    public List<AccountHistoryResponseDTO> findTransactionHistory(String accountNumber) {
        Account account = accountMapper.findByAccountNumber(accountNumber);
        if (account == null) return List.of();

        List<AccountHistory> histories = accountMapper.findHistoryByAccountNo(account.getNo());

        // 엔티티 → DTO 변환 (거래 시간은 yyyy-MM-dd HH:mm:ss 포맷으로 변환)
        return histories.stream()
                .map(history -> new AccountHistoryResponseDTO(
                        history.getType(),
                        history.getAmount(),
                        history.getBalanceAfter(),
                        history.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                        history.getMemo()
                ))
                .collect(Collectors.toList());
    }

    /**
     * 이체 처리 (transfer_history 저장 포함)
     * @param dto 이체 요청 DTO
     * @return true: 이체 성공, false: 실패
     */
    public boolean transfer(TransferRequestDTO dto) {
        Account fromAccount = accountMapper.findByAccountNumber(dto.getFromAccountNumber());
        Account toAccount = accountMapper.findByAccountNumber(dto.getToAccountNumber());

        // 계좌 존재 여부 확인
        if (fromAccount == null || toAccount == null) return false;

        // PIN 확인
        if (!fromAccount.getPin().trim().equals(dto.getPin().trim())) return false;

        // 잔액 부족 확인
        if (fromAccount.getBalance() < dto.getAmount()) return false;

        // 금액 계산
        double fromNewBalance = fromAccount.getBalance() - dto.getAmount();
        double toNewBalance = toAccount.getBalance() + dto.getAmount();

        // 잔액 업데이트
        accountMapper.updateBalance(fromAccount.getAccountNumber(), fromNewBalance);
        accountMapper.updateBalance(toAccount.getAccountNumber(), toNewBalance);

        // 거래 내역 저장 (출금)
        accountMapper.insertHistory(fromAccount.getNo(), "出金", -dto.getAmount(), fromNewBalance, "振込");

        // 거래 내역 저장 (입금)
        accountMapper.insertHistory(toAccount.getNo(), "入金", dto.getAmount(), toNewBalance, "振込");

        // 🔥 이체 내역 저장 (transfer_history)
        accountMapper.insertTransferHistory(fromAccount.getNo(), toAccount.getNo(), dto.getAmount());

        return true;
    }

    public List<TransferHistoryResponseDTO> findTransferHistory(String accountNumber) {
        Account account = accountMapper.findByAccountNumber(accountNumber);
        if (account == null) return List.of();

        List<Map<String, Object>> transfers = accountMapper.findTransferHistoryByAccountNumber(accountNumber);

        return transfers.stream()
                .map(row -> new TransferHistoryResponseDTO(
                        row.get("fromAccountNumber") != null ? row.get("fromAccountNumber").toString() : "不明",
                        row.get("toAccountNumber") != null ? row.get("toAccountNumber").toString() : "不明",
                        row.get("amount") != null ? ((BigDecimal) row.get("amount")).doubleValue() : 0.0,
                        row.get("createdAt") != null ? row.get("createdAt").toString() : ""
                ))
                .collect(Collectors.toList());
    }
}
