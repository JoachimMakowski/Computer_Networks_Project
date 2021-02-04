#include<stdio.h>
#include<stdlib.h>
#include<stdbool.h>
#include"user.c"
#include"message.c"

#define MAX_MESSAGES 100

typedef struct{
    User user1;
    User user2;
    Message messages[MAX_MESSAGES];
    int number_of_messages; //after creating room set number_of_messages = 0
    bool is_active_user1;
    bool is_active_user2;
}Useruserchat;

void add_message_to_chat(Useruserchat useruserchat, Message message){//delete old messages(older then 1000 messages)
    Message *p;
    p=useruserchat.messages;
    if(useruserchat.number_of_messages>=MAX_MESSAGES){
        for (int i = 0; i < MAX_MESSAGES-1; i++ ) {
            *(p+i) = *(p+i+1);
        }
        *(p+MAX_MESSAGES-1) = message;
    }else{
        useruserchat.messages[useruserchat.number_of_messages] = message;
        useruserchat.number_of_messages++;
    }
}

char *get_messages_from_useruserchat(Useruserchat useruserchat) {
    char *messages_to_send;
    for(int i=0;i<useruserchat.number_of_messages;i++){
        messages_to_send = strcat(messages_to_send,useruserchat.messages[i].user.user);
        messages_to_send = strcat(messages_to_send,": ");
        messages_to_send = strcat(messages_to_send,useruserchat.messages[i].content);
        messages_to_send = strcat(messages_to_send,"|");//| is our delimeter
    }
    messages_to_send = strcat(messages_to_send,"\n");
    return messages_to_send;
}