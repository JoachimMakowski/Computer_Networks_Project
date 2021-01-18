#include<stdio.h>
#include<stdlib.h>
#include<stdbool.h>
#include"user.c"
#include"message.c"

#define MAX_MESSAGES 1000

typedef struct{
    User user1;
    User user2;
    Message messages[MAX_MESSAGES];
    int number_of_messages = 0;
}Useruserchat;

void add_message(Useruserchat useruserchat, Message message){//delete old messages(older then 1000 messages)
    Message *p;
    p=useruserchat.messages;
    if(useruserchat.number_of_messages>=MAX_MESSAGES){
        for (int i = 0; i < MAX_MESSAGES-1; i++ ) {
            *(useruserchat.p+i) = *(useruserchat.p+i+1);
        }
        *(useruserchat.p+MAX_MESSAGES-1) = message;
    }else{
        Message[i] = message;
        useruserchat.number_of_messages++;
    }
}