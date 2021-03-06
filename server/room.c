#include<stdio.h>
#include<stdlib.h>
#include<stdbool.h>
#include"user.c"
#include"message.c"

#define MAX_MESSAGES 100
#define MAX_USERS_IN_ROOM 50

typedef struct{
    char name[20];
    User users[MAX_USERS_IN_ROOM];
    bool actual_users[MAX_USERS_IN_ROOM];//used to sending messages each bool variable actual user corresponds to User variable
    Message messages[MAX_MESSAGES];
    int number_of_messages;//after creating room set number_of_messages = 0
    int number_of_users;
}Room;

void add_message_to_room(Room room, Message message){//delete old messages(older then 1000 messages)
    Message *p;
    p=room.messages;
    if(room.number_of_messages>=MAX_MESSAGES){
        for (int i = 0; i < MAX_MESSAGES-1; i++ ) {
            *(p+i) = *(p+i+1);
        }
        *(p+MAX_MESSAGES-1) = message;
    }else{
        room.messages[room.number_of_messages] = message;
        room.number_of_messages++;
    }
}

char *get_messages_from_room(Room room) {
    char *messages_to_send;
    for(int i=0;i<room.number_of_messages;i++){
        messages_to_send = strcat(messages_to_send,room.messages[i].user.user);
        messages_to_send = strcat(messages_to_send,": ");
        messages_to_send = strcat(messages_to_send,room.messages[i].content);
        messages_to_send = strcat(messages_to_send,"|");//| is our delimeter
    }
    messages_to_send = strcat(messages_to_send,"\n");
    return messages_to_send;
}

//add_user

void add_user_to_room(Room room, User user){
    bool find_place = false;
    for(int i=0;i<MAX_USERS_IN_ROOM;i++){
        if(!strcmp(user.user,room.users[i].user)){
            //user that is actually in room is trying to join
        }
        if(i>=room.number_of_users){
            find_place = true;
            room.users[i]=user;
            room.number_of_users++;
            break;
        }
    }
    if(!find_place){
        //send message there is no place in room
    }
}

bool is_user_in_group(Room room, User user){
    for(int i=0;i<room.number_of_users;i++){
        if(!strcmp(user.user,room.users[i].user)) return true;
    }
    return false;
}

//get_users