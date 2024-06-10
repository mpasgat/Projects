#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <string.h>
#include <ctype.h>


struct play {
    char id[30];
    char name[30];
    enum position {Goalkeeper=1, Defender, Midfielder, Forward} positions;
    int age;
    int gs;
};



int main() {
    char *pos[4]={"Goalkeeper", "Defender", "Midfielder", "Forward"}, first[20], sl[20]; // first is my command(Update, Add etc); sl is position that read from the file
    int el=0, prka=0, dc[30]={0}; // el is to add new elements to my arrays; prka is to check the Invalid input cases; dc is responsible to check if the ID is relevant or not(if its element 1 than id goes to the output file)
    FILE* input = fopen("input.txt", "r");
    FILE* output = fopen("output.txt", "w");
    fscanf(input, "%s", first);
    struct play p[30]={0};
    if (strcmp(first, "Display")==0) {prka+=1;} // checked if there is only Display in the file
    while (strcmp(first, "Display")!=0 && prka==0) { // started to read whole file
        if (strcmp(first, "Delete")!=0 && strcmp(first, "Display")!=0 && strcmp(first, "Add")!=0 && strcmp(first, "Search")!=0 && strcmp(first, "Update")!=0) {prka+=1;} // checked if my first is actually a command

        if (feof(input)) {prka+=1; break;} // if there is an empty line(which is also Invalid) that means there was no display then Invalid inputs
        if (strcmp(first, "Add")==0 && prka==0) {
            fscanf(input, "%s", p[el].id);
            dc[el]=1;
            for (int y=0; y<strlen(p[el].id); y++) {
                if (isdigit(p[el].id[y])) {continue;} else {prka+=1;}
            } // checked is ID is only from digits
            int dcc=0;
            for (int x=0; x<20; x++) {
                if (strcmp(p[el].id, p[x].id)==0) {dcc+=1;}
            }
            if (dcc>1) {prka+=1;} // is the ID unique?


            fscanf(input, "%s", p[el].name);
            if (strcmp("Display", p[el].name)==0 || strcmp("Update", p[el].name)==0 || strcmp("Search", p[el].name)==0 || strcmp("Add", p[el].name)==0 || strcmp("Delete", p[el].name)==0) {prka+=1;}
            // if there instead of  name some command then invalid

            if (!(isupper(p[el].name[0]))) {prka+=1;} // is the first letter of name is upercase
            if (!((1<strlen(p[el].name) && strlen(p[el].name)<16))) {prka+=1;}
            for (int j=1; j<strlen(p[el].name); j++) {
                if ((p[el].name[j] >= 'a' && p[el].name[j] <= 'z')) {continue;} else {prka+=1;}
            } //is the name consist of lower letters

            fscanf(input, "%s", sl);
            int ki=0;
            for (int o=0; o<4; o++) {if (strcmp(sl, pos[o])==0) {ki+=1;}}
            if (ki==0) {prka+=1;} // is the position is correct, is it gk, defender, mid or forward


            fscanf(input, "%d", &p[el].age);
            if (100<p[el].age || p[el].age<18) {prka+=1;} // checking age limits
            fscanf(input, "%d", &p[el].gs);
            if (p[el].gs<0 || p[el].gs>=1000) {prka+=1;} // checking goal limits
            if(strcmp("Goalkeeper", sl)==0) {p[el].positions = Goalkeeper;}
            if(strcmp("Defender", sl)==0) {p[el].positions = Defender;}
            if(strcmp("Midfielder", sl)==0) {p[el].positions = Midfielder;}
            if(strcmp("Forward", sl)==0) {p[el].positions = Forward;}
            // assign the position
            el+=1; // preparing for next ID

        }


        if (strcmp(first, "Delete")==0 && prka==0) {
            char idi[20];
            bool dl=false;
            fscanf(input, "%s", idi);
            for (int y=0; y<strlen(idi); y++) {
                if (isdigit(idi[y])) {continue;} else {prka+=1;}
            } // checked if ID is consist of digits
            for (int i=0; i<20; i++) {
                if (strcmp(p[i].id, idi)==0) {dc[i]=0; dl=true;}
            } // if ID is found , I am making dc[i] zero which means that it is irrelevant
            if (dl==false) {fprintf(output, "Impossible to delete\n");}
        }



        if (strcmp(first, "Search")==0 && prka==0) { // Searching ID
            char poisk[20];
            bool prop = false;
            fscanf(input, "%s", poisk);
            for (int i=0; i<20; i++) {if (strcmp(p[i].id, poisk)==0) {prop=true;} }
            if (prop) {fprintf(output, "Found\n");}
            else {fprintf(output, "Not found\n");}
        }



        if (strcmp(first, "Update")==0 && prka==0) {
            char se[20], sl[20];
            int lol=0;
            fscanf(input, "%s", se);
            for (int y=0; y<strlen(se); y++) { // checked if ID is consist of digits
                if (isdigit(se[y])) {continue;} else {prka+=1;}
            }
            for (int i=0; i<20; i++) { // searching ID that I should upgrade
                if (strcmp(p[i].id, se)==0) {
                    fscanf(input, "%s", p[i].name); // Same comments which was in the ADD part besides of the uniqueness of the ID
                    if (strcmp("Display", p[i].name)==0 || strcmp("Update", p[i].name)==0 || strcmp("Search", p[i].name)==0 || strcmp("Add", p[i].name)==0 || strcmp("Delete", p[i].name)==0) {prka+=1;}
                    if (!(isupper(p[i].name[0]))) {prka+=1;}
                    if (!((1<strlen(p[i].name) && strlen(p[i].name)<16))) {prka+=1;}
                    for (int j=1; j<strlen(p[i].name); j++) {
                        if ((p[i].name[j] >= 'a' && p[i].name[j] <= 'z')) {continue;} else {prka+=1;}
                    }
                    fscanf(input, "%s", sl);
                    int ki=0;
                    for (int o=0; o<4; o++) {if (strcmp(sl, pos[o])==0) {ki+=1;}}
                    if (ki==0) {prka+=1;}
                    fscanf(input, "%d", &p[i].age);
                    if (100<p[i].age || p[i].age<18) {prka+=1;}
                    fscanf(input, "%d", &p[i].gs);
                    if (p[i].gs<0 || p[i].gs>=1000) {prka+=1;}
                    if(strcmp("Goalkeeper", sl)==0) {p[i].positions = Goalkeeper;}
                    if(strcmp("Defender", sl)==0) {p[i].positions = Defender;}
                    if(strcmp("Midfielder", sl)==0) {p[i].positions = Midfielder;}
                    if(strcmp("Forward", sl)==0) {p[i].positions = Forward;}
                    lol=1;
                }
            }
            if (lol==0) {prka+=1;} // this will tell me if the id is not found then it wants me to update ID which not exists then Invalid inputs
        }
        fscanf(input, "%s", first);

    }




    for (int i=0; i<20; i++) {
        if (prka!=0) {fprintf(output, "Invalid inputs\n"); break;}
        if (dc[i]==1 && prka==0) { // if everything is alright then I can put to the file my results, I personally used the switch method with positions
            switch(p[i].positions) {
                case 1:
                    fprintf(output, "ID: %s, Name: %s, Position: %s, Age: %d, Goals: %d\n", p[i].id, p[i].name, "Goalkeeper", p[i].age, p[i].gs);
                    break;
                case 2:
                    fprintf(output, "ID: %s, Name: %s, Position: %s, Age: %d, Goals: %d\n", p[i].id, p[i].name, "Defender", p[i].age, p[i].gs);
                    break;
                case 3:
                    fprintf(output, "ID: %s, Name: %s, Position: %s, Age: %d, Goals: %d\n", p[i].id, p[i].name, "Midfielder", p[i].age, p[i].gs);
                    break;
                case 4:
                    fprintf(output, "ID: %s, Name: %s, Position: %s, Age: %d, Goals: %d\n", p[i].id, p[i].name, "Forward", p[i].age, p[i].gs);
                    break;
            };
        }
    }
    printf("%d", prka);
    return 0;
}
