#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <string.h>
int main() {
    int prka=0, n, m, mi;
    char *alp[26] = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
    char solo[120] = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM1234567890()*!@#$%&^";
    FILE* input = fopen("input.txt", "r");
    FILE* output = fopen("output.txt", "w");
    char s[600];
    fscanf(input, "%d", &n);
    fscanf(input, "%s", s);
    fscanf(input, "%d", &m);
    // prka is variable which will help me to find the cases of "Invalid inputs" (if prka is larger than zero it will mean that my inputs are invalid)

    // solo is string of possible characters

    // I opened file and read the n(Length of S(in my solution it is s)), m(Length of array A(in my solution it is ar)), mi is min[n-1, 26]
    if (n-1<=26) {mi=(n-1);}
    else {mi=26;}

    if ((n<2) || (n>50) || (m<1) || (m>mi)) {prka+=1;}

    // I checked is n and m are out of the bounds

    if (prka==0) { // in this IF i am sure that my n and m are "usable"
        for (int i=0; i<n; i++) {
            if (strchr(solo, s[i])) {continue;}
            else {prka+=1;}
        }

        // checking I check if S has the characters specified by the rules

        int ar[m];
        for (int i=0; i<m; i++) {
            int val;
            fscanf(input, "%d", &val);
            ar[i]=val;
        }

        // declared an array ar of m elements

        for (int i=0; i<m; i++) {
            int gc = 0;
            if ((ar[i]>=n) || (ar[i]>26) || (ar[i]<1)) {prka+=1; break;}
            //  checking this cases of "Invalid inputs": maximum value in ar is bigger than or equal to n, a value in ar is bigger than 26 or smaller than 1
            for (int j=0; j<m; j++) {
                if (ar[i]==ar[j]) {gc+=1;}
            }
            if (gc>1) {prka+=1;}
        }


        // in loop of FORs I am making sure that my values in ar are distinct

        if (prka==0) {
            for (int i = 0; i < m; i++) {
                int b = ar[i];
                s[b]=*alp[b-1];
            }
            fprintf(output, "%s\n", s);
        }

        // created my new correct s which I put into the output file

        else {fprintf(output, "Invalid inputs\n");}
    }
    else {fprintf(output, "Invalid inputs\n");}
}
