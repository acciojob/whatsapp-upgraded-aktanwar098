package com.driver;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WhatsappService {

    WhatsappRepository whatsappRepository=new WhatsappRepository();

    public String createUser(String name, String mobile) throws Exception {
        String res= whatsappRepository.createUser(name,mobile);
        return res;
    }

    public Group createGroup(List<User> users){
        Group group= whatsappRepository.createGroup(users);
        return group;
    }

    public int createMessage(String content){
        return whatsappRepository.createMessage(content);
    }

    public int sendMessage(Message message, User sender, Group group) throws Exception {
        return whatsappRepository.sendMessage(message,sender,group);
    }

    public String changeAdmin(User approver, User user, Group group) throws Exception{
        return whatsappRepository.changeAdmin(approver,user,group);
    }

    public int removeUser(User user) throws Exception{
        return whatsappRepository.removeUser(user);
    }
}