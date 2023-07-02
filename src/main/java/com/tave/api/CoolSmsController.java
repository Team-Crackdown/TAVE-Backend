package com.tave.api;

import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
public class CoolSmsController {

    @PostMapping("/coolSms/get")
    public static String sendSMS(String phoneNumber) {
        Random rand  = new Random();
        String numStr = "";
        for(int i=0; i<4; i++) {
            String ran = Integer.toString(rand.nextInt(10)); //네자리 숫자 생성, 0으로 시작하여 인증번호가 세자리가 되는 것 방지
            numStr+=ran;
        }

        sendOne(phoneNumber, numStr);
        System.out.println("수신자 번호 : " + phoneNumber);
        System.out.println("인증번호 : " + numStr);
        return numStr;
    }

    public static SingleMessageSentResponse sendOne(String phoneNumber, String numStr) {
        Message message = new Message();
        // 발신번호 및 수신번호는 01012345678 형태로 입력되어야 합니다.
        message.setFrom(phoneNumber); // 발신번호
        message.setTo(phoneNumber); //수신번호
        message.setText("[TAVE] 인증번호는 ["+numStr+"]입니다."); //메세지 양식

        DefaultMessageService messageService = NurigoApp.INSTANCE.initialize("APIKEY", "APISECRETKEY", "https://api.coolsms.co.kr");
        SingleMessageSentResponse response = messageService.sendOne(new SingleMessageSendingRequest(message));
        System.out.println(response);
        return response;
    }

    @PostMapping("/coolsms/check")
    public static String checkCertification(String inputNumber){
        return inputNumber;
    }
}