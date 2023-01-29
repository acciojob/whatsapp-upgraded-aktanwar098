package com.driver;

import org.springframework.stereotype.Repository;

import java.util.*;


@Repository
public class WhatsappRepository {
    private Map<Group, List<User>> groupUserMp;
    private Map<Group, List<Message>> groupMessageMp;
    private Map<Message, User> senderMp;
    private Map<Group, User> adminMp;
    private Map<String, User> userMp;
    private HashSet<String> userMobile;
    private int customGroupCount;
    private int messageId;

    public WhatsappRepository(){
        this.groupUserMp = new HashMap<Group, List<User>>();
        this.groupMessageMp = new HashMap<Group, List<Message>>();
        this.senderMp = new HashMap<Message, User>();
        this.adminMp = new HashMap<Group, User>();
        this.userMp = new HashMap<String, User>();
        this.userMobile = new HashSet<>();
        this.customGroupCount = 0;
        this.messageId = 0;
    }

    public String createUser(String name, String mobile) throws Exception
    {
        //If the mobile number exists in database, throw "User already exists" exception
        //Otherwise, create the user and return "SUCCESS"
        if (!userMobile.contains(mobile))
        {
            userMobile.add(mobile);
            User user = new User(name, mobile);
            userMp.put(name, user);
            return "SUCCESS";
        }
        else
        {
            throw new Exception("User already exists");
        }
    }

    public Group createGroup(List<User> users)
    {
        //int count = 0;
        if (users.size()>2)
        {
            customGroupCount++;
            String gName = "Group "+String.valueOf(customGroupCount);
            String adName = users.get(0).getName();
            Group grp = new Group(gName, users.size());
            groupUserMp.put(grp, users);
            adminMp.put(grp, users.get(0));
            return grp;
        }
        else if (users.size()==2)
        {
            String gName = users.get(1).getName();
            String adName = users.get(0).getName();
            Group grp = new Group(gName, users.size());
            groupUserMp.put(grp, users);
            adminMp.put(grp, users.get(0));
            return grp;
        }
        return null;         //if users.size()<2
    }

    public int createMessage(String content)
    {
        messageId++;
        Message message = new Message(messageId, content);
        return  messageId;
    }

    public int sendMessage(Message message, User sender, Group group) throws Exception
    {
        if (groupUserMp.containsKey(group))
        {
            List<User> currUsers = groupUserMp.get(group);

            if (currUsers.contains(sender))
            {
                senderMp.put(message, sender);
                if (groupMessageMp.containsKey(group))
                {
                    List<Message> msgList = groupMessageMp.get(group);
                    msgList.add(message);
                    groupMessageMp.put(group, msgList);
                    return msgList.size();
                }
                else
                {
                    List<Message> msgList = new ArrayList<>();
                    msgList.add(message);
                    groupMessageMp.put(group, msgList);
                    return msgList.size();
                }
            }
            else
            {
                throw new Exception("You are not allowed to send message");
            }
        }
        else
        {
            throw new Exception("Group does not exist");
        }
    }

    // 5)
    public String changeAdmin(User approver, User user, Group group) throws Exception
    {
        if (adminMp.containsKey(group))
        {
            User currAdmin = adminMp.get(group);
            if (approver.equals(currAdmin))
            {
                List<User> currUsers = groupUserMp.get(group);
                if (currUsers.contains(user))
                {
                    adminMp.put(group, user);
                    return "SUCCESS";
                }
                else
                {
                    throw new Exception("User is not a participant");
                }
            }
            else
            {
                throw new Exception("Approver does not have rights");
            }
        }
        else
        {
            throw new Exception("Group does not exist");
        }
    }
    public int removeUserfromDb(User user)throws Exception{
        Group checkGroup = null;
        boolean userAvailability = false;
        for(Group group:groupUserMp.keySet()){
            List<User> userList = groupUserMp.get(group);
            for(User user1:userList){
                if(user1 == user){
                    checkGroup = group;
                    userAvailability = true;
                }
            }
        }
        if(!userAvailability){
            throw new Exception("User not found");
        }
        boolean adminCheck = false;

        for(Group group : adminMp.keySet()){
            if(adminMp.get(group) == user){
                adminCheck = true;
            }
        }

        if(adminCheck){
            throw new Exception("Admin Cannot be removed");
        }

        List<User> users = groupUserMp.get(checkGroup);

        for(User user1:users){

            if(user1 == user){
                users.remove(user1);
            }
        }
        Set<String> username = userMp.keySet();
        for(String s:username){

            if(userMp.get(s)==user){
                userMp.remove(s);
            }
        }
        List<Message> messages = groupMessageMp.get(checkGroup);
        for(Message message:messages){

            if(senderMp.get(message)==user){
                senderMp.remove(message);
                messages.remove(message);
            }
        }
        List<Message> messages1 = groupMessageMp.get(checkGroup);

        List<User> users1 = groupUserMp.get(checkGroup);

        int result = messages1.size()+ users1.size()+messageId;


        return result;

    }


}