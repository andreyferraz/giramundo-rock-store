package br.com.giramundo.store.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Controller
public class OrderController {

    @Value("${giramundo.whatsapp.number}")
    private String whatsappNumber;

    @PostMapping("/order/whatsapp")
    public String sendToWhatsapp(@RequestParam String customerName,
                                 @RequestParam String customerPhone,
                                 @RequestParam String address,
                                 @RequestParam String itemsSummary) throws UnsupportedEncodingException {

        StringBuilder sb = new StringBuilder();
        sb.append("Pedido de ").append(customerName).append("\n");
        sb.append("Telefone: ").append(customerPhone).append("\n");
        sb.append("Endereço: ").append(address).append("\n\n");
        sb.append("Itens:\n").append(itemsSummary).append("\n\n");
        sb.append("Obrigado pelo pedido!");

        String text = URLEncoder.encode(sb.toString(), "UTF-8");

        String url = "https://api.whatsapp.com/send?phone=" + whatsappNumber + "&text=" + text;

        return "redirect:" + url;
    }
}
