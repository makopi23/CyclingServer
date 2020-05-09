package com.example.demo.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.example.demo.mapper.CyclingMapper;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

@Controller
public class CyclingServerController {

    @Autowired
    private CyclingMapper cyclingMapper;

    /**
     * Raspberry Piのピンを有効化する。
     * @param locale
     * @param model
     * @return
     */
    @RequestMapping(value = "/start", method = RequestMethod.GET)
    public String start(Locale locale, Model model) {
        System.out.println("CyclingServerController#start");

        // GPIOの00pinをInputとする。
        final GpioController gpio = GpioFactory.getInstance();
        final GpioPinDigitalInput pin00 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_00, PinPullResistance.PULL_DOWN);

        // エアロバイクを漕いでGPIOの00pinに電流が流れたら、H2DBにデータをINSERTする。
        pin00.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(
                    GpioPinDigitalStateChangeEvent arg0) {

                LocalDateTime date = LocalDateTime.now();
                DateTimeFormatter dtformat =
                        DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS E");
                String fdate = dtformat.format(date);

                cyclingMapper.insert(fdate);
            }
        });
        return "start";
    }

    /**
     * Top画面へ遷移する。
     * @param locale
     * @param model
     * @return
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home(Locale locale, Model model) {
        System.out.println("CyclingServerController#home");
        return "home";
    }

}
