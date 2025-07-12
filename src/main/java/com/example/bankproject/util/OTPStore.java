package com.example.bankproject.util;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class OTPStore {
    private static final Set<Integer> verifiedMembers = ConcurrentHashMap.newKeySet();

    public static void verify(int memberNo) {
        verifiedMembers.add(memberNo);
    }

    public static boolean isVerified(int memberNo) {
        return verifiedMembers.contains(memberNo);
    }

    public static void invalidate(int memberNo) {
        verifiedMembers.remove(memberNo);
    }

    public static void clearAll() {
        verifiedMembers.clear();
    }
}
