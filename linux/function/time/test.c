#include <stdio.h>
#include <stddef.h>
#include <time.h>

int main(void) {

    time_t timer;//time_t就是long int 类型
    struct tm *tblock;

    timer = time(NULL);//这一句也可以改成time(&timer);
    //timer = time(&timer);
    //timer = (time_t)((0x8D2835D437B1C00-621355968000000000)/10000000);
    tblock = localtime(&timer);
    printf("%02d, %02d\n", tblock->tm_hour, tblock->tm_min);
    printf("Local time is: %s\n",asctime(tblock));

    //printf("%d\n", sizeof(time_t));

    return 0;

}

//00D2D53E2393D288 



#if 0

0x001C7B435D83D288
0x88D2835D437B1C00
0x00623E675D83D288


0x2C00
0x00000000
0xE8030000
0x00A439785A83D288
0x00EAFC9B5A83D288
0x0100000000
0x0C
0xE6 98 8C E4 B9 9D E5 9F 8E E9 99 85 4A 00 01 01 00 00 00 00 0C E6 98 8C E4 B9 9D E5 9F 8E E9 99 85 00 00 01 00 02 00 03 00 04 00 05 00 06 00 07 01 11 00 02 02 31 31 14 00 00 00 00 00 00 00 0A 00 00 00 03 08 00 09 00 0A 00 0B 00 0C 00 0D 00 0E 00 0F 00 10 00

118 bytes data:

0x2C00                  2 44bytes
0x00000000              4
0xED030000              4
0x001C7B435D83D288      8
0x00623E675D83D288      8
0x01000000              4
0x00                    1

0x0CE6988CE4B99DE59F8EE999854A000101000000000CE6988CE4B99DE59F8EE999850000010002000300040005000600070111000202313114000000000000000A00000003080009000A000B000C000D000E000F001000



98590869708 54 77 58 08
98590869714 54 77 58 08

9859086958854775808
9859086959454775808

12:10-12:11

#endif











