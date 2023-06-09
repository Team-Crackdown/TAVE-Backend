package com.tave.api;

import com.tave.exception.BusinessLogicException;
import com.tave.exception.ExceptionCode;
import io.swagger.models.auth.In;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class CoolSmsController {

    protected static Map<String, String> phoneNumAndCertificationNum = new ConcurrentHashMap<>();
    protected static String apiKey;
    protected static String apiSecretKey;

    @Value("${coolsms.api-key}")
    public void setApiKey(String apiKey) {
        CoolSmsController.apiKey = apiKey;
    }

    @Value("${coolsms.api-secret-key}")
    public void setApiSecretKey(String apiSecretKey) {
        CoolSmsController.apiSecretKey = apiSecretKey;
    }


    @PostMapping("/coolSms/get")
    public static String sendSMS(String phoneNumber) {
        Random rand = new Random();
        String numStr = "";
        for (int i = 0; i < 4; i++) {
            String ran = Integer.toString(rand.nextInt(10)); //네자리 숫자 생성, 0으로 시작하여 인증번호가 세자리가 되는 것 방지
            numStr += ran;
        }

        sendOne(phoneNumber, numStr);
        System.out.println("수신자 번호 : " + phoneNumber);
        System.out.println("인증번호 : " + numStr);
        phoneNumAndCertificationNum.put(phoneNumber, numStr);
        new CertificationThread(phoneNumber, numStr).start();
        return numStr;
    }

    public static SingleMessageSentResponse sendOne(String phoneNumber, String numStr) {
        try {
            Message message = new Message();
            // 발신번호 및 수신번호는 01012345678 형태로 입력되어야 합니다.
            message.setFrom("01048401304"); // 발신번호
            message.setTo(phoneNumber); //수신번호
            message.setText("[TAVE] 인증번호는 [" + numStr + "]입니다."); //메세지 양식

            DefaultMessageService messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecretKey, "https://api.coolsms.co.kr");

            SingleMessageSentResponse response = messageService.sendOne(new SingleMessageSendingRequest(message));
            System.out.println(response);
            return response;
        } catch (Exception e) {
            // 예외 처리
            e.printStackTrace();
            // 필요에 따라 로그를 남기거나 예외 처리 메시지를 반환할 수 있습니다.
            throw new RuntimeException("SMS 발송 중 예외가 발생했습니다.");
        }
    }

    @PostMapping("/coolSms/check")
    public static Boolean checkCertification(String phoneNumber, String certificationNumber) {
        try {
            return phoneNumAndCertificationNum.get(phoneNumber).equals(certificationNumber);
        } catch (NullPointerException e) {
            throw new BusinessLogicException(ExceptionCode.CERTIFICATIONNUMBER_IS_NOT_EXIST);
        }
    }


    @AllArgsConstructor
    private static class CertificationThread extends Thread {

        private final Map<String, String> phoneNumAndCertificationNum = CoolSmsController.phoneNumAndCertificationNum;

        private final String phoneNum;

        private final String certificationNum;

        @Override
        public void run() {
            try {
                Thread.sleep(5 * 60 * 1000); //유효시간 5분
                if (phoneNumAndCertificationNum.get(phoneNum) != null &&
                        phoneNumAndCertificationNum.get(phoneNum).equals(certificationNum))
                    phoneNumAndCertificationNum.remove(phoneNum);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

