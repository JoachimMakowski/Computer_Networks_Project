#include <stdio.h>
#include <string.h>
#include <stdbool.h>
#include <stdlib.h>

typedef struct {//ewentualnie dodać kolor lub czy_zalogowany
    char user[25];
    bool logged;
} User;

int main () {

   /* an array with 5 elements */
   double balance[5] = {1000.0, 2.0, 3.4, 17.0, 50.0};
   double *p;
   int i;

   p = balance;
 
   /* output each array element's value */
   printf( "Array values using pointer\n");
	
   for ( i = 0; i < 4; i++ ) {
      *(p+i) = *(p+i+1);
   }
   *(p+4) = 700.0;

   printf( "Array values using balance as address\n");
	
   for ( i = 0; i < 5; i++ ) {
      printf("*(balance + %d) : %f\n",  i, *(balance + i) );
   }

   char str[80] = "This is \n www.tutorialspoint.com \n website";
   const char s[2] = "\n";
   char *token;

   //printf("%s\n",str);
   
   /* get the first token */
   token = strtok(str, s);
   //printf( "%s\n", token );

   // walk through other tokens 
   while( token != NULL ) {
      printf( "%s\n", token );
    
      token = strtok(NULL, s);
   }
   char asd[20] = "asd";
   printf("qwerty");
   User nums[13];
   memset(nums,'\0',sizeof(User)*13);
   strcpy(nums[2].user, asd);
   //nums[2]->logged = &f_false;
   for(i=0;i<13;i++) {
      if(*nums[i].user=='\0') {
         printf("null char found! in position: %d\n",i);
      }
      else {
         printf("element: %s found in position: %d of int array\n",nums[i].user,i);
      }
   }

   return 0;
}