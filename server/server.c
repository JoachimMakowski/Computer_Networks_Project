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

#define SERVER_PORT 1234
#define QUEUE_SIZE 100
#define MAX_ROOMS 100
#define MAX_USERS 200

//uchwyt na mutex
pthread_mutex_t read_mutex = PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t exit_mutex = PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t start_mutex = PTHREAD_MUTEX_INITIALIZER;
int desc_table[QUEUE_SIZE];
int clients = 0;

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
        char buffor[50];
        int vread = read(th_data -> deskryptor, buffor,50);
        if (vread > 0){//trzeba zaimplementować if-else i odpowiednie rodzaje wiadomości odpowiednio obsługiwać
        /* 0 - log
        1 - logout
        2 - join room
        3 - create new room
        4 - message room
        5 - message face2face
        */
            char str[80] = "This is \n www.tutorialspoint.com \n website";
            const char s[2] = "\n";
            char *token;
            char *token2;
            char *token3;

            printf("%s\n",str);
            
            /* get the first token */
            token = strtok(str, s);//get token with number
            
            /* walk through other tokens */
            /*while( token != NULL ) {
                printf( " %s\n", token );
                
                token = strtok(NULL, s);
            }*/
            buffor[vread] = '\0';
            if(*token == '0'){  //0 - log
                bool find_user = false;
                token = strtok(NULL, s); //username
                for(int i=0;i<MAX_USERS;i++){
                    if(!strcmp(users[i].user,token)){
                        find_user = true;
                        if(users[i].logged == false) users[i].logged = true;
                        else{
                            //return info that user is already logged
                        }
                    }
                    else if(users[i]==NULL){
                        find_user = true;
                        //set new user and add chat with everyone else
                    }
                }
                //not place avalaible for new user
                if(!find_user){
                    
                }
            }
            else if(*token == '1'){//1 - logout
                token = strtok(NULL, s); //username
                bool find_user = false;
                for(int i=0;i<MAX_USERS;i++){
                    if(!strcmp(users[i].user,token)){
                        find_user = true;
                        if(users[i].logged == true) users[i].logged = false;
                        else{
                            //return exception - user is not logged
                        }
                    }
                }
                if(!find_user){
                    //there is no such user
                }
            }
            else if(*token == '2'){//2 - join room
                token = strtok(NULL, s); //room name
                bool find_room = false;
                bool find_user = false;
                for(int i = 0;i<MAX_ROOMS;i++)
                {
                    if(!strcmp(token,rooms[i].name)){
                        find_room = true;
                        token2 = strtok(NULL, s); //username
                        for(int j = 0;j<MAX_USERS;j++){
                            if(!strcmp(users[i].user,token2)){
                                find_user = true;
                                //add new user to room
                            }
                        }
                    }
                }
                if(!find_room){
                    //there is no such room name
                }
                else if(!find_user){
                    //room is ok, no such username
                }
                //if the room is not found then the user is not recognized
            }
            else if(*token == '3'){//3 - create new room
                token = strtok(NULL, s); //room name
                bool find_free_room = false;
                for(int i = 0; i<MAX_ROOMS;i++){
                    if(rooms[i]==NULL){
                        Room room;
                        room.name = token;
                        rooms[i] = room;
                        find_free_room = true;
                    }
                }
                if(!find_free_room){
                    //there is no free room, whole server is occupied
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
                            if(!strcmp(users[i].user,token3)){
                                find_user = true;
                                //add new message to room
                            }
                        }
                    }
                }
                if(!find_room){
                    //there is no such room name
                }
                else if(!find_user){
                    //room is ok, no such username
                }
                //if the room is not found then the user is not recognized
            }
            else if(*token == '5'){//5 - message face2face
                token = strtok(NULL, s); //message content
                token2 = strtok(NULL, s); //username from
                token3 = strtok(NULL, s); //username to
                bool find_user_from = false;
                bool find_user_to = false;
                bool find_user_user_chat = false;
                for(int i=0;i<MAX_USERS;i++){
                    if(!strcmp(token2,rooms[i].name)){
                        find_user_from = true;
                        for(int j = 0;j<MAX_USERS;j++){
                            if(!strcmp(users[j].user,token3)){
                                find_user_to = true;
                                //send new message between users
                                for(int k=0;k<MAX_USERS*(MAX_USERS-1);k++){
                                    if((users[i]==useruserchats[k].user1 && users[j]==useruserchats[k].user2)||
                                    (users[j]==useruserchats[k].user1 && users[i]==useruserchats[k].user2)){
                                        //send message
                                    }
                                }
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
                //if the room is not found then the user is not recognized
            }
            //printf("%s",buffor);
            printf("Mutex start: %d\n",th_data -> deskryptor);
            pthread_mutex_lock(&read_mutex);
            for (int i =0;i<QUEUE_SIZE;i++){
                if(desc_table[i]!=th_data -> deskryptor){
                    write(desc_table[i], buffor, strlen(buffor));	
                }
            }
            pthread_mutex_unlock(&read_mutex);
            printf("Mutex stop: %d\n",th_data -> deskryptor);
        }
        else if (vread == 0){
            printf("Disconnecting...%d\n",th_data -> deskryptor);
            desc_table[th_data -> deskryptor] = 0;
            pthread_mutex_lock(&exit_mutex);
            clients--;
            pthread_mutex_unlock(&exit_mutex);
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
   struct thread_data_t *t_data;

   for (int i = 0; i< 3;i++){
       desc_table[i] = 0;
    }

   //inicjalizacja gniazda serwera
   memset(&server_address, 0, sizeof(struct sockaddr));
   server_address.sin_family = AF_INET;
   server_address.sin_addr.s_addr = htonl(INADDR_ANY);
   server_address.sin_port = htons(SERVER_PORT);

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
            printf("Too many clients connected to the server, disconnecting %d...\n", connection_socket_descriptor);
            close(connection_socket_descriptor);
        }
   }

   close(server_socket_descriptor);
   return(0);
}
