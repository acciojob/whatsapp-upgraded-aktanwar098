package com.driver;

import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class WhatsappRepository {

    private int groupCount=0;

    private int messageCount=0;

    HashMap<String,User> userHashMap=new HashMap<>(); //key as mobile in the hashmap

    HashMap<Group,List<User>> groupHashMap=new HashMap<>(); //group Name as key in the hashmap

    HashMap<Group,List<Message>> messagesInGroup=new HashMap<>();

    List<Message> messageList=new ArrayList<>();

    HashMap<User,List<Message>> userMessageList=new HashMap<>();





    public void createUser(String name,String mobile)throws Exception{

        if(userHashMap.containsKey(mobile)){
            throw new Exception("User already exists");
        }
        User user=new User(name, mobile);
        userHashMap.put(mobile,user);


    }

    public Group createGroup(List<User> users){
        if(users.size()==2){

            Group group=new Group(users.get(1).getName(),2);
            groupHashMap.put(group,users);
            return group;
        }
        Group group=new Group("Group "+ ++groupCount,users.size());
        groupHashMap.put(group,users);
        return group;
    }

    public int createMessage(String content){
        Message message=new Message(++messageCount,content);
        message.setTimestamp(new Date());
        messageList.add(message);
        return messageCount;
    }

    public int sendMessage(Message message,User sender,Group group)throws Exception{
        //Throw "Group does not exist" if the mentioned group does not exist
        //Throw "You are not allowed to send message" if the sender is not a member of the group
        //If the message is sent successfully, return the final number of messages in that group.
        if(!groupHashMap.containsKey(group)){
            throw new Exception("Group does not exist");
        }
        boolean checker=false;

        for(User user:groupHashMap.get(group)){
            if(user.equals(sender)){
                checker=true;
                break;
            }
        }

        if(!checker){
            throw new Exception("You are not allowed to send message");
        }



        //Group List
        if(messagesInGroup.containsKey(group)){
            messagesInGroup.get(group).add(message);
        }
        else {
            List<Message> messages=new ArrayList<>();
            messages.add(message);
            messagesInGroup.put(group,messages);
        }

        //User List
        if(userMessageList.containsKey(sender)){
            userMessageList.get(sender).add(message);
        }
        else {
            List<Message> messages=new ArrayList<>();
            messages.add(message);
            userMessageList.put(sender,messages);
        }

        return messagesInGroup.get(group).size();

    }

    public void changeAdmin(User approver, User user, Group group)throws Exception{
        //Throw "Group does not exist" if the mentioned group does not exist
        //Throw "Approver does not have rights" if the approver is not the current admin of the group
        //Throw "User is not a participant" if the user is not a part of the group
        //Change the admin of the group to "user" and return "SUCCESS". Note that at one time there is only one admin and the admin rights are transferred from approver to user.

        if(!groupHashMap.containsKey(group)){
            throw new Exception("Group does not exist");
        }

        User pastAdmin=groupHashMap.get(group).get(0);
        if(!approver.equals(pastAdmin)){
            throw new Exception("Approver does not have rights");
        }

        boolean check=false;
        for(User user1:groupHashMap.get(group)){
            if(user1.equals(user)){
                check=true;
            }
        }

        if(!check){
            throw new Exception("User is not a participant");
        }

        User newAdmin=null;
        Iterator<User> userIterator=groupHashMap.get(group).iterator();

        while(userIterator.hasNext()){
            User u=userIterator.next();
            if(u.equals(user)){
                newAdmin=u;
                userIterator.remove();
            }
        }

        groupHashMap.get(group).add(0,newAdmin);

    }

    public int removeUser(User user)throws Exception{
        //A user belongs to exactly one group
        //If user is not found in any group, throw "User not found" exception
        //If user is found in a group and it is the admin, throw "Cannot remove admin" exception
        //If user is not the admin, remove the user from the group, remove all its messages from all the databases, and update relevant attributes accordingly.
        //If user is removed successfully, return (the updated number of users in the group + the updated number of messages in group + the updated number of overall messages)


        boolean check=false;
        Group group1=null;
        for(Group group:groupHashMap.keySet()){
            for(User user1:groupHashMap.get(group)){
                if(user1.equals(user)){
                    check=true;
                    group1=group;
                    break;
                }
            }
        }
        if(!check){
            throw new Exception("User not found");
        }

        if(groupHashMap.get(group1).get(0).equals(user)){
            throw new Exception("Cannot remove admin");
        }

        List<Message> userMessages=userMessageList.get(user);

        for(Group group:messagesInGroup.keySet()){
            for(Message message:messagesInGroup.get(group)){
                if(userMessages.contains(message)){
                    messagesInGroup.get(group).remove(message);
                }
            }
        }

        for(Message message:messageList){
            if(userMessages.contains(message)){
                messageList.remove(message);
            }
        }
        groupHashMap.get(group1).remove(user);

        userMessageList.remove(user);

        return groupHashMap.get(group1).size()+messagesInGroup.get(group1).size()+messageList.size();

    }
}