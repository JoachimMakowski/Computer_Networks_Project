#include"user.c"
#pragma once

typedef struct{
    User user;
    char content[200];
    char room_name[20];
}Message;
