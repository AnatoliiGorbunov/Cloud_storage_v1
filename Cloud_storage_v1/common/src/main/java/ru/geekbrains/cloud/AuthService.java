package ru.geekbrains.cloud;

import lombok.Data;
import ru.geekbrains.cloud.messagemodel.AuthMessage;

import java.util.ArrayList;
import java.util.List;

@Data
public class AuthService {
    private static List<AuthMessage> list = new ArrayList<>();

    private static void createList() {
        list.add(new AuthMessage("Client1", "1"));
        list.add(new AuthMessage("Client2", "2"));
        list.add(new AuthMessage("Client3", "3"));
    }

    public static boolean isAuth(AuthMessage msg) {
        createList();
        for (AuthMessage authMessage : list) {
            if (authMessage.getLogin().equals(msg.getLogin()) && authMessage.getPassword().equals(msg.getPassword())) {
                return true;
            }
        }
        return false;
    }
}
