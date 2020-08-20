package com.xuecheng.auth;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author 卖猪
 * @version 1.0.0
 * @date 2020/8/18 20:16
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class JwtTest {
    @Test
    public void testVeruty(){
        //公匙
        String publickey = "-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnASXh9oSvLRLxk901HANYM6KcYMzX8vFPnH/To2R+SrUVw1O9rEX6m1+rIaMzrEKPm12qPjVq3HMXDbRdUaJEXsB7NgGrAhepYAdJnYMizdltLdGsbfyjITUCOvzZ/QgM1M4INPMD+Ce859xse06jnOkCUzinZmasxrmgNV3Db1GtpyHIiGVUY0lSO1Frr9m5dpemylaT0BV3UwTQWVW9ljm6yR3dBncOdDENumT5tGbaDVyClV0FEB1XdSKd7VjiDCDbUAUbDTG1fm3K9sx7kO1uMGElbXLgMfboJ963HEJcU01km7BmFntqI5liyKheX+HBUCD4zbYNPw236U+7QIDAQAB-----END PUBLIC KEY-----";
        //jwt令牌
        String jwtString = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJjb21wYW55SWQiOiIxIiwidXNlcnBpYyI6bnVsbCwidXNlcl9uYW1lIjoiaXRjYXN0Iiwic2NvcGUiOlsiYXBwIl0sIm5hbWUiOiJ0ZXN0MDIiLCJ1dHlwZSI6IjEwMTAwMiIsImlkIjoiNDkiLCJleHAiOjE1OTc4NDk1MzcsImF1dGhvcml0aWVzIjpbInhjX3RlYWNobWFuYWdlcl9jb3Vyc2VfYmFzZSIsInhjX3RlYWNobWFuYWdlcl9jb3Vyc2VfZGVsIiwieGNfdGVhY2htYW5hZ2VyX2NvdXJzZV9saXN0IiwieGNfdGVhY2htYW5hZ2VyX2NvdXJzZV9wbGFuIiwieGNfdGVhY2htYW5hZ2VyX2NvdXJzZSIsImNvdXJzZV9maW5kX2xpc3QiLCJ4Y190ZWFjaG1hbmFnZXIiLCJ4Y190ZWFjaG1hbmFnZXJfY291cnNlX21hcmtldCIsInhjX3RlYWNobWFuYWdlcl9jb3Vyc2VfcHVibGlzaCIsInhjX3RlYWNobWFuYWdlcl9jb3Vyc2VfYWRkIl0sImp0aSI6IjMyY2ZkZjU0LTA2ZmUtNGYxMS1hZDQ3LWJkNDJhODZkNTIzZCIsImNsaWVudF9pZCI6IlhjV2ViQXBwIn0.dxFn-_0OUh-9RnKNEzUMflVGur5wqilJ4LfgnfT5EN2n0wd55CjsqStXPpW648B30K28TR_2CAVi7WbDhMdZEeahlpN7cxrqs3Fbn-DU4BQkuxX7JWSedBK8X_EehTGqXsqExZrCoJ81Tf47BtHHPx2NUtUgVpmEE9o2ZjVLtlbHCq9xfeyYq0kL_pYOHdVLyvVhi3uFHmQ8K12erdHstMLKS_1loYZY_pGF17I2c5M2f2ex0mTMrC7PjVES93dh2nAtL6A3wygx4FoePCzHpjL9xt_E01d6ODATdssmpYB5oVA_Zld7MvzJoBzXyAZIRK4WDu3Uy3Yw_sI7pKZ9_A";
        Jwt jwt = JwtHelper.decodeAndVerify(jwtString,new RsaVerifier(publickey));
        String claims = jwt.getClaims();
        System.out.println(claims);
    }
}
