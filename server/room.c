#include<stdio.h>
#include<stdlib.h>
#include<stdbool.h>
#include"user.c"
#include"message.c"

#define MAX_MESSAGES 1000
#define MAX_USERS_IN_ROOM 5000

typedef struct{
    char name[20];
    User users[500];
    Message messages[MAX_MESSAGES];
    int number_of_messages = 0;
}Room;

void add_message(Room room, Message message){//delete old messages(older then 1000 messages)
    Message *p;
    p=room.messages;
    if(room.number_of_messages>=MAX_MESSAGES){
        for (int i = 0; i < MAX_MESSAGES-1; i++ ) {
            *(room.p+i) = *(room.p+i+1);
        }
        *(room.p+MAX_MESSAGES-1) = message;
    }else{
        Message[i] = message;
        room.number_of_messages++;
    }
}

/*Message *getMessages() {
    return messages;
}*/

//add_user

void add_user(Room room, User user){
    bool find_place = false;
    for(int i=0;i<MAX_USERS_IN_ROOM;i++){
        if(room.users[i]==NULL){
            find_place = true;
            room.users[i]=user;
        }
    }
    if(!find_place){
        //send message there is no place in room
    }
}

//get_users