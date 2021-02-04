#pragma once
#define MAX_LENGHT_NAME 25

typedef struct{//ewentualnie dodać kolor lub czy_zalogowany
    char user[MAX_LENGHT_NAME];
    bool logged;
    int descriptor;
}User;
