package com.delivery.payment_service.context;

public class UserContext {

    private static final ThreadLocal<Long> userId = new ThreadLocal<>();
    private static final ThreadLocal<String> role = new ThreadLocal<>();
    private static final ThreadLocal<Long> branchId = new ThreadLocal<>();
    private static final ThreadLocal<String> email = new ThreadLocal<>();

    public static void setUserId(Long id) { userId.set(id); }
    public static Long getUserId() { return userId.get(); }

    public static void setRole(String r) { role.set(r); }
    public static String getRole() { return role.get(); }

    public static void setBranchId(Long id) { branchId.set(id); }
    public static Long getBranchId() { return branchId.get(); }

    public static void setEmail(String e) { email.set(e); }
    public static String getEmail() { return email.get(); }

    public static boolean isAdmin() { return "ADMIN".equals(role.get()); }
    public static boolean isBranchWorker() { return "BRANCH_WORKER".equals(role.get()); }
    public static boolean isCourier() { return "COURIER".equals(role.get()); }

    public static void clear() {
        userId.remove();
        role.remove();
        branchId.remove();
        email.remove();
    }
}