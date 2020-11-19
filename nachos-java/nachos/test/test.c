#include "stdio.h"
#include "stdlib.h"

int main(int argc, char** argv)
{
    int i, j, num;
   
    char buf[30];
   
    printf("Hello there!!\n");
   
    printf("Enter a number: ");
    readline(buf, 10);

    num = atoi(buf);
    
    for (i = 1; i <= num; ++i) {
      for (j = 1; j <= i; ++j) {
         printf("%d ", j);
      }
      printf("\n");
   }

    printf("\n");

    printf("Enter a character: ");
    char c = getch();

    printf("Your character is %c\n", c);
   
    printf("Enter any string: ");
    readline(buf, 30);
    printf("INPUT by user : %s\n", buf);
   
    printf("------------CHECKING INVALID READ CALLS--------------\n");
    num = read(3, &buf, 10);
    printf("Return on invalid file descriptor: %d\n", num);
    num = read(0, -12, 10);
    printf("Return on invallid vaddr: %d\n", num);
    num = read(0, &buf, -2);
    printf("Return on invallid size: %d\n", num);
    printf("------------END CHECKING INVALID READ CALLS--------------\n");
   
    printf("\n------------CHECKING INVALID WRITE CALLS--------------\n");
    num = write(3, &buf, 10);
    printf("Return on invalid file descriptor: %d\n", num);
    num = write(0, -12, 10);
    printf("Return on invallid vaddr: %d\n", num);
    num = write(0, &buf, -2);
    printf("Return on invallid size: %d\n", num);
    printf("------------END CHECKING INVALID WRITE CALLS--------------\n");
    
    halt();
   
    printf("Halt is not working!!\n");
   
    return 0;
}