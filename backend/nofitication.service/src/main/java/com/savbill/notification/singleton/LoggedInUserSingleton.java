package com.savbill.notification.singleton;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@Slf4j
@Component
public class LoggedInUserSingleton {

    private static  LoggedInUserSingleton INSTANCE = new LoggedInUserSingleton();

    private String userName;
    private Long userId;
    private Long mvnoId;
    private List<String> Teams;

    private LoggedInUserSingleton() { }

    public static LoggedInUserSingleton getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LoggedInUserSingleton();
        }
        return INSTANCE;
    }
    public void setTeams(List<String> teams) {
        this.Teams = teams;
    }
}
