package web.sock.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created with IDEA
 *
 * @author:hj
 * @Date:2018/12/5
 * @Time:17:55
 */
@Controller
@Slf4j
public class Send {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private int count = 0;

    @GetMapping("/index")
    public String index() {
        return "index";
    }

    //接收浏览器消息路径设置
    @MessageMapping("/send")
    //服务端向浏览器推送地址设置
    @SendTo("/topic/send")
    public SocketMessage send(SocketMessage message) throws Exception {
        message.date = "浏览器消息";
        count++;
        return message;
    }

    //由后台发送到浏览器服务
    // @SendTo("/topic/callback")
    //定时5秒给页面推一次数据
    @Scheduled(cron="0/1 * * * * ?")
    public Object callback() throws Exception {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("推送消息了"+df.format(new Date()));
        //向页面这个地址推送消息
        messagingTemplate.convertAndSend("/topic/callback","客户端消息"+count );
        count++;
        return null;
    }

    public static void main(String[] args) {
        System.out.println(Runtime.getRuntime().availableProcessors());

        ScheduledExecutorService newScheduledThreadPool = Executors
                .newScheduledThreadPool(1);

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                test();
            }
        };
        newScheduledThreadPool.schedule(timerTask, 0, TimeUnit.MILLISECONDS);
        newScheduledThreadPool.shutdown();

    }

    public static void test() {
        System.out.println(123);
    }
}
