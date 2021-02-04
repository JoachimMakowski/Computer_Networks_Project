#include <sys/types.h>
#include <sys/wait.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <netdb.h>
#include <signal.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <pthread.h>
#include <stdbool.h>
#include "room.c"
#include "user.c"
#include "useruserchat.c"

#define QUEUE_SIZE 10//how many users in the same time
#define MAX_ROOMS 10//how many rooms
#define MAX_USERS 20//max value of users

//uchwyt na mutex
pthread_mutex_t exit_mutex = PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t start_mutex = PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t add_user_mutex = PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t add_room_mutex = PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t add_user_to_room_mutex = PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t sending_message_to_room_mutex = PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t sending_message_to_chat_mutex = PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t users_logged_mutex = PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t user_change_room_mutex = PTHREAD_MUTEX_INITIALIZER;

int desc_table[QUEUE_SIZE];
int clients = 0; //how many clients is actually logged
int number_of_users = 0;
int number_of_rooms = 0;
int number_of_chats = 0;

//struktura zawierająca dane, które zostaną przekazane do wątku
struct thread_data_t
{
    int deskryptor;
};

Room rooms[MAX_ROOMS];
User users[MAX_USERS];
Useruserchat useruserchats[MAX_USERS*(MAX_USERS-1)];


//funkcja opisującą zachowanie wątku - musi przyjmować argument typu (void *) i zwracać (void *) check
void *ThreadBehavior(void *t_data)
{
    pthread_detach(pthread_self());
    //dostęp do pól struktury: (*th_data).pole
    struct thread_data_t *th_data = (struct thread_data_t*)t_data;
    
    while(1){
        char buffor[300];
        char return_value[1500];//it cant be a pointer
        int vread = read(th_data -> deskryptor, buffor,300);
        if (vread > 0){//trzeba zaimplementować if-else i odpowiednie rodzaje wiadomości odpowiednio obsługiwać
        /* 0 - log
            00 - OK
            01 - already logged
            02 - server is full
            03 - not place for user
        1 - logout
        2 - join room
            20 - sending messages
            21 - no such room
        3 - create new room
            30 - new room created
            31 - room already exists
            32 - no space for new room
        4 - message room
            40 - message send succesfully
            41 - 
        5 - message face2face
        6 - change room
        7 - actual logged users
            70\n%s\n - actually logged users %s = list of users separated by "|"
        potrzebuję jeszcze leave room
        */
            const char s[2] = "\n";
            char *token;
            char *token2;
            char *token3;
            char *token4;
            char *token5;
            
            /* get the first token */
            
            
            /* walk through other tokens */
            /*while( token != NULL ) {
                printf( " %s\n", token );
                
                token = strtok(NULL, s);
            }*/
            buffor[vread] = '\0';

            token = strtok(buffor, s);//get token
            //printf("%s\n%s\n",buffor,token);
            if(*token == '0'){  //0 - log
                bool find_user = false;
                token = strtok(NULL, s); //username
                printf("%s\n",token);
                pthread_mutex_lock(&add_user_mutex);
                for(int i=0;i<MAX_USERS;i++){
                    if(!strcmp(users[i].user,token)){
                        find_user = true;
                        if(users[i].logged == false) {
                            pthread_mutex_lock(&users_logged_mutex);
                            users[i].logged = true;
                            pthread_mutex_unlock(&users_logged_mutex);
                            users[i].descriptor = th_data->deskryptor;
                            //add info about rooms and users online
                            strcpy(return_value,"00\n");
                            printf("IM SENDING!!!\n");
                            write(th_data->deskryptor,return_value,sizeof(return_value));
                            break;
                        }
                        else{
                            //return info that user is already logged
                            strcpy(return_value,"01\n");
                            printf("IM SENDING!!!\n");
                            write(th_data->deskryptor,return_value,sizeof(return_value));
                            break;
                        }
                    }
                    else if(i>=number_of_users){
                        
                        find_user = true;
                        //set new user and add chat with everyone else
                        strcpy(users[i].user,token);
                        pthread_mutex_lock(&users_logged_mutex);
                        users[i].logged=true;
                        pthread_mutex_unlock(&users_logged_mutex);
                        users[i].descriptor = th_data->deskryptor;
                        for (int j = 0;j<number_of_users;j++){
                            useruserchats[number_of_chats].user1 = users[j];
                            useruserchats[number_of_chats].user1 = users[i];
                            useruserchats[number_of_chats].number_of_messages=0;
                            useruserchats[number_of_chats].is_active_user1 = false;
                            useruserchats[number_of_chats].is_active_user2 = false;
                            number_of_chats++;
                        }
                        printf("%s\n",token);
                        //add info about users online and room
                        strcpy(return_value,"00\n");
                        printf("IM SENDING!!!\n");
                        write(users[i].descriptor,return_value,sizeof(return_value));
                        number_of_users++;
                        break;
                    }
                }
                pthread_mutex_unlock(&add_user_mutex);
                //not place avalaible for new user
                if(!find_user){
                    strcpy(return_value,"03\n");
                    printf("IM SENDING!!!\n");
                    write(th_data->deskryptor,return_value,sizeof(return_value));
                }
            }
            else if(*token == '1'){//1 - logout
                token = strtok(NULL, s); //username
                bool find_user = false;
                for(int i=0;i<MAX_USERS;i++){
                    if(!strcmp(users[i].user,token)){
                        find_user = true;
                        if(users[i].logged == true){
                            pthread_mutex_lock(&users_logged_mutex);
                            users[i].logged = false;
                            pthread_mutex_unlock(&users_logged_mutex);
                            break;
                        }
                        else{
                            //return exception - user is not logged
                            printf("User is already offline\n");
                            break;
                        }
                    }
                }
                if(!find_user){
                    //there is no such user
                    printf("There is no such user\n");
                }
                close(th_data->deskryptor);
            }
            else if(*token == '2'){//2 - join room
                token = strtok(NULL, s); //room name
                token2 = strtok(NULL, s); //username
                bool find_room = false;
                bool find_user = false;
                char *messages_to_send;
                for(int i = 0;i<MAX_ROOMS;i++)
                {
                    if(!strcmp(token,rooms[i].name)){
                        find_room = true;
                        for(int j = 0;j<MAX_USERS;j++){
                            if(!strcmp(users[j].user,token2)){
                                messages_to_send = strcat(messages_to_send,"20\n");
                                find_user = true;
                                //add new user to room
                                pthread_mutex_lock(&add_user_to_room_mutex);
                                add_user_to_room(rooms[i],users[j]);
                                pthread_mutex_unlock(&add_user_to_room_mutex);
                                //send all messages to user
                                messages_to_send = getMessages(rooms[i]);
                                printf("%s",messages_to_send);
                                write(th_data->deskryptor,messages_to_send,sizeof(messages_to_send));
                                break;
                            }
                        }
                        break;
                    }
                }
                if(!find_room){
                    //there is no such room name
                    strcpy(return_value,"21\n");
                    printf("there is no such room %s\n", token);
                    write(th_data->deskryptor,return_value,sizeof(return_value));
                }
                else if(!find_user){
                    //room is ok, no such username
                    printf("NO SUCH USER - ROOM IS OK\n");
                }
                //if the room is not found then the user is not recognized
            }
            else if(*token == '3'){//3 - create new room
                token = strtok(NULL, s); //room name
                bool find_free_room = false;
                pthread_mutex_lock(&add_room_mutex);
                for(int i = 0; i<MAX_ROOMS;i++){
                    if(!strcmp(token,rooms[i].name)){
                        //room already exists
                        find_free_room = true;
                        strcpy(return_value,"31\n");
                        printf("Room %s already exists\n", token);
                        write(th_data->deskryptor,return_value,sizeof(return_value));
                        break;
                    }
                    else if(i>=number_of_rooms){
                        strcpy(rooms[i].name,token);
                        rooms[i].number_of_users = 0;
                        rooms[i].number_of_messages = 0;
                        printf("Im creating new room: %s\n",rooms[i].name);
                        pthread_mutex_lock(&add_user_to_room_mutex);
                        //i have to find user by socket descriptor
                        for(int j=0;j<MAX_USERS;j++){
                            if(th_data->deskryptor==users[j].descriptor){
                                printf("Im adding new user to new room: %s\n",users[j].user);
                                add_user_to_room(rooms[i],users[j]);
                            }
                        }
                        pthread_mutex_unlock(&add_user_to_room_mutex);
                        find_free_room = true;
                        number_of_rooms++;
                        strcpy(return_value,"30\n");
                        printf("IM SENDING!!!\n");
                        write(th_data->deskryptor,return_value,sizeof(return_value));
                        break;
                    }
                }
                pthread_mutex_unlock(&add_room_mutex);
                if(!find_free_room){
                    //there is no free room, whole server is occupied
                    strcpy(return_value,"32\n");
                    printf("SERVER IS FULL, CAN'T ADD NEW ROOM\n");
                    write(th_data->deskryptor,return_value,sizeof(return_value));
                }
            }
            else if(*token == '4'){//4 - message room
                bool find_room = false;
                bool find_user = false;
                token = strtok(NULL, s); //message content
                token2 = strtok(NULL, s); //room
                token3 = strtok(NULL, s); //username
                for(int i=0;i<MAX_ROOMS;i++){
                    if(!strcmp(token2,rooms[i].name)){
                        find_room = true;
                        for(int j = 0;j<MAX_USERS;j++){
                            if(!strcmp(users[j].user,token3)){
                                find_user = true;
                                //add new message to room
                                Message message;
                                message.user=users[j];
                                strcpy(message.content,token);
                                pthread_mutex_lock(&sending_message_to_room_mutex);
                                add_message_to_room(rooms[i],message);
                                pthread_mutex_unlock(&sending_message_to_room_mutex);
                                //send message to all actually logged to room users
                                
                                break;
                            }
                        }
                        break;
                    }
                }
                if(!find_room){
                    //there is no such room name
                    printf("There is no such room: %s\n",token2);
                }
                else if(!find_user){
                    //room is ok, no such username
                    printf("there is no such user: %s",token3);
                }
                //if the room is not found then the user is not recognized
            }
            else if(*token == '5'){//5 - message face2face
                token = strtok(NULL, s); //message content
                token2 = strtok(NULL, s); //username to
                token3 = strtok(NULL, s); //username from
                bool find_user_from = false;
                bool find_user_to = false;
                bool find_user_user_chat = false;
                for(int i=0;i<MAX_USERS;i++){
                    if(!strcmp(users[i].user,token2)){
                        find_user_to = true;
                        for(int j = 0;j<MAX_USERS;j++){
                            if(!strcmp(users[j].user,token3)){
                                find_user_from = true;
                                //send new message between users
                                for(int k=0;k<MAX_USERS*(MAX_USERS-1);k++){
                                    if(((!strcmp(users[i].user,useruserchats[k].user1.user)) && (!strcmp(users[j].user,useruserchats[k].user2.user)))||
                                    ((!strcmp(users[j].user,useruserchats[k].user1.user)) && (!strcmp(users[i].user,useruserchats[k].user2.user)))){
                                        //send message
                                        find_user_user_chat=true;
                                        Message message;
                                        strcpy(message.content,token);
                                        message.user=users[i];
                                        add_message_to_chat(useruserchats[k],message);
                                        //finish sending to user_to(now it is only saved on server)
                                        if(users[j].logged){
                                            pthread_mutex_lock(&sending_message_to_chat_mutex);
                                            //send message
                                            pthread_mutex_unlock(&sending_message_to_chat_mutex);
                                        }
                                        else{
                                            //just store it
                                        }

                                        break;
                                    }
                                }
                                break;
                            }
                        }
                    }
                }
                if(!find_user_from){
                    //there is no such username from
                }
                else if(!find_user_to){
                    //username from is ok, no such username to
                }
                else if(!find_user_user_chat){
                    //big problem
                }
                //if the room is not found then the user is not recognized
            }else if(*token=='6'){//change room
                token = strtok(NULL, s); //username
                token2 = strtok(NULL, s); //type of conversation_from('0'-room, '1'- chat)
                token3 = strtok(NULL, s); //conversation name(if chat send username2)_from
                token4 = strtok(NULL, s); //type of conversation_to('0'-room, '1'- chat)
                token5 = strtok(NULL, s); //conversation name(if chat send username2)_to
                
                bool find_room_from = false;
                bool find_room_to = false;
                bool find_user = false;
                for(int i = 0;i<MAX_USERS;i++)
                {
                    if(!strcmp(token,users[i].user)){
                        if(*token2=='0'){//from room
                            find_user = true;
                            for(int j = 0;j<number_of_rooms;j++){
                                if(!strcmp(rooms[j].name,token3)){
                                    find_room_from = true;
                                    if(*token4=='0'){//to room
                                        for(int k=0;k<number_of_rooms;k++){
                                            if(!strcmp(rooms[k].name,token5)){
                                                find_room_to = true;
                                                
                                                for(int l = 0;l<MAX_USERS_IN_ROOM;l++){
                                                    if(!strcmp(token,rooms[j].users[l].user)){
                                                        for(int m = 0;m<MAX_USERS_IN_ROOM;m++){
                                                            if(!strcmp(token,rooms[k].users[m].user)){
                                                                pthread_mutex_lock(&user_change_room_mutex);
                                                                rooms[j].users[l].logged=false;
                                                                rooms[k].users[m].logged=true;
                                                                pthread_mutex_unlock(&user_change_room_mutex);
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }else if(*token4 == '1'){//from room

                                    }
                                    //change room
                                    ;
                                    break;
                                }
                            }
                        }else if(*token2=='1'){//from chat

                        }
                        break;
                    }
                }
                if(!find_user){
                    //there is no such room name
                }
                else if(!find_room_from){
                    //room is ok, no such username
                }
                //if the room is not found then the user is not recognized
            }else if(*token=='7'){ //actually logged users
                char *logged_users = malloc(sizeof(char)*20*20);
                strcpy(logged_users,"70\n");
                for(int i=0;i<number_of_users;i++){
                    if(users[i].logged && users[i].descriptor!=th_data->deskryptor){
                        logged_users = strcat(logged_users,users[i].user);
                        logged_users = strcat(logged_users,"|");
                    }
                }
                logged_users = strcat(logged_users,"\n");
                printf("IM SENDING LOGGED USERS!!!\n");
                write(th_data->deskryptor,logged_users,sizeof(logged_users));
            }else{
                //action not recognized
            }
            //printf("%s",buffor);
        }
        else if (vread == 0){
            printf("Disconnecting...%d\n",th_data -> deskryptor);
            desc_table[th_data -> deskryptor] = 0;
            pthread_mutex_lock(&exit_mutex);
            clients--;
            pthread_mutex_unlock(&exit_mutex);
            for(int i=0;i<MAX_USERS;i++){
                if(th_data->deskryptor==users[i].descriptor){
                    pthread_mutex_lock(&users_logged_mutex);
                    users[i].logged = false;
                    pthread_mutex_unlock(&users_logged_mutex);
                }
            }
            printf("Clients connected to the server: %d\n",clients);
            close(th_data -> deskryptor);
            pthread_exit(NULL);
        }  
    }
    //free(t_data); //zwolnienie pamieci
    pthread_exit(NULL);
}

//funkcja obsługująca połączenie z nowym klientem
void handleConnection(int connection_socket_descriptor) {
    //wynik funkcji tworzącej wątek
    int create_result = 0;

    //uchwyt na wątek
    pthread_t thread1;

    struct thread_data_t* t_data = malloc(sizeof (struct thread_data_t)); //pamiec zajeta tak dluga jak dziala program
    //dane, które zostaną przekazane do wątku
    t_data -> deskryptor = connection_socket_descriptor;
    
    create_result = pthread_create(&thread1, NULL, ThreadBehavior, (void *)t_data);
    if (create_result){
       printf("Błąd przy próbie utworzenia wątku, kod błędu: %d\n", create_result);
       exit(-1);
    }
}

int main(int argc, char* argv[])
{
   int server_socket_descriptor;
   int connection_socket_descriptor;
   int bind_result;
   int listen_result;
   char reuse_addr_val = 1;
   struct sockaddr_in server_address;

   for(int i=0;i<MAX_USERS;i++){
       memset(users[i].user,0,sizeof(char)*MAX_LENGHT_NAME);
   }

   if(argc!=3){
       printf("Please set 2 arguments: 1 - adress IP, 2 - server port\n");
       return(0);
   }

   for (int i = 0; i< 3;i++){
       desc_table[i] = 0;
    }

   //inicjalizacja gniazda serwera
   memset(&server_address, 0, sizeof(struct sockaddr));
   server_address.sin_family = AF_INET;
   server_address.sin_addr.s_addr = htonl(INADDR_ANY);
   server_address.sin_port = htons(atoi(argv[2]));

   server_socket_descriptor = socket(AF_INET, SOCK_STREAM, 0);
   if (server_socket_descriptor < 0)
   {
       fprintf(stderr, "%s: Błąd przy próbie utworzenia gniazda..\n", argv[0]);
       exit(1);
   }
   setsockopt(server_socket_descriptor, SOL_SOCKET, SO_REUSEADDR, (char*)&reuse_addr_val, sizeof(reuse_addr_val));

   bind_result = bind(server_socket_descriptor, (struct sockaddr*)&server_address, sizeof(struct sockaddr));
   if (bind_result < 0)
   {
       fprintf(stderr, "%s: Błąd przy próbie dowiązania adresu IP i numeru portu do gniazda.\n", argv[0]);
       exit(1);
   }

   listen_result = listen(server_socket_descriptor, QUEUE_SIZE);
   if (listen_result < 0) {
       fprintf(stderr, "%s: Błąd przy próbie ustawienia wielkości kolejki.\n", argv[0]);
       exit(1);
   }

    int index;
    while(1)
   {    
        for (int i = 0; i< QUEUE_SIZE;i++){
            if (desc_table[i] == 0){
                index = i;
                break;
            }
        }

       connection_socket_descriptor = accept(server_socket_descriptor, NULL, NULL);
       if (connection_socket_descriptor < 0)
       {
           fprintf(stderr, "%s: Błąd przy próbie utworzenia gniazda dla połączenia.\n", argv[0]);
           exit(1);
       }
        if (clients < QUEUE_SIZE){
            desc_table[index] = connection_socket_descriptor;
            printf("Creating a connection...%d\n",desc_table[index]);
            pthread_mutex_lock(&start_mutex);
            clients++;
            pthread_mutex_unlock(&start_mutex);
            printf("Clients connected to the server: %d\n",clients);
            handleConnection(connection_socket_descriptor);
        }
        else{
            char return_value_2[20];
            printf("Too many clients connected to the server, disconnecting %d...\n", connection_socket_descriptor);
            strcpy(return_value_2,"02\n");
            printf("IM SENDING!!!\n");
            write(connection_socket_descriptor,return_value_2,sizeof(return_value_2));
            close(connection_socket_descriptor);
        }
   }
   for(int i=0;i<MAX_USERS;i++){
       close(users[i].descriptor);
   }
   close(server_socket_descriptor);
   return(0);
}
