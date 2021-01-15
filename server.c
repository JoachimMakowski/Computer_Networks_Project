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

#define SERVER_PORT 1234
#define QUEUE_SIZE 100

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

//funkcja opisującą zachowanie wątku - musi przyjmować argument typu (void *) i zwracać (void *) check
void *ThreadBehavior(void *t_data)
{
    pthread_detach(pthread_self());
    //dostęp do pól struktury: (*th_data).pole
    struct thread_data_t *th_data = (struct thread_data_t*)t_data;
    
    while(1){
        char buffor[50];
        int vread = read(th_data -> deskryptor, buffor,50);
        if (vread > 0){
            buffor[vread] = '\0';
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
