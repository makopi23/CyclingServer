package com.example.demo.controller;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.mapper.CyclingMapper;
import com.example.demo.model.CyclingData;

@CrossOrigin
@RestController
public class CyclingServerRestController {
    @Autowired
    private CyclingMapper cyclingMapper;

    String previous = "";

    /**
     * H2DBのcyclingテーブルから最新のレコードを取得し、HTTPレスポンスとしてリターンする。
     * @param locale
     * @param model
     * @return
     */
    @RequestMapping(value = "/cycling", method = RequestMethod.GET)
    public String cycling(Locale locale, Model model) {

        // H2DBのcyclingテーブルから最新レコードを取得する。
        int id = cyclingMapper.selectMaxId();
        CyclingData cyclingData = cyclingMapper.select(id);
        String message = "id: " + cyclingData.getId() + ", time:" + cyclingData.getTime();

        // 最新レコードが前回レコードと異なれば、標準出力に最新レコードを表示する。（デバッグ用）
        if(!previous.equals(message)) {
            System.out.println(message);
        }
        previous = message;

        return message;
    }

}
